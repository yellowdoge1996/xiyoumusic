package com.xiyoumusic.app.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.loader.content.CursorLoader;
import androidx.navigation.Navigation;

import android.Manifest;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.TextView;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.fragment.UserInfoFragment;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.viewModel.UserViewModel;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import io.reactivex.functions.Consumer;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class ModifyUserInfoActivity extends BaseActivity implements View.OnClickListener,UserInfoFragment.OnFragmentInteractionListener{

    private final String TAG = this.getClass().getSimpleName();
    private Toolbar mToolbar;
    private UserViewModel userViewModel;
    public TextView titleTextView;

    protected static final int CHOOSE_PICTURE = 0;
    protected static final int TAKE_PICTURE = 1;
    protected static Uri tempUri;
    protected static Uri cropUri;
    private static final int CROP_SMALL_PICTURE = 2;
    public static int FRAGMENT_USER_INFO = 1;
    public static int FRAGMENT_OTHER = 2;
    private boolean isPictureGranted = false;
    private boolean isChoseGranted = false;

    public static int modifyFlag = RESULT_CANCELED;
    public static int fragmentFlag = FRAGMENT_USER_INFO;

    @Override
    protected void onDestroy() {
        File tempFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        File cropFile = new File(Environment.getExternalStorageDirectory(), "crop.jpg");
        tempFile.delete();
        cropFile.delete();
        super.onDestroy();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState!= null) {
            String FRAGMENTS_TAG =  "android:support:fragments";
            savedInstanceState.remove(FRAGMENTS_TAG);
        }
        setContentView(R.layout.activity_modify_user_info);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);

        mToolbar.setNavigationOnClickListener(this);

        File tempFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
        tempUri = FileProvider.getUriForFile(ToastUtil.getContext()
                , ConstantTools.authority
                , tempFile);

        File cropFile = new File(Environment.getExternalStorageDirectory(), "crop.jpg");
        cropUri = Uri.fromFile(cropFile);

        titleTextView = findViewById(R.id.title_name_user_info);
        initViewModel();
    }

    @Override
    public void onClick(View view) {
        if ("Toolbar".equals(view.getParent().getClass().getSimpleName()) && view.getId() != R.id.title_name_user_info){
            onBackPressed();
        }
        switch (view.getId()){
            default:break;
        }
    }

    @Override
    public void onBackPressed() {
        if (fragmentFlag == FRAGMENT_USER_INFO) {
            MainActivity.ismodify = true;
            Intent intent = new Intent();
            setResult(modifyFlag, intent);
            super.onBackPressed();
        }else{
            super.onBackPressed();
        }
    }

    public void initViewModel(){
        Map<String,?> userdetail = SPTool.getAll(ToastUtil.getContext());
        User user = new User();
        user.setXh(userdetail.get("xh").toString());
        user.setGxqm(userdetail.get("gxqm").toString());
        user.setMm(userdetail.get("mm").toString());
        user.setNc(userdetail.get("nc").toString());
        user.setSr(userdetail.get("sr").toString());
        user.setTxlj(userdetail.get("txlj").toString());
        user.setUuid(userdetail.get("uuid").toString());

        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);

        userViewModel.user.setValue(user);
    }

    @Override
    public void onFragmentInteraction(String infoName, User user) {
        switch (infoName){
            case "nc":
                fragmentFlag = FRAGMENT_OTHER;
                titleTextView.setText("修改昵称");
                Navigation.findNavController(this, R.id.nav_host2_fragment).navigate(R.id.action_userInfoFragment_to_modifyNcFragment);
                break;
            case "gxqm":
                fragmentFlag = FRAGMENT_OTHER;
                titleTextView.setText("修改个性签名");
                Navigation.findNavController(this, R.id.nav_host2_fragment).navigate(R.id.action_userInfoFragment_to_modifyGxqmFragment);
                break;
            case "txlj":
                /**
                 * 显示修改图片的对话框
                 */
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("设置头像");
                String[] items = { "选择本地照片", "拍照" };
                builder.setNegativeButton("取消", null);
                builder.setItems(items, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case CHOOSE_PICTURE: // 选择本地照片
                                requestPermissionFile();
                                if (isChoseGranted) {
                                    startChoose();
                                }
                                break;
                            case TAKE_PICTURE: // 拍照
                                requestPermissionFile();
                                requestPermissionCamera();
                                if (isChoseGranted && isPictureGranted) {
                                    takePicture();
                                }
                                break;
                        }
                    }
                });
                builder.show();
            case "resume":
                fragmentFlag = FRAGMENT_USER_INFO;
                titleTextView.setText("账号资料");
                break;
            case "save":
                save(user);
                break;
            default:break;
        }
    }

    public void takePicture(){
        Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        openCameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        openCameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);



