package com.xiyoumusic.app.activitys;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.executor.ControlPanel;
import com.xiyoumusic.app.fragment.PlayFragment;
import com.xiyoumusic.app.service.AudioPlayer;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.viewModel.UserViewModel;
import com.xiyoumusic.app.views.CircleImageView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener ,View.OnClickListener{

    public static final int LOGIN_RESULT = 1;
    public static final int MODIFY_USER_INFO_RESULT = 2;
    public static final int FRAGMENT_MAIN = 100;
    public static final int FRAGMENT_OTHER = 200;
    private boolean isPlayFragmentShow = false;
    public static int fragmentFlag = FRAGMENT_MAIN;

    private long mLastTime = 0;
    private static String uuid = null;
    public NavigationView navigationView;
    public static DrawerLayout drawer;
    public TextView usernameView;
    public TextView gxqm;
    public CircleImageView txImageView;
    public ImageButton searchBtn;
    private ControlPanel controlPanel;
    private LinearLayout flPlayBar;
    private PlayFragment mPlayFragment;
    private UserViewModel userViewModel;
    public static Boolean ismodify = false;

    private final String TAG = this.getClass().getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        initView();
        controlPanel = new ControlPanel(flPlayBar);
        AudioPlayer.get().addOnPlayEventListener(controlPanel);
        loginByUUID();
//        autoLoadUser();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ismodify) {
            autoLoadUser();
            ismodify = false;
        }
    }

    @Override
    protected void onServiceBound() {
        parseIntent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        super.onCreateOptionsMenu(menu);
        return true;
    }

    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                    MenuItem item = menu.getItem(0);
                    View actionView = item.getActionView();
                    actionView.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_upload) {
            if (uuid == null || "".equals(uuid)){
                Intent i = new Intent(MainActivity.this, LoginActivity.class);
                startActivityForResult(i, LOGIN_RESULT);
                drawer.closeDrawer(GravityCompat.START);
            }else {
                Intent i = new Intent(MainActivity.this, UploadMusicActivity.class);
                i.putExtra("xh", SPTool.getAll(ToastUtil.getContext()).get("xh").toString());
                startActivity(i);
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        boolean islogout = false;
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_logout) {
            if (SPTool.getSharedPreferences().getString("uuid", "").equals("")){
                ToastUtil.normal("尚未登录");
            }else{
                logout();
            }
            if (fragmentFlag == FRAGMENT_OTHER){
                onBackPressed();
            }
            userViewModel.islogin.setValue(false);
        } else if (id == R.id.nav_exit) {
            android.os.Process.killProcess(android.os.Process.myPid());
        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode){
            case LOGIN_RESULT:
                if (resultCode == RESULT_CANCELED){
                    ToastUtil.error("登录失败");
                    userViewModel.islogin.setValue(false);
                }else if (resultCode == RESULT_OK){
                    userViewModel.islogin.setValue(true);
                    if (data != null) {
                        autoLoadUser();
                    }else{
                        Log.d(TAG,"登录返回数据异常");
                    }
                }
                break;
            case MODIFY_USER_INFO_RESULT:
                if (resultCode == RESULT_OK){
                    autoLoadUser();
                }
                break;
            default:break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.imageView:
                if (uuid == null) {
                    Intent i = new Intent(MainActivity.this, LoginActivity.class);
                    startActivityForResult(i, LOGIN_RESULT);
                    drawer.closeDrawer(GravityCompat.START);
                }else {
                    Intent i = new Intent(MainActivity.this, ModifyUserInfoActivity.class);
                    startActivityForResult(i,MODIFY_USER_INFO_RESULT);
                    drawer.closeDrawer(GravityCompat.START);
                }
                break;
            case R.id.btn_search:
                Intent i = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(i);
                break;
            case R.id.fl_play_bar:
                showPlayingFragment();
                break;
            default:break;
        }
    }
    /*
    初始化界面
     */
    private void initView(){
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        drawer.addDrawerListener(new DrawerLayout.DrawerListener() {
            @Override
            public void onDrawerSlide(@NonNull View view, float v) {
                View content = drawer.getChildAt(0);
                View menu = drawer.getChildAt(1);
                content.setTranslationX(menu.getMeasuredWidth() * v);//0~width
            }

            @Override
            public void onDrawerOpened(@NonNull View view) {

            }

            @Override
            public void onDrawerClosed(@NonNull View view) {
                if (!userViewModel.islogin.getValue().booleanValue() && fragmentFlag == FRAGMENT_OTHER){
                    onBackPressed();
                }
            }

            @Override
            public void onDrawerStateChanged(int i) {

            }
        });

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        usernameView = navigationView.getHeaderView(0).findViewById(R.id.username);
        gxqm = navigationView.getHeaderView(0).findViewById(R.id.gxqm);

        txImageView = navigationView.getHeaderView(0).findViewById(R.id.imageView);
        if (txImageView != null) {
            txImageView.setOnClickListener(this);
        }
        searchBtn = toolbar.findViewById(R.id.btn_search);
        searchBtn.setOnClickListener(this);
        flPlayBar = findViewById(R.id.fl_play_bar);
        flPlayBar.setOnClickListener(this);

    }
    public void autoLoadUser(){
        Map<String, ?> userDetails = SPTool.getAll(ToastUtil.getContext());
        Set keys = userDetails.keySet();
        for (Object key1 : keys) {
            String key = key1.toString();
            switch (key) {
                case "uuid":
                    uuid = "".equals(userDetails.get("uuid")) ? null : userDetails.get("uuid").toString();
                    break;
                case "nc":
                    usernameView.setText(userDetails.get("nc").toString());
                    break;
                case "gxqm":
                    gxqm.setText(("".equals(userDetails.get("gxqm")) ? "该用户有点懒，没有写个性签名" : userDetails.get("gxqm")).toString());
                    break;
                case "txlj":
                    if (!"".equals(userDetails.get("txlj"))) {
                        Glide.with(this)
                                .load(ConstantTools.baseUrl + userDetails.get("txlj"))
                                .into(txImageView);
                    } else {
                        txImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_launcher_foreground));
                    }
                    break;
                default:
                    break;
            }
        }
    }

    public void loginByUUID() {
        if (SPTool.contains(ToastUtil.getContext(),"uuid")) {
            ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
                if ("1".equals(myResponse.getState())) {
                    List<User> userList = myResponse.getUserData();
                    if (userList.size() > 0) {
                        User user = userList.get(0);
                        ToastUtil.normal("欢迎使用西柚音乐！用户：" + user.getXh() + "你的uuid是：" + user.getUuid());
                        autoLoadUser();
                    }
                    userViewModel.islogin.setValue(true);
                } else {
                    SPTool.remove(ToastUtil.getContext(), "xh");
                    SPTool.remove(ToastUtil.getContext(), "mm");
                    SPTool.remove(ToastUtil.getContext(), "uuid");
                    SPTool.remove(ToastUtil.getContext(), "nc");
                    SPTool.remove(ToastUtil.getContext(), "sr");
                    SPTool.remove(ToastUtil.getContext(), "gxqm");
                    SPTool.remove(ToastUtil.getContext(), "txlj");
                    if (myResponse.getError() != null) {
                        ToastUtil.error(myResponse.getError());
                    }
                    userViewModel.islogin.setValue(false);
                }
            };
            String uuid = SPTool.getAll(ToastUtil.getContext()).get("uuid").toString();
            ApiMethods.loginByUUID(new ProgressObserver<>(this, observerOnNextListener, null, false, false), uuid);
        }else {
            clearUser();
        }
    }

    @Override
    public void onBackPressed() {
        if (mPlayFragment != null && isPlayFragmentShow) {
            hidePlayingFragment();
            return;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (fragmentFlag == FRAGMENT_MAIN){
            if ((System.currentTimeMillis() - mLastTime) > 1000) {
                ToastUtil.normal("再次点击返回键切换到桌面");
                mLastTime = System.currentTimeMillis();
            } else {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(startMain);
            }
        }else{
            super.onBackPressed();
        }
    }

    public void logout(){
        ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
            if ("1".equals(myResponse.getState())) {
                SPTool.remove(ToastUtil.getContext(),"txlj");
                SPTool.remove(ToastUtil.getContext(),"xh");
                SPTool.remove(ToastUtil.getContext(),"mm");
                SPTool.remove(ToastUtil.getContext(),"uuid");
                SPTool.remove(ToastUtil.getContext(),"nc");
                SPTool.remove(ToastUtil.getContext(),"sr");
                SPTool.remove(ToastUtil.getContext(),"gxqm");
                uuid = null;
                clearUser();
                ToastUtil.normal("退出登录成功");
            } else {
                if (myResponse.getError() != null) {
                    ToastUtil.normal(myResponse.getError());
                }
            }
        };

        ApiMethods.logout(new ProgressObserver<>(this, observerOnNextListener, "注销中..", true, true),
                SPTool.getAll(ToastUtil.getContext()).get("xh").toString(),SPTool.getAll(ToastUtil.getContext()).get("uuid").toString());
    }

    public void clearUser(){
        txImageView.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_launcher_foreground));
        usernameView.setText("点击头像登录");
        gxqm.setText("个性签名");
    }

    @Override
    public boolean onSupportNavigateUp() {
        return Navigation.findNavController(this, R.id.nav_host_fragment).navigateUp();
    }

    private void parseIntent() {
        Intent intent = getIntent();
        if (intent.hasExtra(ConstantTools.EXTRA_NOTIFICATION)) {
            showPlayingFragment();
            setIntent(new Intent());
        }
    }

    private void showPlayingFragment() {
        if (isPlayFragmentShow) {
            return;
        }
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(R.anim.fragment_slide_up, 0);
        if (mPlayFragment == null) {
            mPlayFragment = new PlayFragment();
            ft.replace(android.R.id.content, mPlayFragment);
        } else {
            ft.show(mPlayFragment);
        }
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = true;
    }

    private void hidePlayingFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(0, R.anim.fragment_slide_down);
        ft.hide(mPlayFragment);
        ft.commitAllowingStateLoss();
        isPlayFragmentShow = false;
    }
}
