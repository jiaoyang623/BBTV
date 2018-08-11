package guru.ioio.alpha.model;

import android.databinding.ObservableBoolean;

import java.util.List;

public class ChannelSet {
    public String name;
    public List<ChannelBean> channels;
    public ObservableBoolean isSelected = new ObservableBoolean(false);
}
