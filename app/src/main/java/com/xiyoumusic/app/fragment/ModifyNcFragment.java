package com.xiyoumusic.app.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.viewModel.UserViewModel;

public class ModifyNcFragment extends Fragment implements View.OnClickListener{
    private String TAG = this.getClass().getSimpleName();
    private TextInputEditText ncText;
    private Button modifyBtn;
    private UserInfoFragment.OnFragmentInteractionListener mListener;

    private UserViewModel userViewModel;
    public ModifyNcFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        userViewModel = ViewModelProviders.of(getActivity()).get(UserViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_modify_nc, container, false);
        ncText = view.findViewById(R.id.new_nc);
        modifyBtn = view.findViewById(R.id.modify_nc);
        modifyBtn.setOnClickListener(this);
        return view;
    }


    @Override
    public void onAttach(Context context) {
        if (context instanceof UserInfoFragment.OnFragmentInteractionListener) {
            mListener = (UserInfoFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
        super.onAttach(context);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.modify_nc:
                if (validNewNc()){
                    User newUser = new User();
                    User user = userViewModel.user.getValue();
                    newUser.setTxlj(user.getTxlj());
                    newUser.setUuid(user.getUuid());
                    newUser.setMm(user.getMm());
                    newUser.setGxqm(user.getGxqm());
                    newUser.setXh(user.getXh());
                    newUser.setSr(user.getSr());
                    newUser.setNc(ncText.getText().toString());
                    if (mListener != null){
                        mListener.onFragmentInteraction("save", newUser);
                    }
                }
                break;
        }
    }

    public boolean validNewNc(){
        String newNc = ncText.getText().toString();
        if (newNc != null && !"".equals(newNc) && newNc.length()<=12){
            return true;
        }else if (newNc.length()>12){
            ToastUtil.error("新昵称不能超过12位");
            return false;
        }else{
            ToastUtil.error("新昵称不能为空");
            return false;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }
}
