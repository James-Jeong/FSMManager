package state;

import com.google.common.util.concurrent.FutureCallback;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.StateMachineConfiguration;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;
import state.akka.AkkaContainer;
import state.basic.state.StateHandler;
import state.basic.state.StateUnit;
import state.squirrel.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @class public class StateManager
 * @brief StateManager class
 */
public class StateManager {

    // Akka Map
    private final Map<String, AkkaContainer> akkaMap = new ConcurrentHashMap<>();

    // Squirrel FSM Map
    private final Map<String, FsmContainer> fsmMap = new ConcurrentHashMap<>();

    // StateHandler Map
    private final Map<String, StateHandler> stateHandlerMap = new ConcurrentHashMap<>();

    private final Map<String, StateUnit> stateMap = new ConcurrentHashMap<>();

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
    // # Akka FSM

    public void addAkkaContainer (String name) {
        if (getAkkaContainer(name) != null) { return; }
        akkaMap.putIfAbsent(
                name,
                new AkkaContainer(name)
        );
    }

    public void removeAkkaContainer (String name) {
        if (getAkkaContainer(name) == null) { return; }

        getAkkaContainer(name).removeAllActorRefs();
        akkaMap.remove(name);
    }

    public AkkaContainer getAkkaContainer (String name) {
        return akkaMap.get(name);
    }

    ////////////////////////////////////////////////////////////////////////////////
    // # Squirrel FSM

    public void addFsmContainer (String name,
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

    public void removeFsmContainer (String name) {
        if (fsmMap.get(name) == null) { return; }
        fsmMap.remove(name);
    }

    public FsmContainer getFsmContainer (String name) {
        return fsmMap.get(name);
    }

    private UntypedStateMachineBuilder getFsmBuilder (String name) {
        return fsmMap.get(name).getUntypedStateMachineBuilder();
    }

    public void setFsmCondition (String name, String from, String to, String event) {
        getFsmBuilder(name).externalTransition().from(from).to(to).on(event);
    }

    public void setFsmOnEntry (String name, String state, String funcName) {
        getFsmBuilder(name).onEntry(state).callMethod(funcName);
    }

    public void setFsmOnExit (String name, String state, String funcName) {
        getFsmBuilder(name).onExit(state).callMethod(funcName);
    }

    public String getFsmCurState (String name) {
        return (String) getFsmContainer(name).getUntypedStateMachine().getCurrentState();
    }

    public String getFsmLastState (String name) {
        return (String) getFsmContainer(name).getUntypedStateMachine().getLastState();
    }

    public void setFsmFinalState (String name, String state) {
        getFsmContainer(name).getUntypedStateMachineBuilder().defineFinalState(state);
    }

    public void buildFsm (String name, String initState, boolean isDebugMode) {
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
    // # Handmade FSM

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public void addStateUnit (String name, String initState)
     * @brief StateUnit 을 새로 추가하는 함수
     * @param name StateUnit 이름
     * @param initState 초기 상태
     */
    public void addStateUnit (String name, String initState) {
        if (stateMap.get(name) != null) { return; }
        stateMap.putIfAbsent(name, new StateUnit(name, initState));
    }

    /**
     * @fn public boolean removeStateUnit (String name)
     * @brief 지정한 이름의 StateUnit 을 삭제하는 함수
     * @param name StateUnit 이름
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeStateUnit (String name) {
        StateUnit stateUnit = stateMap.get(name);
        if (stateUnit == null) { return false; }

        return stateMap.remove(name) != null;
    }

    /**
     * @fn public StateHandler getStateUnit (String name)
     * @brief 지정한 이름의 StateUnit 을 반환하는 함수
     * @param name StateUnit 이름
     * @return 성공 시 StateUnit 객체, 실패 시 null 반환
     */
    public StateUnit getStateUnit (String name) {
        if (stateMap.get(name) == null) { return null; }
        return stateMap.get(name);
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public void addStateHandler (String name)
     * @brief StateHandler 를 새로 추가하는 함수
     * @param name StateHandler 이름
     */
    public void addStateHandler (String name) {
        if (stateHandlerMap.get(name) != null) { return; }
        stateHandlerMap.putIfAbsent(name, new StateHandler(name));
    }

    /**
     * @fn public boolean removeStateHandler (String name)
     * @brief 지정한 이름의 StateHandler 를 삭제하는 함수
     * @param name StateHandler 이름
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeStateHandler (String name) {
        StateHandler stateHandler = stateHandlerMap.get(name);
        if (stateHandler == null) { return false; }

        return stateHandler.clearStateContainer() &&
                stateHandler.clearStateEventManager() &&
                stateHandlerMap.remove(name) != null;
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
