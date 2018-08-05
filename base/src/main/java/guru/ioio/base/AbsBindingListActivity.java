package guru.ioio.base;

import android.app.Activity;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

import guru.ioio.base.adapter.IDataLoader;
import guru.ioio.base.adapter.RVBindingBaseAdapter;
import guru.ioio.base.databinding.ActivityAbslistBinding;


/**
 * Created by daniel on 9/29/17.
 * list to show list
 */

public abstract class AbsBindingListActivity<T> extends Activity implements IDataLoader.OnLoadListener, SwipeRefreshLayout.OnRefreshListener {
    private RVBindingBaseAdapter<T> adapter;
    protected ActivityAbslistBinding binding;
    protected IDataLoader<T> loader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_abslist);
        binding.recycler.setLayoutManager(getLayoutManager());
        binding.swipe.setOnRefreshListener(this);

        loader = getLoader();
        loader.setListener(this);

        binding.setPresenter(this);
        adapter = getAdapter();
        binding.recycler.setAdapter(adapter);

        loader.request();
    }

    protected abstract RVBindingBaseAdapter<T> getAdapter();


    public abstract RecyclerView.LayoutManager getLayoutManager();

    public abstract IDataLoader<T> getLoader();

    @Override
    public void onLoadStart(IDataLoader loader) {
        binding.swipe.setRefreshing(true);
    }

    @Override
    public void onLoad(IDataLoader loader, Object data, int start) {

        binding.swipe.setRefreshing(false);
        List<T> list = (List<T>) data;
        if (start == 0) {
            adapter.clear();
            binding.emptyView.setVisibility(list.size() > 0 ? View.GONE : View.VISIBLE);
        }
        adapter.addToTail(list);
    }

    @Override
    public void onError(int errorCode, String message) {
        binding.swipe.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        loader.request();
    }
}
