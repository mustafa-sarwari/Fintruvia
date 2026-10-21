package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;

class InvertedIndexTest {

    private static InvertedIndex buildIndex(MapType type) throws IOException {
        InvertedIndex index = new InvertedIndex(type);
        index.indexDirectory(Paths.get("documents"));
        return index;
    }

    private static void assertExpectedContents(InvertedIndex index) {
        assertEquals(List.of("doc1.txt", "doc2.txt"), index.getDocuments("the"));
        assertEquals(List.of("doc1.txt", "doc2.txt"), index.getDocuments("quick"));
        assertEquals(List.of("doc1.txt", "doc2.txt"), index.getDocuments("fox"));
        assertEquals(List.of("doc2.txt"), index.getDocuments("dog"));
        assertEquals(List.of("doc2.txt"), index.getDocuments("lazy"));
        assertEquals(List.of("doc3.txt"), index.getDocuments("fast"));
        assertEquals(List.of("doc3.txt"), index.getDocuments("c++"));
        assertEquals(List.of("doc3.txt"), index.getDocuments("program"));
        assertEquals(List.of("doc4.txt"), index.getDocuments("hello"));
        assertEquals(List.of("doc4.txt"), index.getDocuments("world"));
        assertFalse(index.containsTerm("zzz"));
        assertTrue(index.getDocuments("zzz").isEmpty());
    }

    @Test
    void indexContainsExpectedPostingsForEveryMapType() throws IOException {
        for (MapType type : MapType.values()) {
            assertExpectedContents(buildIndex(type));
        }
    }

    @Test
    void storesTermFrequencies() throws IOException {
        InvertedIndex index = buildIndex(MapType.AVL);
        assertEquals(1, index.getFrequency("the", "doc1.txt"));
        assertEquals(2, index.getFrequency("the", "doc2.txt"));
        assertEquals(2, index.getFrequency("c++", "doc3.txt"));
        assertEquals(2, index.getFrequency("hello", "doc4.txt"));
        assertEquals(0, index.getFrequency("hello", "doc1.txt"));
        assertEquals(0, index.getFrequency("zzz", "doc1.txt"));
        assertEquals(3, index.getPostings("the").totalFrequency());
    }

    @Test
    void recordsDocumentsAndLengths() throws IOException {
        InvertedIndex index = buildIndex(MapType.HASH);
        assertEquals(List.of("doc1.txt", "doc2.txt", "doc3.txt", "doc4.txt", "empty.txt"), index.getDocumentIds());
        assertEquals(5, index.documentCount());
        assertEquals(4, index.getDocumentLength("doc1.txt"));
        assertEquals(0, index.getDocumentLength("empty.txt"));
        assertTrue(index.containsDocument("empty.txt"));
    }

    @Test
    void vocabularyIsTheSameForEveryMapType() throws IOException {
        int expected = buildIndex(MapType.BST).vocabularySize();
        assertEquals(15, expected);
        for (MapType type : MapType.values()) {
            assertEquals(expected, buildIndex(type).vocabularySize());
        }
    }

    @Test
    void lookupsAreCaseInsensitive() throws IOException {
        InvertedIndex index = buildIndex(MapType.RED_BLACK);
        assertEquals(List.of("doc3.txt"), index.getDocuments("C++"));
        assertEquals(List.of("doc4.txt"), index.getDocuments("HELLO"));
        assertNull(index.getPostings("!!!"));
    }

    @Test
    void addingSameDocumentTwiceIsRejected() {
        InvertedIndex index = new InvertedIndex();
        index.addDocument("a.txt", Tokenizer.tokenizeText("one two"));
        assertThrows(IllegalArgumentException.class,
                () -> index.addDocument("a.txt", Tokenizer.tokenizeText("three")));
    }

    @Test
    void missingDirectoryThrows() {
        InvertedIndex index = new InvertedIndex();
        assertThrows(IOException.class, () -> index.indexDirectory(Paths.get("no-such-directory")));
    }
}
