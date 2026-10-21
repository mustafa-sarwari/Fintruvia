package searchengine;

import java.util.List;

public class NavigationHistory {

    private static final int DEFAULT_RECENT_CAPACITY = 10;

    private final ArrayStack<String> backStack = new ArrayStack<>();
    private final ArrayStack<String> forwardStack = new ArrayStack<>();
    private final ListQueue<String> recentlyViewed = new ListQueue<>();
    private final int recentCapacity;
    private String current;

    public NavigationHistory() {
        this(DEFAULT_RECENT_CAPACITY);
    }

    public NavigationHistory(int recentCapacity) {
        if (recentCapacity < 1) {
            throw new IllegalArgumentException("Capacity must be positive");
        }
        this.recentCapacity = recentCapacity;
    }

    public String visit(String documentId) {
        if (documentId == null) {
            throw new IllegalArgumentException("Document id must not be null");
        }
        if (current != null) {
            backStack.push(current);
        }
        current = documentId;
        forwardStack.clear();
        recordView(documentId);
        return current;
    }

    public String back() {
        if (backStack.isEmpty()) {
            return null;
        }
        forwardStack.push(current);
        current = backStack.pop();
        recordView(current);
        return current;
    }

    public String forward() {
        if (forwardStack.isEmpty()) {
            return null;
        }
        backStack.push(current);
        current = forwardStack.pop();
        recordView(current);
        return current;
    }

    public boolean canGoBack() {
        return !backStack.isEmpty();
    }

    public boolean canGoForward() {
        return !forwardStack.isEmpty();
    }

    public String getCurrent() {
        return current;
    }

    public List<String> getBackHistory() {
        return backStack.toList();
    }

    public List<String> getForwardHistory() {
        return forwardStack.toList();
    }

    public List<String> getRecentlyViewed() {
        return recentlyViewed.toList();
    }

    private void recordView(String documentId) {
        recentlyViewed.enqueue(documentId);
        while (recentlyViewed.size() > recentCapacity) {
            recentlyViewed.dequeue();
        }
    }
}
