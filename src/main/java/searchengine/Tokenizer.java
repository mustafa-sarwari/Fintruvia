package searchengine;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Locale;
import java.util.regex.Pattern;

public final class Tokenizer {

    private static final Pattern SEPARATORS = Pattern.compile("[^\\p{L}\\p{N}+]+");

    private Tokenizer() {
    }

    public static LinkedList<String> tokenize(Path file) throws IOException {
        byte[] bytes = Files.readAllBytes(file);
        return tokenizeText(new String(bytes, StandardCharsets.UTF_8));
    }

    public static LinkedList<String> tokenizeText(String text) {
        LinkedList<String> tokens = new LinkedList<>();
        if (text == null || text.isEmpty()) {
            return tokens;
        }
        for (String candidate : SEPARATORS.split(text.toLowerCase(Locale.ROOT))) {
            if (isWord(candidate)) {
                tokens.insertLast(candidate);
            }
        }
        return tokens;
    }

    public static String normalizeTerm(String term) {
        LinkedList<String> tokens = tokenizeText(term);
        return tokens.isEmpty() ? null : tokens.getFirst();
    }

    private static boolean isWord(String candidate) {
        for (int i = 0; i < candidate.length(); i++) {
            if (Character.isLetterOrDigit(candidate.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
