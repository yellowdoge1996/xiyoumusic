package com.xiyoumusic.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
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
import com.xiyoumusic.app.viewModel.SearchViewModel;

import java.util.List;


public class SearchMusicResultFragment extends Fragment {
    RecyclerView recyclerView;
    MusicRecyclerViewAdapter musicRecyclerViewAdapter;
    SearchViewModel searchViewModel;
    TextView emptyMusicTextView;
    public SearchMusicResultFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchViewModel = ViewModelProviders.of(getParentFragment().getActivity()).get(SearchViewModel.class);
        searchViewModel.musicList.observe(this, musics -> {
            if (musics.size() == 0){
                emptyMusicTextView.setVisibility(View.VISIBLE);
            }else{
                emptyMusicTextView.setVisibility(View.GONE);
            }
            setAdapter(musics);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_music_result, container, false);
        emptyMusicTextView = view.findViewById(R.id.empty_music_info);
        recyclerView = view.findViewById(R.id.music_info_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getParentFragment().getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public void setAdapter(List<Music> musics){
        if (recyclerView.getAdapter() == null) {
            musicRecyclerViewAdapter = new MusicRecyclerViewAdapter(musics, getParentFragment().getActivity());
            musicRecyclerViewAdapter.setOnItemClickLitener(new MusicRecyclerViewAdapter.OnItemClickLitener(){
                @Override
                public void onItemClick(View view, int position) {
                    Music music = searchViewModel.musicList.getValue().get(position);
                    MusicInfoActivity.start(getContext(), music);
                }
            });
            recyclerView.setAdapter(musicRecyclerViewAdapter);
        } else {
            musicRecyclerViewAdapter.refresh(musics);
        }
    }
}
