package state;

import com.google.common.util.concurrent.FutureCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.StateMachineBuilderFactory;
import org.squirrelframework.foundation.fsm.StateMachineConfiguration;
import org.squirrelframework.foundation.fsm.UntypedStateMachineBuilder;
import state.akka.AkkaContainer;
import state.basic.module.StateHandler;
import state.basic.unit.StateUnit;
import state.squirrel.*;

import java.util.HashMap;
import java.util.Map;

/**
 * @class public class StateManager
 * @brief StateManager class
 */
public class StateManager {

    private static final Logger logger = LoggerFactory.getLogger(StateManager.class);

    // Akka Map
    private final Map<String, AkkaContainer> akkaMap = new HashMap<>();

    // Squirrel FSM Map
    private final Map<String, FsmContainer> fsmMap = new HashMap<>();

    // StateHandler Map
    private final Map<String, StateHandler> stateHandlerMap = new HashMap<>();

    // StateUnit Map
    private final Map<String, StateUnit> stateMap = new HashMap<>();

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

    /**
     * @fn public void addAkkaContainer (String name)
     * @brief AkkaContainer 를 새로 추가하는 함수
     * @param name AkkaContainer 이름
     */
    public void addAkkaContainer (String name) {
        if (getAkkaContainer(name) != null) { return; }

        synchronized (akkaMap) {
            akkaMap.putIfAbsent(
                    name,
                    new AkkaContainer(name)
            );
        }
    }

    /**
     * @fn public void removeAkkaContainer (String name)
     * @brief 지정한 이름의 AkkaContainer 를 삭제하는 함수
     * @param name AkkaContainer 이름
     */
    public void removeAkkaContainer (String name) {
        synchronized (akkaMap) {
            AkkaContainer akkaContainer = akkaMap.get(name);
            if (akkaContainer == null) { return; }

            akkaContainer.removeAllActorRefs();
            akkaMap.remove(name);
        }
    }

