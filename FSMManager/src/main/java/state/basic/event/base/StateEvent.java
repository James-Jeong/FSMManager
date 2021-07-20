package state.basic.event.base;

import java.util.Arrays;

/**
 * @class public class StateEvent
 * @brief StateEvent class
 * FSM 시나리오의 가장 기본 단위 클래스
 */
public class StateEvent {

    // Event name
    private final String name;
    // From state
    private final String fromState;
    // To state
    private final String toState;
    // Success CallBack
    private final CallBack successCallBack;

    // Fail CallBack
    private final CallBack failCallBack;
    // Event Retry Count
    private final int eventRetryCount;

    // Next event
    private final String nextEvent;
    // Interval time for triggering the next event
    private final int nextEventInterval;
    // Next Event Retry Count
    private final int nextEventRetryCount;
    // Parameters for the callback
    private final Object[] nextEventCallBackParams;

    ////////////////////////////////////////////////////////////////////////////////

    public StateEvent(String name,
                      String fromState,
                      String toState,
                      CallBack successCallBack,
                      CallBack failCallBack,
                      int eventRetryCount,
                      String nextEvent,
                      int delay,
                      int nextEventRetryCount,
                      Object... nextEventCallBackParams) {
        this.name = name;
        this.fromState = fromState;
        this.toState = toState;
        this.successCallBack = successCallBack;
        this.failCallBack = failCallBack;
        this.eventRetryCount = Math.max(eventRetryCount, 0);
        this.nextEvent = nextEvent;
        this.nextEventInterval = Math.max(delay, 0);
        this.nextEventRetryCount = Math.max(nextEventRetryCount, 0);
        this.nextEventCallBackParams = nextEventCallBackParams;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public String getFromState() {
        return fromState;
    }

    public String getToState() {
        return toState;
    }

    public CallBack getSuccessCallBack() {
        return successCallBack;
    }

    public CallBack getFailCallBack() {
        return failCallBack;
    }

    public int getEventRetryCount() {
        return eventRetryCount;
    }

    public String getNextEvent() {
        return nextEvent;
    }

    public int getNextEventInterval() {
        return nextEventInterval;
    }

    public int getNextEventRetryCount() {
        return nextEventRetryCount;
    }

    public Object[] getNextEventCallBackParams() {
        return nextEventCallBackParams;
    }

    @Override
    public String toString() {
        return "StateEvent{" +
                "name='" + name + '\'' +
                ", fromState='" + fromState + '\'' +
                ", toState='" + toState + '\'' +
                ", nextEvent='" + nextEvent + '\'' +
                ", nextEventInterval=" + nextEventInterval +
                ", successCallBack=" + successCallBack +
                ", failCallBack=" + failCallBack +
                ", nextEventCallBackParams=" + Arrays.toString(nextEventCallBackParams) +
                '}';
    }
}
