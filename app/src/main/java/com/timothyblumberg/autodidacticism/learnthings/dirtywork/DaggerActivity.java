package com.timothyblumberg.autodidacticism.learnthings.dirtywork;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.squareup.otto.Bus;
import com.timothyblumberg.autodidacticism.learnthings.App;

import javax.inject.Inject;


/**
 * Base activity that injects the concrete class in the Dagger module graph. Subclasses
 * must add the class in the module graph manually.
 * <p>
 * Features: - Registers to the Otto EventBus
 * </p>
 */
public abstract class DaggerActivity extends FragmentActivity {

    @Inject
    Bus mBus;

    /* Activity Methods */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.getInstance().inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mBus.register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBus.unregister(this);
    }

}
