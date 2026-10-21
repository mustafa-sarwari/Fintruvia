package searchengine;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.file.*;
import static org.junit.jupiter.api.Assertions.*;
class SearchFeaturesTest {
    @TempDir Path directory;
    @Test void booleanPhraseAndPrefixQueries() throws Exception {
        Files.writeString(directory.resolve("a.txt"), "alpha beta gamma");
        Files.writeString(directory.resolve("b.txt"), "beta alpha delta");
        SearchEngine engine = new SearchEngine(directory, MapType.AVL);
        assertEquals(2, engine.searchAny("alpha delta").size());
        assertEquals(1, engine.searchPhrase("alpha beta").size());
        assertEquals("a.txt", engine.searchPhrase("alpha beta").get(0).getDocumentId());
        assertEquals(2, engine.searchPrefix("alp").size());
    }
    @Test void cannotReadUnindexedOrEscapedFiles() throws Exception {
        Files.writeString(directory.resolve("a.txt"), "safe");
        SearchEngine engine = new SearchEngine(directory, MapType.AVL);
        assertThrows(IllegalArgumentException.class, () -> engine.readDocument("../private.txt"));
        assertThrows(IllegalArgumentException.class, () -> engine.readDocument("missing.txt"));
    }
}
