package com.xiyoumusic.app.utils.retrofit;

import com.xiyoumusic.app.entity.HuiFu;
import com.xiyoumusic.app.entity.LrcResponse;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.entity.PingLun;
import com.xiyoumusic.app.entity.User;

import java.util.List;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

public interface ApiService {
    @GET("login/{xh}/{mm}")
    Observable<MyResponse> login(@Path("xh") String xh, @Path("mm") String mm);

    @GET("loginByUUID/{uuid}")
    Observable<MyResponse> loginByUUID(@Path("uuid")String uuid);

    @GET("register/{xh}/{mm}")
    Observable<MyResponse> register(@Path("xh") String xh, @Path("mm") String mm);

    @PUT("modifyUserInfo")
    Observable<MyResponse> modifyUserInfo(@Body User user);

    @POST("uploadTx/{xh}")
    @Multipart
    Observable<MyResponse> uploadTx(@Path("xh") String xh, @Part MultipartBody.Part file);

    @GET("logout/{xh}/{uuid}")
    Observable<MyResponse> logout(@Path("xh") String xh, @Path("uuid") String uuid);

    @GET("getSuggestions/{keyword}")
    Observable<MyResponse> getSuggestions(@Path("keyword")String keyword);

    @GET("search/{xh}/{keyword}")
    Observable<MyResponse> search(@Path("xh")String xh,@Path("keyword")String keyword);

    @POST("uploadMusic/{xh}/{name}")
    @Multipart
    Observable<MyResponse> uploadMusic(@Path("xh") String xh, @Path("name") String musicName, @Part List<MultipartBody.Part> files);

    @GET("{musicname}")
    Observable<LrcResponse> searchLrcByMusicName(@Path("musicname")String musicName);

    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String fileUrl);

    @GET("musicInfo/{gqid}/{xh}")
    Observable<MyResponse> getMusicInfo(@Path("gqid")long gqid, @Path("xh")String xh);

    @POST("shoucang/{gqid}/{xh}")
    Observable<MyResponse> shoucang(@Path("gqid")long gqid, @Path("xh")String xh);

    @GET("getShoucang/{xh}")
    Observable<MyResponse> getShoucang(@Path("xh")String xh);

    @POST("pinglun")
    Observable<MyResponse> pinglun(@Body PingLun pingLun);

    @POST("huifu")
    Observable<MyResponse> huifu(@Body HuiFu huiFu);

    @GET("getPinglun/{gqid}")
    Observable<MyResponse> getPinglun(@Path("gqid")long gqid);

    @DELETE("deletePinglun/{plid}")
    Observable<MyResponse> deletePinglun(@Path("plid")long plid);

    @DELETE("deleteHuifu/{hfid}")
    Observable<MyResponse> deleteHuifu(@Path("hfid")long hfid);

    @GET("myMusic/{xh}")
    Observable<MyResponse> myMusic(@Path("xh")String xh);

    @GET("isGuanzhu/{gzzxh}/{bgzzxh}")
    Observable<MyResponse> isGuanzhuan(@Path("gzzxh")String gzzxh, @Path("bgzzxh")String bgzzxh);

    @POST("guanzhu/{gzzxh}/{bgzzxh}")
    Observable<MyResponse> guanzhu(@Path("gzzxh")String gzzxh, @Path("bgzzxh")String bgzzxh);

    @GET("getGuanzhu/{xh}")
    Observable<MyResponse> getGuanzhu(@Path("xh")String xh);

    @GET("dongtai/{xh}/{startPage}")
    Observable<MyResponse> dongtai(@Path("xh")String xh, @Path("startPage")int startPage);
}
