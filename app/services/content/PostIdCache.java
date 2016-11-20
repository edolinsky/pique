package services.content;

import services.PublicConstants;

import java.util.LinkedHashMap;
import java.util.Map;

public class PostIdCache extends LinkedHashMap<String, Long> {

    @Override
    protected boolean removeEldestEntry(Map.Entry<String, Long> eldest) {
        return size() > PublicConstants.MAX_TRACKED_TRENDS;
    }
}
