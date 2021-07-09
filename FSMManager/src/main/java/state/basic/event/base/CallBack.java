package state.basic.event.base;


import state.basic.unit.StateUnit;

/**
 * @class public abstract class CallBack
 * @brief CallBack class
 */
public abstract class CallBack {

    private final String name;
    
    private StateUnit curStateUnit = null;

    ////////////////////////////////////////////////////////////////////////////////

    protected CallBack(String name) {
        this.name = name;
    }

    ////////////////////////////////////////////////////////////////////////////////

    public Object callBackFunc(Object... params) {
        // Must be implemented.
        return null;
    }

    public String getName() {
        return name;
    }

    public StateUnit getCurStateUnit() {
        return curStateUnit;
    }

    public void setCurStateUnit(StateUnit curStateUnit) {
        this.curStateUnit = curStateUnit;
    }
}
