package state.basic;

/**
 * @interface public class CallBack
 * @brief CallBack class
 */
public class CallBack {

    private String name;
    private Object result = null;

    public CallBack(String name) {
        this.name = name;
    }

    public void callBackFunc(Object... object) {
        // Must embody this function.
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }
}
