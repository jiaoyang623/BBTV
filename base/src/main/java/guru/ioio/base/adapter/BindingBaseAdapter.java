package guru.ioio.base.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableInt;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by daniel on 17-4-13.
 * base binding adapter
 */

public class BindingBaseAdapter<T> extends BaseAdapter {
    private final int LAYOUT_ID;
    private final int VARIABLE_ID;
    public ObservableInt size = new ObservableInt(0);
    private List<T> mList = new ArrayList<>();
    private Map<Integer, Object> mPresenterMap = new HashMap<>();

    public BindingBaseAdapter(final int resId, final int valueId) {
        LAYOUT_ID = resId;
        VARIABLE_ID = valueId;
    }

    public BindingBaseAdapter setData(List<T> data) {
        mList.clear();
        if (data != null) {
            mList.addAll(data);
        }
        size.set(mList.size());
        notifyDataSetChanged();

        return this;
    }

    public BindingBaseAdapter add(T t) {
        mList.add(t);
        size.set(mList.size());
        notifyDataSetChanged();
        return this;
    }

    public BindingBaseAdapter add(int position, T t) {
        mList.add(position, t);
        size.set(mList.size());
        notifyDataSetChanged();
        return this;
    }

    public BindingBaseAdapter addPresenter(int variableId, Object presenter) {
        mPresenterMap.put(variableId, presenter);
        return this;
    }

    public BindingBaseAdapter removePresenter(int variableId) {
        if (mPresenterMap.containsKey(variableId)) {
            mPresenterMap.remove(variableId);
        }

        return this;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewDataBinding binding;
        if (convertView == null) {
            binding = DataBindingUtil.inflate(LayoutInflater.from(
                    parent.getContext()), LAYOUT_ID, parent, false);
            convertView = binding.getRoot();
            convertView.setTag(binding);
        } else {
            binding = (ViewDataBinding) convertView.getTag();
        }

        binding.setVariable(VARIABLE_ID, getItem(position));

        if (mPresenterMap.size() > 0) {
            for (Map.Entry<Integer, Object> entry : mPresenterMap.entrySet()) {
                binding.setVariable(entry.getKey(), entry.getValue());
            }
        }

        binding.executePendingBindings();

        return convertView;
    }
}
