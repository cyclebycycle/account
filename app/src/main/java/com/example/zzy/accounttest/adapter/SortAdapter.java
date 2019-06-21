package com.example.zzy.accounttest.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.example.zzy.accounttest.Bean.sortitem;
import com.example.zzy.accounttest.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class SortAdapter extends BaseAdapter {
    private Context mContext=null;
    private List<sortitem> data;
    public SortAdapter(Context ctx,List<sortitem> data){
        super();
        mContext=ctx;
        this.data=data;
    }
    public sortitem getsortitem(int position) {
        return data.get(position);
    }

    @Override
    public int getCount(){
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.add_sort_item, parent, false);
            holder = new ViewHolder();
            holder.sort_item_tv = (TextView)convertView.findViewById(R.id.sort_item_tv);
            convertView.setTag(holder);
        } else {// 有直接获得ViewHolder
            holder = (ViewHolder)convertView.getTag();
        }

        sortitem msg = data.get(position);

        holder.sort_item_tv.setText(msg.getSortitem());
        return convertView;
    }

    static class ViewHolder{

        TextView sort_item_tv;
    }

}
