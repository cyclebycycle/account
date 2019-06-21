package com.example.zzy.accounttest.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
//import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zzy.accounttest.Bean.MyStringUtils;
import com.example.zzy.accounttest.Bean.Sorts;
import com.example.zzy.accounttest.Bean.UserManage;
import com.example.zzy.accounttest.Bean.homebill;
import com.example.zzy.accounttest.Bean.sortitem;
import com.example.zzy.accounttest.R;
import com.example.zzy.accounttest.adapter.SortAdapter;
import com.example.zzy.accounttest.http.Fault;
import com.example.zzy.accounttest.http.HttpLoader;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import butterknife.BindView;
import rx.functions.Action1;


public class AddAccountActivity extends AppCompatActivity implements
        View.OnClickListener {
    @BindView(R.id.bt_sorts)
    RadioGroup mRgType;
    @BindView(R.id.bt_outs)
    RadioButton bt_outs;
    @BindView(R.id.bt_ins)
    RadioButton bt_ins;
    private RadioGroup radioGroup;
    private int mOutInType = 1; //记账类型 1：支出  2：收入

    private List<String> typelist;
    private ArrayAdapter<String> madapter;

    private String[] typeChoice1 = {"娱乐", "饮食", "购物", "出行", "其他"};
    private String[] typeChoice2 = {"工资", "奖金", "红包", "理财", "其他"};
    private String nodeType = typeChoice1[0];//节点类型，默认

    private static final int MSG_SAVE_DONE = 0x1;
    private static final int MSG_SAVE_FALSE = 0x2;
    private ListView lv;
    private HttpLoader httpLoader;
//    private ArrayAdapter<sortitem> adapter;
//    private SortAdapter madapter;
    private List<sortitem> sort=new ArrayList<sortitem>();

    private String username,sorts,mark,type;
    private Date IOtime;
    private SimpleDateFormat dateFormat=new SimpleDateFormat("yyyy-MM-dd");
    private String moneys;
    public static int temp = -1;


    private String  money, used, limit;
    private TextView tv_num,tv_type;
    private Button add_btn_date;

//    private CheckBox bt_outs,bt_ins;
    private EditText add_et_mark;
    private AlertDialog alertDialog;
    private Typeface fontRegular, fontBold, fontThin, fontLight;
//    private ArrayList<HashMap<String, String>> lists = new ArrayList<>();
    private ArrayList<String> lists = new ArrayList<>();
    private SparseBooleanArray sba_cb = new SparseBooleanArray();
    BaseAdapter adapter;
    private int totalDays = 0;
    private int firstDayOfWeek = 7;
    private int currentDay = 1;
    private List<String> day = new ArrayList<>();
    private SparseBooleanArray sba_date = new SparseBooleanArray();
    private MyAdapter adapater;
    private String olddate, newdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addaccount);
        username= UserManage.getInstance().getUserInfo(AddAccountActivity.this).getUserName();
