package base.call.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.StateManager;
import state.base.AbstractFsm;
import state.base.TransitionContext;

/**
 * @class public class CallFsm extends AbstractFsm
 * @brief CallFsm class
 */
public class CallFsm extends AbstractFsm {

    private static final Logger logger = LoggerFactory.getLogger(CallFsm.class);
    public static final String CALL_STATE_NAME = "call_state";

    public CallFsm() {
        // Nothing
    }

    public void callStart (Object from, Object to, Object event, Object transitionContext) {
        TransitionContext context = (TransitionContext) transitionContext;
        context.getCallback().onSuccess(to);
        logger.info("@ Call is started!");
    }

    public void earlyNegoStart (Object from, Object to, Object event, Object transitionContext) {
        TransitionContext context = (TransitionContext) transitionContext;
        context.getCallback().onSuccess(to);
        logger.info("@ Early Nego is started!");
    }

    public void earlyMediaStart (Object from, Object to, Object event, Object transitionContext) {
        TransitionContext context = (TransitionContext) transitionContext;
        context.getCallback().onSuccess(to);
        logger.info("@ Early Media is started!");
    }

    public void activeStart (Object from, Object to, Object event, Object transitionContext) {
        TransitionContext context = (TransitionContext) transitionContext;
        context.getCallback().onSuccess(to);
        logger.info("@ Active is started!");
    }

    public void inActiveStart (Object from, Object to, Object event, Object transitionContext) {
        TransitionContext context = (TransitionContext) transitionContext;
        context.getCallback().onSuccess(to);

        StateManager stateManager = StateManager.getInstance();
        String curState = stateManager.getFsmCurState(CALL_STATE_NAME);
        if (curState.equals(CallState.EARLY_NEGO_REQ) || curState.equals(CallState.NEGO_REQ)) {
            logger.info("@ InActive is started by {}!", curState);
        } else {
            logger.warn("@ Unknown flow for inactive.");
        }
    }

    public void negoStart (Object from, Object to, Object event, Object transitionContext) {
        TransitionContext context = (TransitionContext) transitionContext;
        context.getCallback().onSuccess(to);

        StateManager stateManager = StateManager.getInstance();
        String curState = stateManager.getFsmCurState(CALL_STATE_NAME);
        if (curState.equals(CallState.OFFER) || curState.equals(CallState.EARLY_MEDIA) ) {
            logger.info("@ Nego is started by {}!", curState);
        } else {
            logger.warn("@ Unknown flow for nego.");
        }
    }

    public void hangupStart (Object from, Object to, Object event, Object transitionContext) {
        TransitionContext context = (TransitionContext) transitionContext;
        context.getCallback().onSuccess(to);

        StateManager stateManager = StateManager.getInstance();
        String curState = stateManager.getFsmCurState(CALL_STATE_NAME);
        switch (curState) {
            case CallState.OFFER:
            case CallState.EARLY_MEDIA:
            case CallState.ACTIVE:
                logger.info("@ Hangup is started by {}!", curState);
                break;
            default:
                logger.warn("@ Unknown flow for Hangup.");
                break;
        }
    }

    public void callInitSuccess (Object from, Object to, Object event, Object transitionContext) {
        TransitionContext context = (TransitionContext) transitionContext;
        context.getCallback().onSuccess(to);

        StateManager stateManager = StateManager.getInstance();
        String curState = stateManager.getFsmCurState(CALL_STATE_NAME);

        if (curState.equals(CallState.IDLE) || curState.equals(CallState.HANGUP_REQ)) {
            logger.info("@ Success to init the call by {}!", curState);
        } else {
            logger.warn("@ Unknown flow for CallStop.");
        }
    }

}
