package guru.ioio.alpha.player;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.WindowManager;

import com.fastaccess.permission.base.PermissionHelper;
import com.fastaccess.permission.base.callback.OnPermissionCallback;

import guru.ioio.alpha.BaseActivity;
import guru.ioio.alpha.R;
import guru.ioio.alpha.databinding.ActivityPlayerBinding;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

public class PlayerActivity extends BaseActivity implements OnPermissionCallback {
    public ObservableBoolean isLoading = new ObservableBoolean(true);
    private ActivityPlayerBinding mBinding;
    private static final String KEY_URI = "uri";
    private String mPlayUri = null;

    public static void launch(Context context, String uri) {
        Intent intent = new Intent(context, PlayerActivity.class);
        intent.putExtra(KEY_URI, uri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT > 24) {
            checkPermission();
        } else {
            init();
        }
    }

    private void init() {
        Intent intent = getIntent();
        mPlayUri = intent.getStringExtra(KEY_URI);
        if (TextUtils.isEmpty(mPlayUri)) {
            finish();
            show("资源错误");
            return;
        }
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        mBinding.setPresenter(this);

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Exception e) {
            this.finish();
        }

        mBinding.player.setListener(new VideoPlayerListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                iMediaPlayer.start();
                isLoading.set(false);
            }

        });

        mBinding.player.setVideoPath(mPlayUri);
    }

    private String[] PERMISSIONS = {
            "android.permission.WRITE_EXTERNAL_STORAGE"
    };

    private void checkPermission() {
        PermissionHelper.getInstance(this)
                .setForceAccepting(false)// true if you had like force reshowing the permission dialog on Deny (not recommended)
                .request(PERMISSIONS);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBinding != null) {
            mBinding.player.pause();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mBinding != null && mBinding.player.isPlaying()) {
            mBinding.player.start();
        }
    }

    @Override
    protected void onStop() {
        if (mBinding != null) {
            IjkMediaPlayer.native_profileEnd();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBinding != null) {
            mBinding.player.release();
        }
    }

    @Override
    public void onPermissionGranted(@NonNull String[] permissionName) {
        init();
    }

    @Override
    public void onPermissionDeclined(@NonNull String[] permissionName) {
        finish();
    }

    @Override
    public void onPermissionPreGranted(@NonNull String permissionsName) {
    }

    @Override
    public void onPermissionNeedExplanation(@NonNull String permissionName) {
    }

    @Override
    public void onPermissionReallyDeclined(@NonNull String permissionName) {
    }

    @Override
    public void onNoPermissionNeeded() {
    }
}
