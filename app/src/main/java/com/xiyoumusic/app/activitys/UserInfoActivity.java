package com.xiyoumusic.app.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.utils.ConstantTools;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.viewModel.UserViewModel;
import com.xiyoumusic.app.views.CircleImageView;
import com.xiyoumusic.app.views.MyImageDialog;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener{

    private Toolbar mToolbar;
    private CircleImageView tx;
    private TextView nc;
    private TextView xh;
    private TextView sr;
    private TextView gxqm;
    private Button chat;
    private ImageButton guanzhu;

    private UserViewModel userViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);
        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(v -> finish());

        tx = findViewById(R.id.user_tx);
        nc = findViewById(R.id.user_nc);
        xh = findViewById(R.id.user_xh);
        sr = findViewById(R.id.user_sr);
        gxqm = findViewById(R.id.user_gxqm);
        chat = findViewById(R.id.chat);
        guanzhu = findViewById(R.id.guanzhu);
        userViewModel = ViewModelProviders.of(this).get(UserViewModel.class);
        initView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.guanzhu:
                guanzhu.setClickable(false);
                guanzhu();
                break;
            case R.id.chat:
                break;
            default:break;
        }
    }

    public void guanzhu(){
        if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
            Intent i = new Intent(UserInfoActivity.this, LoginActivity.class);
            startActivity(i);
            return;
        }
        ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                guanzhu.setClickable(true);
                if (Boolean.parseBoolean(myResponse.getMsg()) && !(boolean)guanzhu.getTag()){
                    guanzhu.setImageResource(R.drawable.ic_favorite_24px);
                    guanzhu.setTag(true);
                    ToastUtil.normal("关注成功");
                }else if (!Boolean.parseBoolean(myResponse.getMsg()) && (boolean)guanzhu.getTag()){
                    guanzhu.setImageResource(R.drawable.ic_favorite_border_24px);
                    guanzhu.setTag(false);
                    ToastUtil.normal("成功取消关注");
                }
            }else{
                guanzhu.setClickable(true);
                ToastUtil.normal(myResponse.getError());
            }
        };
        ApiMethods.guanzhu(new ProgressObserver<>(this,
                        observerOnNextListener, "加载中...", false, true),
                SPTool.getSharedPreferences().getString("xh", "000000000000"),
                userViewModel.sczxh.getValue());
    }

    @Override
    protected void onResume() {
        super.onResume();
        initGuanzhu();
    }

    public void initView(){
        Intent intent = getIntent();
        String txlj = intent.getStringExtra("txlj");
        String ncstr = intent.getStringExtra("nc");
        String xhstr = intent.getStringExtra("xh");
        userViewModel.sczxh.setValue(xhstr);
        String srstr = intent.getStringExtra("sr");
        String gxqmstr = intent.getStringExtra("gxqm");
        if (txlj == null || "".equals(txlj)){
            tx.setImageDrawable(this.getResources().getDrawable(R.drawable.ic_launcher_foreground));
        }else{
            Glide.with(this)
                    .load(ConstantTools.baseUrl+txlj)
                    .into(tx);
        }
        tx.setOnClickListener(v -> {
            tx.setDrawingCacheEnabled(true);
            MyImageDialog myImageDialog = new MyImageDialog(UserInfoActivity.this, R.style.dialogWindowAnim,0,-300,tx.getDrawingCache());
            myImageDialog.show();
        });
        nc.setText(ncstr);
        xh.setText(xhstr);
        sr.setText(srstr);
        if (gxqmstr != null && !"".equals(gxqmstr)){
            gxqm.setText(gxqmstr);
        }
    }

    public void initGuanzhu(){
        ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                guanzhu.setClickable(true);
                guanzhu.setOnClickListener(this);
                if (Boolean.parseBoolean(myResponse.getMsg())){
                    guanzhu.setImageResource(R.drawable.ic_favorite_24px);
                    guanzhu.setTag(true);
                }else{
                    guanzhu.setImageResource(R.drawable.ic_favorite_border_24px);
                    guanzhu.setTag(false);
                }
            }else{
                guanzhu.setClickable(true);
                ToastUtil.normal(myResponse.getError());
            }
        };
        ApiMethods.isGuanzhu(new ProgressObserver<>(this,
                observerOnNextListener, "加载中...", false, true),
                SPTool.getSharedPreferences().getString("xh", "000000000000"),
                userViewModel.sczxh.getValue());
    }
}
