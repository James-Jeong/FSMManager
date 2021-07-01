package state.basic.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.info.ResultCode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @class public class StateContainer
 * @brief State Container class
 */
public class StateContainer {

    private static final Logger logger = LoggerFactory.getLogger(StateContainer.class);

    // State Map
    private final Map<String, Map<String, CallBack>> stateMap = new HashMap<>();

    // StateContainer 이름
    private final String name;

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public StateContainer(String name)
     * @brief StateContainer 생성자 함수
     * @param name StateContainer 이름
     */
    public StateContainer(String name) {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public boolean addToStateByFromState(String fromState, String toState, String failState, CallBack callBack)
     * @brief From state 와 연관된 To state 를 모두 Map 에 추가하는 함수
     * @param fromState From state
     * @param toState To state
     * @param callBack CallBack
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean addToStateByFromState(String fromState, String toState, CallBack callBack) {
        if (getCallBackByFromState(fromState, toState) != null) {
            logger.warn("[{}] ({}) Duplicated state. (fromState={}, toState={})",
                    ResultCode.DUPLICATED_STATE, name, fromState, toState
            );
            return false;
        }

        synchronized (stateMap) {
            Map<String, CallBack> toStateMap = stateMap.get(fromState);
            if (toStateMap == null) {
                toStateMap = new HashMap<>();
                toStateMap.putIfAbsent(toState, callBack);
            }

            boolean result = stateMap.putIfAbsent(fromState, toStateMap) == null;

            if (result) {
                logger.debug("[{}] ({}) Success to add state. (fromState={}, toState={})",
                        ResultCode.SUCCESS_ADD_STATE, name, fromState, toState
                );
            } else {
                result = toStateMap.putIfAbsent(toState, callBack) == null;
                if (result) {
                    logger.debug("[{}] ({}) Success to add state. (fromState={}, toState={})",
                            ResultCode.SUCCESS_ADD_STATE, name, fromState, toState
                    );
                } else {
                    logger.warn("[{}] ({}) Fail to add state. (fromState={}, toState={})",
                            ResultCode.FAIL_ADD_STATE, name, fromState, toState
                    );
                }
            }

            return result;
        }
    }

    /**
     * @fn public void removeAllStates()
     * @brief 등록된 모든 상태들을 삭제하는 함수
     */
    public void removeAllStates() {
        synchronized (stateMap) {
            stateMap.clear();
        }
    }

    /**
     * @fn public boolean removeFromState(String fromState)
     * @brief From state 를 Map 에서 삭제하는 함수
     * @param fromState From state
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeFromState(String fromState) {
        Map<String, CallBack> toStateMap = getToStateMapByFromState(fromState);
        if (toStateMap != null) {
            toStateMap.clear();
        }

        synchronized (stateMap) {
            boolean result = stateMap.remove(fromState) != null;
            if (result) {
                logger.debug("[{}] ({}) Success to remove the from state. (fromState={})",
                        ResultCode.SUCCESS_REMOVE_STATE, name, fromState
                );
            } else {
                logger.debug("[{}] ({}) Fail to remove the from state. (fromState={})",
                        ResultCode.FAIL_REMOVE_STATE, name, fromState
                );
            }

            return result;
        }
    }

    /**
     * @fn public boolean removeToStateByFromState(String fromState, String toState)
     * @brief From state 와 연관된 To state 를 Map 에서 삭제
     * From state 는 삭제되지 않고 To state 만 삭제
     * @param fromState From state
     * @param toState To state
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeToStateByFromState(String fromState, String toState) {
        if (getToStateMapByFromState(fromState) == null) {
            logger.warn("[{}] ({}) Unknown state. (fromState={}, toState={})",
                    ResultCode.UNKNOWN_STATE, name, fromState, toState
            );
            return false;
        }

        boolean result = getToStateMapByFromState(fromState).remove(toState) != null;
        if (result) {
            logger.debug("[{}] ({}) Success to remove the to state. (fromState={}, toState={})",
                    ResultCode.SUCCESS_REMOVE_STATE, name, fromState, toState
            );
        } else {
            logger.debug("[{}] ({}) Fail to remove the to state. (fromState={}, toState={})",
                    ResultCode.FAIL_REMOVE_STATE, name, fromState, toState
            );
        }

        return result;
    }

    /**
     * @fn public Map<String, CallBack> getToStateMapByFromState(String fromState)
     * @brief From state 와 연관된 To state Map 을 반환하는 함수
     * @param fromState From state
     * @return 성공 시 To state Map, 실패 시 null 반환
     */
    public Map<String, CallBack> getToStateMapByFromState(String fromState) {
        synchronized (stateMap) {
            return stateMap.get(fromState);
        }
    }

    /**
     * @fn public CallBack getCallBackByFromState(String fromState, String toState)
     * @brief From state 와 연관된 To state 의 CallBack 을 반환하는 함수
     * @param fromState From state
     * @param toState To state
     * @return 성공 시 CallBack, 실패 시 null 반환
     */
    public CallBack getCallBackByFromState(String fromState, String toState) {
        Map<String, CallBack> toStateMap = getToStateMapByFromState(fromState);
        if (toStateMap == null) { return null; }
        return toStateMap.get(toState);
    }

    /**
     * @fn public List<String> getAllStates ()
     * @brief 정의된 모든 상태들을 새로운 리스트에 저장하여 반환하는 함수
     * @return 성공 시 정의된 상태 리스트, 실패 시 null 반환
     */
    public List<String> getAllStates () {
        synchronized (stateMap) {
            return new ArrayList<>(stateMap.keySet());
        }
    }

    /**
     * @fn public Object nextState (String toState)
     * @brief 현재 상태에서 매개변수로 전달받은 다음 상태로 천이하는 함수
     * 이 함수에서 To state 와 연관된 CallBack 이 실행되며, CallBack 결과값이 StateContainer 에 저장됨
     * @param stateUnit State Unit
     * @param toState To state
     * @param failState 천이 실패 시 반환될 State 이름
     * @param params CallBack 가변 매개변수
     * @return 성공 시 다음 상태값, 실패 시 정의된 실패값 반환
     */
    public String nextState (StateUnit stateUnit, String toState, String failState, Object... params) {
        String fromState = stateUnit.getCurState();
        Map<String, CallBack> nextStateCallBackMap = getToStateMapByFromState(fromState);
        if (nextStateCallBackMap == null) {
            logger.warn("[{}] ({}) Fail to transit. To state is not defined. (fromState={}, toState={})",
                    ResultCode.FAIL_GET_STATE, name, fromState, toState
            );
            return failState;
        }

        // 1) 상태 천이 먼저 수행 (이전 상태도 저장)
        stateUnit.setPrevState(fromState);
        stateUnit.setCurState(toState);

        // 2) CallBack 함수 나중에 수행
        CallBack nextStateCallBack = nextStateCallBackMap.get(toState);
        if (nextStateCallBack != null) {
            stateUnit.setCallBackResult(nextStateCallBack.callBackFunc(params));
        }

        // 3) 천이된 현재 상태를 반환
        return stateUnit.getCurState();
    }

}
