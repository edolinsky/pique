package services;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class RehydrationRequest {
    private List<Long> idList;

    public RehydrationRequest() {
        idList = new LinkedList<>();
    }
    /**
     * This method populates a list of ids to be rehydrated in order to pass them from one
     * thread to another. Please note that you will still have to call notify on this object to
     * actually make the request
     */
    public void requestRehydrationFor(List<Long> requestIds) {

        synchronized (idList) {
            idList.addAll(requestIds);
        }
    }

    public List<Long> receiveRehydrationRequest(int numIds) {

        List<Long> getIds = new ArrayList<>();
        synchronized (idList) {
            getIds.addAll(idList.subList(0, numIds));
            idList.removeAll(getIds);
        }

        return getIds;

    }

    public Integer size() {
        synchronized (idList) {
            return idList.size();
        }
    }
}
