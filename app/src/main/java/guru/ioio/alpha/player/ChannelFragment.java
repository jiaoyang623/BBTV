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
import java.util.List;

import guru.ioio.alpha.BaseFragment;
import guru.ioio.alpha.R;
import guru.ioio.alpha.databinding.FragmentChannelBinding;
import guru.ioio.alpha.model.ChannelBean;
import guru.ioio.alpha.model.ChannelRoot;
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
            final String uri = getString(R.string.channel_list_uri) + System.currentTimeMillis();
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
                    HandlerUtils.getMain().postDelayed(ChannelFragment.this::loadPosition, 100);
                },
                e -> {
                },
                () -> isLoading.set(false));
    }

    private void loadPosition() {
        String lastName = PreferenceUtils.get().getString(KEY_CHANNEL, null);
        int pos = 0;
        if (!TextUtils.isEmpty(lastName)) {
            for (int i = mAdapter.getItemCount() - 1; i != -1; i--) {
                ChannelBean bean = mAdapter.getItem(i);
                if (lastName.equals(bean.name)) {
                    pos = i;
                    break;
                }
            }
        }
        setSelection(pos);
    }

    public void setSelection(int position) {
        if (mBinding != null && mAdapter.getItemCount() > position) {
            onItemClick(mAdapter.getItem(position));
            refocus();
        }
    }

    public void refocus() {
        if (mBinding != null && mAdapter.getItemCount() > 0) {
            int pos = 0;
            for (ChannelBean bean : mAdapter.getAll()) {
                if (bean.isSelected.get()) {
                    break;
                } else {
                    pos++;
                }
            }
            if (pos >= mAdapter.getItemCount()) {
                pos = 0;
            }
            mBinding.recycler.smoothScrollToPosition(pos);
            int finalPos = pos;
            HandlerUtils.getMain().postDelayed(() -> {
                View v = mBinding.recycler.getLayoutManager().findViewByPosition(finalPos);
                if (v != null) {
                    v.requestFocus();
                }
            }, 200);
        }
    }

    private static final String KEY_CHANNEL = "guru.ioio.alpha.player.channel";

    public boolean onItemClick(ChannelBean bean) {
        for (ChannelBean b : mAdapter.getAll()) {
            b.isSelected.set(false);
        }
        bean.isSelected.set(true);

        PreferenceUtils.edit().putString(KEY_CHANNEL, bean.name).apply();

        if (mOnChannelClickListener != null) {
            mOnChannelClickListener.onChannelClick(bean);
        }

        return true;
    }

    private int findSelected() {
        for (int i = mAdapter.getItemCount() - 1; i != -1; i--) {
            if (mAdapter.getItem(i).isSelected.get()) {
                return i;
            }
        }
        return -1;
    }

    public void upChannel() {
        int pos = findSelected();
        if (pos > 0 && pos < mAdapter.getItemCount()) {
            setSelection(pos - 1);
        }
    }

    public void downChannel() {
        int pos = findSelected();
        if (pos > 0 && pos < mAdapter.getItemCount() - 1) {
            setSelection(pos + 1);
        }
    }

    public interface OnChannelClickListener {
        void onChannelClick(ChannelBean bean);
    }

    private OnChannelClickListener mOnChannelClickListener = null;

    public void setOnChannelClickListener(OnChannelClickListener l) {
        mOnChannelClickListener = l;
    }
}
