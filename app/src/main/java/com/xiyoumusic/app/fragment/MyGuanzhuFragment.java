package com.xiyoumusic.app.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.UserInfoActivity;
import com.xiyoumusic.app.adapter.UserRecyclerViewAdapter;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;

import java.util.ArrayList;
import java.util.List;

public class MyGuanzhuFragment extends Fragment {
    RecyclerView recyclerView;
    UserRecyclerViewAdapter userRecyclerViewAdapter;
    TextView emptyUserTextView;
    List<User> users;
    public MyGuanzhuFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_guanzhu, container, false);
        emptyUserTextView = view.findViewById(R.id.empty_user_info);
        recyclerView = view.findViewById(R.id.user_info_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        myGuanzhu();
    }

    public void setAdapter(List<User> users){
        if (recyclerView.getAdapter() == null) {
            userRecyclerViewAdapter = new UserRecyclerViewAdapter(users, getActivity());
            userRecyclerViewAdapter.setOnItemClickLitener((view, position) -> {
                User user = users.get(position);
                Intent i = new Intent(getParentFragment().getActivity(), UserInfoActivity.class);
                i.putExtra("xh", user.getXh());
                i.putExtra("nc", user.getNc());
                i.putExtra("txlj", user.getTxlj());
                i.putExtra("sr", user.getSr());
                i.putExtra("gxqm", user.getGxqm());
                startActivity(i);
            });
            recyclerView.setAdapter(userRecyclerViewAdapter);
        } else {
            userRecyclerViewAdapter.refresh(users);
        }
    }

    private void myGuanzhu(){
        ObserverOnNextListener<MyResponse> downloadObserverOnNextListener = myResponse -> {
            if (myResponse.getState().equals("1")){
                if (myResponse.getUserData() != null && myResponse.getUserData().size()>0){
                    users = myResponse.getUserData();
                    setAdapter(users);
                    emptyUserTextView.setVisibility(View.GONE);
                }else{
                    users = new ArrayList<>();
                    setAdapter(users);
                    emptyUserTextView.setVisibility(View.VISIBLE);
                }
            }else{
                ToastUtil.normal(myResponse.getError());
            }
        };

        ApiMethods.getGuanzhu(new ProgressObserver<>(getActivity(), downloadObserverOnNextListener, "加载中...", false, true), SPTool.getSharedPreferences().getString("xh", "000000000000"));
    }
}