    /**
     * @fn public AkkaContainer getAkkaContainer (String name)
     * @brief 지정한 이름의 AkkaContainer 를 반환하는 함수
     * @param name AkkaContainer 이름
     * @return 성공 시 AkkaContainer 객체, 실패 시 null 반환
     */
    public AkkaContainer getAkkaContainer (String name) {
        synchronized (akkaMap) {
            return akkaMap.get(name);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////
    // # Squirrel FSM

    /**
     * @fn public void addFsmContainer (String name)
     * @brief FsmContainer 를 새로 추가하는 함수
     * @param name FsmContainer 이름
     * @param abstractFsm FSM 클래스 (서비스 로직)
     * @param abstractState 상태 클래스 (상태 정의)
     * @param abstractEvent 이벤트 클래스 (이벤트 정의)
     *
     */
    public void addFsmContainer (String name,
                                 AbstractFsm abstractFsm,
                                 AbstractState abstractState,
                                 AbstractEvent abstractEvent) {
        if (getFsmContainer(name) != null) { return; }

        synchronized (fsmMap) {
            FsmContainer fsmContainer = new FsmContainer(
                    StateMachineBuilderFactory.create(
                            abstractFsm.getClass(),
                            abstractState.getClass(),
                            abstractEvent.getClass(),
                            TransitionContext.class
                    ));

            fsmMap.putIfAbsent(name, fsmContainer);
        }
    }

    /**
     * @fn public void removeFsmContainer (String name)
     * @brief 지정한 이름의 FsmContainer 를 삭제하는 함수
     * @param name FsmContainer 이름
     */
    public void removeFsmContainer (String name) {
        synchronized (fsmMap) {
            if (fsmMap.get(name) == null) { return; }
            fsmMap.remove(name);
        }
    }

    /**
     * @fn public FsmContainer getFsmContainer (String name)
     * @brief 지정한 이름의 FsmContainer 를 반환하는 함수
     * @param name FsmContainer 이름
     * @return 성공 시 FsmContainer 객체, 실패 시 null 반환
     */
    public FsmContainer getFsmContainer (String name) {
        synchronized (fsmMap) {
            return fsmMap.get(name);
        }
    }

    /**
     * @fn public UntypedStateMachineBuilder getFsmBuilder (String name)
     * @brief 지정한 이름의 FsmContainer 의 FsmBuilder 를 반환하는 함수
     * @param name FsmContainer 이름
     * @return 성공 시 FsmBuilder 객체, 실패 시 null 반환
     */
    private UntypedStateMachineBuilder getFsmBuilder (String name) {
        FsmContainer fsmContainer = getFsmContainer(name);
        if (fsmContainer == null) { return null; }
        return fsmContainer.getUntypedStateMachineBuilder();
    }

    /**
     * @fn public void setFsmCondition (String name, String from, String to, String event)
     * @brief FSM 상태 천이 조건을 새로 추가하는 함수
     * @param name FsmContainer 이름
     * @param from From state
     * @param to To state
     * @param event 트리거될 이벤트 이름
     */
    public void setFsmCondition (String name, String from, String to, String event) {
        UntypedStateMachineBuilder untypedStateMachineBuilder = getFsmBuilder(name);
        if (untypedStateMachineBuilder == null) {
            logger.warn("Fail to set fsm condition. (name={}, from={}, to={}, event={})",
                    name, from, to, event
            );
            return;
        }

        untypedStateMachineBuilder.externalTransition().from(from).to(to).on(event);
    }

    /**
     * @fn public void setFsmOnEntry (String name, String state, String funcName)
     * @brief 상태 진입 시 실행될 함수를 정의하는 함수
     * @param name FsmContainer 이름
     * @param state 상태 이름
     * @param funcName 함수 이름
     */
    public void setFsmOnEntry (String name, String state, String funcName) {
        UntypedStateMachineBuilder untypedStateMachineBuilder = getFsmBuilder(name);
        if (untypedStateMachineBuilder == null) {
            logger.warn("Fail to set fsm onEntry. (name={}, state={}, funcName={})",
                    name, state, funcName
            );
            return;
        }

        untypedStateMachineBuilder.onEntry(state).callMethod(funcName);
    }

    /**
     * @fn public void setFsmOnExit (String name, String state, String funcName)
     * @brief 상태 종료 시 (다음 상태로 천이 후) 실행될 함수를 정의하는 함수
     * @param name FsmContainer 이름
     * @param state 상태 이름
     * @param funcName 함수 이름
     */
    public void setFsmOnExit (String name, String state, String funcName) {
        UntypedStateMachineBuilder untypedStateMachineBuilder = getFsmBuilder(name);
        if (untypedStateMachineBuilder == null) {
            logger.warn("Fail to set fsm onExit. (name={}, state={}, funcName={})",
                    name, state, funcName
            );
            return;
        }

        untypedStateMachineBuilder.onExit(state).callMethod(funcName);
    }

    /**
     * @fn public String getFsmCurState (String name)
     * @brief 현재 상태를 반환하는 함수
     * @param name FsmContainer 이름
     * @return 성공 시 현재 상태, 실패 시 null 반환
     */
    public String getFsmCurState (String name) {
        return (String) getFsmContainer(name).getUntypedStateMachine().getCurrentState();
    }

    /**
     * @fn public String getFsmLastState (String name)
     * @brief 상태 천이 되기 전 가장 마지막 상태(바로 이전 상태)를 반환하는 함수
     * @param name FsmContainer 이름
     * @return 성공 시 바로 이전 상태, 실패 시 null 반환
     */
    public String getFsmLastState (String name) {
        return (String) getFsmContainer(name).getUntypedStateMachine().getLastState();
    }

    /**
     * @fn public void setFsmFinalState (String name, String state)
     * @brief FSM 의 마지막 상태를 정의하는 함수
     * @param name FsmContainer 이름
     * @param state 마지막 상태
     */
    public void setFsmFinalState (String name, String state) {
        getFsmContainer(name).getUntypedStateMachineBuilder().defineFinalState(state);
    }

    /**
     * @fn public void buildFsm (String name, String initState, boolean isDebugMode)
     * @brief FSM 을 빌드하는 함수
     * @param name FsmContainer 이름
     * @param initState 초기 상태
     * @param isDebugMode 디버그 모드 여부
     */
    public void buildFsm (String name, String initState, boolean isDebugMode) {
        getFsmContainer(name).setUntypedStateMachine(
                getFsmContainer(name).
                        getUntypedStateMachineBuilder().
                        newStateMachine(
                                initState,
                                StateMachineConfiguration.getInstance().enableDebugMode(isDebugMode)
                        )
        );

        getFsmContainer(name).getUntypedStateMachine().start();
    }

    /**
     * @fn public boolean fireFsm (String name, String event, FutureCallback<Object> callback)
     * @brief FSM 이벤트를 발생하는 함수
     * @param name FsmContainer 이름
     * @param event 이벤트 이름
     * @param callback CallBack
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean fireFsm (String name, String event, FutureCallback<Object> callback) {
        TransitionContext transitionContext;
        if (callback != null) {
            transitionContext = new TransitionContext().setCallback(callback);
        } else {
            transitionContext = null;
        }

        FsmContainer fsmContainer = getFsmContainer(name);
        if (fsmContainer != null) {
            fsmContainer.getUntypedStateMachine().fire(
                    event,
                    transitionContext
            );
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
        synchronized (stateMap) {
            if (stateMap.get(name) != null) {
                return;
            }
            stateMap.putIfAbsent(name, new StateUnit(name, initState));
        }
    }

    /**
     * @fn public boolean removeStateUnit (String name)
     * @brief 지정한 이름의 StateUnit 을 삭제하는 함수
     * @param name StateUnit 이름
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeStateUnit (String name) {
        synchronized (stateMap) {
            StateUnit stateUnit = stateMap.get(name);
            if (stateUnit == null) {
                return false;
            }

            return stateMap.remove(name) != null;
        }
    }

    /**
     * @fn public StateHandler getStateUnit (String name)
     * @brief 지정한 이름의 StateUnit 을 반환하는 함수
     * @param name StateUnit 이름
     * @return 성공 시 StateUnit 객체, 실패 시 null 반환
     */
    public StateUnit getStateUnit (String name) {
        synchronized (stateMap) {
            return stateMap.get(name);
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public void addStateHandler (String name)
     * @brief StateHandler 를 새로 추가하는 함수
     * @param name StateHandler 이름
     */
    public void addStateHandler (String name) {
        synchronized (stateHandlerMap) {
            if (stateHandlerMap.get(name) != null) { return; }
            stateHandlerMap.putIfAbsent(name, new StateHandler(name));
        }
    }

    /**
     * @fn public boolean removeStateHandler (String name)
     * @brief 지정한 이름의 StateHandler 를 삭제하는 함수
     * @param name StateHandler 이름
     * @return 성공 시 true, 실패 시 false 반환
     */
    public boolean removeStateHandler (String name) {
        synchronized (stateHandlerMap) {
            StateHandler stateHandler = stateHandlerMap.get(name);
            if (stateHandler == null) {
                return false;
            }

            stateHandler.clearStateContainer();
            stateHandler.clearStateEventManager();
            return stateHandlerMap.remove(name) != null;
        }
    }

    /**
     * @fn public StateHandler getStateHandler (String name)
     * @brief 지정한 이름의 StateHandler 를 반환하는 함수
     * @param name StateHandler 이름
     * @return 성공 시 StateHandler 객체, 실패 시 null 반환
     */
    public StateHandler getStateHandler (String name) {
        synchronized (stateHandlerMap) {
            return stateHandlerMap.get(name);
        }
    }

    public int getTotalEventSize () {
        synchronized (stateHandlerMap) {
            int totalSize = 0;

            for (StateHandler stateHandler : stateHandlerMap.values()) {
                if (stateHandler == null) { continue; }
                totalSize += stateHandler.getTotalEventSize();
            }

            return totalSize;
        }
    }

}
