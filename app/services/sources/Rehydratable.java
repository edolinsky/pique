package services.sources;

import services.dataAccess.proto.PostProto.Post;

import java.util.List;

public interface Rehydratable extends Source {

    /**
     * Retrieves an updated post for each of the specified ids
     * @param ids
     * @return
     */
    public List<Post> rehydrate(List<Long> ids);

    /**
     * Returns the number of rehydration queries allowed per window
     * @return
     */
    public Integer numRehydrationQueries();

    /**
     * Returns the length until the allowed rehydrations refreshes, in millis
     * @return
     */
    public Long rehydrationWindowLength();

    /**
     * Returns the number of posts that are able to be rehydrated at once
     * @return
     */
    public Integer maxPostsForRehydrate();

}
