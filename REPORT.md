# Document Search Engine: Data Structure Performance Report

## 1. Overview

This project implements a document search engine in Java from first-principles data structures: a singly linked list, an array-backed stack, a linked-list queue, merge sort, quick sort, linear and binary search, an unbalanced BST map, an AVL map, a red-black map, and a separate-chaining hash map. All four maps implement one `MapADT<K,V>` interface, so the inverted index can be built on any of them by changing a single `MapType` argument. The engine tokenizes a directory of documents, builds an inverted index of word → (document, frequency) postings, answers single- and multi-term AND queries with TF-IDF ranking, and supports back/forward navigation using two `ArrayStack`s plus a `ListQueue` of recently viewed documents.

All 147 JUnit 5 tests pass, including every scenario in the provided test-case sheet, run against all four map types.

## 2. Experimental Setup

Experiments are in `Benchmark.java` and reproduce with one command. Keys are random 8-letter lowercase strings (fixed seed 42). Each measurement is the median of 3–5 trials after a JIT warm-up phase. Hardware: 2-core Linux VM, Java 21. Absolute times will differ on other machines; the growth rates are what matter.

## 3. Results

### 3.1 Sorting (ms, median of 5)

| n | input | merge sort | quick sort |
|---:|---|---:|---:|
| 10,000 | random | 12.32* | 4.02 |
| 50,000 | random | 16.10 | 17.93 |
| 100,000 | random | 35.62 | 41.31 |
| 100,000 | sorted | 2.29 | 27.61 |
| 100,000 | reversed | 16.14 | 27.76 |

\*Outlier caused by JIT compilation/GC during that run; the normalized cost at 1k, 50k and 100k is a steady ~21 ns per n·log₂n comparison unit.

Doubling n from 50,000 to 100,000 increased random-input time by 2.2× for merge sort and 2.3× for quick sort, matching the O(n log n) prediction of 2 × log(100000)/log(50000) ≈ 2.13×. Merge sort is nearly linear on already-sorted input because of a "skip merge if halves are already in order" check, which reduces the best case to O(n). Quick sort uses a random pivot with three-way partitioning, so sorted and reversed inputs do not trigger its O(n²) worst case, and duplicate keys are handled in one pass.

### 3.2 Linear vs. Binary Search (ns per lookup, 50% hits)

| n | linear | binary | speedup |
|---:|---:|---:|---:|
| 1,000 | 3,098 | 204 | 15× |
| 10,000 | 23,164 | 241 | 96× |
| 100,000 | 348,423 | 401 | 869× |

Linear search grows ~10× per 10× increase in n (O(n)). Binary search grows only by the added ~3.3 comparisons per 10× (O(log n)). The trade-off is the up-front O(n log n) sort; binary search pays off once an array is searched more than roughly log n times.

### 3.3 Map Insertion and Search

| n | key order | BST insert (ms) | AVL | Red-black | Hash | BST height | AVL height | RB height |
|---:|---|---:|---:|---:|---:|---:|---:|---:|
| 10,000 | random | 3.11 | 2.72 | 2.83 | 1.41 | 29 | 16 | 16 |
| 20,000 | random | 7.21 | 7.07 | 6.58 | 1.95 | 33 | 17 | 17 |
| 1,000 | sorted | 6.35 | 0.22 | 0.30 | 0.19 | 1,000 | 10 | 17 |
| 10,000 | sorted | 797.18 | 1.38 | 2.66 | 1.51 | 10,000 | 14 | 24 |
| 20,000 | sorted | 1,762.28 | 2.57 | 5.14 | 1.46 | 20,000 | 15 | 26 |

Average successful search at n = 20,000: BST 380 ns (random) vs. 89,142 ns (sorted); AVL 269–304 ns; red-black 294–412 ns; hash table 37–50 ns.

**Critical test (`long_sorted.txt`, 104 words):** BST height 104 (a linked list), AVL height 7, red-black height 11. The BST took 69 µs vs. ~21–24 µs for the others. At this small n the gap is only 3×; the scaled runs above show it becoming 690× at n = 20,000.

## 4. Analysis: Theory vs. Practice

