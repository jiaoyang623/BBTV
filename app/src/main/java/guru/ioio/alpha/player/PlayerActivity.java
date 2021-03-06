package guru.ioio.alpha.player;


import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.view.Gravity;
import android.view.KeyEvent;

import guru.ioio.alpha.BaseActivity;
import guru.ioio.alpha.R;
import guru.ioio.alpha.databinding.ActivityPlayerBinding;

public class PlayerActivity extends BaseActivity {
    private ActivityPlayerBinding mBinding;
    private PlayerFragment mPlayerFragment;
    private ChannelFragment mChannelFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_player);
        mBinding.setPresenter(this);

        mPlayerFragment = new PlayerFragment();
        mChannelFragment = new ChannelFragment();
        mChannelFragment.setOnChannelClickListener(bean -> mPlayerFragment.play(bean.uri));

        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.player_container, mPlayerFragment)
                .add(R.id.channel_container, mChannelFragment)
                .commit();
        open();
    }

    private void open() {
        if (mBinding != null) {
            mBinding.drawer.openDrawer(Gravity.LEFT);
            mChannelFragment.loadSet();
        }
    }

    private void close() {
        if (mBinding != null) {
            mBinding.drawer.closeDrawer(Gravity.LEFT);
            mBinding.playerContainer.requestFocus();
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mBinding != null) {
            if (mBinding.drawer.isDrawerOpen(Gravity.LEFT)) {
                // open
                switch (keyCode) {
                    case KeyEvent.KEYCODE_MENU:
                    case KeyEvent.KEYCODE_BACK:
                        close();
                        return true;
                }
            } else {
                // close
                switch (keyCode) {
                    case KeyEvent.KEYCODE_DPAD_UP:
                        mChannelFragment.upChannel();
                        break;
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        mChannelFragment.downChannel();
                        break;
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                        mPlayerFragment.preSource();
                        break;
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                        mPlayerFragment.nextSource();
                        break;
                    case KeyEvent.KEYCODE_DPAD_CENTER:
                    case KeyEvent.KEYCODE_MENU:
                        open();
                        return true;
                }

            }
        }
        return super.onKeyUp(keyCode, event);
    }
}
