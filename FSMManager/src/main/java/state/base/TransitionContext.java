package state.base;

import com.google.common.util.concurrent.FutureCallback;

/**
 * @class public class MediaTransitionContext
 * @brief MediaTransitionContext class
 */
public class TransitionContext {

    private FutureCallback<Object> callback;

    ////////////////////////////////////////////////////////////////////////////////

    public TransitionContext() {
        super();
    }

    ////////////////////////////////////////////////////////////////////////////////

    public FutureCallback<Object> getCallback() {
        return callback;
    }

    public TransitionContext setCallback(FutureCallback<Object> callback) {
        this.callback = callback;
        return this;
    }

}
