package base.squirrel.call.base;

import state.squirrel.AbstractEvent;

/**
 * @class public class CallEvent
 * @brief CallEvent class
 */
public class CallEvent extends AbstractEvent {

    public static final String CALL_INIT_EVENT = "call_init";
    public static final String CALL_START_EVENT = "call_start";
    public static final String CALL_FAIL_EVENT = "call_fail";

    public static final String OFFER_STOP_EVENT = "offer_stop";
    public static final String NEGO_STOP_EVENT = "nego_stop";
    public static final String EARLY_NEGO_STOP_EVENT = "early_nego_stop";
    public static final String ACTIVE_STOP_EVENT = "active_stop";
    public static final String INACTIVE_STOP_EVENT = "inactive_stop";

    public static final String CALL_STOP_DONE_SUCCESS_EVENT = "call_stop_done_success";
    public static final String OFFER_EARLY_NEGO_START_EVENT = "early_nego_start";
    public static final String EARLY_MEDIA_START_EVENT = "early_media_start";
    public static final String OFFER_NEGO_START_EVENT = "nego_start";
    public static final String EARLY_NEGO_NEGO_START_EVENT = "nego_start";
    public static final String ACTIVE_START_EVENT = "active_start";
    public static final String EARLY_NEGO_INACTIVE_START_EVENT = "early_nego_inactive_start";
    public static final String NEGO_INACTIVE_START_EVENT = "nego_inactive_start";


    public CallEvent() {
        // Nothing
    }
}
