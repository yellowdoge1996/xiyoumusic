package com.xiyoumusic.app;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;

import androidx.emoji.bundled.BundledEmojiCompatConfig;
import androidx.emoji.text.EmojiCompat;

import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.receiver.StatusBarReceiver;
import com.xiyoumusic.app.service.PlayService;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;


public class MyApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppCache.get().init(this);
        ToastUtil.init(this);
        SPTool.initSP(this);

        EmojiCompat.Config config = new BundledEmojiCompatConfig(this);
        EmojiCompat.init(config);

        Intent intent = new Intent(this, PlayService.class);
        startService(intent);
    }
}
