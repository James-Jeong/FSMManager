package state.basic;

import state.basic.event.StateEventManager;

import java.util.List;

/**
 * @class public class StateHandler
 * @brief StateHandler class
 */
public class StateHandler {

    // StateContainer
    private final StateContainer stateContainer;
    // StateEventManager
    private final StateEventManager stateEventManager;

    // StateHandler 이름
    private final String name;

    /**
     * @fn public StateHandler(String name, String initState)
     * @brief StateHandler 생성자 함수
     * @param name StateHandler 이름
     */
    public StateHandler(String name, String initState) {
       this.name = name;

        stateContainer = new StateContainer(name, initState);
        stateEventManager = new StateEventManager();
    }

    public boolean clearStateContainer() {
        return stateContainer.removeAllStates();
    }

    public boolean clearStateEventManager() {
        return stateEventManager.removeAllEvents();
    }

    /**
     * @fn public String getName()
     * @brief StateHandler 이름을 반환하는 함수
     * @return StateHandler 이름
     */
    public String getName() {
        return name;
    }

    /**
     * @fn public boolean addState (String event, String fromState, String toState, String failState, CallBack callBack)
     * @brief 새로운 State 를 추가하는 함수
     * fromState 가 toState 로 천이되기 위한 trigger 이벤트와 천이 후에 실행될 CallBack 을 정의한다.
     * @param event Trigger 될 이벤트 이름
     * @param fromState 천이 전 State 이름
     * @param toState 천이 후 State 이름
     * @param callBack 천이 후 실행될 CallBack
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean addState (String event, String fromState, String toState, CallBack callBack) {
        return stateEventManager.addEvent(event, fromState, toState) &&
                stateContainer.addToStateByFromState(fromState, toState, callBack);
    }

    /**
     * @fn public boolean removeFromState(String fromState)
     * @brief From state 를 삭제하는 함수
     * 다른 From state 와 To state 로 포함되어 있으면 다 삭제
     * @param fromState From state
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeFromState (String fromState) {
        return  stateContainer.removeFromState(fromState);
    }

    /**
     * @fn public boolean removeToStateByFromState(String fromState, String toState)
     * @brief From state 와 연관된 To state 를 삭제
     * From state 는 삭제되지 않고 To state 만 삭제
     * @param fromState From state
     * @param toState To state
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeToStateByFromState (String fromState, String toState) {
        return stateContainer.removeToStateByFromState(fromState, toState);
    }

    /**
     * @fn public String getCurState ()
     * @brief 현재 State 이름을 반환하는 함수
     * @return 현재 State 이름
     */
    public String getCurState () {
        return stateContainer.getCurState();
    }

    /**
     * @fn public String nextState (String toState, String failState)
     * @brief 현재 상태에서 매개변수로 전달받은 다음 상태로 천이하는 함수
     * @param toState To state
     * @return 성공 시 다음 상태값, 실패 시 정의된 실패값 반환
     */
    public String nextState (String toState, String failState) {
        return stateContainer.nextState(toState, failState);
    }

    /**
     * @fn public List<String> getStateList ()
     * @brief StateContainer 에 정의된 모든 상태들을 새로운 리스트에 저장하여 반환하는 함수
     * @return 성공 시 정의된 상태 리스트, 실패 시 null 반환
     */
    public List<String> getStateList () {
        return stateContainer.getAllStates();
    }

    /**
     * @fn public String fire (String event)
     * @brief 정의된 State 천이를 위해 지정한 이벤트를 발생시키는 함수
     * @param event 발생할 이벤트 이름
     * @param failState 천이 실패 시 반환될 State 이름
     * @return 성공 시 지정한 결과값 반환, 실패 시 failState 반환
     */
    public String fire (String event, String failState) {
        return stateEventManager.callEvent(name, event, getCurState(), failState);
    }

    /**
     * @fn public Map<String, String> findToStateFromEvent (String event, String fromState)
     * @brief 지정한 이벤트에 등록된 State Map 을 찾아서 반환하는 함수
     * @param event 이벤트 이름
     * @return 성공 시 State Map, 실패 시 null 반환
     */
    public synchronized String findToStateFromEvent(String event, String fromState) {
        return stateEventManager.getToStateFromEvent(event, fromState);
    }

    /**
     * @fn public Object getCallBackResult (String fromState, String toState)
     * @brief CallBack 결과를 반환하는 함수
     * @param fromState From state
     * @param toState To state
     * @return 성공 시 CallBack 결과값, 실패 시 null 반환
     */
    public Object getCallBackResult (String fromState, String toState) {
        CallBack callBack = stateContainer.getCallBackByFromState(fromState, toState);
        if (callBack == null) { return null; }
        return callBack.getResult();
    }

}