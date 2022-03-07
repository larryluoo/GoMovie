package im.zego.gomovie.server.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import im.zego.gomovie.server.model.MovieInfo;
import im.zego.gomovie.server.R;

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieListViewHolder> {

    private Listener listener;
    private List<MovieInfo> mData;
    private Context context;

    public MovieListAdapter(Context context, List<MovieInfo> mData) {
        this.context = context;
        this.mData = mData;
    }

    public MovieListAdapter setListener(Listener listener) {
        this.listener = listener;
        return this;
    }

    @NonNull
    @Override
    public MovieListAdapter.MovieListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_list_item, parent, false);
        return new MovieListAdapter.MovieListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieListAdapter.MovieListViewHolder holder, int position) {

        MovieInfo mMovieInfo = mData.get(holder.getAdapterPosition());

        holder.commonItemView.setText(mMovieInfo.getMovieName());

        if (mMovieInfo.isCheck()) {
            holder.selectIcon.setVisibility(View.VISIBLE);
        } else {
            holder.selectIcon.setVisibility(View.GONE);
        }
        /* 最后一行不需要横线 */
        boolean show = true;
        if (mData != null && mData.size() > 0) {
            MovieInfo lastItemdata = mData.get(mData.size() - 1);
            if (lastItemdata == mMovieInfo) {
                show = false;
            }
        }

        holder.itemLineView.setVisibility(show ? View.VISIBLE : View.GONE);

        holder.commonItemView.setOnClickListener(v -> {
            if (!mMovieInfo.isCheck()) {
                setDataCheck();
                mMovieInfo.setCheck(true);
                notifyDataSetChanged();
                if (listener != null) {
                    listener.change(position);
                }
            }
        });

    }

    /**
     * 选中归位
     */
    private void setDataCheck() {
        for (int i = 0; i < mData.size(); i++) {
            MovieInfo mMovieInfo = mData.get(i);
            if (mMovieInfo.isCheck()) {
                mMovieInfo.setCheck(false);
            }
        }
    }

    @Override
    public int getItemCount() {
        if (mData == null) {
            return 0;
        }
        return mData.size();
    }

    static class MovieListViewHolder extends RecyclerView.ViewHolder {

        private TextView commonItemView;
        private ImageView selectIcon;
        private View itemLineView;

        private MovieListViewHolder(View itemView) {
            super(itemView);
            commonItemView = itemView.findViewById(R.id.common_item_view);
            selectIcon = itemView.findViewById(R.id.tiem_select_icon);
            itemLineView = itemView.findViewById(R.id.item_common_line_view);
        }
    }

    public interface Listener {
        void change(int position);
    }

}