//        username="123";
        initView();
        initFontType();
        showNumPickerDialog();
        initDateData();
    }



    private void initDateData() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance(Locale.CHINA);
                currentDay = calendar.get(Calendar.DAY_OF_MONTH);
                totalDays = calendar.getActualMaximum(Calendar.DATE);
                firstDayOfWeek = MyStringUtils.getFirstDayOfWeek();
                for (int i = 1; i <= totalDays; i++) {
                    day.add(Integer.toString(i));
                }
                for (int j = 0; j < day.size() + firstDayOfWeek; j++) {
                    sba_date.put(j, false);// false表示未选中
                }

            }
        }).start();
    }

    public void showDialog(View view) {
        showNumPickerDialog();
    }

    private class MyAdapter extends BaseAdapter {

        TextView tvv;

        MyAdapter(TextView tvv) {
            this.tvv = tvv;
        }

        @Override
        public int getCount() {
            if (firstDayOfWeek == 7)
                return day.size();
            return day.size() + firstDayOfWeek;// 31+2=33
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            final ViewHolder holder;
            final int currentdayposition = position - firstDayOfWeek + 1;
            if (convertView == null) {
                LayoutInflater inflater = LayoutInflater
                        .from(AddAccountActivity.this);
                convertView = inflater.inflate(
                        R.layout.dialog_calender_gv_item,
                        (ViewGroup) convertView, false);
                holder = new ViewHolder();
                holder.tv = (TextView) convertView.findViewById(R.id.tv);
                holder.tv.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (currentdayposition != currentDay
                                && position >= firstDayOfWeek) {
                            if (!(sba_date.get(position))) {
                                boolean p = !sba_date.get(position);
                                for (int i = 0; i < day.size() + firstDayOfWeek; i++) {
                                    sba_date.put(i, false);
                                }
                                sba_date.put(position, p);
                                adapater.notifyDataSetChanged();
                            }

                        }
                        if (currentdayposition == currentDay) {
                            for (int i = 0; i < day.size() + firstDayOfWeek; i++) {
                                sba_date.put(i, false);
                            }
                            adapater.notifyDataSetChanged();
                            tvv.setText(olddate);
                        }
                    }
                });
                if (currentdayposition == currentDay) {// 列表项等于当前日期
                    holder.tv.setTextColor(Color.WHITE);
                    holder.tv
                            .setBackgroundResource(R.drawable.calender_circlebg);
                }
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (currentdayposition != currentDay && position >= firstDayOfWeek) {// 列表项等于当前日期

                if (sba_date.get(position)) {
                    holder.tv.setTextColor(Color.parseColor("#11CD6E"));
                    holder.tv
                            .setBackgroundResource(R.drawable.calender_circlering);

                    String str = MyStringUtils.getSysNowTime(6)
                            + "-"
                            + MyStringUtils.fomatDate(holder.tv.getText()
                            .toString());
                    tvv.setText(str);
                } else {
                    holder.tv.setTextColor(Color.parseColor("#40000000"));
                    holder.tv.setBackgroundResource(Color.TRANSPARENT);
                }
            }
            holder.tv.setTypeface(fontLight);
            if (position < firstDayOfWeek) {
                holder.tv.setText("");
            } else {
                holder.tv
                        .setText(day.get(position - firstDayOfWeek));
            }

            return convertView;
        }

        class ViewHolder {
            TextView tv;
        }
    }


    private void listsortdata(String username) {
        Log.e("TAG", "error message:listsortdata" );
        httpLoader=new HttpLoader();
        httpLoader.sortSelects(username).subscribe(new Action1<Sorts>() {
            @Override
            public void call(Sorts data) {
                System.out.println("数据Admin"+new Gson().toJson(data));
                if(sorts=="支出"){
                    typelist=data.getOuts();
//                    String m=typelist;
//                    tv_type.setText(typelist[1]);
                    madapter=new ArrayAdapter<String>(AddAccountActivity.this, android.R.layout.simple_list_item_1,typelist);
//                    sort=data.getOuts();
                    lv.setAdapter(madapter);
                    madapter.notifyDataSetChanged();
                    Toast.makeText(AddAccountActivity.this, "out类别数据",Toast.LENGTH_SHORT).show();
                }else{
                    typelist=data.getIns();
                    madapter=new ArrayAdapter<String>(AddAccountActivity.this, android.R.layout.simple_list_item_1,typelist);
//                    sort=data.getOuts();
                    lv.setAdapter(madapter);
                    madapter.notifyDataSetChanged();
                    Toast.makeText(AddAccountActivity.this, "类别数据",Toast.LENGTH_SHORT).show();
                }

//                }

            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //获取失败
                Log.e("TAG", "error message:" + throwable.getMessage());
                System.out.println("异常Admin"+throwable.getMessage());
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


    //删除一条记录
    public void accountadd(String name,String income,String type,String sort,
                           Date IOtime,String mark){
        Log.d("TAG", "delete all");
        httpLoader = new HttpLoader();
        httpLoader.countAdds(name,income,type,sort,IOtime,mark).subscribe(new Action1<String>() {
//        httpLoader.countsDelete(name="123",income=5,IOtime="2019-06-11",IOclasses="工资").subscribe(new Action1<String>() {

            @Override
            public void call(String flags) {
                System.out.println("返回账单数据" + new Gson().toJson(flags));
                if(flags.equals("0")){
                    Toast.makeText(AddAccountActivity.this,"添加失败",Toast.LENGTH_LONG).show();
                } else if(flags.equals("1")){
                    Toast.makeText(AddAccountActivity.this,"添加成功",Toast.LENGTH_LONG).show();
                }
            }

        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                //获取失败
                Toast.makeText(AddAccountActivity.this, "添加请求失败",Toast.LENGTH_SHORT).show();
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

    private void initFontType() {
        fontRegular = Typeface.createFromAsset(this.getAssets(),
                "fonts/RobotoCondensed-Regular.ttf");
        fontBold = Typeface.createFromAsset(this.getAssets(),
                "fonts/RobotoCondensed-Bold.ttf");
        fontThin = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Thin.ttf");
        fontLight = Typeface.createFromAsset(this.getAssets(),
                "fonts/Roboto-Light.ttf");
    }

    private void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbars);
//        setSupportActionBar(toolbar);

        radioGroup=( RadioGroup)findViewById(R.id.bt_sorts) ;
        RadioButton bt_o=findViewById(R.id.bt_outs);
        RadioButton bt_i=findViewById(R.id.bt_ins);
        sorts="支出";


        tv_type=findViewById(R.id.add_et_type);
        lv = (ListView) findViewById(R.id.add_lv);
        tv_num = (TextView) findViewById(R.id.add_num);
        TextView add_num_2 = (TextView) findViewById(R.id.add_num_2);
        tv_num.setTypeface(fontBold);
        add_num_2.setTypeface(fontBold);

        add_btn_date = (Button) findViewById(R.id.add_btn_date);
//        radioGroup = (RadioGroup) findViewById(R.id.bt_sorts);
//        bt_ins=(CheckBox)findViewById(R.id.bt_ins) ;
//        bt_outs=(CheckBox)findViewById(R.id.bt_outs);
//        bt_outs.setChecked(true);

//        radioGroup.setOnCheckedChangeListener(setListener());
        setListener();

        olddate = MyStringUtils.getSysNowTime(1);
        add_btn_date.setText(olddate);
        newdate = olddate;

        add_et_mark = (EditText) findViewById(R.id.add_et_mark);
        tv_num.requestFocus();
//        bt_ins.setOnCheckedChangeListener(listener);
//        bt_outs.setOnCheckedChangeListener(listener);

        add_btn_date.setOnClickListener(this);
        listsortdata(username);
        Log.e("TAG", "error message:initview" );
//        final SortAdapter madapter=new SortAdapter(this,sort);
        lv.setAdapter(madapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    final int position, long id) {

                lv.setItemChecked(position,true);
                final String sortitems=madapter.getItem(position);
                if (sortitems == null) return;
                Toast.makeText(AddAccountActivity.this, sortitems, Toast.LENGTH_SHORT).show();
                tv_type.setText(sortitems);
            }
        });



    }
//    privateCompoundButton.OnCheckedChangeListener setListener=new CompoundButton.OnCheckedChangeListener() {
    private void setListener() {
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.bt_outs:
                        mOutInType = 1;
                        sorts="支出";
                        Log.d("TAG", "bt_out sonCheckedChanged: "+sorts);
                        listsortdata(username);
                        break;
                    case R.id.bt_ins:
                        mOutInType = 2;
                        sorts="收入";
                        Log.d("TAG", "bt_ins sonCheckedChanged: "+sorts);
                        listsortdata(username);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    //监听按钮日期
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.add_btn_date:
                showDatePickerDialog();
                break;
            case R.id.btn_dia_cacle:
                alertDialog.dismiss();
                break;

        }
    }









    private void showNumPickerDialog() {
        // TODO showNumPickerDialog
        alertDialog = new AlertDialog.Builder(this, R.style.AlertDialog).create();
        alertDialog.show();
        Window window = alertDialog.getWindow();
        window.setContentView(R.layout.dialog_add_amount);
        final EditText tv_title = (EditText) window.findViewById(R.id.txt_amount);
        TextView txt_rmb = (TextView) window.findViewById(R.id.txt_rmb);
        MyStringUtils.setPricePoint(tv_title);
        tv_title.setTypeface(fontRegular);
        txt_rmb.setTypeface(fontRegular);
        Button delete = (Button) window.findViewById(R.id.btn_delete);
        TextView decimal = (TextView) window.findViewById(R.id.decimal);
        TextView digit_1 = (TextView) window.findViewById(R.id.digit_1);
        TextView digit_2 = (TextView) window.findViewById(R.id.digit_2);
        TextView digit_3 = (TextView) window.findViewById(R.id.digit_3);
        TextView digit_4 = (TextView) window.findViewById(R.id.digit_4);
        TextView digit_5 = (TextView) window.findViewById(R.id.digit_5);
        TextView digit_6 = (TextView) window.findViewById(R.id.digit_6);
        TextView digit_7 = (TextView) window.findViewById(R.id.digit_7);
        TextView digit_8 = (TextView) window.findViewById(R.id.digit_8);
        TextView digit_9 = (TextView) window.findViewById(R.id.digit_9);
        TextView digit_0 = (TextView) window.findViewById(R.id.digit_0);
        setTypeFont(decimal, digit_1, digit_2, digit_3, digit_4, digit_5,
                digit_6, digit_7, digit_8, digit_9, digit_0);
        TextView btn_dia_cacle = (TextView) window.findViewById(R.id.btn_dia_cacle);
        TextView btn_dia_ok = (TextView) window.findViewById(R.id.btn_dia_ok);
        btn_dia_cacle.setOnClickListener(this);
        numberClick(tv_title, delete, decimal, digit_1, digit_2, digit_3,
                digit_4, digit_5, digit_6, digit_7, digit_8, digit_9, digit_0);
        btn_dia_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                money = tv_title.getText().toString();
                if (money.equals("")) {
                    money = "0.00";
                }
                if (money.contains(".")) {
                    String[] i = money.split("\\.");
                    try {
                        if (i[1].equals("") || i[1] == null) {
                            money = i[0] + ".00";
                        } else if (i[1].length() == 1) {
                            money = i[0] + "." + i[1] + "0";
                        }
                    } catch (Exception e) {
                        money = i[0] + ".00";
                    }

                } else {
                    money = money + ".00";// 2.00
                }
                alertDialog.dismiss();
                refreshNum();
            }
        });

    }

    private void numberClick(final EditText tv_title, Button delete,
                             TextView decimal, TextView digit_1, TextView digit_2, TextView digit_3,
                             TextView digit_4, TextView digit_5, TextView digit_6, TextView digit_7,
                             TextView digit_8, TextView digit_9, TextView digit_0) {
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                try {
                    tv_title.setText(myStr.substring(0, myStr.length() - 1));
                } catch (Exception e) {

                }
            }
        });
        decimal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                if (!myStr.contains(".")) {// no_dot
                    myStr += ".";
                    tv_title.setText(myStr);
                }
            }
        });

        digit_1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "1";
                tv_title.setText(myStr);
            }
        });
        digit_2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "2";
                tv_title.setText(myStr);
            }
        });
        digit_3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "3";
                tv_title.setText(myStr);
            }
        });
        digit_4.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "4";
                tv_title.setText(myStr);
            }
        });
        digit_5.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "5";
                tv_title.setText(myStr);
            }
        });
        digit_6.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "6";
                tv_title.setText(myStr);
            }
        });
        digit_7.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "7";
                tv_title.setText(myStr);
            }
        });
        digit_8.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "8";
                tv_title.setText(myStr);
            }
        });
        digit_9.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "9";
                tv_title.setText(myStr);
            }
        });
        digit_0.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String myStr = tv_title.getText().toString();
                myStr += "0";
                tv_title.setText(myStr);
            }
        });
    }

    private void setTypeFont(TextView decimal, TextView digit_1, TextView digit_2,
                             TextView digit_3, TextView digit_4, TextView digit_5, TextView digit_6,
                             TextView digit_7, TextView digit_8, TextView digit_9, TextView digit_0) {
        decimal.setTypeface(fontThin);
        digit_1.setTypeface(fontThin);
        digit_2.setTypeface(fontThin);
        digit_3.setTypeface(fontThin);
        digit_4.setTypeface(fontThin);
        digit_5.setTypeface(fontThin);
        digit_6.setTypeface(fontThin);
        digit_7.setTypeface(fontThin);
        digit_8.setTypeface(fontThin);
        digit_9.setTypeface(fontThin);
        digit_0.setTypeface(fontThin);
    }

    protected void refreshNum() {
        tv_num.setText(money);
        add_btn_date.setText(newdate);
    }

    private void showDatePickerDialog() {
        // TODO showDatePickerDialog
        LayoutInflater layoutInflater = getLayoutInflater();
        View dilogview = layoutInflater.inflate(R.layout.dialog_calender,
                (ViewGroup) findViewById(R.id.calender_dialog));
        final GridView gv = (GridView) dilogview.findViewById(R.id.calender_gv);
        final TextView tvdate = (TextView) dilogview
                .findViewById(R.id.calender_tv_date);
        final TextView btn_cancle = (TextView) dilogview
                .findViewById(R.id.calender_btn_cancle);
        final TextView btn_done = (TextView) dilogview
                .findViewById(R.id.calender_btn_done);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(dilogview);
        tvdate.setText(olddate);
        adapater = new MyAdapter(tvdate);
        gv.setAdapter(adapater);
        final Dialog dialog = builder.show();
        btn_cancle.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        btn_done.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                newdate = tvdate.getText().toString();
                refreshNum();
                dialog.dismiss();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu_add, menu);
        return true;
    }
    //确认后修改
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.tool_cancle) {
            AddAccountActivity.this.finish();
            return true;
        }
        if (item.getItemId() == R.id.tool_done) {
            Log.d("TAG", "tool_done");
            Toast.makeText(AddAccountActivity.this, "选择了正确数据",Toast.LENGTH_SHORT).show();
            money = tv_num.getText().toString();
            if (money.equals("0.00")){
                showToast("金额不能为零！");
            }else{
                Log.d("TAG", "tool1_done");
                moneys=money;
//                moneys=7;
                try {
                    IOtime= dateFormat.parse(newdate);
                    System.out.println(IOtime.toLocaleString().split(" ")[0]);//切割掉不要的时分秒数据
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                mark=add_et_mark.getText().toString();
                type=tv_type.getText().toString();
                Log.d("TAG", "tool1_done"+moneys+IOtime+mark+type);
                accountadd(username,moneys,type,sorts,IOtime,mark);//="支出"
                Log.d("TAG", "tool2_done");
//                Intent intent=
                AddAccountActivity.this.finish();
                return true;

            }

//            if (money.equals("0.00")) {
//                showSnackbar("金额不能为零！");
//            } else {
//
//                new Thread(new Runnable() {
//
//                    @Override
//                    public void run() {
//                        for (int i = 0; i < sba_cb.size(); ) {
//                            int pos = sba_cb.keyAt(i);
//                            if (sba_cb.valueAt(pos)) {
//                                type = lists.get(pos).get("name");
//                                break;
//                            }
//                            i++;
//                        }
//                        if (type != null) {
//                            used = dataBase.getProORLimit(0, type);
//                            limit = dataBase.getProORLimit(2, type);
//                            if (saveToDB(used)) {
//                                MyStringUtils.saveSharedpre(
//                                        ActivityAddAccount.this, 0, "1");
//                                handler.sendEmptyMessage(MSG_SAVE_DONE);
//                            } else {
//                                handler.sendEmptyMessage(MSG_SAVE_FALSE);
//                            }
//                        } else {
//                            showSnackbar("请选择一个类别！");
//                        }
//                    }
//                }).start();
//
//            }
//            return true;
        }
        return super.onOptionsItemSelected(item);
    }
