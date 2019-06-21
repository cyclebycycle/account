package com.example.zzy.accounttest.http;


import com.example.zzy.accounttest.Bean.CountS;
import com.example.zzy.accounttest.Bean.GetHomeBillInfo;
import com.example.zzy.accounttest.Bean.LoginReturnObject;
import com.example.zzy.accounttest.Bean.RegistReturnObject;
import com.example.zzy.accounttest.Bean.Sorts;

import java.util.Date;

import rx.Observable;

public class HttpLoader extends ObjectLoader {
    private ApiService apiService;

    public HttpLoader(){
        apiService = RetrofitServiceManager.getInstance().create(ApiService.class);
    }

    //登陆
    public Observable<String> login(String name, String password){
        return observe(apiService.Login(name,password));
    }

    //普通用户注册
    public Observable<String> regist(String name, String password){
        return observe(apiService.Register(name,password));
    }

    //主页请求数据

    public Observable<GetHomeBillInfo> homeBillInfo(String name){
        return observe(apiService.HomeBillInfo(name));
    }
    //主页请求数据

    public Observable<CountS> counts(String name, String month){
        return observe(apiService.Counts(name,month));
    }

    public Observable<String> countsDelete(String name, int income,String IOclasses,Date IOtime){
        return observe(apiService.CountsDelete(name,income,IOclasses,IOtime));//顺序必须一致
    }


//    public Observable<Sorts> sortSelects(String name){
//        return observe(apiService.SortSelects(name));
//    }

    public Observable<Sorts> sortSelects(String name){
        return observe(apiService.SortSelects(name));
    }

    public Observable<String> countAdds(String username, String income,String type, String sort,
                                        Date IOtime, String mark){
        return observe(apiService.CountAdds(username,income,type,sort,IOtime,mark));
    }



}
