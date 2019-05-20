package com.xiyoumusic.app.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.MusicInfoActivity;
import com.xiyoumusic.app.adapter.MusicRecyclerViewAdapter;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;

import java.util.ArrayList;
import java.util.List;

public class MyMusicFragment extends Fragment {
    RecyclerView recyclerView;
    MusicRecyclerViewAdapter musicRecyclerViewAdapter;
    TextView emptyMusicTextView;
    List<Music> musics = new ArrayList<>();

    public MyMusicFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_music, container, false);
        emptyMusicTextView = view.findViewById(R.id.empty_music_info);
        recyclerView = view.findViewById(R.id.music_info_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        myMusic();
    }

    public void setAdapter(List<Music> musics){
        if (recyclerView.getAdapter() == null) {
            musicRecyclerViewAdapter = new MusicRecyclerViewAdapter(musics, getActivity());
            musicRecyclerViewAdapter.setOnItemClickLitener((view, position) -> {
                Music music = musics.get(position);
                MusicInfoActivity.start(getContext(), music);
            });
            recyclerView.setAdapter(musicRecyclerViewAdapter);
        } else {
            musicRecyclerViewAdapter.refresh(musics);
        }
    }

    private void myMusic(){
        ObserverOnNextListener<MyResponse> downloadObserverOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                if (myResponse.getMusicData() != null && myResponse.getMusicData().size()>0){
                    musics = myResponse.getMusicData();
                    setAdapter(musics);
                    emptyMusicTextView.setVisibility(View.GONE);
                }else{
                    musics = new ArrayList<>();
                    setAdapter(musics);
                    emptyMusicTextView.setVisibility(View.VISIBLE);
                }
            }else{
                ToastUtil.normal(myResponse.getError());
            }
        };

        ApiMethods.myMusic(new ProgressObserver<>(getActivity(), downloadObserverOnNextListener, "加载中...", false, true), SPTool.getSharedPreferences().getString("xh", "000000000000"));
    }
}
