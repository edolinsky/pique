package services.sources;

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
}
