package com.timothyblumberg.autodidacticism.learnthings.dirtywork;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.internal.bind.DateTypeAdapter;
import com.timothyblumberg.autodidacticism.learnthings.App;
import com.timothyblumberg.autodidacticism.learnthings.MCActivity;

import java.util.Date;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Module for data-related objects.
 */
@Module(
        injects = {
                MCActivity.class,
                App.class,
        },
        staticInjections = {

        },
        complete = false,
        library = true)
public class DataModule {

    @Provides @Singleton Gson provideGson() {
        return new GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .registerTypeAdapter(Date.class, new DateTypeAdapter())
                .registerTypeAdapter(byte[].class, new ByteArrayDeserializer())
                .create();
    }

}