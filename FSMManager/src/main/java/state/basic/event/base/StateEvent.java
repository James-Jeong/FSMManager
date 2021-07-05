package state.basic.event.base;


public class StateEvent {

    private final String fromState;
    private final String toState;
    private final String failEvent;
    private final int delay;
    private final CallBack callBack;
    private final Object[] params;

    public StateEvent(String fromState, String toState, CallBack callBack, String failEvent, int delay, Object... params) {
        this.fromState = fromState;
        this.toState = toState;
        this.callBack = callBack;
        this.failEvent = failEvent;

        if (delay < 0) { delay = 0; }
        this.delay = delay;

        this.params = params;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public String getFailEvent() {
        return failEvent;
    }

    public int getDelay() {
        return delay;
    }

    public Object[] getParams() {
        return params;
    }

}
