package state.basic.event.base;

public class StateEvent {

    // From state
    private final String fromState;
    // To state
    private final String toState;
    // Next event
    private final String nextEvent;
    // Delay for triggering the next event
    private final int delay;
    // CallBack
    private final CallBack callBack;
    // Parameters for the callback
    private final Object[] nextEventCallBackParams;

    ////////////////////////////////////////////////////////////////////////////////

    public StateEvent(String fromState,
                      String toState,
                      CallBack callBack,
                      String nextEvent,
                      int delay,
                      Object... nextEventCallBackParams) {
        this.fromState = fromState;
        this.toState = toState;
        this.callBack = callBack;
        this.nextEvent = nextEvent;
        this.nextEventCallBackParams = nextEventCallBackParams;

        if (delay < 0) { delay = 0; }
        this.delay = delay;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }

    public CallBack getCallBack() {
        return callBack;
    }

    public String getNextEvent() {
        return nextEvent;
    }

    public int getDelay() {
        return delay;
    }

    public Object[] getNextEventCallBackParams() {
        return nextEventCallBackParams;
    }
}
