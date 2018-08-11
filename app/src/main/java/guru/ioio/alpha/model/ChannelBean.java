package guru.ioio.alpha.model;

import android.databinding.ObservableBoolean;

import java.util.List;

public class ChannelBean {
    public String name;
    public List<String> uri;
    public ObservableBoolean isSelected = new ObservableBoolean(false);
}
