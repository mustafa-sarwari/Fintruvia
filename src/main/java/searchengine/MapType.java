package searchengine;

import java.util.Locale;

public enum MapType {
    BST,
    AVL,
    RED_BLACK,
    HASH;

    public <K extends Comparable<? super K>, V> MapADT<K, V> create() {
        switch (this) {
            case BST:
                return new BSTMap<>();
            case AVL:
                return new AVLMap<>();
            case RED_BLACK:
                return new RedBlackMap<>();
            default:
                return new HashMap<>();
        }
    }

    public static MapType fromString(String name) {
        String normalized = name.trim().toUpperCase(Locale.ROOT).replace('-', '_');
        if (normalized.equals("RB") || normalized.equals("REDBLACK")) {
            return RED_BLACK;
        }
        return MapType.valueOf(normalized);
    }
}
