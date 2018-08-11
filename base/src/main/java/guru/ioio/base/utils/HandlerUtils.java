package guru.ioio.base.utils;

import android.os.Handler;
import android.os.Looper;

public class HandlerUtils {
    private static Handler sMainHandler;

    public static Handler getMain() {
        if (sMainHandler == null) {
            sMainHandler = new Handler(Looper.getMainLooper());
        }
        return sMainHandler;
    }
}
