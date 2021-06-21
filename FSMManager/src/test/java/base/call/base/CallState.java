package base.call.base;

public enum CallState {

    INIT,
    OFFER,
    EARLY_NEGO_REQ,
    NEGO_REQ,
    EARLY_MEDIA,
    INACTIVE,
    ACTIVE,
    IDLE,
    HANGUP_REQ;

    CallState() {
        // Nothing
    }
}
