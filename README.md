# Java Document Search Engine

[![Java CI](https://github.com/mustafa-sarwari/java-document-search-engine/actions/workflows/java-ci.yml/badge.svg)](https://github.com/mustafa-sarwari/java-document-search-engine/actions/workflows/java-ci.yml)

A command-line **document search engine built in Java 17**, demonstrating hand-built data structures, inverted indexing, TF-IDF relevance ranking, Boolean retrieval, and benchmark-driven algorithm analysis.

## Highlights

- Custom **BST, AVL, Red-Black Tree and Hash Map** implementations, selectable as index backends.
- Custom linked list, array stack, and queue; merge sort, quicksort, linear/binary search.
- Text normalization, per-document term frequencies, inverted index, and ranked multi-term AND queries.
- **OR queries**, **contiguous-token phrase search**, and **prefix retrieval**.
- Browsable results with backward/forward document navigation.
- JUnit tests, GitHub Actions Java CI, and an experimental [performance report](REPORT.md).

## Requirements

- JDK 17 or newer
- Maven 3.8+

## Quick start

```bash
mvn clean verify
mvn compile exec:java -Dexec.args="examples/documents avl"
```

Supported maps: `bst`, `avl` (default), `red_black`, `hash`.

### CLI examples

```text
search ranking search       # AND: all terms must match
or trees hashing           # OR: any term can match
phrase inverted index      # adjacent normalized tokens
prefix alg                 # vocabulary terms starting with alg
open 1                     # display a numbered search result
back                       # navigate browsing history
forward
history
help
quit
```

`search` and `or` use TF-IDF scoring. Phrase matching is token based: punctuation and case are normalized, and matching scans candidate document text. Prefix matches at most 100 vocabulary terms in index iteration order and ranks their matching documents. This is an educational engine, not a web crawler or a production distributed search service.

### Benchmarks

```bash
mvn compile exec:java -Dexec.mainClass=searchengine.Benchmark
```

Detailed experiment and caveats: [REPORT.md](REPORT.md). Committed measurements are historical results, not universal performance guarantees.

## Architecture

```text
.txt files -> Tokenizer -> InvertedIndex -> MapADT backend
                                         -> PostingList (cached document lookup)
query -> QueryProcessor -> AND/OR/prefix candidate retrieval -> TF-IDF -> ranked results
                            phrase -> candidate token-sequence verification
```

## Structure

| Directory | Purpose |
|---|---|
| `src/main/java/searchengine/` | Core engine, data structures, CLI, benchmarks |
| `src/test/java/searchengine/` | JUnit regression tests |
| `documents/` | Original small test fixtures |
| `examples/documents/` | Larger demonstration text corpus |
| `.github/workflows/java-ci.yml` | Automated build/test workflow |
| `REPORT.md` | Analysis of algorithms and experimental results |

## Security and performance notes

File reads require IDs from the index, verify canonical paths, and reject traversal outside the document root. Posting lists maintain a document-frequency lookup cache to avoid rebuilding hash maps for every query. AND queries still scan the smallest posting list and probe other lists; building and maintaining the index has its own cost. Prefix expansion currently scans the vocabulary; phrase search scans matching documents and is deliberately not position-indexed.

## Next steps

Position-aware postings for faster phrase search, snippets, incremental indexing and a more extensive benchmark corpus are natural production-oriented extensions.

## License

[MIT](LICENSE) — Ghulam Mustafa Sarwari.
