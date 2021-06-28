package base.basic.call.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.state.CallBack;

/**
 * @class public class CallCallBack implements CallBack
 * @brief CallCallBack class
 */
public class CallCallBack extends CallBack {

    private static final Logger logger = LoggerFactory.getLogger(CallCallBack.class);

    public CallCallBack(String name) {
        super(name);
    }

    @Override
    public Object callBackFunc(Object... object) {
        if (object.length == 0) {
            return null;
        }

        String stateName = (String) object[0];
        logger.info("({}) To state is {}", getName(), stateName);
        return stateName;
    }

}
