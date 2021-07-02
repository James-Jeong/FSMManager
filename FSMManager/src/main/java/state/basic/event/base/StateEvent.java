package state.basic.event.base;


public class StateEvent {

    private final String fromState;
    private final String toState;
    private final String failEvent;
    private final int delay;

    public StateEvent(String fromState, String toState, String failEvent, int delay) {
        this.fromState = fromState;
        this.toState = toState;
        this.failEvent = failEvent;

        if (delay < 0) { delay = 0; }
        this.delay = delay;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }

    public String getFailEvent() {
        return failEvent;
    }

    public int getDelay() {
        return delay;
    }

}
