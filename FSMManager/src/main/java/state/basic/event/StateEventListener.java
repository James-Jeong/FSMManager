package state.basic.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.info.ResultCode;
import state.basic.state.StateHandler;
import state.basic.state.StateUnit;

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
     * @fn public String onEvent(String handlerName, String event, String fromState, Object... objects)
     * @brief 지정한 이벤트 발생 시 호출되는 함수
     * @param handlerName 이벤트를 발생시킨 StateHandler 이름
     * @param event 이벤트 이름
     * @param stateUnit State unit
     * @param failState 천이 실패 시 반환될 State 이름
     * @param params CallBack 가변 매개변수
     * @return 성공 시 다음 상태값, 실패 시 정의된 실패값 반환
     */
    @Override
    public String onEvent(String handlerName, String event, StateUnit stateUnit, String failState, Object... params) {
        if (stateUnit == null) {
            logger.warn("[{}] ({}) StateUnit is null. (event={})",
                    ResultCode.NULL_OBJECT, handlerName, event
            );
            return failState;
        }

        StateHandler stateHandler = StateManager.getInstance().getStateHandler(handlerName);
        if (stateHandler == null) {
            logger.warn("[{}] ({}) Fail to find stateHandler. (event={}, stateUnit={})",
                    ResultCode.FAIL_GET_STATE_HANDLER, handlerName, event, stateUnit
            );
            return failState;
        }

        String fromState = stateUnit.getCurState();
        if (fromState == null) {
            logger.warn("[{}] ({}) Fail to transit. From state is null. (fromState=null)",
                    ResultCode.FAIL_TRANSIT_STATE, handlerName
            );
            return failState;
        }

        String toState = stateHandler.findToStateFromEvent(event, fromState);
        if (toState == null) {
            //logger.warn("[{}] ({}) Fail to find To state. (event={}, fromState={})", ResultCode.FAIL_GET_STATE, handlerName, event, fromState);
            return failState;
        }

        if (fromState.equals(toState)) {
            logger.warn("[{}] ({}) Fail to transit. State is same. (curState={}, nextState={})",
                    ResultCode.SAME_STATE, handlerName, fromState, toState
            );
            return failState;
        }

        return stateHandler.nextState(stateUnit, toState, failState, params);
    }
}
