package im.zego.gomovie.client.movieroom.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import im.zego.gomovie.client.R;
import im.zego.zegoexpress.entity.ZegoBroadcastMessageInfo;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    private List<ZegoBroadcastMessageInfo> mData = new ArrayList<>();
    private Context context;

    public ChatAdapter(Context context) {
        this.context = context;
    }

    public void addListData(List<ZegoBroadcastMessageInfo> newData) {
        mData.addAll(newData);
        notifyDataSetChanged();
    }

    public void addBeanData(ZegoBroadcastMessageInfo newData) {
        mData.add(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_chat_item_msg, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatAdapter.ChatViewHolder holder, int position) {

        ZegoBroadcastMessageInfo messageInfo = mData.get(holder.getAdapterPosition());

        holder.mMsgOwner.setText(messageInfo.fromUser.userName);
        holder.mContent.setText(messageInfo.message);

    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView mMsgOwner;
        private TextView mContent;

        private ChatViewHolder(View itemView) {
            super(itemView);
            mMsgOwner = itemView.findViewById(R.id.msg_owner);
            mContent = itemView.findViewById(R.id.msg);
        }
    }

}
