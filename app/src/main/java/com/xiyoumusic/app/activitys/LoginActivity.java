package com.xiyoumusic.app.activitys;

import android.content.Intent;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.utils.retrofit.ApiService;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.utils.retrofit.RetrofitApi;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import java.text.SimpleDateFormat;
import java.util.List;

public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private String TAG = this.getClass().getSimpleName();
    ApiService service = RetrofitApi.getService();
    private static final String ACTION_LOGIN = "login";
    private static final String ACTION_REGISTER = "register";

    // UI references.
    private TextInputEditText mxhView;
    private TextInputEditText mmmView;
    private Toolbar mToolbar;
    private TextView mForgetPassword;
    private Button mBtnLogin;
    private Button mBtnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mToolbar = findViewById(R.id.toolbar);
        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationOnClickListener(this);

        mForgetPassword = findViewById(R.id.forget_mm);
        mBtnLogin = findViewById(R.id.btn_login);
        mBtnRegister = findViewById(R.id.btn_register);
        mBtnLogin.setOnClickListener(this);
        mBtnRegister.setOnClickListener(this);

        // Set up the login form.
        mxhView = findViewById(R.id.xh);
        mmmView = findViewById(R.id.mm);
        /**
        *
        *测试用账号
        *
        */
        mxhView.setText("201531060482");
        mmmView.setText("chu520");
    }



    private boolean isxhValid(String email) {
        return email.length() == 12;
    }

    private boolean isPasswordValid(String password) {
        return (password.length() >= 6 && password.length() < 16);
    }

    @Override
    public void onClick(View view) {
        if ("Toolbar".equals(view.getParent().getClass().getSimpleName()) && view.getId() != R.id.title_name
        && view.getId() != R.id.forget_mm){
            onBackPressed();
        }
        switch (view.getId()){
            case R.id.btn_login:
                attemptLoginorRegister(ACTION_LOGIN);
                break;
            case R.id.btn_register:
                attemptLoginorRegister(ACTION_REGISTER);
                break;
            default:break;
        }
    }

    private void attemptLoginorRegister(String action) {
        mxhView.setError(null);

        String xh = mxhView.getText().toString();
        String mm = mmmView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        String errorInfo = "";
        if (TextUtils.isEmpty(mm) && !isPasswordValid(mm)) {
            errorInfo = (getString(R.string.error_invalid_password));
            focusView = mmmView;
            cancel = true;
        }

        if (TextUtils.isEmpty(xh)) {
            mxhView.setError(getString(R.string.error_field_required));
            errorInfo = null;
            focusView = mxhView;
            cancel = true;
        } else if (!isxhValid(xh)) {
            errorInfo = getString(R.string.error_invalid_email);
            focusView = mxhView;
            cancel = true;
        }

        if (cancel) {
            if (errorInfo != null) {
                ToastUtil.error(errorInfo);
            }
            focusView.requestFocus();
        } else {
            ObserverOnNextListener<MyResponse> observerOnNextListener = new ObserverOnNextListener<MyResponse>() {
                @Override
                public void onNext(MyResponse myResponse) {
                    if ("1".equals(myResponse.getState())){
                        List<User> userList = myResponse.getUserData();
                        if (userList.size()>0){
                            User user = userList.get(0);
                            SPTool.put("xh",user.getXh());
                            SPTool.put("mm",user.getMm());
                            SPTool.put("uuid",user.getUuid());
                            SPTool.put("nc",user.getNc());
                            SPTool.put("sr",user.getSr());
                            SPTool.put("gxqm",user.getGxqm());
                            SPTool.put("txlj",user.getTxlj());
                            SPTool.put("uuid", user.getUuid());
                            ToastUtil.normal("欢迎使用西柚音乐！用户："+user.getXh()+"你的uuid是："+user.getUuid());

                            MainActivity.ismodify = true;
                            Intent intent = new Intent();
                            intent.putExtra("uuid",user.getUuid());
                            setResult(RESULT_OK,intent);
                            finish();
                        }
                    }else{
//                        SPTool.remove(ToastUtil.getContext(),"xh");
//                        SPTool.remove(ToastUtil.getContext(),"mm");
//                        SPTool.remove(ToastUtil.getContext(),"uuid");
//                        SPTool.remove(ToastUtil.getContext(),"nc");
//                        SPTool.remove(ToastUtil.getContext(),"sr");
//                        SPTool.remove(ToastUtil.getContext(),"gxqm");
                        if (myResponse.getError() != null) {
                            ToastUtil.error(myResponse.getError());
                            mmmView.setText(null);
                        }
                    }
                }
            };

            if ("login".equals(action)) {
                ApiMethods.login(new ProgressObserver<MyResponse>(this, observerOnNextListener, "登录中..", true, true), xh, mm);
            }else if ("register".equals(action)){
                ApiMethods.register(new ProgressObserver<MyResponse>(this, observerOnNextListener, "注册中..", false, true), xh, mm);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        setResult(RESULT_CANCELED,intent);
        super.onBackPressed();
    }
}

