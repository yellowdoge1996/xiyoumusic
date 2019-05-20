package com.xiyoumusic.app.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.MainActivity;
import com.xiyoumusic.app.activitys.MusicInfoActivity;
import com.xiyoumusic.app.adapter.MusicRecyclerViewAdapter;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.viewModel.UserViewModel;

import java.util.ArrayList;
import java.util.List;

public class ShoucangFragment extends Fragment {

    RecyclerView recyclerView;
    MusicRecyclerViewAdapter musicRecyclerViewAdapter;
    TextView emptyMusicTextView;

    private UserViewModel userViewModel;

    public ShoucangFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shoucang, container, false);
        emptyMusicTextView = view.findViewById(R.id.empty_music_info);
        recyclerView = view.findViewById(R.id.music_info_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        userViewModel.shoucang.observe(this, musics -> {
            if (musics.size() == 0){
                emptyMusicTextView.setVisibility(View.VISIBLE);
            }else{
                emptyMusicTextView.setVisibility(View.GONE);
            }
            setAdapter(musics);
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        getShoucang();
    }

    public void setAdapter(List<Music> musics){
        if (recyclerView.getAdapter() == null) {
            musicRecyclerViewAdapter = new MusicRecyclerViewAdapter(musics, getActivity());
            musicRecyclerViewAdapter.setOnItemClickLitener((view, position) -> {
                Music music = userViewModel.shoucang.getValue().get(position);
                MusicInfoActivity.start(getContext(), music);
            });
            recyclerView.setAdapter(musicRecyclerViewAdapter);
        } else {
            musicRecyclerViewAdapter.refresh(musics);
        }
    }

    private void getShoucang(){
        ObserverOnNextListener<MyResponse> downloadObserverOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                if (myResponse.getMusicData() != null && myResponse.getMusicData().size()>0){
                    userViewModel.shoucang.setValue(myResponse.getMusicData());
                }else{
                    userViewModel.shoucang.setValue(new ArrayList<>());
                }
            }else{
                ToastUtil.normal(myResponse.getError());
            }
        };

        ApiMethods.getShoucang(new ProgressObserver<>(getActivity(), downloadObserverOnNextListener, "加载中...", false, true), userViewModel.sczxh.getValue());
    }
}
