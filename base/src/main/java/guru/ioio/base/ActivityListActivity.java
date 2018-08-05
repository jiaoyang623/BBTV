package guru.ioio.base;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import guru.ioio.base.adapter.IDataLoader;
import guru.ioio.base.adapter.RVBindingBaseAdapter;

/**
 * Created by daniel on 9/29/17.
 * to list activities in this app
 */

public class ActivityListActivity extends AbsBindingListActivity<ActivityInfo> {

    @Override
    protected RVBindingBaseAdapter<ActivityInfo> getAdapter() {
        RVBindingBaseAdapter<ActivityInfo> adapter = new RVBindingBaseAdapter<>(R.layout.item_activity, guru.ioio.base.BR.data);
        adapter.addPresenter(guru.ioio.base.BR.presenter, this);
        return adapter;
    }

    @Override
    public RecyclerView.LayoutManager getLayoutManager() {
        return new GridLayoutManager(this, 1, RecyclerView.VERTICAL, false);
    }

    @Override
    public IDataLoader<ActivityInfo> getLoader() {
        return mLoader;
    }

    private IDataLoader<ActivityInfo> mLoader = new IDataLoader<ActivityInfo>() {
        private List<ActivityInfo> list = new ArrayList<>();
        private OnLoadListener listener;

        @Override
        public List<ActivityInfo> getAll() {
            return new ArrayList<>(list);
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean hasMore() {
            return false;
        }

        private AsyncTask<Void, Void, List<ActivityInfo>> mTask = null;

        @Override
        public void request() {
            if (list.size() > 0) {
                if (listener != null) {
                    listener.onLoad(this, list, 0);
                }
            } else if (mTask == null) {
                mTask = new AsyncTask<Void, Void, List<ActivityInfo>>() {
                    @Override
                    protected List<ActivityInfo> doInBackground(Void... voids) {
                        return getActivities();
                    }

                    @Override
                    protected void onPostExecute(List<ActivityInfo> data) {
                        mTask = null;
                        list.addAll(data);
                        if (listener != null) {
                            listener.onLoad(mLoader, data, 0);
                        }
                    }
                };
                mTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            }
        }

        @Override
        public void more() {
            if (listener != null) {
                listener.onLoad(mLoader, new ArrayList<ActivityInfo>(), list.size());
            }
        }

        @Override
        public void setListener(OnLoadListener l) {
            listener = l;
        }
    };

    private List<ActivityInfo> getActivities() {
        try {
            PackageInfo packageInfo = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_ACTIVITIES);
            List<ActivityInfo> list = new ArrayList<>(packageInfo.activities.length);
            for (ActivityInfo info : packageInfo.activities) {
                if (ActivityListActivity.class.getName().equals(info.name)) {
                    continue;
                }
                list.add(info);
            }
            return list;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public boolean onItemClick(ActivityInfo info) {
        Intent intent = new Intent();
        intent.setClassName(getApplicationContext(), info.name);
        startActivity(intent);

        return true;
    }
}
