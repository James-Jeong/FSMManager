package state.basic.event;

import state.basic.module.StateHandler;
import state.basic.unit.StateUnit;

/**
 * @interface  public interface EventCallBack
 * @brief EventCallBack interface
 */
public interface StateEventCallBack {

    String onEvent (StateHandler stateHandler, String event, StateUnit stateUnit, String fromState, String toState, Object... params);

}
