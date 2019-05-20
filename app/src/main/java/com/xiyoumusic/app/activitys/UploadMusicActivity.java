package com.xiyoumusic.app.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.loader.content.CursorLoader;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class UploadMusicActivity extends BaseActivity implements View.OnClickListener{
    private String xh;
    private String musicFilePath;
    private static final int CHOOSE_PICTURE = 0;
    private static final int REQ_CODE_PICK_SOUNDFILE = 1;
    private static final int CROP_SMALL_PICTURE = 2;
    private boolean isChoseGranted = false;
    private String TAG = this.getClass().getSimpleName();

//    protected static Uri tempUri;
    protected static Uri cropUri;

    private EditText musicName;
    private ImageView musicPthoto;
    private Button musicFile;
    private Toolbar mToolbar;
    private TextView doUpload;

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        ViewGroup.LayoutParams layoutParams = musicPthoto.getLayoutParams();
        layoutParams.width = musicPthoto.getHeight();
        musicPthoto.setLayoutParams(layoutParams);
        Log.d(TAG, musicPthoto.getMeasuredHeight()+"");
        super.onWindowFocusChanged(hasFocus);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upolad_music);
        xh = getIntent().getStringExtra("xh");

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(this);

        File cropFile = new File(Environment.getExternalStorageDirectory(), "crop.jpg");
        cropUri = Uri.fromFile(cropFile);

        doUpload = findViewById(R.id.do_upload);
        doUpload.setOnClickListener(this);
        musicName = findViewById(R.id.new_music_name);
        musicName.clearFocus();
        musicPthoto = findViewById(R.id.new_music_photo);
        musicPthoto.setOnClickListener(this);

        musicFile = findViewById(R.id.new_music_file);
        musicFile.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if ("Toolbar".equals(v.getParent().getClass().getSimpleName()) && v.getId() != R.id.title_name_upload_music
        && v.getId() != R.id.do_upload){
            onBackPressed();
        }
        switch (v.getId()){
            case R.id.new_music_photo:
                requestPermissionFile();
                if (isChoseGranted) {
                    startChoose();
                }
                break;
            case R.id.new_music_file:
                Intent intent;
                intent = new Intent(Intent.ACTION_PICK);
                intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQ_CODE_PICK_SOUNDFILE);
                break;
            case R.id.do_upload:
                upload();
                break;
        }
    }

    @Override
    protected void onDestroy() {
//        File tempFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        File cropFile = new File(Environment.getExternalStorageDirectory(), "crop.jpg");
//        tempFile.delete();
        cropFile.delete();
        super.onDestroy();
    }

    private void requestPermissionFile() {
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            isChoseGranted = true;
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            ToastUtil.error("你已经拒绝了文件读写权限");
                            isChoseGranted =false;
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            ToastUtil.error("你已经拒绝了文件读写权限");
                            isChoseGranted = false;
                        }
                    }
                });
    }
    public void startChoose(){
        Intent openAlbumIntent = new Intent(
                Intent.ACTION_PICK);
        openAlbumIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case CHOOSE_PICTURE:
                    Log.d(TAG,data.getData().toString());
                    String filepath = changeToPath(data.getData());
                    Log.d("filepath", filepath);
                    Uri mediaUri2 = getImageContentUri(this, new File(filepath));
                    Log.d(TAG, mediaUri2.toString());
                    cutImage(mediaUri2); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    showImage(); // 让刚才选择裁剪得到的图片显示在界面上
                    break;
                case REQ_CODE_PICK_SOUNDFILE:
                    if ((data != null) && (data.getData() != null)){
                        Log.d(TAG,data.getData().toString());
                        musicFilePath = changeToPathMusic(data.getData());
                        String[] strings = musicFilePath.split("/");
                        musicFile.setText(strings[strings.length-1]);
                    }
                    break;
            }
        }
    }
    public boolean vilidate(){
        if ("".equals(musicName.getText().toString())){
            ToastUtil.normal("请输入歌曲名称");
            return false;
        }
        File cropFile = new File(Environment.getExternalStorageDirectory(), "crop.jpg");
        if (!cropFile.exists()){
            ToastUtil.normal("请选择插图");
            return false;
        }
        if (musicFilePath == null){
            ToastUtil.normal("请选择歌曲名称");
            return false;
        }
        File musicfile = new File(musicFilePath);
        if (!musicfile.exists()){
            ToastUtil.normal("歌曲文件不存在");
            return false;
        }
        return true;
    }
    public void showImage(){
        musicPthoto.setImageURI(cropUri);
    }
    public Uri getImageContentUri(Context context, File imageFile) {
        String filePath = imageFile.getAbsolutePath();
        Cursor cursor = context.getContentResolver().query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                new String[] { MediaStore.Images.Media._ID },
                MediaStore.Images.Media.DATA + "=? ",
                new String[] { filePath }, null);

        if (cursor != null && cursor.moveToFirst()) {
            int id = cursor.getInt(cursor
                    .getColumnIndex(MediaStore.MediaColumns._ID));
            Uri baseUri = Uri.parse("content://media/external/images/media");
            return Uri.withAppendedPath(baseUri, "" + id);
        } else {
            if (imageFile.exists()) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.DATA, filePath);
                return context.getContentResolver().insert(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            } else {
                return null;
            }
        }
    }
    public String changeToPath(Uri uri){
        String[] proj = { MediaStore.Images.Media.DATA };
        //sdk<=11，cursor = manageQuery(uri,proj,null,null,null)
        CursorLoader loader = new CursorLoader(this,uri,proj,null,null,null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        Log.w("changtoUri",cursor.getString(column_index));
        return cursor.getString(column_index);
    }

    public String changeToPathMusic(Uri uri){
        String filepath = "";
        String[] filePathColumn = {MediaStore.Audio.Media.DATA};
        Cursor cursor = getContentResolver().query( uri, filePathColumn, null, null, null );
        if ( cursor.moveToFirst() ) {
            int index = cursor.getColumnIndex( filePathColumn[0] );
            if ( index > -1 ) {
                filepath = cursor.getString( index );
                Log.d(TAG, filepath);
            }
        }
        cursor.close();
        return filepath;
    }

    protected void cutImage(Uri uri) {
        if (!isChoseGranted){
            return;
        }
        if (uri == null) {
            Log.d(TAG, "The uri is not exist.");
        }
        Intent intent = new Intent("com.android.camera.action.CROP");
        //com.android.camera.action.CROP这个action是用来裁剪图片用的
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);

//        List<ResolveInfo> resInfoList = getPackageManager()
//                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
//        for (ResolveInfo resolveInfo : resInfoList) {
//            String packageName = resolveInfo.activityInfo.packageName;
//            grantUriPermission(packageName, tempUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
//                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
//        }

        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        intent.putExtra("scale", true);
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 1000);
        intent.putExtra("outputY", 1000);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, cropUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        startActivityForResult(intent, CROP_SMALL_PICTURE);
    }
    public void upload(){
        if(vilidate()){
            MultipartBody.Part filepart = file2MultipartBody(new File(Environment.getExternalStorageDirectory(), "crop.jpg"));
            MultipartBody.Part filepartmusic = file2MultipartBody(new File(musicFilePath));
            List<MultipartBody.Part> filelist = new ArrayList<>();
            filelist.add(filepart);
            filelist.add(filepartmusic);

            Music music = new Music();
            ObserverOnNextListener<MyResponse> observerOnNextListener = new ObserverOnNextListener<MyResponse>() {
                @Override
                public void onNext(MyResponse myResponse) {
                    if ("1".equals(myResponse.getState())){
                        ToastUtil.normal("上传成功");
                        onBackPressed();
                    }else{
                        if (myResponse.getError() != null) {
                            ToastUtil.error(myResponse.getError());
                        }
                    }
                }
            };
            ApiMethods.uploadMusic(new ProgressObserver<MyResponse>(this, observerOnNextListener, "上传中..", true, true), xh, musicName.getText().toString(), filelist);
        }
    }
    public MultipartBody.Part file2MultipartBody(File filepath){
        RequestBody file = RequestBody.create(MediaType.parse("application/octet-stream"), filepath);
        String name = filepath.getName();
        MultipartBody.Part filePart = null;
        try {
            filePart = MultipartBody.Part.createFormData("file", URLEncoder.encode(name, "UTF-8"), file);
        } catch (UnsupportedEncodingException e) {
            ToastUtil.error(e.getMessage());
        }
        return filePart;
    }
}
