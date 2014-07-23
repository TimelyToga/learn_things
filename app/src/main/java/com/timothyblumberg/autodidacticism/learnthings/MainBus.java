package com.timothyblumberg.autodidacticism.learnthings;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

/**
 * A subclass of Otto bus to ensure that all events posted are through the main
 * thread.
 *
 * Created by chris on 1/5/14.
 */
public class MainBus extends Bus {

    private final Handler mMainThreadHandler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mMainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    post(event);
                }
            });
        }
    }

}