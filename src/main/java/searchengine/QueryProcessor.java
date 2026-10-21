package searchengine;

import java.util.ArrayList;
import java.util.List;

public class QueryProcessor {

    private final InvertedIndex index;

    public QueryProcessor(InvertedIndex index) {
        this.index = index;
    }

    public List<String> query(String queryText) {
        List<String> documents = new ArrayList<>();
        for (SearchResult result : findMatches(queryText)) {
            documents.add(result.getDocumentId());
        }
        return documents;
    }

    public List<SearchResult> rankedQuery(String queryText) {
        List<SearchResult> matches = findMatches(queryText);
        SearchResult[] ranked = matches.toArray(new SearchResult[0]);
        Sorting.mergeSort(ranked);
        List<SearchResult> results = new ArrayList<>(ranked.length);
        for (SearchResult result : ranked) {
            results.add(result);
        }
        return results;
    }

    public List<String> parseTerms(String queryText) {
        List<String> terms = new ArrayList<>();
        HashMap<String, Boolean> seen = new HashMap<>();
        for (String token : Tokenizer.tokenizeText(queryText)) {
            if (!seen.containsKey(token)) {
                seen.insert(token, Boolean.TRUE);
                terms.add(token);
            }
        }
        return terms;
    }

    private List<SearchResult> findMatches(String queryText) {
        List<SearchResult> matches = new ArrayList<>();
        List<String> terms = parseTerms(queryText);
        if (terms.isEmpty()) {
            return matches;
        }
        List<PostingList> postingLists = new ArrayList<>();
        PostingList rarest = null;
        for (String term : terms) {
            PostingList postings = index.getPostings(term);
            if (postings == null) {
                return matches;
            }
            postingLists.add(postings);
            if (rarest == null || postings.documentFrequency() < rarest.documentFrequency()) {
                rarest = postings;
            }
        }
        double totalDocuments = index.documentCount();
        for (Posting candidate : rarest) {
            String documentId = candidate.getDocumentId();
            double score = 0.0;
            boolean containsAll = true;
            for (int i = 0; i < postingLists.size(); i++) {
                Integer frequency = postingLists.get(i).getFrequency(documentId);
                if (frequency == 0) {
                    containsAll = false;
                    break;
                }
                double inverseDocumentFrequency = Math.log(1.0 + totalDocuments / postingLists.get(i).documentFrequency());
                score += (1.0 + Math.log(frequency)) * inverseDocumentFrequency;
            }
            if (containsAll) {
                matches.add(new SearchResult(documentId, score));
            }
        }
        return matches;
    }

    /** OR query: a document qualifies if it contains at least one term. */
    public List<SearchResult> orQuery(String queryText) {
        java.util.Map<String, Double> scores = new java.util.HashMap<>();
        double total = index.documentCount();
        for (String term : parseTerms(queryText)) {
            PostingList postings = index.getPostings(term);
            if (postings == null) continue;
            double idf = Math.log(1.0 + total / postings.documentFrequency());
            for (Posting posting : postings) {
                double weight = (1.0 + Math.log(posting.getFrequency())) * idf;
                scores.merge(posting.getDocumentId(), weight, Double::sum);
            }
        }
        return sortScores(scores);
    }

    /** Prefix search across the indexed vocabulary, with an explicit matching-term cap. */
    public List<SearchResult> prefixQuery(String prefix, int maxTerms) {
        if (maxTerms <= 0) throw new IllegalArgumentException("maxTerms must be positive");
        String normalized = Tokenizer.normalizeTerm(prefix);
        if (normalized == null) return new ArrayList<>();
        java.util.Map<String, Double> scores = new java.util.HashMap<>();
        int count = 0;
        for (String term : index.getTerms()) {
            if (!term.startsWith(normalized)) continue;
            if (++count > maxTerms) break;
            PostingList postings = index.getPostings(term);
            double idf = Math.log(1.0 + (double) index.documentCount() / postings.documentFrequency());
            for (Posting posting : postings) {
                scores.merge(posting.getDocumentId(),
                    (1.0 + Math.log(posting.getFrequency())) * idf, Double::sum);
            }
        }
        return sortScores(scores);
    }

    private List<SearchResult> sortScores(java.util.Map<String, Double> scores) {
        SearchResult[] ranked = scores.entrySet().stream()
            .map(e -> new SearchResult(e.getKey(), e.getValue()))
            .toArray(SearchResult[]::new);
        Sorting.mergeSort(ranked);
        return new ArrayList<>(java.util.Arrays.asList(ranked));
    }
}
