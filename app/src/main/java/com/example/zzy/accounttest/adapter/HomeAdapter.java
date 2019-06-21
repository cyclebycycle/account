package com.example.zzy.accounttest.adapter;


import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.zzy.accounttest.Bean.homebill;
import com.example.zzy.accounttest.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Map;

public class HomeAdapter extends BaseAdapter{

    private ArrayList<homebill> myList;
    private Context myContext;

    public HomeAdapter(Context mContext) {
        super();
        myList=new ArrayList<homebill>();
        this.myContext = mContext;
    }

    public void addbill(ArrayList<homebill> homeb) {
        for (int i=0;i< homeb.size();i++){
            System.out.println("homebill函数详细数据" + new Gson().toJson(homeb.get(i)));
        }
        myList=homeb;
    }

    public homebill getHomebill(int position) {
        return myList.get(position);
    }

    @Override
    public int getCount() {
        return myList.size();
    }

    @Override
    public Object getItem(int position) {
        return myList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    public void clear() {
        myList.clear();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            LayoutInflater mInflater = LayoutInflater.from(myContext);
            convertView = mInflater.inflate(R.layout.home_bill_item, null);
            holder = new ViewHolder();
            holder.bt_income= (TextView) convertView
                    .findViewById(R.id.bt_income);
            holder.bt_IOclasses = (TextView) convertView
                    .findViewById(R.id.bt_IOclasses);
            holder.bt_time = (TextView) convertView
                    .findViewById(R.id.bt_time);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        homebill h=myList.get(position);
        String io_time=h.getIOtime();
        String io_class=h.getIOclasses();
        int income=h.getIncome();

        holder.bt_income.setText(income+"");
        holder.bt_IOclasses.setText(io_class+"");
        holder.bt_time.setText(io_time+"");
        return convertView;
    }

    static class ViewHolder {

        public TextView bt_income;
        public TextView bt_IOclasses;
        public TextView bt_time;

    }

}
