package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.jupiter.api.Test;

class TokenizerTest {

    private static final Path DOCUMENTS = Paths.get("documents");

    @Test
    void tokenizesDoc1() throws IOException {
        assertEquals(List.of("the", "quick", "brown", "fox"),
                Tokenizer.tokenize(DOCUMENTS.resolve("doc1.txt")).toList());
    }

    @Test
    void tokenizesDoc2() throws IOException {
        assertEquals(List.of("the", "quick", "dog", "and", "the", "lazy", "fox"),
                Tokenizer.tokenize(DOCUMENTS.resolve("doc2.txt")).toList());
    }

    @Test
    void tokenizesDoc3KeepingCPlusPlus() throws IOException {
        assertEquals(List.of("a", "fast", "c++", "program", "is", "a", "good", "c++", "program"),
                Tokenizer.tokenize(DOCUMENTS.resolve("doc3.txt")).toList());
    }

    @Test
    void tokenizesDoc4StrippingPunctuation() throws IOException {
        assertEquals(List.of("hello", "world", "hello", "world"),
                Tokenizer.tokenize(DOCUMENTS.resolve("doc4.txt")).toList());
    }

    @Test
    void emptyFileProducesEmptyList() throws IOException {
        assertTrue(Tokenizer.tokenize(DOCUMENTS.resolve("empty.txt")).isEmpty());
    }

    @Test
    void handlesMixedCaseDigitsAndWhitespace() {
        assertEquals(List.of("java", "21", "is", "here"),
                Tokenizer.tokenizeText("  JAVA\t21 -- is\nHERE!! ").toList());
    }

    @Test
    void ignoresStandalonePlusSigns() {
        assertEquals(List.of("1", "1"), Tokenizer.tokenizeText("1 + 1").toList());
    }

    @Test
    void nullOrBlankTextProducesEmptyList() {
        assertTrue(Tokenizer.tokenizeText(null).isEmpty());
        assertTrue(Tokenizer.tokenizeText("   ...  ").isEmpty());
    }

    @Test
    void normalizeTerm() {
        assertEquals("c++", Tokenizer.normalizeTerm("C++"));
        assertEquals("fox", Tokenizer.normalizeTerm("Fox!"));
        assertNull(Tokenizer.normalizeTerm("?!"));
    }
}
