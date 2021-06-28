package state.basic.info;

/**
 * @class public class ResultCode
 * @brief ResultCode class
 */
public class ResultCode {

    // 상태 저장 성공
    public static int SUCCESS_ADD_STATE = 1000;
    // 상태 삭제 성공
    public static int SUCCESS_REMOVE_STATE = 1001;
    // 상태 천이 성공
    public static int SUCCESS_TRANSIT_STATE = 1002;

    // 상태 저장 실패
    public static int FAIL_ADD_STATE = 2000;
    // 상태 삭제 실패
    public static int FAIL_REMOVE_STATE = 2001;
    // 상태 천이 실패
    public static int FAIL_TRANSIT_STATE = 2002;
    // 상태 조회 실패
    public static int FAIL_GET_STATE = 2003;
    // 콜백 조회 실패
    public static int FAIL_GET_CALLBACK = 2004;
    // 상태 처리자 조회 실패
    public static int FAIL_GET_STATE_HANDLER = 2005;

    // 상태 중복
    public static int DUPLICATED_STATE = 3000;
    // 이벤트 중복
    public static int DUPLICATED_EVENT = 3001;
    // 알 수 없는 상태
    public static int UNKNOWN_STATE = 3010;
    // 같은 상태
    public static int SAME_STATE = 3011;
    // NULL 객체
    public static int NULL_OBJECT = 3100;

}
