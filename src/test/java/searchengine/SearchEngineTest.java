package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;

class SearchEngineTest {

    private static SearchEngine engine() throws IOException {
        return new SearchEngine(Paths.get("documents"), MapType.AVL);
    }

    @Test
    void searchThenOpenResultUpdatesHistory() throws IOException {
        SearchEngine engine = engine();
        List<SearchResult> results = engine.search("lazy dog");
        assertEquals(1, results.size());
        assertEquals("The quick dog, and the lazy fox.", engine.openResult(1).trim());
        assertEquals("doc2.txt", engine.getHistory().getCurrent());
    }

    @Test
    void openingDocumentsSupportsBackAndForward() throws IOException {
        SearchEngine engine = engine();
        engine.open("doc1.txt");
        engine.open("doc3.txt");
        assertEquals("doc1.txt", engine.back());
        assertEquals("doc3.txt", engine.forward());
        assertNull(engine.forward());
    }

    @Test
    void openingUnknownDocumentThrows() throws IOException {
        SearchEngine engine = engine();
        assertThrows(IllegalArgumentException.class, () -> engine.open("missing.txt"));
        assertThrows(IllegalArgumentException.class, () -> engine.openResult(1));
    }

    @Test
    void searchDocumentsMatchesSpec() throws IOException {
        assertEquals(List.of("doc1.txt", "doc2.txt"), engine().searchDocuments("quick fox"));
    }
}
