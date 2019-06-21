package com.example.zzy.accounttest.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzy.accounttest.Bean.LoginReturnObject;
import com.example.zzy.accounttest.Bean.UserInfo;
import com.example.zzy.accounttest.Bean.UserManage;
import com.example.zzy.accounttest.R;
import com.example.zzy.accounttest.http.Fault;
import com.example.zzy.accounttest.http.HttpLoader;
import com.google.gson.Gson;

import rx.functions.Action1;

public class LoginActivity extends AppCompatActivity {
    private EditText et_username, et_pwd;
    private Button btn_login;
    private TextView  tv_register;
    private HttpLoader httpLoader;
    public int login_get;
    public int monitor_get;
    public String areaName_get;
    public String companyName_get;
    public String username;
    public String password;
    private long exitTime = 0;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置禁止横屏
        initView();
    }

    private void initView() {
        et_username =  this.findViewById(R.id.et_username);
        et_pwd =  this.findViewById(R.id.et_pwd);
        tv_register =  this.findViewById(R.id.tv_register);
        btn_login =  this.findViewById(R.id.btn_login);
        UserInfo userInfo= UserManage.getInstance().getUserInfo(LoginActivity.this);
        et_username.setText(userInfo.getUserName());
        et_pwd.setText(userInfo.getPassword());
        //有用户和密码
        if(UserManage.getInstance().hasUserInfo(LoginActivity.this)){
            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }


        //登录
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                httpLoader = new HttpLoader();
                username=et_username.getText().toString().trim();
                password=et_pwd.getText().toString().trim();
                if (username.isEmpty()){
                    Toast.makeText(LoginActivity.this, "请输入用户名", Toast.LENGTH_LONG).show();
                }else if ("".equals(password)){
                    Toast.makeText(LoginActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                }else{
                    Log.d("TAG","用户名："+username+"  密码："+password);
                    login(username,password);
                }
            }
        });

        //普通用户注册
        tv_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
    }




    //网络通信，登陆
    public void login(String name, final String password) {
        Log.d("TAG","进入通信");
        httpLoader.login(name, password).subscribe(new Action1<String>() {
            @Override
            public void call(String flags) {
                Log.d("TAG","数据" + new Gson().toJson(flags));
                if(flags.equals("0")) {
                    Toast.makeText(LoginActivity.this, "用户名不存在", Toast.LENGTH_SHORT).show();
                }else if(flags.equals("1")){
                    Toast.makeText(LoginActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                }else if(flags.equals("2")){
                    Log.d("TAG","登录成功");
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                    UserManage.getInstance().saveUserInfo(LoginActivity.this, username, password);

                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("username", username);
//                    bundle.putString("areaname", areaName_get);
//                    bundle.putString("companyname", companyName_get);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
//                Toast.makeText(LoginActivity.this, "获取数据" + loginReturnObject.getLogin() + "&" + loginReturnObject.getMonitor(),
//                        Toast.LENGTH_LONG).show();
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //获取失败
                Log.e("TAG", "error message:" + throwable.getMessage());
                if (throwable instanceof Fault) {
                    Fault fault = (Fault) throwable;
                    if (fault.getErrorCode() == 404) {
                        //错误处理
                    } else if (fault.getErrorCode() == 500) {
                        //错误处理
                    } else if (fault.getErrorCode() == 501) {
                        //错误处理
                    }
                }

            }
        });
    }

    /**
     * 返回键
     * @param keyCode
     * @param event
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }
    /**
     * 连续点击两次返回键退出应用
     */
    private void exit(){
        if((System.currentTimeMillis() - exitTime)>2000){
            Log.e("再按一次退出程序","app");
            Toast.makeText(getApplicationContext(),"再按一次退出程序",Toast.LENGTH_LONG).show();
            exitTime = System.currentTimeMillis();
            Log.e("exitTime","app");
        }else {
            finish();
            Log.e("退出","app");
            System.exit(0);
        }
    }
}
