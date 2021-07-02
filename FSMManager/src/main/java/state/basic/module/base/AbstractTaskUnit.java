package state.basic.module.base;

/**
 * @author jamesj
 * @class public abstract class AbstractTaskUnit implements Runnable
 * @brief Task Unit Abstract Class
 */
public abstract class AbstractTaskUnit implements Runnable {

    private int interval;

    protected AbstractTaskUnit(int interval) {
        this.interval = interval;
    }

    public int getInterval ( ) {
        return interval;
    }

    public void setInterval (int interval) {
        this.interval = interval;
    }
}
