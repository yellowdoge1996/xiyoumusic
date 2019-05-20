package com.xiyoumusic.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.utils.ConstantTools;

import java.util.List;

public class MusicRecyclerViewAdapter extends RecyclerView.Adapter<MusicRecyclerViewAdapter.MusicViewHolder> {
    List<Music> musics;
    Context context;
    private OnItemClickLitener mOnItemClickLitener;

    public interface OnItemClickLitener{
        void onItemClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener){
        this.mOnItemClickLitener = mOnItemClickLitener;
    }
    public MusicRecyclerViewAdapter(List<Music> musics, Context context) {
        this.musics = musics;
        this.context = context;
    }

    @NonNull
    @Override
    public MusicRecyclerViewAdapter.MusicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.music_card, parent, false);
        MusicViewHolder musicViewHolder = new MusicViewHolder(view);
        return musicViewHolder;
    }

    public void onBindViewHolder(@NonNull MusicViewHolder holder, final int position) {
        Music music = musics.get(position);
        String gqct = music.getGqct();
        String gqmc = music.getGqmc();
        String zzxh = music.getZzxh();
        if (gqct == null || "".equals(gqct)){
            holder.gqct.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_launcher_foreground));
        }else{
            Glide.with(context)
                    .load(ConstantTools.baseUrl+gqct)
                    .into(holder.gqct);
        }
        holder.gqmc.setText(gqmc);
        holder.zzxh.setText(zzxh);

        //通过为条目设置点击事件触发回调
        if (mOnItemClickLitener != null) {
            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mOnItemClickLitener.onItemClick(view, position);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return musics.size();
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public void refresh(List<Music> musics){
        this.musics = musics;
        notifyDataSetChanged();
    }

    public static class MusicViewHolder extends RecyclerView.ViewHolder{
        CardView cardView;
        ImageView gqct;
        TextView gqmc;
        TextView zzxh;
        public MusicViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.music_card);
            gqct = itemView.findViewById(R.id.info_gqct);
            gqmc = itemView.findViewById(R.id.info_gqmc);
            zzxh = itemView.findViewById(R.id.info_zzxh);
        }
    }
}