//        File imagePath = new File(getFilesDir(), "images");
//        File newFile = new File(imagePath, "default_image.jpg");

        // 将拍照所得的相片保存到SD卡根目录
        List<ResolveInfo> resInfoList = getPackageManager()
                .queryIntentActivities(openCameraIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, tempUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempUri);
        startActivityForResult(openCameraIntent, TAKE_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TAKE_PICTURE:
                    Uri mediaUri = getImageContentUri(this
                            , new File(Environment.getExternalStorageDirectory(), "temp.jpg"));
                    Log.d(TAG, mediaUri.toString());
                    cutImage(mediaUri); // 对图片进行裁剪处理
                    break;
                case CHOOSE_PICTURE:
                    Log.d(TAG,data.getData().toString());
                    String filepath = changeToPath(data.getData());
                    Log.d("filepath", filepath);
                    Uri mediaUri2 = getImageContentUri(this, new File(filepath));
                    Log.d(TAG, mediaUri2.toString());
                    cutImage(mediaUri2); // 对图片进行裁剪处理
                    break;
                case CROP_SMALL_PICTURE:
                    uploadImage(); // 让刚才选择裁剪得到的图片显示在界面上
                    break;
            }
        }else {
            File tempFile = new File(Environment.getExternalStorageDirectory(), "temp.jpg");
            if (tempFile.exists()){
                if (!tempFile.delete()){
                    ToastUtil.normal("tempImage删除失败");
                }
            }

            File cropFile = new File(Environment.getExternalStorageDirectory(), "crop.jpg");
            if (cropFile.exists()){
                if (!cropFile.delete()){
                    ToastUtil.normal("cropImage删除失败");
                }
            }
        }

    }

    /**
     * 裁剪图片方法实现
     */
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

        List<ResolveInfo> resInfoList = getPackageManager()
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, tempUri, Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }

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
    /**
     * 保存裁剪之后的图片数据
     */
    protected void uploadImage() {
            MultipartBody.Part filepart = file2MultipartBody(new File(Environment.getExternalStorageDirectory(), "crop.jpg"));

            ObserverOnNextListener<MyResponse> observerOnNextListener = new ObserverOnNextListener<MyResponse>() {
                @Override
                public void onNext(MyResponse myResponse) {
                    if ("1".equals(myResponse.getState())) {
                        List<User> userList = myResponse.getUserData();
                        if (userList.size() > 0) {
                            User user = userList.get(0);
                            SPTool.put("xh", user.getXh());
                            SPTool.put("mm", user.getMm());
                            SPTool.put("uuid", user.getUuid());
                            SPTool.put("nc", user.getNc());
                            SPTool.put("sr", user.getSr());
                            SPTool.put("gxqm", user.getGxqm());
                            SPTool.put("txlj",user.getTxlj());
                            ToastUtil.normal("上传成功");
                            userViewModel.user.postValue(user);
                            modifyFlag = RESULT_OK;
                        }
                    } else {
                        if (myResponse.getError() != null) {
                            ToastUtil.normal("上传失败");
                            Log.e(TAG, myResponse.getError());
                        }
                    }
                }
            };

            ApiMethods.uploadTx(new ProgressObserver<MyResponse>(this, observerOnNextListener, "头像上传中..", false, true),
                    userViewModel.user.getValue().getXh(), filepart);

        }

    public MultipartBody.Part file2MultipartBody(File txImage){
        RequestBody file = RequestBody.create(MediaType.parse("application/octet-stream"), txImage);
        String name = txImage.getName();
        MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", name, file);
        return filePart;
    }
    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host2_fragment).navigateUp();
    }
    private void requestPermissionCamera() {
        RxPermissions rxPermission = new RxPermissions(this);
        rxPermission
                .requestEach(
                        Manifest.permission.CAMERA)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        if (permission.granted) {
                            // 用户已经同意该权限
                            isPictureGranted = true;
                        } else if (permission.shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）,那么下次再次启动时，还会提示请求权限的对话框
                            ToastUtil.error("你已经拒绝了相机权限");
                            isPictureGranted =false;
                        } else {
                            // 用户拒绝了该权限，并且选中『不再询问』
                            ToastUtil.error("你已经拒绝了相机权限");
                            isPictureGranted = false;
                        }
                    }
                });
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
    public void save(User newUser){
        ObserverOnNextListener<MyResponse> observerOnNextListener = new ObserverOnNextListener<MyResponse>() {
            @Override
            public void onNext(MyResponse myResponse) {
                if ("1".equals(myResponse.getState())) {
                    List<User> userList = myResponse.getUserData();
                    if (userList.size() > 0) {
                        User user = userList.get(0);
                        SPTool.put("xh", user.getXh());
                        SPTool.put("mm", user.getMm());
                        SPTool.put("uuid", user.getUuid());
                        SPTool.put("nc", user.getNc());
                        SPTool.put("sr", user.getSr());
                        SPTool.put("gxqm", user.getGxqm());
                        SPTool.put("txlj",user.getTxlj());
                        ToastUtil.normal("保存成功");
                        userViewModel.user.postValue(user);
                        modifyFlag = RESULT_OK;
                    }
                } else {
                    if (myResponse.getError() != null) {
                        ToastUtil.error(myResponse.getError());
                    }
                }
            }
        };

        ApiMethods.modifyUserInfo(new ProgressObserver<MyResponse>(this, observerOnNextListener, "保存中..", false, true), newUser);
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

    public void startChoose(){
        Intent openAlbumIntent = new Intent(
                Intent.ACTION_PICK);
        openAlbumIntent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        //用startActivityForResult方法，待会儿重写onActivityResult()方法，拿到图片做裁剪操作
        startActivityForResult(openAlbumIntent, CHOOSE_PICTURE);
    }
}
