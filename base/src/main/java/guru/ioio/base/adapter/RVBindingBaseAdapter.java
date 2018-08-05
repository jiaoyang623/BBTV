package guru.ioio.base.adapter;

import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jiaoyang on 16/06/2017.
 * base adapter for RecyclerView.Adapter
 */

public class RVBindingBaseAdapter<T> extends RecyclerView.Adapter<RVBindingBaseAdapter.RViewHolder> {
    private final int sLayoutId, sValueId;
    protected List<T> mList = new ArrayList<>();
    private Map<Integer, Object> mPresenterMap = new HashMap<>();

    public RVBindingBaseAdapter<T> set(List<T> data) {
        mList.clear();
        if (data != null) {
            mList.addAll(data);
            notifyItemRangeChanged(0, mList.size());
        }

        return this;
    }

    public RVBindingBaseAdapter<T> addToTail(T t) {
        return add(t, mList.size());
    }

    public RVBindingBaseAdapter<T> addToHead(T t) {
        return add(t, 0);
    }


    public RVBindingBaseAdapter<T> add(T t, int position) {
        if (t != null && !mList.contains(t)) {
            mList.add(position, t);
            notifyItemInserted(position);
        }
        return this;
    }

    public RVBindingBaseAdapter<T> add(List<T> data) {
        return add(data, mList.size());
    }

    public RVBindingBaseAdapter<T> add(List<T> data, int position) {
        if (data == null) {
            return this;
        }

        data.removeAll(mList);
        if (data.size() > 0) {
            mList.addAll(position, data);
            notifyItemRangeInserted(position, data.size());
        }
        return this;
    }

    public RVBindingBaseAdapter<T> remove(T t) {
        if (t != null) {
            int position = mList.indexOf(t);
            if (position >= 0) {
                mList.remove(position);
                notifyItemRemoved(position);
            }
        }
        return this;
    }

    public int indexOf(T t) {
        if (mList != null && t != null) {
            return mList.indexOf(t);
        } else {
            return -1;
        }
    }

    public void clear() {
        mList.clear();
        notifyDataSetChanged();
    }

    public RVBindingBaseAdapter<T> addToHead(List<T> data) {
        add(data, 0);
        return this;
    }

    public RVBindingBaseAdapter<T> addToTail(List<T> data) {
        add(data, mList.size());
        return this;
    }


    public RVBindingBaseAdapter<T> addPresenter(int variableId, Object presenter) {
        mPresenterMap.put(variableId, presenter);
        return this;
    }

    public RVBindingBaseAdapter<T> removePresenter(int variableId) {
        if (mPresenterMap.containsKey(variableId)) {
            mPresenterMap.remove(variableId);
        }

        return this;
    }

    public RVBindingBaseAdapter(int layoutId, int valueId) {
        sLayoutId = layoutId;
        sValueId = valueId;
    }

    @Override
    public RViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        ViewDataBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()), sLayoutId, parent, false);
        return new RViewHolder(binding, sValueId, mPresenterMap);
    }

    public void onBindViewHolder(RViewHolder holder, int position) {
        T t = mList.get(position);
        if (t instanceof Adaptable) {
            ((Adaptable) t).adapt(position, mList, holder.binding);
        }
        holder.bind(t);
    }

    @Override
    public int getItemViewType(int position) {
        T t = mList.get(position);
        if (t instanceof IType) {
            return ((IType) t).getType();
        } else {
            return super.getItemViewType(position);
        }
    }

    public T getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public static class RViewHolder extends RecyclerView.ViewHolder {
        private ViewDataBinding binding;
        private int valueId;
        private Map<Integer, Object> presenterMap;

        public RViewHolder(ViewDataBinding binding, int valueId, Map<Integer, Object> presenterMap) {
            super(binding.getRoot());
            this.binding = binding;
            this.valueId = valueId;
            this.presenterMap = presenterMap;
        }

        public RViewHolder(View v) {
            super(v);
        }

        public void bind(Object t) {
            binding.setVariable(valueId, t);
            if (presenterMap != null && presenterMap.size() > 0) {
                for (Map.Entry<Integer, Object> entry : presenterMap.entrySet()) {
                    binding.setVariable(entry.getKey(), entry.getValue());
                }
            }
            binding.executePendingBindings();
        }
    }

    public interface Adaptable<T> {
        void adapt(int position, List<T> list, ViewDataBinding binding);
    }

    public interface IType {
        int getType();
    }

    public interface OnItemClickListener {
        void onClick(View v, Object data);
    }
}
