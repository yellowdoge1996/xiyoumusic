package com.xiyoumusic.app.adapter;

import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.LocalMusic;
import com.xiyoumusic.app.service.AudioPlayer;
import com.xiyoumusic.app.utils.CoverLoader;
import com.xiyoumusic.app.utils.FileTools;

import java.util.List;

/**
 * 本地音乐列表适配器
 * Created by wcy on 2015/11/27.
 */
public class PlaylistAdapter extends BaseAdapter {
    private List<LocalMusic> musicList;
    private OnMoreClickListener listener;
    private boolean isPlaylist;

    public PlaylistAdapter(List<LocalMusic> musicList) {
        this.musicList = musicList;
    }

    public void setIsPlaylist(boolean isPlaylist) {
        this.isPlaylist = isPlaylist;
    }

    public void setOnMoreClickListener(OnMoreClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return musicList.size();
    }

    @Override
    public Object getItem(int position) {
        return musicList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_holder_music, parent, false);
            holder = new ViewHolder();
            holder.ivCover = convertView.findViewById(R.id.iv_cover);
            holder.ivMore = convertView.findViewById(R.id.iv_more);
            holder.tvArtist = convertView.findViewById(R.id.tv_artist);
            holder.vDivider = convertView.findViewById(R.id.v_divider);
            holder.tvTitle = convertView.findViewById(R.id.tv_title);
            holder.vPlaying = convertView.findViewById(R.id.v_playing);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.vPlaying.setVisibility((isPlaylist && position == AudioPlayer.get().getPlayPosition()) ? View.VISIBLE : View.INVISIBLE);
        LocalMusic music = musicList.get(position);
        Bitmap cover = CoverLoader.get().loadThumb(music);
        holder.ivCover.setImageBitmap(cover);
        holder.tvTitle.setText(music.getTitle());
        String artist = FileTools.getArtistAndAlbum(music.getArtist(), music.getAlbum());
        holder.tvArtist.setText(artist);
        holder.ivMore.setOnClickListener(v -> {
            if (listener != null) {
                listener.onMoreClick(position);
            }
        });
        holder.vDivider.setVisibility(isShowDivider(position) ? View.VISIBLE : View.GONE);
        return convertView;
    }

    private boolean isShowDivider(int position) {
        return position != musicList.size() - 1;
    }

    private static class ViewHolder {
        private View vPlaying;
        private ImageView ivCover;
        private TextView tvTitle;
        private TextView tvArtist;
        private ImageView ivMore;
        private View vDivider;
    }
}
