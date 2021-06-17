package event;

/**
 * @interface  public interface EventCallBack
 * @brief EventCallBack interface
 */
public interface StateEventCallBack {

    void onEvent (String event, String fromState);

}
