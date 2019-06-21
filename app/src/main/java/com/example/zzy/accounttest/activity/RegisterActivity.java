package com.example.zzy.accounttest.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.zzy.accounttest.R;
import com.example.zzy.accounttest.http.Fault;
import com.example.zzy.accounttest.http.HttpLoader;
import com.google.gson.Gson;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.functions.Action1;

public class RegisterActivity extends AppCompatActivity {
    private EditText et_username;
    private EditText et_pwd;
    private EditText et_pwd_again;
    private Button reg_ok;
    private HttpLoader httpLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);//设置禁止横屏
        initView();

    }

    private void initView(){

        et_username=(EditText) findViewById(R.id.et_username);
        et_pwd=(EditText) findViewById(R.id.et_pwd);
        et_pwd_again=(EditText) findViewById(R.id.et_pwd_again);
        reg_ok=(Button)findViewById(R.id.reg_ok);

        //注册
        reg_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                httpLoader = new HttpLoader();
                String username=et_username.getText().toString().trim();
                String password=et_pwd.getText().toString().trim();
                String passwordconf=et_pwd_again.getText().toString().trim();//trim去掉不需要的空格
                int pwdlength=et_pwd.getText().length();
                if("".equals(username)){
                    Toast.makeText(RegisterActivity.this, "请输入用户名", Toast.LENGTH_LONG).show();
                }else if("".equals(password)){
                    Toast.makeText(RegisterActivity.this, "请输入密码", Toast.LENGTH_LONG).show();
                }else if(pwdlength < 6||pwdlength > 20){
                    Toast.makeText(RegisterActivity.this, "密码长度应为6到20之间", Toast.LENGTH_LONG).show();
                }else if(isChar(password)){
                    Toast.makeText(RegisterActivity.this, "密码不能全为字母", Toast.LENGTH_LONG).show();
                }else if(isNumeric(password)){
                    Toast.makeText(RegisterActivity.this, "密码不能全为数字", Toast.LENGTH_LONG).show();
                }else if("".equals(passwordconf)){
                    Toast.makeText(RegisterActivity.this, "请确认密码", Toast.LENGTH_LONG).show();
                }else if(!password.equals(passwordconf)){
                    Toast.makeText(RegisterActivity.this, "密码输入不一致，请重新输入", Toast.LENGTH_LONG).show();
                }
//                else if(!isMobileNO(phone)){
//                    Toast.makeText(RegisterActivity.this, "请输入正确的电话号码", Toast.LENGTH_LONG).show();
//                }else if(!isEmail(email)){
//                    Toast.makeText(RegisterActivity.this, "请输入正确的邮箱", Toast.LENGTH_LONG).show();
//                }
                else{
                    regist(username,password);
                }
            }
        });

    }

    public void regist(String name, String password) {
        httpLoader.regist(name, password).subscribe(new Action1<String>() {
            @Override
            public void call(String registReturnObject) {
                Log.d("TAG","数据" + new Gson().toJson(registReturnObject));
                //获取成功，数据在registReturnObject

                if(registReturnObject.equals("1")) {
                    Log.d("TAG","注册成功");
                    Intent intent=new Intent(RegisterActivity.this, LoginActivity.class);
                    startActivity(intent);
                    Toast.makeText(RegisterActivity.this, "注册成功", Toast.LENGTH_SHORT).show();
                    finish();
                }else if(registReturnObject.equals("2")){
                    Toast.makeText(RegisterActivity.this, "注册失败", Toast.LENGTH_SHORT).show();
                }else if(registReturnObject.equals("3")){
                    Toast.makeText(RegisterActivity.this, "该用户名已存在", Toast.LENGTH_SHORT).show();
                }
//                Toast.makeText(RegisterActivity.this, "获取数据" + registReturnObject.getRegist(),
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
    //判断手机格式是否正确

    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(14[0-9])|(17[0-9])|(19[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }
    //判断email格式是否正确
    public boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }
    /**
     * 判断字符串是否只包含数字
     * @param str
     * @return
     */
    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }
//    public static boolean isNumeric(String str) {
//        for (int i = str.length(); --i >= 0; ) {
//            if (!Character.isDigit(str.charAt(i))) {
//                return false;
//            }
//        }
//        return true;
//    }

    /** * 纯字母
     * @param fstrData
     * @return */
    public static boolean isChar(String fstrData){
        for (int i = fstrData.length(); --i >= 0; ) {
            char c = fstrData.charAt(i);
            if (!((c>='a'&&c<='z')   ||   (c>='A'&&c<='Z'))) {
                return false;
            }
        }
        return true;

    }


}
