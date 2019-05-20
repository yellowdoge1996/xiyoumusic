package com.xiyoumusic.app.activitys;

import android.Manifest;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Icon;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;
import androidx.emoji.widget.EmojiEditText;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiyoumusic.app.AppCache;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.adapter.PinglunAdapter;
import com.xiyoumusic.app.adapter.PinglunOnMoreClickListener;
import com.xiyoumusic.app.entity.CommentBean;
import com.xiyoumusic.app.entity.HuiFu;
import com.xiyoumusic.app.entity.LocalMusic;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.entity.PingLun;
import com.xiyoumusic.app.service.AudioPlayer;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.CoverLoader;
import com.xiyoumusic.app.utils.FileTools;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.SystemUtils;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.views.PinglunExpandableListView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import okhttp3.ResponseBody;


public class MusicInfoActivity extends BaseActivity implements View.OnClickListener, PinglunOnMoreClickListener {
    private ScrollView scrollView;
    private FloatingActionButton btnDownloadMusic;
    private FloatingActionButton btnShoucangMusic;
    private ImageView ivCover;
    private EditText etTitle;
    private EditText etArtist;
    private EditText etAlbum;
    private TextView tvDuration;
    private TextView tvFileName;
    private TextView tvFileSize;
    private TextView tvFilePath;
    private FloatingActionMenu fabmenu;
    private LinearLayout pinglunLayout;
    private TextView tvPinglun;
    private PinglunExpandableListView pinglunContent;
    private TextView emptyTextView;
    private LinearLayout editPinglunLayout;
    private EmojiEditText plnrEdit;
    private Button sendPinglunBtn;

    private String PINGLUN = "pinglun";
    private String HUIFU = "huifu";
    private String mode = PINGLUN;
    private PinglunAdapter pinglunAdapter;
    private List<CommentBean> commentBeanList;
    private LocalMusic mMusic;
    private Music music;
    private File mMusicFile;
    private Bitmap mCoverBitmap;
    private String type;
    private String uri;
    private boolean isDownload;
    private boolean isShoucang;

    private InputMethodManager imm;

    public static void start(Context context, LocalMusic music) {
        Intent intent = new Intent(context, MusicInfoActivity.class);
        intent.putExtra("type", ConstantTools.TYPE_LOCAL_MUSIC);
        intent.putExtra(ConstantTools.MUSIC, music);
        context.startActivity(intent);
    }

    public static void start(Context context, Music music) {
        Intent intent = new Intent(context, MusicInfoActivity.class);
        intent.putExtra("type", ConstantTools.TYPE_ONLINE_MUSIC);
        intent.putExtra(ConstantTools.MUSIC, music);
        context.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_info);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        TextView toolbarTitle = findViewById(R.id.toolbar_title);
        toolbarTitle.setText("音乐信息");
        toolbar.setNavigationOnClickListener(this);

        scrollView = findViewById(R.id.scrollview);
        ivCover = findViewById(R.id.iv_music_info_cover);
        etTitle = findViewById(R.id.et_music_info_title);
        etArtist = findViewById(R.id.et_music_info_artist);
        etAlbum = findViewById(R.id.et_music_info_album);
        tvDuration = findViewById(R.id.tv_music_info_duration);
        tvFileName = findViewById(R.id.tv_music_info_file_name);
        tvFileSize = findViewById(R.id.tv_music_info_file_size);
        tvFilePath = findViewById(R.id.tv_music_info_file_path);

        fabmenu = findViewById(R.id.fabmenu);
        btnDownloadMusic = findViewById(R.id.btn_download_music);
        btnShoucangMusic = findViewById(R.id.btn_shoucang_music);

