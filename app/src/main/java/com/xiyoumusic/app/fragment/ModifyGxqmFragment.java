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


public class ModifyGxqmFragment extends Fragment implements View.OnClickListener{
    private String TAG = this.getClass().getSimpleName();
    private TextInputEditText gxqmText;
    private Button modifyBtn;
    private UserInfoFragment.OnFragmentInteractionListener mListener;

    private UserViewModel userViewModel;

    public ModifyGxqmFragment() {
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
        View view =  inflater.inflate(R.layout.fragment_modify_gxqm, container, false);
        gxqmText = view.findViewById(R.id.new_gxqm);
        gxqmText.setText(userViewModel.user.getValue().getGxqm());
        modifyBtn = view.findViewById(R.id.modify_gxqm);
        modifyBtn.setOnClickListener(this);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof UserInfoFragment.OnFragmentInteractionListener) {
            mListener = (UserInfoFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.modify_gxqm:
                if (validNewGxqm()) {
                    User newUser = new User();
                    User user = userViewModel.user.getValue();
                    newUser.setTxlj(user.getTxlj());
                    newUser.setUuid(user.getUuid());
                    newUser.setMm(user.getMm());
                    newUser.setNc(user.getNc());
                    newUser.setXh(user.getXh());
                    newUser.setSr(user.getSr());
                    newUser.setGxqm(gxqmText.getText().toString());
                    if (mListener != null) {
                        mListener.onFragmentInteraction("save", newUser);
                    }
                }
                break;
            default:break;
        }
    }

    public boolean validNewGxqm(){
        String newGxqm = gxqmText.getText().toString();
        if (newGxqm.length() <= 50){
            return true;
        }else{
            ToastUtil.error("新个性签名不能超过50位");
            return false;
        }
    }

}
