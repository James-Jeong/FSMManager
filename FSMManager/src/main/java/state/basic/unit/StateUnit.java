package state.basic.unit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import state.basic.info.ResultCode;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @class public class StateUnit
 * @brief StateUnit class
 */
public class StateUnit {

    private static final Logger logger = LoggerFactory.getLogger(StateUnit.class);

    // StateUnit 식별 이름
    private final String name;
    // StateHandler 이름
    private final String handlerName;

    private final AtomicBoolean isAlive = new AtomicBoolean(false);

    // 바로 이전 상태
    private String prevState = null;
    private final ReentrantLock prevStateLock = new ReentrantLock();

    // 현재 상태
    private String curState;
    private final ReentrantLock curStateLock = new ReentrantLock();

    // 천이 실패 시 실행될 이벤트 키
    private String nextEventKey = null;
    private final ReentrantLock nextEventKeyLock = new ReentrantLock();

    // Success CallBack 결과값
    private Object successCallBackResult = null;
    private final ReentrantLock successCallBackResultLock = new ReentrantLock();

    // Fail CallBack 결과값
    private Object failCallBackResult = null;
    private final ReentrantLock failCallBackResultLock = new ReentrantLock();

    // Spare Data
    private Object data;
    private final ReentrantLock dataLock = new ReentrantLock();

    ////////////////////////////////////////////////////////////////////////////////

    public StateUnit(String name, String handlerName, String curState, Object data) {
        this.name = name;
        this.handlerName = handlerName;
        this.curState = curState;
        this.data = data;
    }

    /**
     * @fn public String getName()
     * @brief StateUnit 이름을 반환하는 함수
     * @return StateUnit 이름
     */
    public String getName() {
        return name;
    }

    /**
     * @fn public String getHandlerName()
     * @brief StateHandler 이름을 반환하는 함수
     * @return StateHandler 이름
     */
    public String getHandlerName() {
        return handlerName;
    }

    /**
     * @fn public String getNextEventKey()
     * @brief nextEventKey 를 반환하는 함수
     * @return 기존에 설정된 nextEventKey
     */
    public String getNextEventKey() {
        try {
            nextEventKeyLock.lock();
            return nextEventKey;
        } finally {
            nextEventKeyLock.unlock();
        }
    }

    /**
     * @fn public String setFailEventKey(String curState)
     * @brief nextEventKey 를 설정하고 반환하는 함수
     * @param curState 현재 상태
     * @return 새로 설정된 nextEventKey
     */
    public String setNextEventKey(String curState) {
        try {
            nextEventKeyLock.lock();
            this.nextEventKey = makeNextEventKey(curState);
            return this.nextEventKey;
        } finally {
            nextEventKeyLock.unlock();
        }
    }

    /**
     * @fn private String makeNextEventKey(String curState)
     * @brief nextEventKey 로 사용될 문자열을 생성하고 반환하는 함수
     * @param curState 현재 상태
     * @return 새로 생성된 nextEventKey
     */
    private String makeNextEventKey(String curState) {
        if (curState == null) { return null; }
        return name + ":" + curState + ":" + UUID.randomUUID();
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public String getData()
     * @brief StateUnit Spare data 를 반환하는 함수
     * @return data
     */
    public Object getData() {
        try {
            dataLock.lock();
            return data;
        } finally {
            dataLock.unlock();
        }
    }

    public void setData(Object data) {
        try {
            dataLock.lock();
            this.data = data;
        } finally {
            dataLock.unlock();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void setState(String fromState, String toState) {
        setPrevState(fromState);
        setCurState(toState);
    }

    /**
     * @fn public String getCurState ()
     * @brief 현재 State 이름을 반환하는 함수
     * @return 현재 State 이름
     */
    public String getCurState() {
        try {
            curStateLock.lock();
            return curState;
        } finally {
            curStateLock.unlock();
        }
    }

    /**
     * @fn private void setCurState (String state)
     * @brief 현재 State 를 설정하는 함수
     * @param curState 현재 State 이름
     */
    public void setCurState(String curState) {
        try {
            curStateLock.lock();
            logger.info("[{}] ({}) Cur State is changed. ([{}] > [{}])",
                    ResultCode.SUCCESS_TRANSIT_STATE, name, getCurState(), curState
            );
            this.curState = curState;
        } finally {
            curStateLock.unlock();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public String getPrevState ()
     * @brief 이전 State 이름을 반환하는 함수
     * @return 이전 State 이름
     */
    public String getPrevState() {
        try {
            prevStateLock.lock();
            return prevState;
        } finally {
            prevStateLock.unlock();
        }
    }

    /**
     * @fn private void setPrevState (String state)
     * @brief 이전 State 를 설정하는 함수
     * @param prevState 이전 State 이름
     */
    public void setPrevState(String prevState) {
        try {
            prevStateLock.lock();
            logger.info("[{}] ({}) Prev State is changed. ([{}] > [{}])",
                    ResultCode.SUCCESS_TRANSIT_STATE, name, getPrevState(), prevState
            );
            this.prevState = prevState;
        } finally {
            prevStateLock.unlock();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public Object getCallBackResult()
     * @brief CallBack 실행 결과를 반환하는 함수
     * @return CallBack 결과값
     */
    public Object getSuccessCallBackResult() {
        try {
            successCallBackResultLock.lock();
            return successCallBackResult;
        } finally {
            successCallBackResultLock.unlock();
        }
    }

    /**
     * @fn public void setCallBackResult(Object result)
     * @brief CallBack 실행 결과를 저장하는 함수
     * @param result 저장할 CallBack 결과값
     */
    public void setSuccessCallBackResult(Object result) {
        try {
            successCallBackResultLock.lock();
            this.successCallBackResult = result;
        } finally {
            successCallBackResultLock.unlock();
        }
    }

    public Object getFailCallBackResult() {
        try {
            failCallBackResultLock.lock();
            return failCallBackResult;
        } finally {
            failCallBackResultLock.unlock();
        }
    }

    public void setFailCallBackResult(Object failCallBackResult) {
        try {
            failCallBackResultLock.lock();
            this.failCallBackResult = failCallBackResult;
        } finally {
            failCallBackResultLock.unlock();
        }
    }

    ////////////////////////////////////////////////////////////////////////////////

    public void setIsAlive (boolean isAlive) {
        this.isAlive.set(isAlive);
    }

    public boolean getIsAlive () {
        return isAlive.get();
    }

    ////////////////////////////////////////////////////////////////////////////////

    @Override
    public String toString() {
        return "StateUnit{" +
                "name='" + name + '\'' +
                ", handlerName='" + handlerName + '\'' +
                ", prevState='" + prevState + '\'' +
                ", curState='" + curState + '\'' +
                ", nextEventKey='" + nextEventKey + '\'' +
                ", successCallBackResult=" + successCallBackResult +
                ", failCallBackResult=" + failCallBackResult +
                ", data=" + data +
                '}';
    }
}
