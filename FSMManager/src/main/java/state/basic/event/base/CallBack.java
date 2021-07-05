package state.basic.event.base;

/**
 * @interface public class CallBack
 * @brief CallBack class
 */
public class CallBack {

    private String name;

    public CallBack(String name) {
        this.name = name;
    }

    public Object callBackFunc(Object... objects) {
        // Must embody this function.
        return null;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
