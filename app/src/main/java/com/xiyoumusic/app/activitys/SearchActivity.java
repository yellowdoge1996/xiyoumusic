package com.xiyoumusic.app.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProviders;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.xiyoumusic.app.R;
import com.xiyoumusic.app.adapter.SuggestionAdapter;
import com.xiyoumusic.app.entity.MyResponse;
import com.xiyoumusic.app.entity.User;
import com.xiyoumusic.app.fragment.SearchHistoryFragment;
import com.xiyoumusic.app.utils.SPTool;
import com.xiyoumusic.app.utils.ToastUtil;
import com.xiyoumusic.app.utils.retrofit.ApiMethods;
import com.xiyoumusic.app.utils.retrofit.ObserverOnNextListener;
import com.xiyoumusic.app.utils.retrofit.ProgressObserver;
import com.xiyoumusic.app.viewModel.SearchViewModel;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends BaseActivity implements SearchHistoryFragment.OnFragmentInteractionListener
        , View.OnClickListener {
    public String TAG = this.getClass().getSimpleName();
    public SharedPreferences sharedPreferences;
    private final String SEARCH_HISTORY = "search_history";
    public SearchViewModel searchViewModel;
    public SearchView searchView;
    public TextView cancel;
    public LinearLayout searchSuggestionLayout;
    public ListView searchSuggestionList;
    public AutoCompleteTextView autoCompleteTextView;
    public SuggestionAdapter adapter;
    public static boolean isSearching = false;
    public static int FRAGMENT_SEARCH_HISTORY = 1;
    public static int FRAGMENT_SEARCH_RESULT = 2;
    public static int fragmentFlag = FRAGMENT_SEARCH_HISTORY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        searchView = findViewById(R.id.search_view);
        cancel = findViewById(R.id.cancel);
        cancel.setOnClickListener(this);
        searchSuggestionLayout = findViewById(R.id.search_suggestion_layout);
        searchSuggestionLayout.setVisibility(View.GONE);
        searchSuggestionLayout.setOnClickListener(this);
        searchSuggestionList = findViewById(R.id.search_suggestion_list);
        searchSuggestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = view.findViewById(R.id.text1);
                String text = textView.getText().toString();
                isSearching = true;
                autoCompleteTextView.setText(text);
                searchSuggestionLayout.setVisibility(View.GONE);
                search(text);
            }
        });

        initSearchView();
        initViewModel();
    }

    public void initViewModel(){
        searchViewModel = ViewModelProviders.of(this).get(SearchViewModel.class);
        sharedPreferences = SPTool.getSharedPreferences();
        initSearchHistory();
        searchViewModel.xh.setValue(sharedPreferences.getString("xh", "000000000000"));
    }

    public void initSearchHistory() {
        String history = sharedPreferences.getString(SEARCH_HISTORY,"");
        List<String> list = new ArrayList<>();
        if (!TextUtils.isEmpty(history)){
            for(Object o : history.split(",")) {
                list.add((String)o);
            }
        }
        searchViewModel.searchList.setValue(list);
        searchViewModel.searchOption.setValue("");
    }


    @Override
    public void onFragmentInteraction(String uri) {
        String[] splits = uri.split("/");
        switch (splits[0]){
            case "delete":
                delete(splits[1]);
                break;
            case "search":
                isSearching = true;
                search(splits[1]);
                save(splits[1]);
                break;
            case "clear":
                cleanHistory();
                break;
            default:break;
        }
    }

    public void initSearchView(){
        searchView.onActionViewExpanded();// 写上此句后searchView初始是可以点击输入的状态，如果不写，那么就需要点击下放大镜，才能出现输入框,也就是设置为ToolBar的ActionView，默认展开
        searchView.requestFocus();//输入焦点
//        searchView.setSubmitButtonEnabled(true);//添加提交按钮，监听在OnQueryTextListener的onQueryTextSubmit响应
        searchView.setFocusable(true);//将控件设置成可获取焦点状态,默认是无法获取焦点的,只有设置成true,才能获取控件的点击事件
        searchView.setIconified(false);//输入框内icon不显示
        searchView.requestFocusFromTouch();//模拟焦点点击事件

//        searchView.setFocusable(false);//禁止弹出输入法，在某些情况下有需要
//        searchView.clearFocus();//禁止弹出输入法，在某些情况下有需要
        autoCompleteTextView = searchView.findViewById(R.id.search_src_text);
        autoCompleteTextView.setTextColor(0x40000000);
        autoCompleteTextView.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                        isSearching = false;
                        if (adapter == null || adapter.getSuggestions() == null || adapter.getSuggestions().size() == 0){
                            return;
                        }
                        searchSuggestionLayout.setVisibility(View.VISIBLE);
                }else {
                    searchSuggestionLayout.setVisibility(View.GONE);
                }
            }
        });

