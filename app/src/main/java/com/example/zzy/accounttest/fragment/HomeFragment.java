package com.example.zzy.accounttest.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzy.accounttest.Bean.CountS;
import com.example.zzy.accounttest.Bean.GetHomeBillInfo;
import com.example.zzy.accounttest.Bean.MyStringUtils;
import com.example.zzy.accounttest.Bean.UserManage;
import com.example.zzy.accounttest.Bean.homebill;
import com.example.zzy.accounttest.R;
import com.example.zzy.accounttest.activity.MainActivity;
import com.example.zzy.accounttest.adapter.HomeAdapter;
import com.example.zzy.accounttest.http.Fault;
import com.example.zzy.accounttest.http.HttpLoader;
import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Month;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import rx.functions.Action1;

public class HomeFragment extends Fragment {
    private View view, frame_home_head;
    private ListView lisView;
    private String username,month,IOclasses;
    private Date IOtime;
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    private int income;
    private HomeAdapter homeAdapter;
    private HttpLoader httpLoader;
    private TextView tv_expend;
    private TextView tv_income;
    private TextView frame_home_tv_month;
//    private SwipeRefreshLayout swipeRefreshLayout;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.content_main, container, false);
        frame_home_head = view.findViewById(R.id.frame_home_head);
        tv_expend = view.findViewById(R.id.tv_expend);
        tv_income = view.findViewById(R.id.tv_income);
        frame_home_tv_month =  view.findViewById(R.id.frame_home_tv_month);
        frame_home_tv_month.setText(MyStringUtils.getSysNowTime(3));
        username= UserManage.getInstance().getUserInfo(getContext()).getUserName();
//        username="123";
        month=frame_home_tv_month.getText()+"";
        month=month.replace("月","");

        Typeface typeFace = Typeface.createFromAsset(getActivity()
                .getAssets(), "fonts/RobotoCondensed-Bold.ttf");
        tv_expend.setTypeface(typeFace);
        tv_income.setTypeface(typeFace);
        lisView = (ListView) view.findViewById(R.id.frame_home_lv);
        homeAdapter=new HomeAdapter(this.getContext());
        final List<homebill> hbs=new ArrayList<>();

        lisView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {
                final homebill hb=homeAdapter.getHomebill(position);

                if (hb == null) return;

                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setTitle("是否删除？");
                dialog.setCancelable(true);
                dialog.setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        income=hb.getIncome();
                        String s=hb.getIOtime();
                        IOclasses=hb.getIOclasses();
                        try {
                            IOtime= dateFormat.parse(s);
                            System.out.println(IOtime.toLocaleString().split(" ")[0]);//切割掉不要的时分秒数据
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }


                        accountdelete(username,income,IOclasses,IOtime);
                        Log.e("TAG", "error messagess:" + s+""+IOtime+""+IOclasses+""+username);
                        Toast.makeText(getActivity(),"删除chengg"+IOtime+" "+IOclasses,Toast.LENGTH_LONG).show();
//                        HomebillInfo(username);
//                        accountall(username, month);

//                        hbs.remove(position);//选择行的位置
//                        homeAdapter.notifyDataSetChanged();
//                        lisView.invalidate();
                    }
                });
                dialog.setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialog.show();

            }
        });
        accountall(username,month);
        HomebillInfo(username);
        Log.d("TAG", "8");
        return view;
    }

    //删除一条记录
    public void accountdelete(String name,int income,String IOclasses,Date IOtime){
        Log.d("TAG", "delete all");
        httpLoader = new HttpLoader();
        httpLoader.countsDelete(name,income,IOclasses,IOtime).subscribe(new Action1<String>() {
//        httpLoader.countsDelete(name="123",income=5,IOtime="2019-06-11",IOclasses="工资").subscribe(new Action1<String>() {

            @Override
            public void call(String flags) {
                System.out.println("返回账单数据" + new Gson().toJson(flags));
                if(flags.equals("0")){
                    Toast.makeText(getActivity(),"删除失败",Toast.LENGTH_LONG).show();
                } else if(flags.equals("1")){
                    Toast.makeText(getActivity(),"删除成功",Toast.LENGTH_LONG).show();
                }
            }

        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //获取失败
                Toast.makeText(getActivity(), "删除请求失败",Toast.LENGTH_SHORT).show();
                Log.e("TAG", "error messagess:" + throwable.getMessage());
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


    //获取当月总量
    public void accountall (String name,String month){
        Log.d("TAG", "send all");
        httpLoader = new HttpLoader();
        httpLoader.counts(name,month).subscribe(new Action1<CountS>() {
            @Override
            public void call(CountS countS) {
                System.out.println("返回的该月账单数据" + new Gson().toJson(countS));
                tv_income.setText(countS.getIncomes()+"");
                tv_expend.setText(countS.getOutcomes()+"");

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //获取失败
                Toast.makeText(getActivity(), "统计请求失败",Toast.LENGTH_SHORT).show();

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



    //获取当前区域所有数据
    public void HomebillInfo (String name){
        Log.d("TAG", "GET all");
        httpLoader = new HttpLoader();
        httpLoader.homeBillInfo(name).subscribe(new Action1<GetHomeBillInfo>() {
            @Override
            public void call(GetHomeBillInfo gethomebillInfo) {
                System.out.println("返回的主要页面数据" + new Gson().toJson(gethomebillInfo));
                ArrayList<homebill> billInfo=gethomebillInfo.getData();
                for (int i=0;i< billInfo.size();i++){
                    System.out.println("详细数据" + new Gson().toJson(billInfo.get(i)));
                }
                if(billInfo.size()!=0){
                    homeAdapter.clear();
                    homeAdapter.addbill(billInfo);
                    lisView.setAdapter(homeAdapter);
                    Log.d("TAG", "1");
                    homeAdapter.notifyDataSetChanged();
                    Log.d("TAG", "2");
                    Toast.makeText(getActivity(), "you数据",Toast.LENGTH_SHORT).show();

                }else{
                    Log.d("TAG", "3");
                    Toast.makeText(getActivity(), "暂无数据",Toast.LENGTH_SHORT).show();
                }

                Log.d("TAG", "4");

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //获取失败

                Toast.makeText(getActivity(), "请求失败",Toast.LENGTH_SHORT).show();
                Log.e("TAG", "error messages:" + throwable.getMessage());
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




    @Override
    public void onDestroy() {
        super.onDestroy();
    }




}
