package com.example.zzy.accounttest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.example.zzy.accounttest.R;
import com.example.zzy.accounttest.http.HttpLoader;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.functions.Action1;

public class ChangeNumberActivity extends AppCompatActivity {

    private EditText et_tel_old;
    private EditText et_tel_new;
    private Button btn_num;
    private String tel_old;
    private String tel_new;
    private String user_name;
//    String flag;

    private HttpLoader httpLoader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_changenumber);

        tel_old = getIntent().getStringExtra("tel_old");
        user_name = getIntent().getStringExtra("user_name");
        et_tel_old = (EditText)findViewById(R.id.tel_old);
        et_tel_old.setText(tel_old);
        et_tel_new = (EditText)findViewById(R.id.tel_new);
        btn_num = (Button)findViewById(R.id.btn_num);

        httpLoader = new HttpLoader();
        btn_num.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                tel_new = et_tel_new.getText().toString().trim();
                if ("".equals(tel_new)){
                    Toast.makeText(ChangeNumberActivity.this, "手机号不能为空！",Toast.LENGTH_LONG).show();
                }else{
                    if(!isMobileNO(tel_new)){
//                        Toast.makeText(getApplicationContext(), "手机号码格式有误！",Toast.LENGTH_LONG).show();
                        Toast.makeText(ChangeNumberActivity.this, "手机号码格式有误！",Toast.LENGTH_LONG).show();
                    }else{
                        change_phone(UserManage.getInstance().getUserInfo(ChangeNumberActivity.this).getUserName(),
                                tel_new);
//                        System.out.println("服务器返回的信号"+flag);
                    }
                }

            }


        });
    }

    public boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("^((13[0-9])|(15[^4,\\D])|(18[0-9]))\\d{8}$");
        Matcher m = p.matcher(mobiles);

        return m.matches();
    }

    //网络通信，修改电话
    public void change_phone (String name, String tel){
        httpLoader.change_phone(name, tel).subscribe(new Action1<String>() {
            @Override
            public void call(String flag) {
                System.out.println("服务器返回数据" +flag);
                //获取成功，数据在loginReturnObject
//                Toast.makeText(ChangeNumberActivity.this, "获取数据" + flag ,
//                        Toast.LENGTH_LONG).show();
//                flag = changePhoneObject.getFlag();
                if(flag.equals("1")){
                    Toast.makeText(ChangeNumberActivity.this, "修改成功",Toast.LENGTH_LONG).show();
                    finish();
                }else if(flag.equals("2")){
                    Toast.makeText(ChangeNumberActivity.this, "修改失败",Toast.LENGTH_LONG).show();
                    finish();
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //获取失败
                Log.e("TAG", "异常ChangeNumerror message:" + throwable.getMessage());
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
}
