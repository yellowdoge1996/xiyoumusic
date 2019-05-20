package com.xiyoumusic.app.viewModel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.User;

import java.util.List;

public class SearchViewModel extends ViewModel {
    public MutableLiveData<List<String>> searchList = new MutableLiveData<>();
    public MutableLiveData<String> searchOption = new MutableLiveData<>();
    public MutableLiveData<String> xh = new MutableLiveData<>();
    public MutableLiveData<List<User>> userList = new MutableLiveData<>();
    public MutableLiveData<List<Music>> musicList = new MutableLiveData<>();
}
