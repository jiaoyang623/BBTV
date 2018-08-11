package guru.ioio.alpha.player;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
        if (mPlayUri != null) {
            mBinding.player.setVideoPath(mPlayUri);
        }
        return mBinding.getRoot();
    }


    public ObservableBoolean isLoading = new ObservableBoolean(true);
    private FragmentPlayerBinding mBinding;
    private String mPlayUri = null;

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

    public void play(String uri) {
        if (mPlayUri != null && mPlayUri.equals(uri)) {
            return;
        }
        mPlayUri = uri;
        if (mBinding != null) {
            mBinding.player.setVideoPath(mPlayUri);
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
