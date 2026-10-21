package searchengine;

public class SearchResult implements Comparable<SearchResult> {

    private final String documentId;
    private final double score;

    public SearchResult(String documentId, double score) {
        this.documentId = documentId;
        this.score = score;
    }

    public String getDocumentId() {
        return documentId;
    }

    public double getScore() {
        return score;
    }

    @Override
    public int compareTo(SearchResult other) {
        int byScore = Double.compare(other.score, score);
        return byScore != 0 ? byScore : documentId.compareTo(other.documentId);
    }

    @Override
    public String toString() {
        return String.format("%s (%.4f)", documentId, score);
    }
}
