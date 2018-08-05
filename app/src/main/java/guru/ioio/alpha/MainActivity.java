package guru.ioio.alpha;

import android.databinding.DataBindingUtil;
import android.os.Bundle;

import guru.ioio.alpha.databinding.ActivityMainBinding;

public class MainActivity extends BaseActivity {
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setPresenter(this);
        ChannelListActivity.launch(this);
        finish();
    }

    public boolean onPlayClick() {
        ChannelListActivity.launch(this);
        return true;
    }
}
