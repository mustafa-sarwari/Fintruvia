package searchengine;

public class Posting {

    private final String documentId;
    private int frequency;

    public Posting(String documentId) {
        this.documentId = documentId;
        this.frequency = 1;
    }

    public String getDocumentId() {
        return documentId;
    }

    public int getFrequency() {
        return frequency;
    }

    void increment() {
        frequency++;
    }

    @Override
    public String toString() {
        return documentId + "=" + frequency;
    }
}
