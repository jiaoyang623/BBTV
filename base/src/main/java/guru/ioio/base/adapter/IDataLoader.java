package guru.ioio.base.adapter;

import java.util.List;

/**
 * Created by daniel on 9/29/17.
 * Used for adapter
 */

public interface IDataLoader<T> {
    List<T> getAll();

    int getCount();

    boolean hasMore();

    void request();

    void more();

    void setListener(OnLoadListener l);

    interface OnLoadListener {
        void onLoadStart(IDataLoader loader);

        void onLoad(IDataLoader loader, Object data, int start);

        void onError(int errorCode, String message);
    }
}
