package com.xiyoumusic.app.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiyoumusic.app.AppCache;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.MusicInfoActivity;
import com.xiyoumusic.app.adapter.OnMoreClickListener;
import com.xiyoumusic.app.adapter.PlaylistAdapter;
import com.xiyoumusic.app.entity.LocalMusic;
import com.xiyoumusic.app.executor.MusicLoaderCallback;
import com.xiyoumusic.app.service.AudioPlayer;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.ToastUtil;

import java.io.File;

import io.reactivex.functions.Consumer;

/**
 * 本地音乐列表
 * Created by wcy on 2015/11/26.
 */
public class LocalMusicFragment extends Fragment implements AdapterView.OnItemClickListener, OnMoreClickListener {
    private ListView lvLocalMusic;
    private TextView vSearching;

    private Loader<Cursor> loader;
    private PlaylistAdapter adapter;
    String TAG = this.getClass().getSimpleName();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_local_music, container, false);
        lvLocalMusic = view.findViewById(R.id.lv_local_music);
        vSearching = view.findViewById(R.id.v_searching);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (adapter == null) {
            adapter = new PlaylistAdapter(AppCache.get().getLocalMusicList());
        }else{
            adapter = (PlaylistAdapter)lvLocalMusic.getAdapter();
        }
        adapter.setOnMoreClickListener(this);
        lvLocalMusic.setAdapter(adapter);
        if (adapter.getCount() == 0) {
            loadMusic();
        }
    }
    private void requestPermission() {
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .requestEach(
                        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        initLoader();
                        // 用户已经同意该权限
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        ToastUtil.error("你已经拒绝了文件读取权限");
                        lvLocalMusic.setVisibility(View.VISIBLE);
                        vSearching.setVisibility(View.GONE);
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        ToastUtil.error("你已经拒绝了文件读取权限");
                        lvLocalMusic.setVisibility(View.VISIBLE);
                        vSearching.setVisibility(View.GONE);
                    }
                });
    }
    private void loadMusic() {
        lvLocalMusic.setVisibility(View.GONE);
        vSearching.setVisibility(View.VISIBLE);
        requestPermission();
    }

    private void initLoader() {
        Log.d(TAG, "initLoader");
        loader = getActivity().getLoaderManager().initLoader(0, null, new MusicLoaderCallback(getContext(), value -> {
            AppCache.get().getLocalMusicList().clear();
            AppCache.get().getLocalMusicList().addAll(value);
            lvLocalMusic.setVisibility(View.VISIBLE);
            vSearching.setVisibility(View.GONE);
            adapter.notifyDataSetChanged();
        }));
    }

    @Override
    public void onStart() {
        super.onStart();
        setListener();
    }

//    @Subscribe(tags = { @Tag(RxBusTags.SCAN_MUSIC) })
//    public void scanMusic(Object object) {
//        if (loader != null) {
//            loader.forceLoad();
//        }
//    }

    @Override
    public void onResume() {
        super.onResume();

    }

    protected void setListener() {
        lvLocalMusic.setOnItemClickListener(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        LocalMusic music = AppCache.get().getLocalMusicList().get(position);
        AudioPlayer.get().addAndPlay(music);
    }

    @Override
    public void onMoreClick(final int position) {
        LocalMusic music = AppCache.get().getLocalMusicList().get(position);
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        dialog.setTitle(music.getTitle());
        dialog.setItems(R.array.local_music_dialog, (dialog1, which) -> {
            switch (which) {
                case 0:// 分享
                    shareMusic(music);
                    break;
                case 1:// 查看歌曲信息
                    MusicInfoActivity.start(getContext(), music);
                    break;
                case 2:// 删除
                    deleteMusic(music);
                    break;
            }
        });
        dialog.show();
    }

    /**
     * 分享音乐
     */
    private void shareMusic(LocalMusic music) {
        File file = new File(music.getPath());
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("audio/*");
        intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file));
        startActivity(Intent.createChooser(intent, "分享"));
    }

    private void deleteMusic(final LocalMusic music) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
        String title = music.getTitle();
        String msg = getString(R.string.delete_music, title);
        dialog.setMessage(msg);
        dialog.setPositiveButton("删除", (dialog1, which) -> {
            File file = new File(music.getPath());
            if (file.delete()) {
                // 刷新媒体库
                Intent intent =
                        new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://".concat(music.getPath())));
                getContext().sendBroadcast(intent);
            }
        });
        dialog.setNegativeButton("取消", null);
        dialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        int position = lvLocalMusic.getFirstVisiblePosition();
        int offset = (lvLocalMusic.getChildAt(0) == null) ? 0 : lvLocalMusic.getChildAt(0).getTop();
        outState.putInt(ConstantTools.LOCAL_MUSIC_POSITION, position);
        outState.putInt(ConstantTools.LOCAL_MUSIC_OFFSET, offset);
    }

    public void onRestoreInstanceState(final Bundle savedInstanceState) {
        lvLocalMusic.post(() -> {
            int position = savedInstanceState.getInt(ConstantTools.LOCAL_MUSIC_POSITION);
            int offset = savedInstanceState.getInt(ConstantTools.LOCAL_MUSIC_OFFSET);
            lvLocalMusic.setSelectionFromTop(position, offset);
        });
    }
}
