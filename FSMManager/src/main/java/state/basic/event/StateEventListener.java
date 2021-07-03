package state.basic.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.event.base.StateEvent;
import state.basic.info.ResultCode;
import state.basic.module.StateHandler;
import state.basic.unit.StateUnit;

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
     * @fn public String onEvent(String handlerName, String event, StateUnit stateUnit, Object... params)
     * @brief 지정한 이벤트 발생 시 호출되는 함수
     * @param stateHandler 이벤트를 발생시킨 StateHandler
     * @param event 이벤트 이름
     * @param stateUnit State unit
     * @param params CallBack 가변 매개변수
     * @return 성공 시 다음 상태값, 실패 시 정의된 실패값 반환
     */
    @Override
    public String onEvent (StateHandler stateHandler, String event, StateUnit stateUnit, String fromState, String toState, Object... params) {
        if (fromState.equals(toState)) {
            logger.warn("[{}] ({}) Fail to transit. State is same. (curState={}, nextState={})",
                    ResultCode.SAME_STATE, stateHandler.getName(), fromState, toState
            );
            return null;
        }

        return stateHandler.nextState(stateUnit, toState, params);
    }
}
