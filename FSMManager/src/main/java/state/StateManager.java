package state;

import java.util.HashMap;
import java.util.Map;

/**
 * @class public class StateManager
 * @brief StateManager class
 */
public class StateManager {

    private final Map<String, StateHandler> stateHandlerMap = new HashMap<>();

    private static StateManager stateManager;

    ////////////////////////////////////////////////////////////////////////////////

    public StateManager() {
        // Nothing
    }

    public static StateManager getInstance() {
        if (stateManager == null) {
            stateManager = new StateManager();
        }

        return stateManager;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void addStateHandler (String name) {
        if (stateHandlerMap.get(name) != null) { return; }
        stateHandlerMap.putIfAbsent(name, new StateHandler(name));
    }

    public void removeStateHandler (String name) {
        if (stateHandlerMap.get(name) == null) { return; }
        stateHandlerMap.remove(name);
    }

    public StateHandler getStateHandler (String name) {
        if (stateHandlerMap.get(name) == null) { return null; }
        return stateHandlerMap.get(name);
    }

}
