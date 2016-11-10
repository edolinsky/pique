import com.google.inject.AbstractModule;

import controllers.ContentController;
import play.Logger;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;

/**
 * This class is a Guice module that tells Guice how to bind several
 * different types. This Guice module is created when the Play
 * application starts.
 *
 * Play will automatically use any class called `Module` that is in
 * the root package. You can create modules in other locations by
 * adding `play.modules.enabled` settings to the `application.conf`
 * configuration file.
 */
public class Module extends AbstractModule {

    @Override
    public void configure() {
        bind(AbstractDataAccess.class).to(InMemoryAccessObject.class).asEagerSingleton();
        bind(ContentController.class).asEagerSingleton();
        Logger.info("module created");
    }

}
