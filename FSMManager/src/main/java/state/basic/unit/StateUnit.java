package state.basic.unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.info.ResultCode;

import java.util.UUID;

/**
 * @class public class StateUnit
 * @brief StateUnit class
 */
public class StateUnit {

    private static final Logger logger = LoggerFactory.getLogger(StateUnit.class);

    // StateUnit 식별 이름
    private final String name;

    // 바로 이전 상태
    private String prevState = null;
    // 현재 상태
    private String curState;
    // 천이 실패 시 실행될 이벤트 키
    private String failEventKey = null;
    // CallBack 결과값
    private Object callBackResult = null;

    ////////////////////////////////////////////////////////////////////////////////

    public StateUnit(String name, String curState) {
        this.name = name;
        this.curState = curState;
    }

    public String getName() {
        return name;
    }

    public String getFailEventKey() {
        return failEventKey;
    }

    public String setFailEventKey() {
        this.failEventKey = makeFailEventKey();
        return this.failEventKey;
    }

    ////////////////////////////////////////////////////////////////////////////////

    private String makeFailEventKey () {
        return name + ":" + curState + ":" + UUID.randomUUID();
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public synchronized String getCurState ()
     * @brief 현재 State 이름을 반환하는 함수
     * @return 현재 State 이름
     */
    public synchronized String getCurState() {
        return curState;
    }

    /**
     * @fn private synchronized void setCurState (String state)
     * @brief 현재 State 를 설정하는 함수
     * @param curState 현재 State 이름
     */
    public synchronized void setCurState(String curState) {
        logger.info("[{}] ({}) Cur State is changed. ([{}] > [{}])",
                ResultCode.SUCCESS_TRANSIT_STATE, name, getCurState(), curState
        );
        this.curState = curState;
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public synchronized String getPrevState ()
     * @brief 이전 State 이름을 반환하는 함수
     * @return 이전 State 이름
     */
    public synchronized String getPrevState() {
        return prevState;
    }

    /**
     * @fn private synchronized void setPrevState (String state)
     * @brief 이전 State 를 설정하는 함수
     * @param prevState 이전 State 이름
     */
    public synchronized void setPrevState(String prevState) {
        logger.info("[{}] ({}) Prev State is changed. ([{}] > [{}])",
                ResultCode.SUCCESS_TRANSIT_STATE, name, getPrevState(), prevState
        );
        this.prevState = prevState;
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public synchronized Object getCallBackResult()
     * @brief CallBack 실행 결과를 반환하는 함수
     * @return CallBack 결과값
     */
    public synchronized Object getCallBackResult() {
        return callBackResult;
    }

    /**
     * @fn public synchronized void setCallBackResult(Object result)
     * @brief CallBack 실행 결과를 저장하는 함수
     * @param result 저장할 CallBack 결과값
     */
    public synchronized void setCallBackResult(Object result) {
        this.callBackResult = result;
    }

    @Override
    public String toString() {
        return "StateUnit{" +
                "name='" + name + '\'' +
                ", prevState='" + prevState + '\'' +
                ", curState='" + curState + '\'' +
                ", callBackResult=" + callBackResult +
                '}';
    }
}
