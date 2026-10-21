package searchengine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class SearchEngine {

    private final Path documentDirectory;
    private final InvertedIndex index;
    private final QueryProcessor queryProcessor;
    private final NavigationHistory history = new NavigationHistory();
    private List<SearchResult> lastResults = new ArrayList<>();

    public SearchEngine(Path documentDirectory, MapType mapType) throws IOException {
        this.documentDirectory = documentDirectory.toRealPath();
        this.index = new InvertedIndex(mapType);
        this.index.indexDirectory(this.documentDirectory);
        this.queryProcessor = new QueryProcessor(index);
    }

    public List<SearchResult> search(String queryText) {
        lastResults = queryProcessor.rankedQuery(queryText);
        return lastResults;
    }

    public List<SearchResult> searchAny(String terms) {
        lastResults = queryProcessor.orQuery(terms);
        return lastResults;
    }

    public List<SearchResult> searchPrefix(String prefix) {
        lastResults = queryProcessor.prefixQuery(prefix, 100);
        return lastResults;
    }

    public List<SearchResult> searchPhrase(String phrase) throws IOException {
        List<String> words = new ArrayList<>();
        for (String word : Tokenizer.tokenizeText(phrase)) words.add(word);
        List<SearchResult> results = new ArrayList<>();
        if (words.isEmpty()) return results;
        for (SearchResult candidate : queryProcessor.rankedQuery(phrase)) {
            List<String> documentWords = new ArrayList<>();
            for (String word : Tokenizer.tokenizeText(readDocument(candidate.getDocumentId()))) {
                documentWords.add(word);
            }
            boolean found = false;
            for (int i = 0; i + words.size() <= documentWords.size(); i++) {
                boolean matches = true;
                for (int j = 0; j < words.size(); j++) {
                    if (!words.get(j).equals(documentWords.get(i + j))) {
                        matches = false;
                        break;
                    }
                }
                if (matches) { found = true; break; }
            }
            if (found) results.add(candidate);
        }
        lastResults = results;
        return results;
    }

    public List<String> searchDocuments(String queryText) {
        return queryProcessor.query(queryText);
    }

    public String open(String documentId) throws IOException {
        if (!index.containsDocument(documentId)) {
            throw new IllegalArgumentException("Unknown document: " + documentId);
        }
        history.visit(documentId);
        return readDocument(documentId);
    }

    public String openResult(int resultNumber) throws IOException {
        if (resultNumber < 1 || resultNumber > lastResults.size()) {
            throw new IllegalArgumentException("No result #" + resultNumber);
        }
        return open(lastResults.get(resultNumber - 1).getDocumentId());
    }

    public String back() {
        return history.back();
    }

    public String forward() {
        return history.forward();
    }

    public String readDocument(String documentId) throws IOException {
        if (documentId == null || !index.containsDocument(documentId)) {
            throw new IllegalArgumentException("Unknown document: " + documentId);
        }
        Path resolved = documentDirectory.resolve(documentId).toRealPath();
        if (!resolved.startsWith(documentDirectory) || !Files.isRegularFile(resolved)) {
            throw new IllegalArgumentException("Document path is outside indexed directory");
        }
        return Files.readString(resolved, StandardCharsets.UTF_8);
    }

    public InvertedIndex getIndex() {
        return index;
    }

    public NavigationHistory getHistory() {
        return history;
    }

    public static void main(String[] args) throws IOException {
        Path directory = Paths.get(args.length > 0 ? args[0] : "documents");
        MapType mapType = args.length > 1 ? MapType.fromString(args[1]) : MapType.AVL;
        SearchEngine engine = new SearchEngine(directory, mapType);
        System.out.printf("Indexed %d documents, %d unique terms using %s map.%n",
                engine.index.documentCount(), engine.index.vocabularySize(), mapType);
        printHelp();
        Scanner scanner = new Scanner(System.in, StandardCharsets.UTF_8);
        while (true) {
            System.out.print("> ");
            if (!scanner.hasNextLine()) {
                break;
            }
            String line = scanner.nextLine().trim();
            if (line.isEmpty()) {
                continue;
            }
            String[] parts = line.split("\\s+", 2);
            String command = parts[0].toLowerCase();
            String argument = parts.length > 1 ? parts[1] : "";
            try {
                switch (command) {
                    case "search":
                        printResults(engine.search(argument));
                        break;
                    case "or":
                        printResults(engine.searchAny(argument));
                        break;
                    case "prefix":
                        printResults(engine.searchPrefix(argument));
                        break;
                    case "phrase":
                        printResults(engine.searchPhrase(argument));
                        break;
                    case "open":
                        String content = argument.matches("\\d+")
                                ? engine.openResult(Integer.parseInt(argument))
                                : engine.open(argument);
                        printDocument(engine.history.getCurrent(), content);
                        break;
                    case "back":
                        printNavigation(engine, engine.back());
                        break;
                    case "forward":
                        printNavigation(engine, engine.forward());
                        break;
                    case "history":
                        System.out.println("Back:    " + engine.history.getBackHistory());
                        System.out.println("Current: " + engine.history.getCurrent());
                        System.out.println("Forward: " + engine.history.getForwardHistory());
                        System.out.println("Recent:  " + engine.history.getRecentlyViewed());
                        break;
                    case "help":
                        printHelp();
                        break;
                    case "quit":
                    case "exit":
                        return;
                    default:
                        printResults(engine.search(line));
                }
            } catch (IllegalArgumentException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private static void printHelp() {
        System.out.println("Commands: search <terms> | or <terms> | prefix <term> | phrase <terms> | open <result # or file> | back | forward | history | help | quit");
    }

    private static void printResults(List<SearchResult> results) {
        if (results.isEmpty()) {
            System.out.println("No documents found.");
            return;
        }
        for (int i = 0; i < results.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, results.get(i));
        }
    }

    private static void printNavigation(SearchEngine engine, String documentId) throws IOException {
        if (documentId == null) {
            System.out.println("Nothing to navigate to.");
        } else {
            printDocument(documentId, engine.readDocument(documentId));
        }
    }

    private static void printDocument(String documentId, String content) {
        System.out.println("---- " + documentId + " ----");
        System.out.println(content.isEmpty() ? "(empty document)" : content.trim());
    }
}
