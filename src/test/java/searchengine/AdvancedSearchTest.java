package searchengine;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

class AdvancedSearchTest {
    @TempDir Path directory;

    private SearchEngine fixture() throws IOException {
        Files.writeString(directory.resolve("first.txt"), "Blue green blue trees search indexing.", StandardCharsets.UTF_8);
        Files.writeString(directory.resolve("second.txt"), "Green blue maps and indexes.", StandardCharsets.UTF_8);
        Files.writeString(directory.resolve("third.txt"), "Yellow mountain landscape.", StandardCharsets.UTF_8);
        return new SearchEngine(directory, MapType.AVL);
    }

    @Test void orMatchesEitherTerm() throws IOException {
        SearchEngine engine = fixture();
        assertEquals(2, engine.searchAny("trees mountain").size());
    }

    @Test void contiguousPhraseRespectsOrder() throws IOException {
        SearchEngine engine = fixture();
        assertEquals(List.of("first.txt"), engine.searchPhrase("blue green").stream().map(SearchResult::getDocumentId).toList());
        assertEquals(2, engine.searchPhrase("green blue").size());
    }

    @Test void prefixMatchesMultipleTerms() throws IOException {
        SearchEngine engine = fixture();
        assertEquals(List.of("first.txt", "second.txt"),
            engine.searchPrefix("ind").stream()
                .map(SearchResult::getDocumentId)
                .sorted()
                .toList());
    }

    @Test void traversalIsRejected() throws IOException {
        SearchEngine engine = fixture();
        assertThrows(IllegalArgumentException.class, () -> engine.readDocument("../elsewhere.txt"));
        assertThrows(IllegalArgumentException.class, () -> engine.open("../elsewhere.txt"));
    }

    @Test void emptyAndUnknownQueriesBehaveSafely() throws IOException {
        SearchEngine engine = fixture();
        assertTrue(engine.search("").isEmpty());
        assertTrue(engine.searchAny("unknowntoken").isEmpty());
        assertTrue(engine.searchPrefix("unknowntoken").isEmpty());
        assertTrue(engine.searchPhrase("unknowntoken").isEmpty());
    }
}
