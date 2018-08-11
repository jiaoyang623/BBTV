package guru.ioio.alpha.player;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import guru.ioio.alpha.BaseFragment;
import guru.ioio.alpha.R;
import guru.ioio.alpha.databinding.FragmentPlayerBinding;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerFragment extends BaseFragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        init(inflater, container);
        if (mPlayUri != null && mPlayUri.size() > uriPosition.get()) {
            mBinding.player.setVideoPath(mPlayUri.get(uriPosition.get()));
        }
        return mBinding.getRoot();
    }


    public ObservableBoolean isLoading = new ObservableBoolean(true);
    private FragmentPlayerBinding mBinding;
    private List<String> mPlayUri = new ArrayList<>();
    public ObservableInt uriCount = new ObservableInt(0);
    public ObservableInt uriPosition = new ObservableInt(0);

    private void init(LayoutInflater inflater, ViewGroup container) {
        if (mBinding != null) {
            return;
        }
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_player, container, false);
        mBinding.setPresenter(this);

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {

        }

        mBinding.player.setListener(new VideoPlayerListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                iMediaPlayer.start();
                isLoading.set(false);
            }

        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBinding != null) {
            IjkMediaPlayer.native_profileEnd();
            mBinding.player.release();
        }
    }

    public void play(List<String> uriList) {
        if (uriList == null || uriList == mPlayUri) {
            return;
        }
        uriPosition.set(0);
        uriCount.set(uriList.size());
        mPlayUri = uriList;
        if (mBinding != null) {
            mBinding.player.setVideoPath(mPlayUri.get(uriPosition.get()));
        }
    }

    public void preSource() {
        int pos = uriPosition.get() - 1;
        if (pos < 0) {
            pos += uriCount.get();
        }
        uriPosition.set(pos);
        if (mBinding != null) {
            mBinding.player.setVideoPath(mPlayUri.get(uriPosition.get()));
        }
    }

    public void nextSource() {
        int pos = uriPosition.get() + 1;
        if (pos >= uriCount.get()) {
            pos -= uriCount.get();
        }
        uriPosition.set(pos);
        if (mBinding != null) {
            mBinding.player.setVideoPath(mPlayUri.get(uriPosition.get()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mBinding != null) {
            mBinding.player.pause();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mBinding != null && mBinding.player.isPlaying()) {
            mBinding.player.start();
        }
    }
}
