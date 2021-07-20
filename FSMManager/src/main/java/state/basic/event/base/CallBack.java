package state.basic.event.base;

import state.basic.unit.StateUnit;

/**
 * @class public abstract class CallBack
 * @brief CallBack class
 * 이벤트 실행 시 사용자가 직접 정의 가능한 동작 클래스
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

    @Override
    public String toString() {
        return "CallBack{" +
                "name='" + name + '\'' +
                ", curStateUnit=" + curStateUnit +
                '}';
    }
}
