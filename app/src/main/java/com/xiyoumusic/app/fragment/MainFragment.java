package com.xiyoumusic.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.LoginActivity;
import com.xiyoumusic.app.activitys.MainActivity;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.viewModel.UserViewModel;

public class MainFragment extends Fragment implements View.OnClickListener{

    private Button next;
    private ImageButton localMusic;
    private ImageButton collection;
    private ImageButton myMusic;
    private ImageButton favorite;
    private ImageButton dongtai;
    private ImageButton chat;
    private ImageButton fan;
    private ImageButton comment;
    private ImageButton rank;

    private UserViewModel userViewModel;

    public MainFragment() {
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
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        next = view.findViewById(R.id.next);
        localMusic = view.findViewById(R.id.local_music);
        collection = view.findViewById(R.id.collection);
        myMusic = view.findViewById(R.id.my_music);
        favorite = view.findViewById(R.id.my_favorite);
        dongtai = view.findViewById(R.id.dong_tai);
        chat = view.findViewById(R.id.chat);
        fan = view.findViewById(R.id.my_fan);
        comment = view.findViewById(R.id.music_comment);
        rank = view.findViewById(R.id.rank);

        next.setOnClickListener(this);
        localMusic.setOnClickListener(this);
        collection.setOnClickListener(this);
        myMusic.setOnClickListener(this);
        favorite.setOnClickListener(this);
        dongtai.setOnClickListener(this);
        chat.setOnClickListener(this);
        fan.setOnClickListener(this);
        comment.setOnClickListener(this);
        rank.setOnClickListener(this);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.next:
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_mainFragment_to_twoFragment);
                MainActivity.fragmentFlag = MainActivity.FRAGMENT_OTHER;
                break;
            case R.id.local_music:
                NavHostFragment.findNavController(MainFragment.this)
                        .navigate(R.id.action_mainFragment_to_localMusicFragment);
                MainActivity.fragmentFlag = MainActivity.FRAGMENT_OTHER;
                break;
            case R.id.collection:
                if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivityForResult(login, MainActivity.LOGIN_RESULT);
                }else{
                    userViewModel.sczxh.setValue(SPTool.getSharedPreferences().getString("xh", "000000000000"));
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_shoucangFragment);
                    MainActivity.fragmentFlag = MainActivity.FRAGMENT_OTHER;
                }
                break;
            case R.id.my_music:
                if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivityForResult(login, MainActivity.LOGIN_RESULT);
                }else{
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_myMusicFragment);
                    MainActivity.fragmentFlag = MainActivity.FRAGMENT_OTHER;
                }
                break;
            case R.id.my_favorite:
                if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivityForResult(login, MainActivity.LOGIN_RESULT);
                }else{
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_myGuanzhuFragment);
                    MainActivity.fragmentFlag = MainActivity.FRAGMENT_OTHER;
                }
                break;
            case R.id.dong_tai:
                if (SPTool.getSharedPreferences().getString("uuid","").equals("")){
                    Intent login = new Intent(getActivity(), LoginActivity.class);
                    getActivity().startActivityForResult(login, MainActivity.LOGIN_RESULT);
                }else{
                    NavHostFragment.findNavController(MainFragment.this)
                            .navigate(R.id.action_mainFragment_to_dongtaiFragment);
                    MainActivity.fragmentFlag = MainActivity.FRAGMENT_OTHER;
                }
                break;
        }
    }

    @Override
    public void onResume() {
        MainActivity.fragmentFlag = MainActivity.FRAGMENT_MAIN;
        super.onResume();
    }
}
