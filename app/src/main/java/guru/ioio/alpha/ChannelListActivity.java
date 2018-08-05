package guru.ioio.alpha;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.net.URI;
import java.util.List;

import guru.ioio.alpha.databinding.ActivityChannelListBinding;
import guru.ioio.alpha.model.ChannelBean;
import guru.ioio.alpha.model.ChannelRoot;
import guru.ioio.alpha.player.PlayerActivity;
import guru.ioio.base.adapter.RVBindingBaseAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChannelListActivity extends BaseActivity {
    public ObservableBoolean isLoading = new ObservableBoolean(true);
    private ActivityChannelListBinding mBinding;
    private RVBindingBaseAdapter<ChannelBean> mAdapter;

    public static void launch(Context context) {
        context.startActivity(new Intent(context, ChannelListActivity.class));
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_channel_list);
        mBinding.setPresenter(this);
        mAdapter = new RVBindingBaseAdapter<>(R.layout.item_channel, guru.ioio.alpha.BR.data);
        mAdapter.addPresenter(guru.ioio.alpha.BR.presenter, this);
        mBinding.recycler.setLayoutManager(new GridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false));
        mBinding.recycler.setAdapter(mAdapter);

        loadChannels();
    }


    @SuppressLint("CheckResult")
    private void loadChannels() {
        isLoading.set(true);
        Observable.create(s -> {
            final String uri = "https://raw.githubusercontent.com/jiaoyang623/BBTV/master/app/src/main/assets/channels.json?t=" + System.currentTimeMillis();
            String data = IOUtils.toString(URI.create(uri), "utf-8");
            Gson gson = new Gson();
            ChannelRoot root = gson.fromJson(data, ChannelRoot.class);
            if (root != null && root.list != null) {
                s.onNext(root.list);
            } else {
                s.onError(new Exception("no data"));
            }
            s.onComplete();
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> mAdapter.add((List<ChannelBean>) list), e -> {
                }, () -> isLoading.set(false));
    }

    public boolean onItemClick(ChannelBean bean) {
        PlayerActivity.launch(this, bean.uri.get(0));
        return true;
    }
}
