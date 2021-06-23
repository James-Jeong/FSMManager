package state.basic.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.StateHandler;

/**
 * @class public class EventListener implements EventCallBack
 * @brief EventListener class
 */
public class StateEventListener implements StateEventCallBack {

    private static final Logger logger = LoggerFactory.getLogger(StateEventListener.class);

    /**
     * @fn public StateEventListener()
     * @brief StateEventListener 생성자 함수
     */
    public StateEventListener() {
        // Nothing
    }

    /**
     * @fn public String onEvent(String handlerName, String event, String fromState)
     * @brief 지정한 이벤트 발생 시 호출되는 함수
     * @param handlerName 이벤트를 발생시킨 StateHandler 이름
     * @param event 이벤트 이름
     * @param fromState 천이 전 State 이름
     * @return 성공 시 다음 상태값, 실패 시 정의된 실패값 반환
     */
    @Override
    public String onEvent(String handlerName, String event, String fromState, String failState) {
        if (handlerName == null) {
            logger.warn("(null) Handler name is null. (event={}, fromState={})", event, fromState);
            return failState;
        }

        StateHandler stateHandler = StateManager.getInstance().getStateHandler(handlerName);
        if (stateHandler == null) {
            logger.warn("({}) Fail to find stateHandler. (event={}, fromState={})", handlerName, event, fromState);
            return failState;
        }

        String toState = stateHandler.findToStateFromEvent(event, fromState);
        if (toState == null) {
            //logger.warn("({}) Fail to find To state. (event={}, fromState={})", handlerName, event, fromState);
            return failState;
        }

        return stateHandler.nextState(toState, failState);
    }
}
