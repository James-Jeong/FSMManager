package state.basic.event;

/**
 * @interface  public interface EventCallBack
 * @brief EventCallBack interface
 */
public interface StateEventCallBack {

    void onEvent (String handlerName, String event, String fromState);

}
