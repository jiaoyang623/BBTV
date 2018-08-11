package guru.ioio.alpha.player;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.net.URI;
import java.util.List;

import guru.ioio.alpha.BaseFragment;
import guru.ioio.alpha.R;
import guru.ioio.alpha.databinding.FragmentChannelBinding;
import guru.ioio.alpha.model.ChannelBean;
import guru.ioio.alpha.model.ChannelRoot;
import guru.ioio.base.adapter.RVBindingBaseAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class ChannelFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init(inflater, container);
        return mBinding.getRoot();
    }

    public ObservableBoolean isLoading = new ObservableBoolean(true);
    private FragmentChannelBinding mBinding;
    private RVBindingBaseAdapter<ChannelBean> mAdapter;

    private void init(LayoutInflater inflater, ViewGroup container) {
        if (mBinding != null) {
            return;
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_channel, container, false);
        mBinding.setPresenter(this);
        mAdapter = new RVBindingBaseAdapter<>(R.layout.item_channel, guru.ioio.alpha.BR.data);
        mAdapter.addPresenter(guru.ioio.alpha.BR.presenter, this);
        mBinding.recycler.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
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
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                list -> {
                    mAdapter.add((List<ChannelBean>) list);

                },
                e -> {
                },
                () -> isLoading.set(false));
    }

    public boolean onItemClick(ChannelBean bean) {
        if (mOnChannelClickListener != null) {
            mOnChannelClickListener.onChannelClick(bean);
        }
        return true;
    }

    public interface OnChannelClickListener {
        void onChannelClick(ChannelBean bean);
    }

    private OnChannelClickListener mOnChannelClickListener = null;

    public void setOnChannelClickListener(OnChannelClickListener l) {
        mOnChannelClickListener = l;
    }
}
