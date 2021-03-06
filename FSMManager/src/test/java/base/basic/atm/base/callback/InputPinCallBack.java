package base.basic.atm.base.callback;

import base.basic.atm.base.AtmAccount;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.basic.event.base.CallBack;
import state.basic.unit.StateUnit;

/**
 * @class public class ActiveStartCallBack extends CallBack
 * @brief ActiveStartCallBack class
 */
public class InputPinCallBack extends CallBack {

    private static final Logger logger = LoggerFactory.getLogger(InputPinCallBack.class);

    public InputPinCallBack(StateManager stateManager, String name) {
        super(stateManager, name);
    }

    @Override
    public Object callBackFunc(StateUnit stateUnit) {
        if (stateUnit == null) { return null; }

        AtmAccount atmAccount = (AtmAccount) stateUnit.getData();
        if (atmAccount == null) { return null; }

        String pinString = atmAccount.getPinString();
        if (pinString == null) {
            logger.warn("InputPinCallBack: PinString is null.");
            return null;
        }

        int curVerificationWrongCount = atmAccount.getVerificationWrongCount();
        if (!atmAccount.verifyPinString(pinString)) {
            logger.warn("InputPinCallBack: Pin number is wrong!");
            curVerificationWrongCount = atmAccount.addAndGetVerificationWrongCount(1);
            atmAccount.setVerified(false);
        } else {
            logger.warn("InputPinCallBack: Pin number is correct!");
            atmAccount.setVerificationWrongCount(0);
            atmAccount.setVerified(true);
        }

        logger.debug("InputPinCallBack: id={}, name={}, isVerified={}, verificationWrongCount={}",
                atmAccount.getId(), atmAccount.getName(), atmAccount.isVerified(), curVerificationWrongCount
        );

        return true;
    }

}
