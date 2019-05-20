package com.xiyoumusic.app.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.adapter.PlayPagerAdapter;
import com.xiyoumusic.app.entity.LocalMusic;
import com.xiyoumusic.app.entity.LrcResponse;
import com.xiyoumusic.app.enums.PlayModeEnum;
import com.xiyoumusic.app.service.AudioPlayer;
import com.xiyoumusic.app.service.OnPlayerEventListener;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.CoverLoader;
import com.xiyoumusic.app.utils.FileTools;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ScreenUtils;
import com.xiyoumusic.app.utils.SystemUtils;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.views.AlbumCoverView;
import com.xiyoumusic.app.views.IndicatorLayout;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import me.wcy.lrcview.LrcView;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;

/**
 * 正在播放界面
 * Created by wcy on 2015/11/27.
 */
public class PlayFragment extends Fragment implements View.OnClickListener,
        ViewPager.OnPageChangeListener, SeekBar.OnSeekBarChangeListener, OnPlayerEventListener,
        LrcView.OnPlayClickListener {
    private String TAG = this.getClass().getSimpleName();
    private LinearLayout llContent;
    private ImageView ivPlayingBg;
    private ImageView ivBack;
    private TextView tvTitle;
    private TextView tvArtist;
    private ViewPager vpPlay;
    private IndicatorLayout ilIndicator;
    private SeekBar sbProgress;
    private TextView tvCurrentTime;
    private TextView tvTotalTime;
    private ImageView ivMode;
    private ImageView ivPlay;
    private ImageView ivNext;
    private ImageView ivPrev;
    private AlbumCoverView mAlbumCoverView;
    private LrcView mLrcViewSingle;
    private LrcView mLrcViewFull;
    private SeekBar sbVolume;

    private AudioManager mAudioManager;
    private List<View> mViewPagerContent;
    private int mLastProgress;
    private boolean isDraggingProgress;


    private void init(View view){
        llContent = view.findViewById(R.id.ll_content);
        ivPlayingBg = view.findViewById(R.id.iv_play_page_bg);
        ivBack = view.findViewById(R.id.iv_back);
        tvTitle = view.findViewById(R.id.tv_title);
        tvArtist = view.findViewById(R.id.tv_artist);
        vpPlay = view.findViewById(R.id.vp_play_page);
        ilIndicator = view.findViewById(R.id.il_indicator);
        sbProgress = view.findViewById(R.id.sb_progress);
        tvCurrentTime = view.findViewById(R.id.tv_current_time);
        tvTotalTime = view.findViewById(R.id.tv_total_time);
        ivMode = view.findViewById(R.id.iv_mode);
        ivPlay = view.findViewById(R.id.iv_play);
        ivNext = view.findViewById(R.id.iv_next);
        ivPrev = view.findViewById(R.id.iv_prev);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_play, container, false);
        init(view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initSystemBar();
        initViewPager();
        ilIndicator.create(mViewPagerContent.size());
        initPlayMode();
        onChangeImpl(AudioPlayer.get().getPlayMusic());
        AudioPlayer.get().addOnPlayEventListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        IntentFilter filter = new IntentFilter(ConstantTools.VOLUME_CHANGED_ACTION);
        getContext().registerReceiver(mVolumeReceiver, filter);
    }

    @Override
    public void onStart() {
        super.onStart();
        setListener();
    }

    private void setListener() {
        ivBack.setOnClickListener(this);
        ivMode.setOnClickListener(this);
        ivPlay.setOnClickListener(this);
        ivPrev.setOnClickListener(this);
        ivNext.setOnClickListener(this);
        sbProgress.setOnSeekBarChangeListener(this);
        sbVolume.setOnSeekBarChangeListener(this);
        vpPlay.addOnPageChangeListener(this);
    }

    /**
     * 沉浸式状态栏
     */
    private void initSystemBar() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int top = ScreenUtils.getStatusBarHeight();
            llContent.setPadding(0, top, 0, 0);
        }
    }

    private void initViewPager() {
        View coverView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_play_page_cover, null);
        View lrcView = LayoutInflater.from(getContext()).inflate(R.layout.fragment_play_page_lrc, null);
        mAlbumCoverView = coverView.findViewById(R.id.album_cover_view);
        mLrcViewSingle = coverView.findViewById(R.id.lrc_view_single);
        mLrcViewFull = lrcView.findViewById(R.id.lrc_view_full);
        sbVolume = lrcView.findViewById(R.id.sb_volume);
        mAlbumCoverView.initNeedle(AudioPlayer.get().isPlaying());
        mLrcViewFull.setOnPlayClickListener(this);
        initVolume();

        mViewPagerContent = new ArrayList<>(2);
        mViewPagerContent.add(coverView);
        mViewPagerContent.add(lrcView);
        vpPlay.setAdapter(new PlayPagerAdapter(mViewPagerContent));
    }

    private void initVolume() {
        mAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
        sbVolume.setMax(mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
    }

    private void initPlayMode() {
        int mode = SPTool.getSharedPreferences().getInt(ConstantTools.PLAY_MODE, 0);
        ivMode.setImageLevel(mode);
    }

    @Override
    public void onChange(LocalMusic music) {
        onChangeImpl(music);
    }

    @Override
    public void onPlayerStart() {
        ivPlay.setSelected(true);
        mAlbumCoverView.start();
    }

    @Override
    public void onPlayerPause() {
        ivPlay.setSelected(false);
        mAlbumCoverView.pause();
    }

    /**
     * 更新播放进度
     */
    @Override
    public void onPublish(int progress) {
        if (!isDraggingProgress) {
            sbProgress.setProgress(progress);
        }

        if (mLrcViewSingle.hasLrc()) {
            mLrcViewSingle.updateTime(progress);
            mLrcViewFull.updateTime(progress);
        }
    }

    @Override
    public void onBufferingUpdate(int percent) {
        sbProgress.setSecondaryProgress(sbProgress.getMax() * 100 / percent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.iv_mode:
                switchPlayMode();
                break;
            case R.id.iv_play:
                play();
                break;
            case R.id.iv_next:
                next();
                break;
            case R.id.iv_prev:
                prev();
                break;
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        ilIndicator.setCurrent(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (seekBar == sbProgress) {
            if (Math.abs(progress - mLastProgress) >= DateUtils.SECOND_IN_MILLIS) {
                tvCurrentTime.setText(formatTime(progress));
                mLastProgress = progress;
            }
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = true;
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        if (seekBar == sbProgress) {
            isDraggingProgress = false;
            if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()) {
                int progress = seekBar.getProgress();
                AudioPlayer.get().seekTo(progress);

                if (mLrcViewSingle.hasLrc()) {
                    mLrcViewSingle.updateTime(progress);
                    mLrcViewFull.updateTime(progress);
                }
            } else {
                seekBar.setProgress(0);
            }
        } else if (seekBar == sbVolume) {
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, seekBar.getProgress(),
                    AudioManager.FLAG_REMOVE_SOUND_AND_VIBRATE);
        }
    }

    @Override
    public boolean onPlayClick(long time) {
        if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPausing()) {
            AudioPlayer.get().seekTo((int) time);
            if (AudioPlayer.get().isPausing()) {
                AudioPlayer.get().playPause();
            }
            return true;
        }
        return false;
    }

    private void onChangeImpl(LocalMusic music) {
        if (music == null) {
            return;
        }

        tvTitle.setText(music.getTitle());
        tvArtist.setText(music.getArtist());
        sbProgress.setProgress((int) AudioPlayer.get().getAudioPosition());
        sbProgress.setSecondaryProgress(0);
        sbProgress.setMax((int) music.getDuration());
        mLastProgress = 0;
        tvCurrentTime.setText(R.string.play_time_start);
        tvTotalTime.setText(formatTime(music.getDuration()));
        setCoverAndBg(music);
        requestPermissionFile(music);
        if (AudioPlayer.get().isPlaying() || AudioPlayer.get().isPreparing()) {
            ivPlay.setSelected(true);
            mAlbumCoverView.start();
        } else {
            ivPlay.setSelected(false);
            mAlbumCoverView.pause();
        }
    }

    private void play() {
        AudioPlayer.get().playPause();
    }

    private void next() {
        AudioPlayer.get().next();
    }

    private void prev() {
        AudioPlayer.get().prev();
    }

    private void switchPlayMode() {
        PlayModeEnum mode = PlayModeEnum.valueOf(SPTool.getSharedPreferences().getInt(ConstantTools.PLAY_MODE, 0));
        switch (mode) {
            case LOOP:
                mode = PlayModeEnum.SHUFFLE;
                ToastUtil.normal(R.string.mode_shuffle);
                break;
            case SHUFFLE:
                mode = PlayModeEnum.SINGLE;
                ToastUtil.normal(R.string.mode_one);
                break;
            case SINGLE:
                mode = PlayModeEnum.LOOP;
                ToastUtil.normal(R.string.mode_loop);
                break;
        }
        SPTool.put(ConstantTools.PLAY_MODE, mode.value());
        initPlayMode();
    }

    private void onBackPressed() {
        getActivity().onBackPressed();
        ivBack.setEnabled(false);
        ivBack.postDelayed(() -> ivBack.setEnabled(true), 300);
    }

    private void setCoverAndBg(LocalMusic music) {
        mAlbumCoverView.setCoverBitmap(CoverLoader.get().loadRound(music));
        ivPlayingBg.setImageBitmap(CoverLoader.get().loadBlur(music));
    }

    private void setLrc(final LocalMusic music) {
        if (music.getType() == LocalMusic.Type.LOCAL) {
            String lrcPath = FileTools.getLrcFilePath(music);
            if (!TextUtils.isEmpty(lrcPath)) {
                loadLrc(lrcPath);
            } else {
                vpPlay.setTag(music);

                loadLrc("");
                setLrcLabel("正在搜索歌词");

                ObserverOnNextListener<LrcResponse> observerOnNextListener = lrcResponse -> {
                    if (lrcResponse.getCode() == 0 && lrcResponse.getCount() > 0
                            && vpPlay.getTag() == music) {
                        List<LrcResponse.LrcInfo> lrcList = lrcResponse.getResult();
                        LrcResponse.LrcInfo lrcInfo = lrcList.get(0);
                        String filename = FileTools.getLrcDir() + FileTools.getLrcFileName(music.getArtist(), music.getTitle());
                        downloadLrc(lrcInfo.getLrc(), filename);
                        setLrcLabel("暂无歌词");
                    }else{
                        vpPlay.setTag(null);

                        setLrcLabel("暂无歌词");
                    }
                };
                ApiMethods.searchLrcByMusicName(new ProgressObserver<>(getActivity(), observerOnNextListener, null, false, false), music.getTitle());
            }
        } else {
            String lrcPath = FileTools.getLrcDir() + FileTools.getLrcFileName(music.getArtist(), music.getTitle());
            loadLrc(lrcPath);
        }
    }

    public void downloadLrc(String uri, String filename){
        ObserverOnNextListener<ResponseBody> observerOnNextListener = responseBody -> {
            boolean writtenToDisk = FileTools.writeResponseBodyToDisk(responseBody, filename);
            if (writtenToDisk){
                vpPlay.setTag(null);
                loadLrc(filename);
            }
        };
        ApiMethods.download(new ProgressObserver<>(getActivity(), observerOnNextListener, null, false, false), uri+"?time="+System.currentTimeMillis());
    }

    private void loadLrc(String path) {
        File file = new File(path);
        mLrcViewSingle.loadLrc(file);
        mLrcViewFull.loadLrc(file);
    }

    private void setLrcLabel(String label) {
        mLrcViewSingle.setLabel(label);
        mLrcViewFull.setLabel(label);
    }

    private String formatTime(long time) {
        return SystemUtils.formatTime("mm:ss", time);
    }

    private BroadcastReceiver mVolumeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            sbVolume.setProgress(mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
        }
    };

    @Override
    public void onDestroy() {
        getContext().unregisterReceiver(mVolumeReceiver);
        AudioPlayer.get().removeOnPlayEventListener(this);
        super.onDestroy();
    }
    private void requestPermissionFile(LocalMusic music) {
        RxPermissions rxPermission = new RxPermissions(getActivity());
        rxPermission
                .requestEach(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {

                        setLrc(music);
                        // 用户已经同意该权限
                    } else if (permission.shouldShowRequestPermissionRationale) {
                        // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                        ToastUtil.error("你已经拒绝了文件读写权限");
                    } else {
                        // 用户拒绝了该权限，并且选中『不再询问』
                        ToastUtil.error("你已经拒绝了文件读写权限");
                    }
                });
    }
}
