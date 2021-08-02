package state.basic.event.retry.base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @class public class RetryUnit
 * @brief RetryUnit class
 */
public class RetryUnit {

    private static final Logger logger = LoggerFactory.getLogger(RetryUnit.class);

    private final String name;
    /* Retry Count Limit */
    private final int retryCountLimit;
    /* Current Retry Count */
    private int curRetryCount;
    /* Retry Status */
    private RetryStatus retryStatus = RetryStatus.IDLE;

    ////////////////////////////////////////////////////////////////////////////////

    /**
     * @fn public RetryUnit(String name, int retryCountLimit, int curRetryCount)
     * @brief RetryUnit 생성자 함수
     * 일회성 객체 > curRetryCount 값이 retryCountLimit 과 동일해지면 RetryManager 에서 참조 해제한다.
     * @param name RetryUnit Name
     * @param retryCountLimit 재시도 제한 횟수
     * @param curRetryCount 재시도 진행 횟수
     */
    public RetryUnit(String name, int retryCountLimit, int curRetryCount) {
        this.name = name;
        this.retryCountLimit = Math.max(retryCountLimit, 0);
        this.curRetryCount = Math.max(curRetryCount, 0);
    }

    ////////////////////////////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public int getRetryCountLimit() {
        return retryCountLimit;
    }

    public int getCurRetryCount() {
        return curRetryCount;
    }

    public void setCurRetryCount(int curRetryCount) {
        if (this.curRetryCount != curRetryCount) {
            logger.info("RetryUnit({}): Retry Count is changed. ({} > {})",
                    name, this.curRetryCount, curRetryCount
            );
        }
        this.curRetryCount = curRetryCount;
    }

    public RetryStatus getRetryStatus() {
        return retryStatus;
    }

    public void setRetryStatus(RetryStatus retryStatus) {
        if (this.retryStatus != retryStatus) {
            logger.info("RetryUnit({}): Retry Status is changed. ({} > {})",
                    name, this.retryStatus.name(), retryStatus.name()
            );
        }
        this.retryStatus = retryStatus;
    }

    @Override
    public String toString() {
        return "RetryUnit{" +
                "name='" + name + '\'' +
                ", retryCountLimit=" + retryCountLimit +
                ", curRetryCount=" + curRetryCount +
                ", retryStatus=" + retryStatus +
                '}';
    }
}
