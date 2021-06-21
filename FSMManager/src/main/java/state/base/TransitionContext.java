package state.base;

import com.google.common.util.concurrent.FutureCallback;

/**
 * @class public class MediaTransitionContext
 * @brief MediaTransitionContext class
 */
public class TransitionContext {

    private FutureCallback<Void> callback;

    ////////////////////////////////////////////////////////////////////////////////

    public TransitionContext() {
        super();
    }

    ////////////////////////////////////////////////////////////////////////////////

    public FutureCallback<Void> getCallback() {
        return callback;
    }

    public TransitionContext setCallback(FutureCallback<Void> callback) {
        this.callback = callback;
        return this;
    }

}
