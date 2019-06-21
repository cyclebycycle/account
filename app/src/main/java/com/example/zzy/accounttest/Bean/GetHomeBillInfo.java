package com.example.zzy.accounttest.Bean;

import java.util.ArrayList;

public class GetHomeBillInfo {
    private ArrayList<homebill> data;
    public GetHomeBillInfo(ArrayList<homebill> data) {
        this.data = data;
    }
    public ArrayList<homebill> getData(){
        return data;
    }
    public void setData(ArrayList<homebill> data) {
        this.data = data;
    }



}
