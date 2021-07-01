package base.basic.call.base.callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.state.CallBack;

/**
 * @class public class CallCallBack implements CallBack
 * @brief CallCallBack class
 */
public class CommonCallBack extends CallBack {

    private static final Logger logger = LoggerFactory.getLogger(CommonCallBack.class);

    public CommonCallBack(String name) {
        super(name);
    }

    @Override
    public Object callBackFunc(Object... object) {
        if (object.length == 0) {
            return null;
        }

        String stateName = (String) object[0];
        logger.debug("({}) To state is {}", getName(), stateName);
        return stateName;
    }

}
