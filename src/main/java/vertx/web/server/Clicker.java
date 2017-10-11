package vertx.web.server;

public class Clicker {

    public static Clicker INSTANCE = new Clicker();
    private int clicksCount = 0;

    public static synchronized Clicker getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new Clicker();
        }
        return INSTANCE;
    }

    public int getClicksCount() {
        return clicksCount;
    }

    public void setClicksCount(int clicksCount) {
        this.clicksCount = clicksCount;
    }
}
