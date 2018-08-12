package guru.ioio.alpha.player;

import android.annotation.SuppressLint;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;

import org.apache.commons.io.IOUtils;

import java.net.URI;
import java.nio.channels.Channels;
import java.util.List;

import guru.ioio.alpha.BR;
import guru.ioio.alpha.BaseFragment;
import guru.ioio.alpha.R;
import guru.ioio.alpha.databinding.FragmentChannelBinding;
import guru.ioio.alpha.model.ChannelBean;
import guru.ioio.alpha.model.ChannelRoot;
import guru.ioio.alpha.model.ChannelSet;
import guru.ioio.alpha.model.ChannelSetRoot;
import guru.ioio.alpha.utils.PreferenceUtils;
import guru.ioio.base.adapter.RVBindingBaseAdapter;
import guru.ioio.base.utils.HandlerUtils;
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
    private RVBindingBaseAdapter<ChannelSet> mSetAdapter;
    private RVBindingBaseAdapter<ChannelBean> mChannelAdapter;

    private void init(LayoutInflater inflater, ViewGroup container) {
        if (mBinding != null) {
            return;
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_channel, container, false);
        mBinding.setPresenter(this);
        // set
        mSetAdapter = new RVBindingBaseAdapter<>(R.layout.item_set, guru.ioio.alpha.BR.data);
        mSetAdapter.addPresenter(BR.presenter, this);
        mBinding.setsRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        mBinding.setsRecycler.setAdapter(mSetAdapter);
        // channels
        mChannelAdapter = new RVBindingBaseAdapter<>(R.layout.item_channel, guru.ioio.alpha.BR.data);
        mChannelAdapter.addPresenter(guru.ioio.alpha.BR.presenter, this);
        mBinding.channelRecycler.setLayoutManager(new GridLayoutManager(getActivity(), 1, GridLayoutManager.VERTICAL, false));
        mBinding.channelRecycler.setAdapter(mChannelAdapter);

        loadChannels();
    }


    @SuppressLint("CheckResult")
    private void loadChannels() {
        isLoading.set(true);
        Observable.create(s -> {
            final String uri = getString(R.string.channel_list_uri) + System.currentTimeMillis();
            String data = IOUtils.toString(URI.create(uri), "utf-8");
            Gson gson = new Gson();
            ChannelSetRoot root = gson.fromJson(data, ChannelSetRoot.class);
            if (root != null && root.list != null) {
                s.onNext(root.list);
            } else {
                s.onError(new Exception("no data"));
            }
            s.onComplete();
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(
                list -> {
                    mSetAdapter.add((List<ChannelSet>) list);
                    HandlerUtils.getMain().postDelayed(ChannelFragment.this::loadSet, 100);
                },
                e -> {
                },
                () -> isLoading.set(false));
    }


    public void loadSet() {
        if (mBinding == null) {
            return;
        }
        if (mPlayingBean != null) {
            int index = mChannelAdapter.indexOf(mPlayingBean);
            if (index >= 0) {
                View v = mBinding.channelRecycler.getLayoutManager().findViewByPosition(index);
                if (v != null) {
                    v.requestFocus();
                    return;
                }
            }
        }

        String lastSet = PreferenceUtils.get().getString(KEY_SET, null);
        int pos = 0;
        if (!TextUtils.isEmpty(lastSet)) {
            for (int i = mSetAdapter.getItemCount() - 1; i != -1; i--) {
                if (lastSet.equals(mSetAdapter.getItem(i).name)) {
                    pos = i;
                    break;
                }
            }
        }
        mBinding.setsRecycler.scrollToPosition(pos);
        View v = mBinding.setsRecycler.getLayoutManager().findViewByPosition(pos);
        if (v != null) {
            v.requestFocus();
        }
    }

    private static final String KEY_CHANNEL = "guru.ioio.alpha.player.channel";
    private static final String KEY_SET = "guru.ioio.alpha.player.set";

    private ChannelSet mPlayingSet = null;

    public void onItemFocus(ChannelSet bean, boolean isSelected) {
        if (isSelected) {
            if (mPlayingSet != null) {
                mPlayingSet.isSelected.set(false);
            }
            mPlayingSet = bean;
            bean.isSelected.set(true);
            PreferenceUtils.edit().putString(KEY_SET, bean.name).apply();
            mChannelAdapter.set(bean.channels);
            HandlerUtils.getMain().post(this::findChannelFocus);
        }
    }

    private void findChannelFocus() {
        int pos = 0;
        String lastChannel = PreferenceUtils.get().getString(KEY_CHANNEL, null);
        if (!TextUtils.isEmpty(lastChannel)) {
            for (int i = mChannelAdapter.getItemCount() - 1; i != -1; i--) {
                if (lastChannel.equals(mChannelAdapter.getItem(i).name)) {
                    pos = i;
                    break;
                }
            }
        }
        mBinding.channelRecycler.scrollToPosition(pos);
        View v = mBinding.channelRecycler.getLayoutManager().findViewByPosition(pos);
        if (v != null) {
            if (mPlayingBean == null) {
                v.requestFocus();
                v.performClick();
            }
        }
    }

    private ChannelBean mPlayingBean = null;

    public boolean onItemClick(ChannelBean bean) {
        if (mPlayingBean != null) {
            mPlayingBean.isSelected.set(false);
        }
        mPlayingBean = bean;
        bean.isSelected.set(true);

        PreferenceUtils.edit().putString(KEY_CHANNEL, bean.name).apply();

        if (mOnChannelClickListener != null) {
            mOnChannelClickListener.onChannelClick(bean);
        }

        return true;
    }

    private int findSelected() {
        for (int i = mChannelAdapter.getItemCount() - 1; i != -1; i--) {
            if (mChannelAdapter.getItem(i).isSelected.get()) {
                return i;
            }
        }
        return -1;
    }

    public void upChannel() {
        int pos = findSelected();
        if (pos > 0 && pos < mChannelAdapter.getItemCount()) {
            setSelection(pos - 1);
        }
    }

    public void downChannel() {
        int pos = findSelected();
        if (pos > 0 && pos < mChannelAdapter.getItemCount() - 1) {
            setSelection(pos + 1);
        }
    }

    public void setSelection(int selection) {
        onItemClick(mChannelAdapter.getItem(selection));
    }

    public interface OnChannelClickListener {
        void onChannelClick(ChannelBean bean);
    }

    private OnChannelClickListener mOnChannelClickListener = null;

    public void setOnChannelClickListener(OnChannelClickListener l) {
        mOnChannelClickListener = l;
    }
}
