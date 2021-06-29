package com.example.video;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.localvideo.MediaBean;
import com.example.localvideo.OnAdapterItemClickListener;

import java.util.ArrayList;

/**
 * Created           :Herve on 2017/3/9.
 *
 * @ Author          :Herve
 * @ e-mail          :lijianyou.herve@gmail.com
 * @ LastEdit        :2017/3/9
 * @ projectName     :LocalMedia
 * @ version
 */
public class MediaItemAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private ArrayList<MediaBean> data;
    private OnAdapterItemClickListener onAdapterItemClickListener;
    private Context mContext;

    public MediaItemAdapter(Context mContext) {
        this.mContext = mContext;
    }

    public void setData(ArrayList<MediaBean> data) {
        this.data = data;
        notifyDataSetChanged();
    }

    @Override
    public MediaItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View contentView = LayoutInflater.from(mContext).inflate(R.layout.item_media_item_layout, parent, false);
        return new MediaItemViewHolder(contentView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        String filePath = data.get(position).getOriginalFilePath();
        MediaItemViewHolder viewHolder = (MediaItemViewHolder) holder;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (onAdapterItemClickListener != null) {
                    onAdapterItemClickListener.OnAdapterItemClickListener(holder, holder.itemView, position);
                }
            }
        });

        if (filePath.endsWith("mp4") || filePath.endsWith("jpg") || filePath.endsWith("png")) {
            Glide.with(mContext).load(data.get(position).getOriginalFilePath()).into(viewHolder.ivMediaCover);
        }
        String mediaLength = data.get(position).getMediaLength();
        long videoTime = Long.parseLong(mediaLength);
        String hTime = getDoubleTime("" + videoTime / (3600 * 1000));
        String mTime = getDoubleTime("" + videoTime % (3600 * 1000) / (60 * 1000));
        String sTime = getDoubleTime("" + videoTime % (3600 * 1000) % (60 * 1000) / 1000);
        viewHolder.tvVideoTime.setText(hTime + ":" + mTime + ":" + sTime);
    }

    private String getDoubleTime(String time) {
        if (TextUtils.isEmpty(time)) {
            time = "00";
        } else if (time.length() == 1) {
            time = "0" + time;
        }
        return time;
    }

    @Override
    public int getItemCount() {
        return data == null ? 0 : data.size();
    }

    public class MediaItemViewHolder extends RecyclerView.ViewHolder {
        ImageView ivMediaCover;
        TextView tvVideoTime;

        public MediaItemViewHolder(View itemView) {
            super(itemView);
            ivMediaCover = itemView.findViewById(R.id.iv_media_cover);
            tvVideoTime = itemView.findViewById(R.id.tv_video_time);
        }
    }

    public void setOnAdapterItemClickListener(OnAdapterItemClickListener onAdapterItemClickListener) {
        this.onAdapterItemClickListener = onAdapterItemClickListener;
    }

}
