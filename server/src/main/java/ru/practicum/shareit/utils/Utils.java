package ru.practicum.shareit.utils;

import java.util.Collections;
import java.util.Map;

public class Utils {
    public static long getNextId(Map<Long, ?> collection) {
        if (collection.isEmpty()) {
            return 1;
        }
        return Collections.max(collection.keySet()) + 1;
    }
}