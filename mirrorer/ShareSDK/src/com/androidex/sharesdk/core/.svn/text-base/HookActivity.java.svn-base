package com.androidex.sharesdk.core;

import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;

public class HookActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityCreated(this);
        }
    }

    @Override
    protected void onStart() {
        try {
            super.onStart();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityStarted(this);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityStopped(this);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (LifeCycleListener listener : mListeners) {
            listener.onActivityDestroyed(this);
        }
    }

    public void addLifeCycleListener(LifeCycleListener listener) {

        if (mListeners.contains(listener))
            return;
        mListeners.add(listener);
    }

    public void removeLifeCycleListener(LifeCycleListener listener) {

        mListeners.remove(listener);
    }

    private final ArrayList<LifeCycleListener> mListeners = new ArrayList<LifeCycleListener>();

    public static interface LifeCycleListener {

        public void onActivityCreated(HookActivity activity);

        public void onActivityDestroyed(HookActivity activity);

        public void onActivityPaused(HookActivity activity);

        public void onActivityResumed(HookActivity activity);

        public void onActivityStarted(HookActivity activity);

        public void onActivityStopped(HookActivity activity);
    }

    public static class LifeCycleAdapter implements LifeCycleListener {

        public void onActivityCreated(HookActivity activity) {

        }

        public void onActivityDestroyed(HookActivity activity) {

        }

        public void onActivityPaused(HookActivity activity) {

        }

        public void onActivityResumed(HookActivity activity) {

        }

        public void onActivityStarted(HookActivity activity) {

        }

        public void onActivityStopped(HookActivity activity) {

        }
    }
}
