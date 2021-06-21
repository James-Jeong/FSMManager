package base.media.base;

import state.base.AbstractState;

public class MediaState extends AbstractState {

    public static final String IDLE_STATE = "IDLE_STATE";
    public static final String ACTIVE_REQUEST = "ACTIVE_REQUEST";
    public static final String ACTIVE_STATE = "ACTIVE_STATE";
    public static final String IDLE_REQUEST = "IDLE_REQUEST";

    public MediaState() {
        // Nothing
    }

}
