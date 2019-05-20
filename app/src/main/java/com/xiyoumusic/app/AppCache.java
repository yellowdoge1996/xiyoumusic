package com.xiyoumusic.app;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.collection.LongSparseArray;

import com.xiyoumusic.app.entity.LocalMusic;
import com.xiyoumusic.app.utils.CoverLoader;
import com.xiyoumusic.app.utils.ScreenUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hzwangchenyan on 2016/11/23.
 */
public class AppCache {
    private Context mContext;
    private final List<LocalMusic> mLocalMusicList = new ArrayList<>();
//    private final List<SheetInfo> mSheetList = new ArrayList<>();
//    private final List<Activity> mActivityStack = new ArrayList<>();
    private final LongSparseArray<LocalMusic> mDownloadList = new LongSparseArray<>();

    private AppCache() {
    }

    private static class SingletonHolder {
        private static AppCache instance = new AppCache();
    }

    public static AppCache get() {
        return SingletonHolder.instance;
    }

    public void init(Application application) {
        mContext = application.getApplicationContext();
        ScreenUtils.init(mContext);
        CoverLoader.get().init(mContext);
        application.registerActivityLifecycleCallbacks(new ActivityLifecycle());
    }

    public Context getContext() {
        return mContext;
    }

    public List<LocalMusic> getLocalMusicList() {
        return mLocalMusicList;
    }

    public LongSparseArray<LocalMusic> getDownloadList() {
        return mDownloadList;
    }

    private class ActivityLifecycle implements Application.ActivityLifecycleCallbacks {
        private static final String TAG = "Activity";

        @Override
        public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
            Log.i(TAG, "onCreate: " + activity.getClass().getSimpleName());
        }

        @Override
        public void onActivityStarted(Activity activity) {
        }

        @Override
        public void onActivityResumed(Activity activity) {
        }

        @Override
        public void onActivityPaused(Activity activity) {
        }

        @Override
        public void onActivityStopped(Activity activity) {
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            Log.i(TAG, "onDestroy: " + activity.getClass().getSimpleName());
        }
    }
}