        pinglunLayout = findViewById(R.id.layout_pinglun);
        tvPinglun = findViewById(R.id.tv_pinglun);
        pinglunContent = findViewById(R.id.content_pinglun);
        emptyTextView = findViewById(R.id.tv_empty_pinglun);
        editPinglunLayout = findViewById(R.id.layout_edit_pinglun);
        plnrEdit = findViewById(R.id.edit_plnr);
        sendPinglunBtn = findViewById(R.id.btn_send_pinglun);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    protected void onServiceBound() {
        type = getIntent().getStringExtra("type");
        if (ConstantTools.TYPE_LOCAL_MUSIC.equals(type)){
            mMusic = (LocalMusic) getIntent().getSerializableExtra(ConstantTools.MUSIC);
            if (mMusic == null) {
                finish();
            }
            mMusicFile = new File(mMusic.getPath());
            mCoverBitmap = CoverLoader.get().loadThumb(mMusic);
            initView();
        }else{
            music = (Music) getIntent().getSerializableExtra(ConstantTools.MUSIC);
            if (music == null){
                finish();
            }
            mMusic = new LocalMusic();
            mMusic.setType(LocalMusic.Type.ONLINE);
            mMusic.setSongId(music.getGqid());
            mMusic.setTitle(music.getGqmc());
            mMusic.setAlbum("unknown");
            mMusic.setAlbumId(music.getGqid());
            mMusic.setPath(ConstantTools.baseUrl+music.getGqlj());
            String[] strings1 = music.getGqlj().split("/");
            mMusic.setFileName(strings1[strings1.length-1].replaceFirst("\\?.*",""));
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < strings1.length; i++) {
                if (i != strings1.length-1){
                    sb.append(strings1[i]+"/");
                }
            }
            uri = ConstantTools.baseUrl+sb.toString();

            ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
                if ("1".equals(myResponse.getState())) {
                    String[] strings = myResponse.getMsg().split(",");
                    mMusic.setArtist(strings[0]);
                    mMusic.setDuration(Long.parseLong(strings[1])*1000);
                    mMusic.setFileSize(Integer.parseInt(strings[2]));
                    String filename = FileTools.getCoverDir() + FileTools.getFileName(mMusic.getArtist(), mMusic.getTitle());
                    mMusic.setCoverPath(filename);

                    isShoucang = Boolean.parseBoolean(strings[3]);
                    isDownload = FileTools.isDownload(mMusic);
                    if (isDownload){
                        type = ConstantTools.TYPE_LOCAL_MUSIC;
                        mMusic.setPath(FileTools.getMusicDir() + FileTools.getMp3FileName(mMusic.getArtist(), mMusic.getTitle()));
                        mMusicFile = new File(mMusic.getPath());
                    }

                    AudioPlayer.get().addAndPlay(mMusic);
                    requestPermissionFile(filename);
                    } else {
                    if (myResponse.getError() != null) {
                        ToastUtil.normal(myResponse.getError());
                    }
                }
            };

            String xh = SPTool.getSharedPreferences().getString("xh", "000000000000");
            ApiMethods.getMusicInfo(new ProgressObserver<>(this, observerOnNextListener, "加载中..", false, true),
                    music.getGqid(), xh);
            pinglunLayout.setVisibility(View.VISIBLE);
            tvPinglun.setOnClickListener(this);

