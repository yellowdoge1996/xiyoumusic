package com.xiyoumusic.app.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.viewpager.widget.ViewPager;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.entity.Music;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.viewModel.SearchViewModel;

import java.util.ArrayList;
import java.util.List;

public class SearchResultFragment extends Fragment {
    private SearchHistoryFragment.OnFragmentInteractionListener mListener;
    private SearchViewModel searchViewModel;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private MyPagerAdapter myPagerAdapter;
    private String[] titles = new String[]{"用户","歌曲"};

    public SearchResultFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        searchViewModel.searchOption.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String searchOption) {
                getResult();
            }
        });
        myPagerAdapter = new MyPagerAdapter(getChildFragmentManager());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search_result, container, false);
        tabLayout = view.findViewById(R.id.tablayout);
        viewPager = view.findViewById(R.id.viewpager);

        myPagerAdapter.add(new SearchUserResultFragment());
        tabLayout.addTab(tabLayout.newTab());
        myPagerAdapter.add(new SearchMusicResultFragment());
        tabLayout.addTab(tabLayout.newTab());

        viewPager.setAdapter(myPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);
        for(int i=0;i<titles.length;i++){
            tabLayout.getTabAt(i).setText(titles[i]);
        }
        getResult();
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof SearchHistoryFragment.OnFragmentInteractionListener) {
            mListener = (SearchHistoryFragment.OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    public void getResult(){
        String xh = searchViewModel.xh.getValue();
        if (TextUtils.isEmpty(xh)){
            xh = "000000000000";
        }
        String keyword = searchViewModel.searchOption.getValue();
        ObserverOnNextListener<MyResponse> observerOnNextListener = myResponse -> {
            List<User> userList;
            List<Music> musicList;
            if (myResponse.getUserData() == null){
                userList = new ArrayList<>();
            }else{
                userList = myResponse.getUserData();
            }
            if (myResponse.getMusicData() == null){
                musicList = new ArrayList<>();
            }else{
                musicList = myResponse.getMusicData();
            }
            searchViewModel.userList.postValue(userList);
            searchViewModel.musicList.postValue(musicList);
        };
        ApiMethods.seasrch(new ProgressObserver<>(getActivity(), observerOnNextListener, "搜索中..", true, true), xh, keyword);
    }
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    class MyPagerAdapter extends FragmentPagerAdapter{

        private final List<Fragment> mFragmentList = new ArrayList<>();

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void add(Fragment fragment){
            mFragmentList.add(fragment);
        }
    }
}
