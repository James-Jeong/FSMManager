package state.basic.event;

/**
 * @interface  public interface EventCallBack
 * @brief EventCallBack interface
 */
public interface StateEventCallBack {

    String onEvent (String handlerName, String event, String fromState, String failState);

}
