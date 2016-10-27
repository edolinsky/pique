package services.content;

import services.dataAccess.AbstractDataAccess;
import services.sources.AbstractJavaSource;

/**
 * This class interacts with an {@link AbstractJavaSource} to collect data through a Java
 * library and an {@link AbstractDataAccess} to place the results
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class JavaDataCollector extends AbstractDataCollector {

	AbstractJavaSource source;

	public JavaDataCollector(AbstractDataAccess dataAccess, AbstractJavaSource source) {
		super(dataAccess);
		this.source = source;
	}

	@Override
	public Post fetch() {
		/**
		 * Unlike the RestfulDataCollector, JavaDataCollectors will have very little common
		 * behaviour and instead depend heavily on their library functions, so we offload the
		 * work to the source object.
		 */

		return new Post();
	}
}
