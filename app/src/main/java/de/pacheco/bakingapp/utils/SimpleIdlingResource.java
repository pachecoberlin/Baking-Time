package de.pacheco.bakingapp.utils;

import androidx.annotation.Nullable;
import androidx.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleIdlingResource implements IdlingResource {
    AtomicBoolean isIdle = new AtomicBoolean(true);
    @Nullable
    private volatile ResourceCallback callback;

    @Override
    public String getName() {
        return getClass().getName();
    }

    @Override
    public boolean isIdleNow() {
        return isIdle.get();
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.callback = callback;
    }

    public void setIdleState(boolean isIdleNow) {
        isIdle.set(isIdleNow);
        if (callback != null && isIdleNow) {
            callback.onTransitionToIdle();
        }
    }
}