            getPinglun(false);
        }
    }

    public void getPinglun(boolean isReload){
        ObserverOnNextListener<MyResponse> observerOnNextListener1 = myResponse -> {
            if ("1".equals(myResponse.getState())) {
                if (myResponse.getCommentData() == null || myResponse.getCommentData().size() == 0){
                    commentBeanList = new ArrayList<>();
                    setAdapter(commentBeanList);
                    emptyTextView.setText("暂无评论");
                    emptyTextView.setVisibility(View.VISIBLE);
                    pinglunContent.setVisibility(View.GONE);
                }else{
                    commentBeanList = myResponse.getCommentData();
                    setAdapter(commentBeanList);
                    emptyTextView.setVisibility(View.GONE);
                    pinglunContent.setVisibility(View.VISIBLE);
                }
            } else {
                commentBeanList = new ArrayList<>();
                setAdapter(commentBeanList);
                if (myResponse.getError() != null) {
                    ToastUtil.normal(myResponse.getError());
                }
                emptyTextView.setText("加载失败\n点击刷新");
                emptyTextView.setOnClickListener(this);
                emptyTextView.setVisibility(View.VISIBLE);
                pinglunContent.setVisibility(View.GONE);
            }
        };

        if (isReload) {
            ApiMethods.getPinglun(new ProgressObserver<>(this, observerOnNextListener1, "加载中", true, true),
                    music.getGqid());
        }else{
            ApiMethods.getPinglun(new ProgressObserver<>(this, observerOnNextListener1, null, false, false),
                    music.getGqid());
        }
    }
    private void requestPermissionFile(String filepath) {
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(permission -> {
                    if (permission.granted) {
                        downloadCover(filepath);
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

    private void downloadCover(String filepath){
        ObserverOnNextListener<ResponseBody> downloadObserverOnNextListener = responseBody -> {
            boolean writtenToDisk = FileTools.writeResponseBodyToDisk(responseBody, filepath);
            if (writtenToDisk){
                mCoverBitmap = CoverLoader.get().loadThumb(mMusic);
                initView();
            }
        };
        ApiMethods.download(new ProgressObserver<>(this, downloadObserverOnNextListener, null, false, false), ConstantTools.baseUrl+music.getGqct());
    }
    private void initView() {
            scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
                if (editPinglunLayout.getVisibility() == View.VISIBLE) {
                    editPinglunLayout.setVisibility(View.GONE);
                }
            });


        ivCover.setImageBitmap(mCoverBitmap);

        etTitle.setText(mMusic.getTitle());
        etTitle.setSelection(etTitle.length());

        etArtist.setText(mMusic.getArtist());
        etArtist.setSelection(etArtist.length());

        etAlbum.setText(mMusic.getAlbum());
        etAlbum.setSelection(etAlbum.length());

        tvDuration.setText(SystemUtils.formatTime("mm:ss", mMusic.getDuration()));

        tvFileName.setText(mMusic.getFileName());

        tvFileSize.setText(String.format(Locale.getDefault(), "%.2fMB", FileTools.b2mb((int) mMusic.getFileSize())));

        if (ConstantTools.TYPE_LOCAL_MUSIC.equals(mMusic.getType())) {
            tvFilePath.setText(mMusicFile.getParent());
        }else if (isDownload){
            tvFilePath.setText(mMusicFile.getParent());
            fabmenu.setVisibility(View.VISIBLE);
            btnDownloadMusic.setLabelText("已下载");
            btnDownloadMusic.setClickable(false);
            if (!isShoucang) {
                btnShoucangMusic.setLabelText("收藏");
                btnShoucangMusic.setImageIcon(Icon.createWithResource(this, R.drawable.ic_star_border_white_36dp));
            }else{
                btnShoucangMusic.setLabelText("已收藏");
                btnShoucangMusic.setImageIcon(Icon.createWithResource(this, R.drawable.ic_star_white_36dp));
            }
            btnShoucangMusic.setOnClickListener(this);
        }else if (ConstantTools.TYPE_ONLINE_MUSIC.equals(type)){
            fabmenu.setVisibility(View.VISIBLE);
            tvFilePath.setText(uri);
            btnDownloadMusic.setLabelText("下载");
            btnDownloadMusic.setOnClickListener(this);
            if (!isShoucang) {
                btnShoucangMusic.setLabelText("收藏");
                btnShoucangMusic.setImageIcon(Icon.createWithResource(this, R.drawable.ic_star_border_white_36dp));
            }else{
                btnShoucangMusic.setLabelText("已收藏");
                btnShoucangMusic.setImageIcon(Icon.createWithResource(this, R.drawable.ic_star_white_36dp));
            }
            btnShoucangMusic.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        if ("Toolbar".equals(v.getParent().getClass().getSimpleName()) && v.getId() != R.id.toolbar_title){
            onBackPressed();
        }
        switch (v.getId()){
            case R.id.btn_download_music:
                startDownload(mMusic);
                break;
            case R.id.btn_shoucang_music:
                shoucang(mMusic);
                break;
            case R.id.tv_pinglun:
                mode = PINGLUN;
                fabmenu.setVisibility(View.GONE);
                editPinglunLayout.setVisibility(View.VISIBLE);
                plnrEdit.requestFocus();
                plnrEdit.requestFocusFromTouch();
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                sendPinglunBtn.setOnClickListener(this);
                break;
            case R.id.btn_send_pinglun:
                if (mode.equals(PINGLUN)) {
                    sendpinglun();
                }else if (mode.equals(HUIFU)){
                    sendhuifu();
                }
                imm.hideSoftInputFromWindow(plnrEdit.getWindowToken(), 0);
                fabmenu.setVisibility(View.VISIBLE);
                plnrEdit.setText("");
                plnrEdit.clearFocus();
                editPinglunLayout.setVisibility(View.GONE);
                break;
            case R.id.tv_empty_pinglun:
                getPinglun(true);
                emptyTextView.setOnClickListener(null);
            default:break;
        }
    }

    @Override
    public void onMoreClick(int groupPosition, int childPosition, View v) {
        PopupMenu popup = new PopupMenu(this, v);
        if (childPosition == -1){
            popup.getMenu().add(0,0,0,
                    SPTool.getSharedPreferences().getString("xh", "000000000000")
                            .equals(commentBeanList.get(groupPosition).getPlzxh())?"删除":"回复");
        }else{
            popup.getMenu().add(0,0,0,
                    SPTool.getSharedPreferences().getString("xh", "000000000000")
                            .equals(commentBeanList.get(groupPosition).getHuiFuList().get(childPosition).getHfzxh())?"删除":"回复");
        }
        popup.setOnMenuItemClickListener(item -> {
            if(item.getTitle().equals("删除")){
                if (childPosition == -1){
                    deletePinglun(groupPosition);
                }else{
                    deleteHuifu(groupPosition, childPosition);
                }
            }else if (item.getTitle().equals("回复")){
                mode = HUIFU;
                HuiFu huifu = new HuiFu();
                if (childPosition == -1) {
                    huifu.setBhfzxh(commentBeanList.get(groupPosition).getPlzxh());
                }else {
                    huifu.setBhfzxh(commentBeanList.get(groupPosition).getHuiFuList().get(childPosition).getHfzxh());
                }
                huifu.setHfzxh(SPTool.getSharedPreferences().getString("xh", "000000000000"));
                huifu.setPlid(commentBeanList.get(groupPosition).getPlid());
                if (childPosition != -1){
                    huifu.setHfmbid(commentBeanList.get(groupPosition).getHuiFuList().get(childPosition).getHfid());
                }
                sendPinglunBtn.setTag(R.id.tag_huifu, huifu);
                sendPinglunBtn.setTag(R.id.tag_group_id, groupPosition);
                fabmenu.setVisibility(View.GONE);
                editPinglunLayout.setVisibility(View.VISIBLE);
                plnrEdit.requestFocus();
                plnrEdit.requestFocusFromTouch();
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                sendPinglunBtn.setOnClickListener(MusicInfoActivity.this);
            }
            return true;
        });
        popup.show();
    }

    public void deletePinglun(int groupPosition){
        if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
            Intent i = new Intent(MusicInfoActivity.this, LoginActivity.class);
            startActivityForResult(i,0);
            return;
        }
        if (!commentBeanList.get(groupPosition).getPlzxh().equals(SPTool.getSharedPreferences().getString("xh", "000000000000"))){
            ToastUtil.normal("不能删除别人的评论");
            return;
        }
        long plid = commentBeanList.get(groupPosition).getPlid();
        ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                commentBeanList.remove(groupPosition);
                setAdapter(commentBeanList);
                ToastUtil.normal("评论删除成功");
            }else{
                ToastUtil.normal(myResponse.getError());
            }
        };
        ApiMethods.deletePinglun(new ProgressObserver<>(this,
                observerOnNextListener, "评论删除中...", false, true), plid);
    }

    public void deleteHuifu(int groupPosition, int childPosition){
        if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
            Intent i = new Intent(MusicInfoActivity.this, LoginActivity.class);
            startActivityForResult(i,0);
            return;
        }
        if (!commentBeanList.get(groupPosition).getHuiFuList().get(childPosition).getHfzxh().equals(SPTool.getSharedPreferences().getString("xh", "000000000000"))){
            ToastUtil.normal("不能删除别人的回复");
            return;
        }
        long hfid = commentBeanList.get(groupPosition).getHuiFuList().get(childPosition).getHfid();
        ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                CommentBean comment = (CommentBean) myResponse.getCommentData().get(0);
                commentBeanList.remove(groupPosition);
                commentBeanList.add(groupPosition, comment);
                setAdapter(commentBeanList);
                ToastUtil.normal("回复删除成功");
            }else{
                ToastUtil.normal(myResponse.getError());
            }
        };
        ApiMethods.deleteHuifu(new ProgressObserver<>(this, observerOnNextListener, "回复删除中...", false, true), hfid);
    }

    public void sendhuifu(){
        if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
            Intent i = new Intent(MusicInfoActivity.this, LoginActivity.class);
            startActivityForResult(i,0);
            return;
        }
        HuiFu huiFu = (HuiFu) sendPinglunBtn.getTag(R.id.tag_huifu);
        int groupPosition = (int) sendPinglunBtn.getTag(R.id.tag_group_id);
        if (huiFu == null || groupPosition <0){
            return;
        }
        if (TextUtils.isEmpty(plnrEdit.getText().toString())){
            return;
        }
        huiFu.setHfnr(plnrEdit.getText().toString());
        ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                CommentBean comment = (CommentBean) myResponse.getCommentData().get(0);
                commentBeanList.remove(groupPosition);
                commentBeanList.add(groupPosition, comment);
                setAdapter(commentBeanList);
            }else{
                ToastUtil.normal(myResponse.getError());
            }
        };
        ApiMethods.huifu(new ProgressObserver<>(this, observerOnNextListener, "回复发送中...", false, true), huiFu);
    }

    public void sendpinglun(){
        if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
            Intent i = new Intent(MusicInfoActivity.this, LoginActivity.class);
            startActivityForResult(i,0);
            return;
        }
        if (TextUtils.isEmpty(plnrEdit.getText().toString())){
            return;
        }
        PingLun pingLun = new PingLun();
        pingLun.setGqid(mMusic.getSongId());
        pingLun.setPlnr(plnrEdit.getText().toString());
        pingLun.setPlzxh(SPTool.getSharedPreferences().getString("xh", "000000000000"));
        ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                CommentBean comment = (CommentBean) myResponse.getCommentData().get(0);
                commentBeanList.add(0,comment);
                setAdapter(commentBeanList);
            }else{
                ToastUtil.normal(myResponse.getError());
            }
        };
        ApiMethods.pinglun(new ProgressObserver<>(this, observerOnNextListener, "评论发送中...", false, true), pingLun);
    }
    @Override
    public void onBackPressed() {
        if (editPinglunLayout.getVisibility() == View.VISIBLE){
            fabmenu.setVisibility(View.VISIBLE);
            plnrEdit.clearFocus();
            editPinglunLayout.setVisibility(View.GONE);
        }else if (editPinglunLayout.getVisibility() == View.GONE){
            super.onBackPressed();
        }
    }

    public void startDownload(LocalMusic music){
        try {
            String fileName = FileTools.getMp3FileName(music.getArtist(), music.getTitle());
            String uri = ConstantTools.baseUrl+"download/"+music.getSongId();
            Uri url = Uri.parse(uri);
            DownloadManager.Request request = new DownloadManager.Request(url);
            request.setTitle(FileTools.getFileName(music.getArtist(), music.getTitle()));
            request.setDescription("正在下载…");
            request.setDestinationInExternalPublicDir(FileTools.getRelativeMusicDir(), fileName);
            String fileExtensionFromUrl = MimeTypeMap.getFileExtensionFromUrl(uri);
            request.setMimeType(fileExtensionFromUrl);
            request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE | DownloadManager.Request.NETWORK_WIFI);
            request.setAllowedOverRoaming(false); // 不允许漫游
            request.setNotificationVisibility(View.VISIBLE);
            DownloadManager downloadManager = (DownloadManager) AppCache.get().getContext().getSystemService(Context.DOWNLOAD_SERVICE);
            long id = downloadManager.enqueue(request);
            AppCache.get().getDownloadList().put(id, music);
            btnDownloadMusic.setClickable(false);
            btnDownloadMusic.setLabelText("下载中");
        } catch (Throwable th) {
            th.printStackTrace();
            btnDownloadMusic.setClickable(true);
            ToastUtil.normal("下载失败");
        }
    }

    public void shoucang(LocalMusic music){
        if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
            Intent i = new Intent(MusicInfoActivity.this, LoginActivity.class);
            startActivityForResult(i,0);
            return;
        }
        ObserverOnNextListener<MyResponse> downloadObserverOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                if ("收藏".equals(btnShoucangMusic.getLabelText())){
                    btnShoucangMusic.setLabelText("已收藏");
                    btnShoucangMusic.setImageIcon(Icon.createWithResource(this, R.drawable.ic_star_white_36dp));
                    ToastUtil.normal("收藏成功");
                }else{
                    btnShoucangMusic.setLabelText("收藏");
                    btnShoucangMusic.setImageIcon(Icon.createWithResource(this, R.drawable.ic_star_border_white_36dp));
                    ToastUtil.normal("取消收藏成功");
                }
            }else{
                ToastUtil.normal(myResponse.getError());
            }
        };
        if ("收藏".equals(btnShoucangMusic.getLabelText())) {
            ApiMethods.shoucang(new ProgressObserver<>(this, downloadObserverOnNextListener, "收藏中...", false, true), music.getSongId(), SPTool.getSharedPreferences().getString("xh", "000000000000"));
        }else {
            ApiMethods.shoucang(new ProgressObserver<>(this, downloadObserverOnNextListener, "取消收藏中...", false, true), music.getSongId(), SPTool.getSharedPreferences().getString("xh", "000000000000"));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (resultCode == RESULT_OK){
            if (requestCode == 0){
                ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
                    if ("1".equals(myResponse.getState())) {
                        String[] strings = myResponse.getMsg().split(",");
                        isShoucang = Boolean.parseBoolean(strings[3]);
                        if (!isShoucang) {
                            btnShoucangMusic.setLabelText("收藏");
                            btnShoucangMusic.setImageIcon(Icon.createWithResource(this, R.drawable.ic_star_border_white_36dp));
                        }else{
                            btnShoucangMusic.setLabelText("已收藏");
                            btnShoucangMusic.setImageIcon(Icon.createWithResource(this, R.drawable.ic_star_white_36dp));
                        }
                    } else {
                        if (myResponse.getError() != null) {
                            ToastUtil.normal(myResponse.getError());
                        }
                    }
                };
                String xh = SPTool.getSharedPreferences().getString("xh", "000000000000");
                ApiMethods.getMusicInfo(new ProgressObserver<>(this, observerOnNextListener, "加载中..", false, true),
                        mMusic.getSongId(), xh);
            }
        }
    }

    public void setAdapter(List<CommentBean> commentBeanList){
        if (pinglunContent.getAdapter() == null) {
            pinglunAdapter = new PinglunAdapter(commentBeanList, this);
            pinglunAdapter.setOnMoreClickListener(this);
            pinglunContent.setAdapter(pinglunAdapter);
            pinglunContent.setOnGroupClickListener((parent, v, groupPosition, id) -> true);
        } else {
            pinglunAdapter.refresh(commentBeanList);
        }
        if (commentBeanList.size() > 0){
            for (int i = 0; i < commentBeanList.size(); i++){
                pinglunContent.expandGroup(i);
            }
        }
    }
}
