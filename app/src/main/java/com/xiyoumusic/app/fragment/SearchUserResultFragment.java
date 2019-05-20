package com.xiyoumusic.app.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.UserInfoActivity;
import com.xiyoumusic.app.adapter.UserRecyclerViewAdapter;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.viewModel.SearchViewModel;

import java.util.List;


public class SearchUserResultFragment extends Fragment {
    RecyclerView recyclerView;
    UserRecyclerViewAdapter userRecyclerViewAdapter;
    SearchViewModel searchViewModel;
    TextView emptyUserTextView;
    public SearchUserResultFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchViewModel = ViewModelProviders.of(getParentFragment().getActivity()).get(SearchViewModel.class);
        searchViewModel.userList.observe(this, users -> {
            if (users.size() == 0){
                emptyUserTextView.setVisibility(View.VISIBLE);
            }else{
                emptyUserTextView.setVisibility(View.GONE);
            }
            setAdapter(users);
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_user_result, container, false);
        emptyUserTextView = view.findViewById(R.id.empty_user_info);
        recyclerView = view.findViewById(R.id.user_info_recycler_view);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getParentFragment().getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (searchViewModel.userList.getValue() != null) {
            setAdapter(searchViewModel.userList.getValue());
        }else{
            emptyUserTextView.setVisibility(View.VISIBLE);
        }
    }

    public void setAdapter(List<User> users){
        if (recyclerView.getAdapter() == null) {
            userRecyclerViewAdapter = new UserRecyclerViewAdapter(users, getParentFragment().getActivity());
            userRecyclerViewAdapter.setOnItemClickLitener((view, position) -> {
                User user = searchViewModel.userList.getValue().get(position);
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
}
