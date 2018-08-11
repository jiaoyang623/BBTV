package guru.ioio.base.utils;

import android.content.Context;

public class ContextUtils {

    private static Context sContext = null;

    public static Context getContext() {
        return sContext;
    }

    public static void setContext(Context context) {
        sContext = context.getApplicationContext();
    }
}