//事件监听
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearching = true;
                searchSuggestionLayout.setVisibility(View.GONE);
                search(query);

                return false;
            }

            @Override
            public boolean onQueryTextChange(final String newText) {
                if (TextUtils.isEmpty(newText)){
                    setAdapter(new ArrayList<String>());
                    searchSuggestionLayout.setVisibility(View.GONE);
                    return false;
                }
                if (!isSearching) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            String nowText = searchView.getQuery().toString();
                            if (!TextUtils.isEmpty(newText) && nowText.equals(newText)) {
                                ObserverOnNextListener<MyResponse> observerOnNextListener = new ObserverOnNextListener<MyResponse>() {
                                    @Override
                                    public void onNext(MyResponse myResponse) {
                                        if ("1".equals(myResponse.getState())){
                                            List<String> suggestions = new ArrayList<String>();
                                            String[] strings = myResponse.getMsg().split(",");
                                            for (int i = 0; i < strings.length; i++) {
                                                suggestions.add(strings[i]);
                                            }
                                            setAdapter(suggestions);
                                            if (!isSearching) {
                                                searchSuggestionLayout.setVisibility(View.VISIBLE);
                                            }
                                        }else{
                                            if (myResponse.getError() != null) {
                                                setAdapter(new ArrayList<String>());
                                                searchSuggestionLayout.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                };
                                ApiMethods.getSuggestions(new ProgressObserver<MyResponse>(SearchActivity.this, observerOnNextListener, null, true, false), newText);
                            }
                        }
                    }, 500);
                }
                return false;
            }
        });
    }
    public void setAdapter(List<String> list){
        if (searchSuggestionList.getAdapter() == null) {
            adapter = new SuggestionAdapter(this, list);
            searchSuggestionList.setAdapter(adapter);
        } else {
            adapter.refresh(list);
        }
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.search_suggestion_layout:
                searchSuggestionLayout.setVisibility(View.GONE);
                break;
            case R.id.cancel:
                onBackPressed();
                break;
        }
    }

    public void search(String searchOption){
        searchViewModel.searchOption.setValue(searchOption);
        searchView.clearFocus();
        if (fragmentFlag == FRAGMENT_SEARCH_HISTORY) {
             Navigation.findNavController(this, R.id.nav_host3_fragment).navigate(R.id.action_searchHistoryFragment_to_searchResultFragment);
             fragmentFlag = FRAGMENT_SEARCH_RESULT;
        }
        save(searchOption);
    }
    public void save(String history) {
        String oldText = sharedPreferences.getString(SEARCH_HISTORY,"");
        String[] strings = oldText.split(",");
        if (!TextUtils.isEmpty(history)) {
            for (int i = 0; i < strings.length; i++) {
                if (strings[i].equals(history)){
                    if (0 == i){
                        oldText = oldText.replaceFirst(history+",", "");
                    }else {
                        oldText = oldText.replace(","+history+",", ",");
                    }
                }
            }
            SPTool.put(SEARCH_HISTORY, history + "," + oldText);
            List<String> oldlist =  searchViewModel.searchList.getValue();
            oldlist.remove(history);
            oldlist.add(0,history);
            searchViewModel.searchList.postValue(oldlist);
        }
    }

    public void delete(String history){
        String oldText = sharedPreferences.getString(SEARCH_HISTORY,"");
        if (!TextUtils.isEmpty(history) && oldText.contains(history)) {
            SPTool.put(SEARCH_HISTORY, oldText.replace(history+",", ""));
            List<String> oldlist =  searchViewModel.searchList.getValue();
            oldlist.remove(history);
//            searchViewModel.searchList.postValue(oldlist);
        }
    }

    public void cleanHistory() {
        SPTool.remove(ToastUtil.getContext(), SEARCH_HISTORY);
        List<String> oldlist =  searchViewModel.searchList.getValue();
        oldlist.clear();
        searchViewModel.searchList.postValue(oldlist);
    }

    @Override
    public void onBackPressed() {
        searchSuggestionLayout.setVisibility(View.GONE);
        super.onBackPressed();
    }
}
