package base.basic.call.base;

/**
 * @class public class CallInfo
 * @brief CallInfo class
 */
public class CallInfo {

    String callId;
    String fromNo;
    String toNo;
    String sipStateUnitName;
    String mediaStateUnitName;

    public CallInfo(String callId, String fromNo, String toNo) {
        this.callId = callId;
        this.fromNo = fromNo;
        this.toNo = toNo;

        this.sipStateUnitName = callId + "_sip";
        this.mediaStateUnitName = callId + "_media";
    }

    public String getCallId() {
        return callId;
    }

    public String getFromNo() {
        return fromNo;
    }

    public String getToNo() {
        return toNo;
    }

    public String getSipStateUnitName() {
        return sipStateUnitName;
    }

    public String getMediaStateUnitName() {
        return mediaStateUnitName;
    }
}
