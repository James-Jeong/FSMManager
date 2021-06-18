package event;

import state.StateHandler;
import state.StateManager;

import java.util.Map;

/**
 * @class public class EventListener implements EventCallBack
 * @brief EventListener class
 */
public class StateEventListener implements StateEventCallBack {

    /**
     * @fn public StateEventListener()
     * @brief StateEventListener 생성자 함수
     */
    public StateEventListener() {
        // Nothing
    }

    /**
     * @fn public void onEvent(String handlerName, String event, String fromState)
     * @brief 지정한 이벤트 발생 시 호출되는 함수
     * @param handlerName 이벤트를 발생시킨 StateHandler 이름
     * @param event 이벤트 이름
     * @param fromState 천이 전 State 이름
     */
    @Override
    public void onEvent(String handlerName, String event, String fromState) {
        if (handlerName == null) { return; }

        StateManager stateManager = StateManager.getInstance();
        StateHandler stateHandler = stateManager.getStateHandler(handlerName);
        if (stateHandler == null) { return; }

        Map<String, String> stateMap = stateHandler.findEvent(event);
        if (stateMap != null) {
            String toState = stateMap.get(fromState);
            stateHandler.nextState(toState);
        }
    }
}