**Unbalanced BST.** With random keys the BST height was about 2× the AVL height (33 vs. 17 at n = 20,000). That is still logarithmic (theory predicts roughly 3·log₂ n for random insertion order), so the BST was competitive. With sorted keys every insertion walks the entire right spine, giving Σi = n(n−1)/2 = O(n²) total work. Measured growth from n = 1,000 to 20,000 (20× more keys) was 277×, clearly super-linear and close to the 400× quadratic prediction. The single 10,000 → 20,000 step grew only 2.2× instead of 4×; I could not isolate the cause from one run, and individual timings at this scale are noisy, so the multi-size trend is the more reliable evidence.

**AVL vs. red-black.** Both guarantee O(log n) height. AVL is more strictly balanced (height ≤ 1.44 log₂ n): on sorted input it built a near-perfect tree (height 15 for 20,000 keys, log₂ 20,000 ≈ 14.3). Red-black trees allow height up to 2 log₂ n and reached 26. In exchange, red-black performed fewer rotations on random input (11,555 vs. 13,971 at n = 20,000) because recoloring resolves many violations without restructuring. In these runs AVL searched slightly faster and inserted slightly faster on sorted input; red-black's advantage would show more in delete-heavy workloads, where AVL may rotate O(log n) times per deletion and red-black at most three times.

**Hash table.** Separate chaining with a 0.75 load factor and power-of-two resizing gave the fastest search at every size (5–10× faster than the trees) because expected chain length stays constant: the longest chain at n = 20,000 was only 6. Search is O(1) expected but O(n) worst case if many keys collide (the `"Aa"`/`"BB"` collision test shows chaining handles this correctly). Insert-time spikes come from O(n) resizes, which amortize to O(1).

## 5. Design Decisions and Trade-offs

- **One map interface, four implementations.** The inverted index depends only on `MapADT`, which made the experiments a fair comparison and let the integration tests verify identical results for every structure.
- **Hash table for the index in practice; trees when order matters.** For pure term lookup the hash table is the best choice. A balanced tree is worth its cost when ordered operations are needed (alphabetical term listing, prefix or range queries such as "all terms starting with `pro`"), which a hash table cannot do efficiently. The unbalanced BST should never be used for keys that may arrive sorted, and real documents indexed in alphabetical order make that common.
- **Postings append in O(1).** Documents are indexed one at a time, so each posting list only needs to check its last entry to increment a frequency. Indexing is O(total tokens × map lookup cost).
- **Queries.** Multi-term queries use AND semantics, intersecting from the rarest term first and using per-term hash tables, so cost is proportional to the smallest posting list. `query()` returns matches in document order (what the provided test sheet expects); `rankedQuery()` orders them by TF-IDF, score = Σ (1 + ln tf) · ln(1 + N/df), with ties broken by name. For "the", ranking places doc2.txt (2 occurrences) above doc1.txt.
- **Tokenizer.** Lowercases and splits on anything that is not a letter, digit, or `+`, which keeps "c++" as one term while stripping all other punctuation. The same tokenizer normalizes queries, so "C++" and "Fox!" match correctly.
- **Navigation.** Back and forward are two stacks; any new visit clears the forward stack, matching browser behavior. A bounded `ListQueue` records the most recent views in FIFO order.
- **Robustness.** The BST uses iterative insert, search, remove and traversal, so a 50,000-node degenerate tree does not overflow the call stack; the balanced trees are recursive because their depth is logarithmic.

## 6. Reflection

The clearest lesson is that asymptotic guarantees matter most on adversarial but realistic inputs. On random keys the plain BST looked "good enough," within 10–30% of the balanced trees. On sorted keys the same code became 690× slower, while AVL and red-black trees barely changed. Balancing costs a constant number of rotations per insertion and buys protection against the worst case. Hash tables were fastest overall, but they give up ordering and depend on a good hash function. Measured growth rates generally matched Big-O predictions, but small inputs and individual runs were noisy because of JIT compilation, garbage collection, and CPU caching, which is why medians, warm-up runs, and multiple input sizes were necessary to draw conclusions.
