package base.call.base;

/**
 * @class public class CallGlobalContext
 * @brief CallGlobalContext class
 */
public class CallGlobalContext {

    private String mdn;

    public CallGlobalContext(String mdn) {
        this.mdn = mdn;
    }

    public String getMdn() {
        return mdn;
    }

    public void setMdn(String mdn) {
        this.mdn = mdn;
    }
}
