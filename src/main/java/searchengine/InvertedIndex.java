package searchengine;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class InvertedIndex {

    private final MapType mapType;
    private final MapADT<String, PostingList> index;
    private final HashMap<String, Integer> documentLengths = new HashMap<>();
    private final List<String> documentIds = new ArrayList<>();

    public InvertedIndex() {
        this(MapType.AVL);
    }

    public InvertedIndex(MapType mapType) {
        this.mapType = mapType;
        this.index = mapType.create();
    }

    public int indexDirectory(Path directory) throws IOException {
        if (!Files.isDirectory(directory)) {
            throw new IOException("Not a directory: " + directory);
        }
        String[] fileNames;
        try (Stream<Path> files = Files.list(directory)) {
            fileNames = files
                    .filter(Files::isRegularFile)
                    .map(path -> path.getFileName().toString())
                    .filter(name -> name.toLowerCase().endsWith(".txt"))
                    .toArray(String[]::new);
        }
        Sorting.mergeSort(fileNames);
        for (String fileName : fileNames) {
            Path document = directory.resolve(fileName);
            if (!document.toRealPath().startsWith(directory.toRealPath())) continue;
            addDocument(fileName, Tokenizer.tokenize(document));
        }
        return fileNames.length;
    }

    public void addDocument(String documentId, LinkedList<String> tokens) {
        if (documentId == null || tokens == null) {
            throw new IllegalArgumentException("Document id and tokens must not be null");
        }
        if (documentLengths.containsKey(documentId)) {
            throw new IllegalArgumentException("Document already indexed: " + documentId);
        }
        for (String token : tokens) {
            PostingList postings = index.search(token);
            if (postings == null) {
                postings = new PostingList();
                index.insert(token, postings);
            }
            postings.addOccurrence(documentId);
        }
        documentLengths.insert(documentId, tokens.size());
        documentIds.add(documentId);
    }

    public PostingList getPostings(String term) {
        String normalized = Tokenizer.normalizeTerm(term);
        return normalized == null ? null : index.search(normalized);
    }

    public List<String> getDocuments(String term) {
        PostingList postings = getPostings(term);
        return postings == null ? new ArrayList<>() : postings.getDocumentIds();
    }

    public int getFrequency(String term, String documentId) {
        PostingList postings = getPostings(term);
        return postings == null ? 0 : postings.getFrequency(documentId);
    }

    public boolean containsTerm(String term) {
        return getPostings(term) != null;
    }

    public boolean containsDocument(String documentId) {
        return documentLengths.containsKey(documentId);
    }

    public int getDocumentLength(String documentId) {
        Integer length = documentLengths.search(documentId);
        return length == null ? 0 : length;
    }

    public int documentCount() {
        return documentIds.size();
    }

    public int vocabularySize() {
        return index.size();
    }

    public List<String> getDocumentIds() {
        return new ArrayList<>(documentIds);
    }

    public List<String> getTerms() {
        return index.keys();
    }

    public MapType getMapType() {
        return mapType;
    }
}
