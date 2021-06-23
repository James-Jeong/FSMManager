package base.basic.call.base;

import state.basic.CallBack;

/**
 * @class public class CallCallBack implements CallBack
 * @brief CallCallBack class
 */
public class CallCallBack extends CallBack {

    public CallCallBack(String name) {
        super(name);
    }

    @Override
    public void callBackFunc(Object... object) {
        if (object.length == 0) { return; }

        String stateName = (String) object[0];
        setResult(stateName);
    }

}
