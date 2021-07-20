package base.basic.call.base.callback;

import base.basic.call.base.CallInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.event.base.CallBack;
import state.basic.unit.StateUnit;

/**
 * @class public class CallCallBack implements CallBack
 * @brief CallCallBack class
 */
public class FailCallBack extends CallBack {

    private static final Logger logger = LoggerFactory.getLogger(FailCallBack.class);

    public FailCallBack(String name) {
        super(name);
    }

    @Override
    public Object callBackFunc(Object... object) {
        StateUnit stateUnit = getCurStateUnit();
        if (stateUnit == null) {
            return null;
        }

        CallInfo callInfo = (CallInfo) stateUnit.getData();
        if (callInfo == null) {
            return null;
        }

        logger.info("({}) Fail to transit. (callInfo={})", getName(), callInfo);
        return stateUnit.getCurState();
    }

}
