package services.sources;

import services.dataAccess.proto.PostProto.Post;
import twitter4j.Trend;
import twitter4j.Trends;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Class representing a source of data
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public interface Source {

    /**
     * returns the name of the source
     * @return
     */
	public String getSourceName();

	/**
	 * gets the time that the data collector should wait between queries. Implementers should
     * operate at 80% of the maximum allowed rate.
	 * @return
	 */
	public long getQueryDelta();

    /**
     * Gets the trends to use for a certain location, if available.
     * @param country
     * @param city
     * @return an {@link Optional} containing the trends, or an empty {@link Optional} if
     * trends are not available for that country.
     */
    public Collection<? extends String> getTrends(String country, String city);

}