////
//    private void showSnackbar(String message) {
//        TSnackbar snackbar = TSnackbar.make(findViewById(android.R.id.content),
//                message, TSnackbar.LENGTH_LONG);
//        snackbar.setActionTextColor(Color.WHITE);
//        View snackbarView = snackbar.getView();
//        snackbarView.setBackgroundColor(Color.parseColor("#FF0000"));
//        TextView textView = (TextView) snackbarView
//                .findViewById(R.id.snackbar_text);
//        textView.setTextColor(Color.WHITE);
//        textView.setTextSize(20);
//        snackbar.show();
//    }

//    private boolean saveToDB(String savaMoney) {
//        // TODO saveToDB
//        MyStringUtils mUtils = new MyStringUtils();
//        mUtils.initDate();
//        AccountData mRecData = new AccountData(1);
//        mRecData.setType(type);
//        mRecData.setMoney(money);
//        mRecData.setTime(newdate);// 消费时间，格式2016-03-27
//        mRecData.setMonth(MyStringUtils.getSysNowTime(3));// 这里是月份
//        mRecData.setWeek(MyStringUtils.getDate("week"));
//        mRecData.setMark(add_et_mark.getText().toString());
//        mRecData.setOther("");
//        long state1 = dataBase.inserDataToAccount(mRecData);// 插入到account数据表中
//        String monthh = MyStringUtils.getSysNowTime(3);
//        boolean s = dataBase.isNameExist("groups", "_month", monthh);
//        long state2 = 1;
//        if (!s) {
//            // 该月月份不存在，插入这个月份到groups数据表中
//            state2 = dataBase.inserDataToGroup(monthh);
//        }
//        boolean ss = dataBase.isNameExist("limits", "_type", type);
//        if (ss) {
//            if (savaMoney.equals(0)) {// 保存的金额为0，更新这个金额
//                float f = Float.parseFloat(money);
//                dataBase.updateDataTolimitsUsed(type, f);
//                dataBase.updateDataTolimitsLimit(type, limit, String.valueOf(f));
//            } else {// 保存的金额不为0，更新这个金额
//                float f = Float.parseFloat(money) + Float.parseFloat(savaMoney);
//                dataBase.updateDataTolimitsUsed(type, f);
//                if (!limit.equals("0")) {
//                    dataBase.updateDataTolimitsLimit(type, limit,
//                            String.valueOf(f));
//                }
//            }
//        }
//        String month = MyStringUtils.getSysNowTime(3);
//        String zc = dataBase.getSRGL(1, month);
//        if (!zc.equals("0")) {
//            float f = Float.parseFloat(money) + Float.parseFloat(zc);
//            money = Float.toString(f);
//        }
//        dataBase.updateGLSR(month, "_zc", money);
//
//        if (state1 > 0 && state2 > 0) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_SAVE_DONE:
                    showToast("记录添加成功！");
                    AddAccountActivity.this.finish();
                    break;
                case MSG_SAVE_FALSE:
                    showToast("记录添加失败！");
                    AddAccountActivity.this.finish();
                    break;
            }
        }
    };

    private void showToast(String str) {
        Toast.makeText(AddAccountActivity.this, str, Toast.LENGTH_SHORT).show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
//        dataBase.close();
    }
}

