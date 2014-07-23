package com.timothyblumberg.autodidacticism.learnthings;

import dagger.Module;

/**
 * Dagger object-module
 */
@Module(includes = {
        DataModule.class
})
public final class AppModule {

    private App mApp;

    public AppModule(App app) {
        mApp = app;
    }

//    @Provides
//    @Singleton
//    App provideApp() {
//        return mApp;
//    }

//    @Provides @Singleton Bus provideBus() {
//        return new MainBus();
//    }

}