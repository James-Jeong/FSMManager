package state;

import com.google.common.util.concurrent.FutureCallback;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.StateMachineConfiguration;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;
import state.base.*;
import state.module.StateHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * @class public class StateManager
 * @brief StateManager class
 */
public class StateManager {

    private final Map<String, FsmContainer> fsmMap = new HashMap<>();

    // StateHandler Map
    private final Map<String, StateHandler> stateHandlerMap = new HashMap<>();

    // StateManager 싱글턴 인스턴스 변수
    private static StateManager stateManager;

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public StateManager()
     * @brief StateManager 생성자 함수
     */
    public StateManager() {
        // Nothing
    }

    /**
     * @fn public static StateManager getInstance()
     * @brief StateManager 싱글턴 인스턴스를 반환하는 함수
     * @return StateManager 싱글턴 인스턴스
     */
    public static StateManager getInstance() {
        if (stateManager == null) {
            stateManager = new StateManager();
        }

        return stateManager;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public synchronized void addFsmContainer (String name,
                                             AbstractFsm abstractFsm,
                                             AbstractState abstractState,
                                             AbstractEvent abstractEvent) {
        if (fsmMap.get(name) != null) { return; }

        FsmContainer fsmContainer = new FsmContainer(
                StateMachineBuilderFactory.create(
                abstractFsm.getClass(),
                abstractState.getClass(),
                abstractEvent.getClass(),
                TransitionContext.class
        ));

        fsmMap.putIfAbsent(name, fsmContainer);
    }

    public synchronized void removeFsmContainer (String name) {
        if (fsmMap.get(name) == null) { return; }
        fsmMap.remove(name);
    }

    public FsmContainer getFsmContainer (String name) {
        return fsmMap.get(name);
    }

    private UntypedStateMachineBuilder getFsmBuilder (String name) {
        return fsmMap.get(name).getUntypedStateMachineBuilder();
    }

    public synchronized void setFsmCondition (String name, String from, String to, String event) {
        getFsmBuilder(name).externalTransition().from(from).to(to).on(event);
    }

    public synchronized void setFsmOnEntry (String name, String state, String funcName) {
        getFsmBuilder(name).onEntry(state).callMethod(funcName);
    }

    public synchronized void setFsmOnExit (String name, String state, String funcName) {
        getFsmBuilder(name).onExit(state).callMethod(funcName);
    }

    public String getFsmCurState (String name) {
        return (String) getFsmContainer(name).getUntypedStateMachine().getCurrentState();
    }

    public String getFsmLastState (String name) {
        return (String) getFsmContainer(name).getUntypedStateMachine().getLastState();
    }

    public synchronized void setFsmFinalState (String name, String state) {
        getFsmContainer(name).getUntypedStateMachineBuilder().defineFinalState(state);
    }

    public synchronized void buildFsm (String name, String initState, boolean isDebugMode) {
        getFsmContainer(name).setUntypedStateMachine(getFsmContainer(name).getUntypedStateMachineBuilder().newStateMachine(initState, StateMachineConfiguration.getInstance().enableDebugMode(isDebugMode)));
        getFsmContainer(name).getUntypedStateMachine().start();
    }

    public boolean fireFsm (String name, String event, FutureCallback<Object> callback) {
        TransitionContext transitionContext;
        if (callback != null) {
            transitionContext = new TransitionContext().setCallback(callback);
        } else {
            transitionContext = null;
        }

        FsmContainer fsmContainer = getFsmContainer(name);
        if (fsmContainer != null) {
            fsmContainer.getUntypedStateMachine().fire(event, transitionContext);
            return true;
        }

        return false;
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public synchronized void addStateHandler (String name)
     * @brief StateHandler 를 새로 추가하는 함수
     * @param name StateHandler 이름
     */
    public synchronized void addStateHandler (String name) {
        if (stateHandlerMap.get(name) != null) { return; }
        stateHandlerMap.putIfAbsent(name, new StateHandler(name));
    }

    /**
     * @fn public synchronized void removeStateHandler (String name)
     * @brief 지정한 이름의 StateHandler 를 삭제하는 함수
     * @param name StateHandler 이름
     */
    public synchronized void removeStateHandler (String name) {
        if (stateHandlerMap.get(name) == null) { return; }
        stateHandlerMap.remove(name);
    }

    /**
     * @fn public StateHandler getStateHandler (String name)
     * @brief 지정한 이름의 StateHandler 를 반환하는 함수
     * @param name StateHandler 이름
     * @return 성공 시 StateHandler 객체, 실패 시 null 반환
     */
    public StateHandler getStateHandler (String name) {
        if (stateHandlerMap.get(name) == null) { return null; }
        return stateHandlerMap.get(name);
    }

}
