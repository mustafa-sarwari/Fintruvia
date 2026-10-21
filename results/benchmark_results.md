# Benchmark Results

Java 21.0.12.1, Linux, 2 cores. Times are medians.

## 1. Sorting (ms)

| n | input | merge sort | quick sort | merge ns/(n log2 n) | quick ns/(n log2 n) |
|---:|---|---:|---:|---:|---:|
| 1,000 | random | 0.22 | 0.26 | 22.1 | 25.9 |
| 1,000 | sorted | 0.02 | 0.32 | 2.4 | 31.7 |
| 1,000 | reversed | 0.23 | 0.30 | 22.7 | 30.3 |
| 10,000 | random | 12.32 | 4.02 | 92.7 | 30.3 |
| 10,000 | sorted | 0.22 | 4.56 | 1.6 | 34.3 |
| 10,000 | reversed | 8.94 | 5.18 | 67.2 | 39.0 |
| 50,000 | random | 16.10 | 17.93 | 20.6 | 23.0 |
| 50,000 | sorted | 1.21 | 12.64 | 1.5 | 16.2 |
| 50,000 | reversed | 7.83 | 14.47 | 10.0 | 18.5 |
| 100,000 | random | 35.62 | 41.31 | 21.4 | 24.9 |
| 100,000 | sorted | 2.29 | 27.61 | 1.4 | 16.6 |
| 100,000 | reversed | 16.14 | 27.76 | 9.7 | 16.7 |

## 2. Linear vs. binary search (average ns per lookup, 1000 lookups, 50% hits)

| n | linear search | binary search | speedup |
|---:|---:|---:|---:|
| 1,000 | 3,098 | 204 | 15x |
| 10,000 | 23,164 | 241 | 96x |
| 100,000 | 348,423 | 401 | 869x |

## 3. Map implementations

Insert = total ms to insert all n keys. Search = average ns per successful lookup (keys queried in random order).
Height counts nodes on the longest root-to-leaf path. For the hash table, the height column shows the longest chain.

| n | key order | structure | insert (ms) | search (ns) | height | rotations |
|---:|---|---|---:|---:|---:|---:|
| 1,000 | random | BST | 0.29 | 333 | 23 | - |
| 1,000 | random | AVL | 0.28 | 216 | 12 | 670 |
| 1,000 | random | RED_BLACK | 0.39 | 264 | 12 | 560 |
| 1,000 | random | HASH | 0.19 | 115 | 3 | - |
| 1,000 | sorted | BST | 6.35 | 6,366 | 1000 | - |
| 1,000 | sorted | AVL | 0.22 | 196 | 10 | 990 |
| 1,000 | sorted | RED_BLACK | 0.30 | 199 | 17 | 983 |
| 1,000 | sorted | HASH | 0.19 | 114 | 3 | - |
| 5,000 | random | BST | 1.73 | 313 | 29 | - |
| 5,000 | random | AVL | 1.13 | 191 | 15 | 3,517 |
| 5,000 | random | RED_BLACK | 1.27 | 196 | 15 | 2,917 |
| 5,000 | random | HASH | 0.50 | 31 | 5 | - |
| 5,000 | sorted | BST | 175.55 | 34,673 | 5000 | - |
| 5,000 | sorted | AVL | 0.58 | 187 | 13 | 4,987 |
| 5,000 | sorted | RED_BLACK | 1.12 | 183 | 22 | 4,978 |
| 5,000 | sorted | HASH | 0.52 | 18 | 5 | - |
| 10,000 | random | BST | 3.11 | 283 | 29 | - |
| 10,000 | random | AVL | 2.72 | 210 | 16 | 6,957 |
| 10,000 | random | RED_BLACK | 2.83 | 236 | 16 | 5,810 |
| 10,000 | random | HASH | 1.41 | 27 | 6 | - |
| 10,000 | sorted | BST | 797.18 | 80,064 | 10000 | - |
| 10,000 | sorted | AVL | 1.38 | 216 | 14 | 9,986 |
| 10,000 | sorted | RED_BLACK | 2.66 | 228 | 24 | 9,976 |
| 10,000 | sorted | HASH | 1.51 | 31 | 6 | - |
| 20,000 | random | BST | 7.21 | 380 | 33 | - |
| 20,000 | random | AVL | 7.07 | 304 | 17 | 13,971 |
| 20,000 | random | RED_BLACK | 6.58 | 412 | 17 | 11,555 |
| 20,000 | random | HASH | 1.95 | 50 | 6 | - |
| 20,000 | sorted | BST | 1762.28 | 89,142 | 20000 | - |
| 20,000 | sorted | AVL | 2.57 | 269 | 15 | 19,985 |
| 20,000 | sorted | RED_BLACK | 5.14 | 294 | 26 | 19,974 |
| 20,000 | sorted | HASH | 1.46 | 37 | 6 | - |

## 4. Critical test: inserting long_sorted.txt

Words in file: 104 (already in alphabetical order)

| structure | insert time (microseconds) | height | rotations |
|---|---:|---:|---:|
| BST | 69.4 | 104 | - |
| AVL | 20.8 | 7 | 97 |
| RED_BLACK | 23.7 | 11 | 93 |
| HASH | 23.4 | 4 | - |

