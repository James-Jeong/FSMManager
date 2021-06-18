package state;

import java.util.HashMap;
import java.util.Map;

/**
 * @class public class StateManager
 * @brief StateManager class
 */
public class StateManager {

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
