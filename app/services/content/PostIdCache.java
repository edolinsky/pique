package services.content;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a cache of some type of key to a list of IDs associated with that key.
 * This cache is insert-ordered, and so when the cache exceeds its limit the least recently
 * inserted entry is removed, rather than the least recently accessed.
 * @param <T>
 */
public class PostIdCache<T> extends LinkedHashMap<T, Long> {

    private Integer maxSize;

    public PostIdCache(int maxSize) {
        this.maxSize = maxSize;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<T, Long> eldest) {
        return size() > maxSize;
    }
}
