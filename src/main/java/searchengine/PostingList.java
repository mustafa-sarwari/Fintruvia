package searchengine;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class PostingList implements Iterable<Posting> {

    private final LinkedList<Posting> postings = new LinkedList<>();
    private int totalFrequency;
    private final HashMap<String, Posting> byDocument = new HashMap<>();

    public void addOccurrence(String documentId) {
        Posting last = postings.getLast();
        if (last != null && last.getDocumentId().equals(documentId)) {
            last.increment();
        } else {
            Posting created = new Posting(documentId);
            postings.insertLast(created);
            byDocument.insert(documentId, created);
        }
        totalFrequency++;
    }

    public int getFrequency(String documentId) {
        Posting posting = byDocument.search(documentId);
        return posting == null ? 0 : posting.getFrequency();
    }

    public int documentFrequency() {
        return postings.size();
    }

    public int totalFrequency() {
        return totalFrequency;
    }

    public List<String> getDocumentIds() {
        List<String> ids = new ArrayList<>(postings.size());
        for (Posting posting : postings) {
            ids.add(posting.getDocumentId());
        }
        return ids;
    }

    @Override
    public Iterator<Posting> iterator() {
        return postings.iterator();
    }

    @Override
    public String toString() {
        return postings.toString();
    }
}
