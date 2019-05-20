package com.xiyoumusic.app.activitys;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.adapter.OnMoreClickListener;
import com.xiyoumusic.app.adapter.PlaylistAdapter;
import com.xiyoumusic.app.entity.LocalMusic;
import com.xiyoumusic.app.service.AudioPlayer;
import com.xiyoumusic.app.service.OnPlayerEventListener;


/**
 * 播放列表
 */
public class PlaylistActivity extends BaseActivity implements AdapterView.OnItemClickListener, OnMoreClickListener, OnPlayerEventListener {

    private ListView lvPlaylist;

    private PlaylistAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        lvPlaylist = findViewById(R.id.lv_playlist);
    }

    @Override
    protected void onServiceBound() {
        adapter = new PlaylistAdapter(AudioPlayer.get().getMusicList());
        adapter.setIsPlaylist(true);
        adapter.setOnMoreClickListener(this);
        lvPlaylist.setAdapter(adapter);
        lvPlaylist.setOnItemClickListener(this);
        AudioPlayer.get().addOnPlayEventListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        AudioPlayer.get().play(position);
    }

    @Override
    public void onMoreClick(int position) {
        String[] items = new String[]{"移除"};
        LocalMusic music = AudioPlayer.get().getMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setTitle(music.getTitle());
        dialog.setItems(items, (dialog1, which) -> {
            AudioPlayer.get().delete(position);
            adapter.notifyDataSetChanged();
        });
        dialog.show();
    }

    @Override
    public void onChange(LocalMusic music) {
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onPlayerStart() {
    }

    @Override
    public void onPlayerPause() {
    }

    @Override
    public void onPublish(int progress) {
    }

    @Override
    public void onBufferingUpdate(int percent) {
    }

    @Override
    protected void onDestroy() {
        AudioPlayer.get().removeOnPlayEventListener(this);
        super.onDestroy();
    }
}
