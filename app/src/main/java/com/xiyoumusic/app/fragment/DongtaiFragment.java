package com.xiyoumusic.app.fragment;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.LoginActivity;
import com.xiyoumusic.app.activitys.MainActivity;
import com.xiyoumusic.app.activitys.MusicInfoActivity;
import com.xiyoumusic.app.adapter.DongtaiRecyclerViewAdapter;
import com.xiyoumusic.app.adapter.MusicRecyclerViewAdapter;
import com.xiyoumusic.app.entity.Dongtai;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.viewModel.SearchViewModel;

import java.util.ArrayList;
import java.util.List;


public class DongtaiFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<Dongtai> dongtaiList;
    private DongtaiRecyclerViewAdapter dongtaiRecyclerViewAdapter;
    private int startPage = 1;
    public DongtaiFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (SPTool.getSharedPreferences().getString("xh", "").equals("")){
            Intent i = new Intent(getActivity(), LoginActivity.class);
            startActivity(i);
        }
    }

    public void getDongtai(){
        Log.d("123", startPage+"");
        ObserverOnNextListener<MyResponse> downloadObserverOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                if (myResponse.getDongtaiData() != null && myResponse.getDongtaiData().size()>0){
                    if (dongtaiList == null){
                        dongtaiList = new ArrayList<>();
                    }
                    dongtaiList.addAll(myResponse.getDongtaiData());
                    setAdapter(dongtaiList);
                }else{
                    if (dongtaiList == null) {
                        dongtaiList = new ArrayList<>();
                        setAdapter(dongtaiList);
                        ToastUtil.normal("还没有好友上传音乐哟");
                    }else {
                        ToastUtil.normal("已经加载完了哟");
                    }
                }
            }else{
                ToastUtil.normal(myResponse.getError());
            }
        };

        ApiMethods.dongtai(new ProgressObserver<>(getActivity(), downloadObserverOnNextListener, "加载中...", true, true), SPTool.getSharedPreferences().getString("xh", "000000000000"), startPage++);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dongtai, container, false);
        recyclerView = view.findViewById(R.id.dongtai_list_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        getDongtai();
        return view;
    }

    public void setAdapter(List<Dongtai> dongtaiList){
        if (recyclerView.getAdapter() == null) {
            dongtaiRecyclerViewAdapter = new DongtaiRecyclerViewAdapter(dongtaiList, getActivity());
            dongtaiRecyclerViewAdapter.setOnItemClickLitener(v->{
                getDongtai();
                v.setVisibility(View.GONE);
            });
            recyclerView.setAdapter(dongtaiRecyclerViewAdapter);
        } else {
            dongtaiRecyclerViewAdapter.refresh(dongtaiList);
        }
    }
}
