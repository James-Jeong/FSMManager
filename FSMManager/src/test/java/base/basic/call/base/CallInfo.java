package base.basic.call.base;

/**
 * @class public class CallInfo
 * @brief CallInfo class
 */
public class CallInfo {

    private final String callId;
    private final String fromNo;
    private final String toNo;
    private final String sipStateUnitName;
    private final String mediaStateUnitName;

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

    @Override
    public String toString() {
        return "CallInfo{" +
                "callId='" + callId + '\'' +
                ", fromNo='" + fromNo + '\'' +
                ", toNo='" + toNo + '\'' +
                ", sipStateUnitName='" + sipStateUnitName + '\'' +
                ", mediaStateUnitName='" + mediaStateUnitName + '\'' +
                '}';
    }
}
