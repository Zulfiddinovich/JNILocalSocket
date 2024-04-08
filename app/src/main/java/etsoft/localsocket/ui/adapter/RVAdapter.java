package etsoft.localsocket.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import etsoft.localsocket.R;
import etsoft.localsocket.uidata.SocketData;

/**
 * Author: Zukhriddin Kamolov
 * Date: 05-Apr-24, 12:10 PM.
 * Project: localSocket
 */

public class RVAdapter extends ListAdapter<SocketData, RVAdapter.MyViewHolder> {

    public RVAdapter() {
        super(diff);
    }

    static DiffUtil.ItemCallback<SocketData> diff  = new DiffUtil.ItemCallback<SocketData>() {
        @Override
        public boolean areItemsTheSame(@NonNull SocketData oldItem, @NonNull SocketData newItem) {
            return oldItem.hashCode() == newItem.hashCode();
        }

        @Override
        public boolean areContentsTheSame(@NonNull SocketData oldItem, @NonNull SocketData newItem) {
            return oldItem.id == newItem.id;
        }
    };


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.onBind(getCurrentList().get(position));
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{
        TextView id, from, to, message;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            id = itemView.findViewById(R.id.id);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);
            message = itemView.findViewById(R.id.message);
        }
        void onBind(SocketData data){
            id.setText(data.id + ".");
            from.setText(String.valueOf(data.from));
            to.setText(String.valueOf(data.to));
            message.setText(String.valueOf(data.message));
        }
    }

    @Override
    public void submitList(@Nullable List<SocketData> list) {
        super.submitList(list);
    }
}
