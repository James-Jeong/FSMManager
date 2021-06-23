package base.basic.media.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.CallBack;

/**
 * @class public class MediaCallBack implements CallBack
 * @brief MediaCallBack class
 */
public class MediaCallBack extends CallBack {

    private static final Logger logger = LoggerFactory.getLogger(MediaCallBack.class);

    public MediaCallBack(String name) {
        super(name);
    }

    @Override
    public void callBackFunc(Object... object) {
        if (object.length == 0) { return; }

        String stateName = (String) object[0];
        logger.info("({}) To state is {}", getName(), stateName);
        setResult(stateName);
    }

}
