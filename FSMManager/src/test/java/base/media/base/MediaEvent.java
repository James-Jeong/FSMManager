package base.media.base;

import state.base.AbstractEvent;

/**
 * @class public class MediaEvent
 * @brief MediaEvent class
 */
public class MediaEvent extends AbstractEvent {

    public static final String MEDIA_START_EVENT = "media_start_success";
    public static final String MEDIA_STOP_EVENT = "media_stop_success";
    public static final String MEDIA_CREATE_SUCCESS_EVENT = "media_create_success";
    public static final String MEDIA_CREATE_FAIL_EVENT = "media_create_fail";
    public static final String MEDIA_DELETE_SUCCESS_EVENT = "media_delete_success";
    public static final String MEDIA_DELETE_FAIL_EVENT = "media_delete_fail";

    public MediaEvent() {
        // Nothing
    }
}
