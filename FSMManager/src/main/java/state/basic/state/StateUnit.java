package state.basic.state;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.info.ResultCode;

/**
 * @class public class StateUnit
 * @brief StateUnit class
 */
public class StateUnit {

    private static final Logger logger = LoggerFactory.getLogger(StateUnit.class);

    private final String name;

    private String prevState = null;
    private String curState;
    private Object callBackResult = null;

    ////////////////////////////////////////////////////////////////////////////////

    public StateUnit(String name, String curState) {
        this.name = name;
        this.curState = curState;
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

    public synchronized Object getCallBackResult() {
        return callBackResult;
    }

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
