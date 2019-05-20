package com.xiyoumusic.app.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.xiyoumusic.app.AppCache;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.LocalMusic;
import com.xiyoumusic.app.utils.FileTools;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.id3.ID3TagUtils;
import com.xiyoumusic.app.utils.id3.ID3Tags;

import java.io.File;

/**
 * 下载完成广播接收器
 * Created by hzwangchenyan on 2015/12/30.
 */
public class DownloadReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        LocalMusic music = AppCache.get().getDownloadList().get(id);
        if (music != null) {
            String fileName = FileTools.getMp3FileName(music.getArtist(), music.getTitle());
            ToastUtil.normal(context.getString(R.string.download_success, music.getTitle()));
            String musicPath = FileTools.getMusicDir().concat(fileName);
            String coverPath = FileTools.getCoverDir()+FileTools.getFileName(music.getArtist(), music.getTitle());
            if (!TextUtils.isEmpty(musicPath) && !TextUtils.isEmpty(coverPath)) {
                // 设置专辑封面
                File musicFile = new File(musicPath);
                File coverFile = new File(coverPath);
                if (musicFile.exists() && coverFile.exists()) {
                    ID3Tags id3Tags = new ID3Tags.Builder()
                            .setCoverFile(coverFile)
                            .setArtist(music.getArtist())
                            .setAlbum(music.getAlbum())
                            .setTitle(music.getTitle()).build();
                    ID3TagUtils.setID3Tags(musicFile, id3Tags, false);
                }
            }
        }
    }
}
