package com.xiyoumusic.app.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.User;

import java.util.List;

public class UserViewModel extends ViewModel {
    public MutableLiveData<User> user = new MutableLiveData<>();
    public MutableLiveData<List<Music>> shoucang = new MutableLiveData<>();
    public MutableLiveData<String> sczxh = new MutableLiveData<>();
    public MutableLiveData<Boolean> islogin = new MutableLiveData<>();

    public UserViewModel() {
        super();
        islogin.setValue(false);
    }
}
