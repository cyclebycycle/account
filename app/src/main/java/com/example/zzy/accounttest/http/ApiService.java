package com.example.zzy.accounttest.http;

import rx.Observable;

import com.example.zzy.accounttest.Bean.CountS;
import com.example.zzy.accounttest.Bean.GetHomeBillInfo;
import com.example.zzy.accounttest.Bean.LoginReturnObject;
import com.example.zzy.accounttest.Bean.RegistReturnObject;
import com.example.zzy.accounttest.Bean.Sorts;

import java.util.Date;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {
    //登录
    @FormUrlEncoded
    @POST("accountLoginJudge/")
    Observable<String> Login(@Field("name") String name,
                                        @Field("password") String password);

    //用户注册
    @FormUrlEncoded
    @POST("UserRegist/")
    Observable<String> Register(
            @Field("r_name") String names,
            @Field("r_pwd") String password);

    //获取当月账单
    @FormUrlEncoded
    @POST("accounts/")
    Observable<GetHomeBillInfo> HomeBillInfo(
            @Field("username") String names);

    @FormUrlEncoded
    @POST("Counts/")
    Observable<CountS> Counts(
            @Field("username") String names,
            @Field("month") String month);
//删除
    @FormUrlEncoded
    @POST("CountsDelete/")
    Observable<String> CountsDelete(
            @Field("username") String username,
            @Field("income") int income,
            @Field("IOclasses") String IOclasses,
            @Field("IOtime") Date IOtime);

    //类别显示
    @FormUrlEncoded
    @POST("SortSelect/")
    Observable<Sorts> SortSelects(
            @Field("username") String names);

    @FormUrlEncoded
    @POST("CountAdd/")
    Observable<String> CountAdds(
            @Field("username") String username,
            @Field("income") String income,
            @Field("type") String type,
            @Field("sort") String sort,
            @Field("time") Date IOtime,
            @Field("mark") String mark);




}
