package services.sources;

import services.dataAccess.proto.PostProto;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface JavaSource extends Source {
    /**
     * Gets posts corresponding to the given trend term
     * @param trend the trend to query
     * @param numPosts the number of posts to get (Max is 100 set by API)
     * @param sinceId get all posts newer than this id only, or set as null for all
     * @return
     */
    public List<PostProto.Post> getTrendingPosts(String trend, int numPosts, Long sinceId);

    public List<PostProto.Post> getMaxTrendingPostsSince(String trend, Long sinceId);

    public List<PostProto.Post> getMaxTrendingPosts(String trend);
}
