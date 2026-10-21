package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;

class QueryProcessorTest {

    private static QueryProcessor processorFor(MapType type) throws IOException {
        InvertedIndex index = new InvertedIndex(type);
        index.indexDirectory(Paths.get("documents"));
        return new QueryProcessor(index);
    }

    @Test
    void specQueriesReturnExpectedDocumentsForEveryMapType() throws IOException {
        for (MapType type : MapType.values()) {
            QueryProcessor processor = processorFor(type);
            assertEquals(List.of("doc1.txt", "doc2.txt"), processor.query("fox"));
            assertEquals(List.of("doc3.txt"), processor.query("C++"));
            assertEquals(List.of("doc4.txt"), processor.query("world"));
            assertEquals(List.of("doc1.txt", "doc2.txt"), processor.query("the"));
            assertEquals(List.of("doc1.txt", "doc2.txt"), processor.query("quick fox"));
            assertEquals(List.of("doc2.txt"), processor.query("lazy dog"));
            assertEquals(List.of("doc3.txt"), processor.query("fast program"));
            assertEquals(List.of(), processor.query("hello fox"));
            assertEquals(List.of(), processor.query("zzz"));
        }
    }

    @Test
    void emptyAndPunctuationOnlyQueriesReturnNothing() throws IOException {
        QueryProcessor processor = processorFor(MapType.AVL);
        assertTrue(processor.query("").isEmpty());
        assertTrue(processor.query("   ?!  ").isEmpty());
    }

    @Test
    void queryPunctuationAndDuplicateTermsAreIgnored() throws IOException {
        QueryProcessor processor = processorFor(MapType.AVL);
        assertEquals(List.of("doc2.txt"), processor.query("Lazy, lazy DOG!"));
        assertEquals(List.of("lazy", "dog"), processor.parseTerms("Lazy, lazy DOG!"));
    }

    @Test
    void rankedQueryPutsHigherTermFrequencyFirst() throws IOException {
        QueryProcessor processor = processorFor(MapType.HASH);
        List<SearchResult> results = processor.rankedQuery("the");
        assertEquals(2, results.size());
        assertEquals("doc2.txt", results.get(0).getDocumentId());
        assertEquals("doc1.txt", results.get(1).getDocumentId());
        assertTrue(results.get(0).getScore() > results.get(1).getScore());
    }

    @Test
    void rankedQueryBreaksTiesByDocumentName() throws IOException {
        QueryProcessor processor = processorFor(MapType.BST);
        List<SearchResult> results = processor.rankedQuery("quick fox");
        assertEquals("doc1.txt", results.get(0).getDocumentId());
        assertEquals("doc2.txt", results.get(1).getDocumentId());
        assertEquals(results.get(0).getScore(), results.get(1).getScore(), 1e-9);
    }

    @Test
    void rankedQueryMissReturnsEmptyList() throws IOException {
        assertTrue(processorFor(MapType.RED_BLACK).rankedQuery("hello fox").isEmpty());
    }
}
