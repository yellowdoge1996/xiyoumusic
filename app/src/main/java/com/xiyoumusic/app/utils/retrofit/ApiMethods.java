package com.xiyoumusic.app.utils.retrofit;

import com.xiyoumusic.app.entity.HuiFu;
import com.xiyoumusic.app.entity.LrcResponse;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.entity.PingLun;
import com.xiyoumusic.app.entity.User;

import java.io.File;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.MultipartBody;
import okhttp3.ResponseBody;

public class ApiMethods {
    /**
     * 封装线程管理和订阅的过程
     */
    public static void ApiSubscribe(Observable observable, Observer observer) {
        observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    public static void login(Observer<MyResponse> observer, String xh, String mm) {
        ApiSubscribe(RetrofitApi.getService().login(xh, mm), observer);
    }

    public static void loginByUUID(Observer<MyResponse> observer, String uuid){
        ApiSubscribe(RetrofitApi.getService().loginByUUID(uuid), observer);
    }

    public static void register(Observer<MyResponse> observer, String xh, String mm){
        ApiSubscribe(RetrofitApi.getService().register(xh, mm), observer);
    }

    public static void modifyUserInfo(Observer<MyResponse> observer, User user){
        ApiSubscribe(RetrofitApi.getService().modifyUserInfo(user), observer);
    }

    public static void uploadTx(Observer<MyResponse> observer, String xh, MultipartBody.Part file){
        ApiSubscribe(RetrofitApi.getService().uploadTx(xh, file), observer);
    }

    public static void logout(Observer<MyResponse> observer, String xh, String uuid){
        ApiSubscribe(RetrofitApi.getService().logout(xh, uuid), observer);
    }

    public static void getSuggestions(Observer<MyResponse> observer, String keyword){
        ApiSubscribe(RetrofitApi.getService().getSuggestions(keyword), observer);
    }

    public static void seasrch(Observer<MyResponse> observer, String xh, String keyword){
        ApiSubscribe(RetrofitApi.getService().search(xh, keyword), observer);
    }

    public static void uploadMusic(Observer<MyResponse> observer, String xh, String musicName, List<MultipartBody.Part> files){
        ApiSubscribe(RetrofitApi.getService().uploadMusic(xh, musicName, files), observer);
    }

    public static void searchLrcByMusicName(Observer<LrcResponse> observer, String musicName){
        ApiSubscribe(RetrofitApiMusic.getService().searchLrcByMusicName(musicName), observer);
    }

    public static void download(Observer<ResponseBody> observer, String uri){
        ApiSubscribe(RetrofitApiMusic.getService().download(uri), observer);
    }

    public static void getMusicInfo(Observer<MyResponse> observer, long gqid, String xh){
        ApiSubscribe(RetrofitApi.getService().getMusicInfo(gqid, xh), observer);
    }

    public static void shoucang(Observer<MyResponse> observer, long gqid, String xh){
        ApiSubscribe(RetrofitApi.getService().shoucang(gqid, xh), observer);
    }

    public static void getShoucang(Observer<MyResponse> observer, String xh){
        ApiSubscribe(RetrofitApi.getService().getShoucang(xh), observer);
    }

    public static void pinglun(Observer<MyResponse> observer, PingLun pingLun){
        ApiSubscribe(RetrofitApi.getService().pinglun(pingLun), observer);
    }

    public static void huifu(Observer<MyResponse> observer, HuiFu huiFu){
        ApiSubscribe(RetrofitApi.getService().huifu(huiFu), observer);
    }

    public static void getPinglun(Observer<MyResponse> observer, long gqid){
        ApiSubscribe(RetrofitApi.getService().getPinglun(gqid), observer);
    }

    public static void deletePinglun(Observer<MyResponse> observer, long plid){
        ApiSubscribe(RetrofitApi.getService().deletePinglun(plid), observer);
    }

    public static void deleteHuifu(Observer<MyResponse> observer, long hfid){
        ApiSubscribe(RetrofitApi.getService().deleteHuifu(hfid), observer);
    }

    public static void myMusic(Observer<MyResponse> observer, String xh){
        ApiSubscribe(RetrofitApi.getService().myMusic(xh), observer);
    }

    public static void isGuanzhu(Observer<MyResponse> observer, String gzzxh, String bgzzxh){
        ApiSubscribe(RetrofitApi.getService().isGuanzhuan(gzzxh, bgzzxh), observer);
    }

    public static void guanzhu(Observer<MyResponse> observer, String gzzxh, String bgzzxh){
        ApiSubscribe(RetrofitApi.getService().guanzhu(gzzxh, bgzzxh), observer);
    }

    public static void getGuanzhu(Observer<MyResponse> observer, String xh){
        ApiSubscribe(RetrofitApi.getService().getGuanzhu(xh), observer);
    }

    public static void dongtai(Observer<MyResponse> observer, String xh, int startPage){
        ApiSubscribe(RetrofitApi.getService().dongtai(xh, startPage), observer);
    }
}
