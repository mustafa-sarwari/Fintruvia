package searchengine;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import org.junit.jupiter.api.Test;

class NavigationHistoryTest {

    @Test
    void scenarioOneBackAndForward() {
        NavigationHistory history = new NavigationHistory();
        history.visit("doc1.txt");
        assertEquals("doc1.txt", history.getCurrent());

        history.visit("doc3.txt");
        assertEquals(List.of("doc1.txt"), history.getBackHistory());
        assertEquals(List.of(), history.getForwardHistory());
        assertEquals("doc3.txt", history.getCurrent());

        assertEquals("doc1.txt", history.back());
        assertEquals(List.of(), history.getBackHistory());
        assertEquals(List.of("doc3.txt"), history.getForwardHistory());
        assertEquals("doc1.txt", history.getCurrent());

        assertEquals("doc3.txt", history.forward());
        assertEquals(List.of("doc1.txt"), history.getBackHistory());
        assertEquals(List.of(), history.getForwardHistory());
        assertEquals("doc3.txt", history.getCurrent());

        assertNull(history.forward());
        assertEquals("doc3.txt", history.getCurrent());
        assertEquals(List.of("doc1.txt"), history.getBackHistory());
    }

    @Test
    void scenarioTwoNewViewClearsForwardHistory() {
        NavigationHistory history = new NavigationHistory();
        history.visit("doc1.txt");
        history.visit("doc2.txt");
        history.visit("doc4.txt");
        assertEquals(List.of("doc1.txt", "doc2.txt"), history.getBackHistory());
        assertEquals("doc4.txt", history.getCurrent());

        assertEquals("doc2.txt", history.back());
        assertEquals(List.of("doc1.txt"), history.getBackHistory());
        assertEquals(List.of("doc4.txt"), history.getForwardHistory());

        history.visit("doc3.txt");
        assertEquals("doc3.txt", history.getCurrent());
        assertEquals(List.of("doc1.txt", "doc2.txt"), history.getBackHistory());
        assertEquals(List.of(), history.getForwardHistory());

        assertNull(history.forward());
        assertEquals("doc3.txt", history.getCurrent());
    }

    @Test
    void backWithNoHistoryDoesNothing() {
        NavigationHistory history = new NavigationHistory();
        assertNull(history.back());
        assertNull(history.getCurrent());
        history.visit("doc1.txt");
        assertNull(history.back());
        assertEquals("doc1.txt", history.getCurrent());
        assertFalse(history.canGoBack());
        assertFalse(history.canGoForward());
    }

    @Test
    void canGoFlagsTrackStacks() {
        NavigationHistory history = new NavigationHistory();
        history.visit("a");
        history.visit("b");
        assertTrue(history.canGoBack());
        history.back();
        assertTrue(history.canGoForward());
    }

    @Test
    void recentlyViewedQueueKeepsLatestViewsInOrder() {
        NavigationHistory history = new NavigationHistory(3);
        history.visit("a");
        history.visit("b");
        history.visit("c");
        history.back();
        assertEquals(List.of("b", "c", "b"), history.getRecentlyViewed());
    }

    @Test
    void nullDocumentIsRejected() {
        NavigationHistory history = new NavigationHistory();
        assertThrows(IllegalArgumentException.class, () -> history.visit(null));
    }
}
