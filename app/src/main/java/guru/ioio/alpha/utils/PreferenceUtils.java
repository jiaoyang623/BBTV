package guru.ioio.alpha.utils;

import android.content.Context;
import android.content.SharedPreferences;

import guru.ioio.base.utils.ContextUtils;

public class PreferenceUtils {
    private static final String KEY_NAME = "main_pref";

    public static SharedPreferences get() {
        return ContextUtils.getContext().getSharedPreferences(KEY_NAME, Context.MODE_PRIVATE);
    }

    public static SharedPreferences.Editor edit() {
        return get().edit();
    }
}
