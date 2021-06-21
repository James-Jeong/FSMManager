package base.call.base;

import state.base.AbstractState;

/**
 * @class public class CallState extends AbstractState
 * @brief CallState class
 */
public class CallState extends AbstractState {

    public static final String INIT = "INIT";
    public static final String OFFER = "OFFER";
    public static final String EARLY_NEGO_REQ = "EARLY_NEGO_REQ";
    public static final String NEGO_REQ = "NEGO_REQ";
    public static final String EARLY_MEDIA = "EARLY_MEDIA";
    public static final String INACTIVE = "INACTIVE";
    public static final String ACTIVE = "ACTIVE";
    public static final String IDLE = "IDLE";
    public static final String HANGUP_REQ = "HANGUP_REQ";

    public CallState() {
        // Nothing
    }
}
