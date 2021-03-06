package guru.ioio.base.frame;

import android.app.Application;

import guru.ioio.base.utils.ContextUtils;

/**
 * Created by daniel on 9/29/17.
 */

public class BaseApp extends Application {
    public static BaseApp INSTANCE = null;

    public static BaseApp getInstance() {
        return INSTANCE;
    }

    public BaseApp() {
        INSTANCE = this;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ContextUtils.setContext(this);
    }
}
