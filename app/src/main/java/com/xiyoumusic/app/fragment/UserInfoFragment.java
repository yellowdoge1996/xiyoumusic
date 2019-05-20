package com.xiyoumusic.app.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.adapter.UserInfoAdapter;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.utils.DataTools;
import com.xiyoumusic.app.viewModel.UserViewModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserInfoFragment extends Fragment {
//    public Context context;
    private UserViewModel userViewModel;
    private OnFragmentInteractionListener mListener;
    private final String TAG = this.getClass().getSimpleName();
    public UserInfoAdapter userInfoAdapter;

    final List<Map<String, String>> list = new ArrayList<>();


    public UserInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
        userViewModel.user.observe(this, user -> {
            getData();
            userInfoAdapter.refresh(list);
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        ListView listView = view.findViewById(R.id.user_info_list);
        getData();
        userInfoAdapter = new UserInfoAdapter(getActivity(), list);
        listView.setAdapter(userInfoAdapter);
        listView.setOnItemClickListener((adapterView, view1, i, l) -> {
            String key = list.get(i).keySet().iterator().next();
            if ("sr".equals(key)){
                final String sr = userViewModel.user.getValue().getSr();
                String[] split = sr.split("-");

                // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
                new DatePickerDialog(getActivity(),
                        // 绑定监听器
                        (view11, year, monthOfYear, dayOfMonth) -> {
                            String newSr = year+"-"+(monthOfYear+1)+"-"+dayOfMonth;
                            User newUser = new User();
                            User user = userViewModel.user.getValue();
                            newUser.setTxlj(user.getTxlj());
                            newUser.setUuid(user.getUuid());
                            newUser.setMm(user.getMm());
                            newUser.setNc(user.getNc());
                            newUser.setXh(user.getXh());
                            newUser.setGxqm(user.getGxqm());
                            newUser.setSr(newSr);
                            if (mListener != null) {
                                mListener.onFragmentInteraction("save", newUser);
                            }
                        }
                        // 设置初始日期
                        , DataTools.stringToInt(split[0])
                        , DataTools.stringToInt(split[1])-1
                        , DataTools.stringToInt(split[2])).show();
            }else{
                if (mListener != null) {
                    mListener.onFragmentInteraction(key,null);
                }
            }
        });
        return view;
    }

    private void getData() {
        User user = userViewModel.user.getValue();
        list.clear();
        Map<String, String> txljMap= new HashMap<>();
        txljMap.put("txlj",user.getTxlj());
        list.add(txljMap);
        Map<String, String> ncMap= new HashMap<>();
        ncMap.put("nc",user.getNc());
        list.add(ncMap);
        Map<String, String> xhMap= new HashMap<>();
        xhMap.put("xh",user.getXh());
        list.add(xhMap);
        Map<String, String> srMap= new HashMap<>();
        srMap.put("sr",user.getSr());
        list.add(srMap);
        Map<String, String> gxqmMap= new HashMap<>();
        gxqmMap.put("gxqm",user.getGxqm());
        list.add(gxqmMap);
    }

    @Override
    public void onAttach(Context context) {
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
//        this.context = context;
        super.onAttach(context);
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(String infoName, User user);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        if (mListener != null) {
            mListener.onFragmentInteraction("resume",null);
        }
        super.onResume();
    }

}
