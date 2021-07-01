package state.basic.event;

import state.basic.unit.StateUnit;

/**
 * @interface  public interface EventCallBack
 * @brief EventCallBack interface
 */
public interface StateEventCallBack {

    String onEvent (String handlerName, String event, StateUnit stateUnit, String failState, Object... params);

}
