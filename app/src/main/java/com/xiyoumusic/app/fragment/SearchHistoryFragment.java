package com.xiyoumusic.app.fragment;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.xiyoumusic.app.R;
import com.xiyoumusic.app.activitys.SearchActivity;
import com.xiyoumusic.app.viewModel.SearchViewModel;

import java.util.List;

public class SearchHistoryFragment extends Fragment {
    private OnFragmentInteractionListener mListener;
    private ChipGroup chipGroup;
    private SearchViewModel searchViewModel;
    private Button clearBtn;

    public SearchHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        searchViewModel = ViewModelProviders.of(getActivity()).get(SearchViewModel.class);
        searchViewModel.searchList.observe(this, new Observer<List<String>>() {
            @Override
            public void onChanged(List<String> list) {
                updateChipGroup();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_history, container, false);

        chipGroup = view.findViewById(R.id.chipGroup_search_history);
        updateChipGroup();

        clearBtn = view.findViewById(R.id.btn_clear_history);
        clearBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListener.onFragmentInteraction("clear");
            }
        });
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(String uri);
    }
    public void updateChipGroup(){
        chipGroup.removeAllViews();
        List<String> list = searchViewModel.searchList.getValue();
        for (String searchHistory : list){
            final Chip chip = new Chip(getActivity());

            ChipDrawable chipDrawable = ChipDrawable.createFromResource(getActivity(), R.xml.chip_drawable);
            chipDrawable.setAlpha(80);
            chip.setChipDrawable(chipDrawable);

            chip.setText(searchHistory);
            chip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFragmentInteraction("search/"+chip.getText());
                }
            });
            chip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mListener.onFragmentInteraction("delete/"+chip.getText());
                    Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.chip_close);
                    chip.startAnimation(animation);
                    chip.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            chip.setVisibility(View.GONE);
                        }
                    }, 500);
                }
            });
            chipGroup.addView(chip);
        }
    }

    @Override
    public void onResume() {
        SearchActivity.fragmentFlag = SearchActivity.FRAGMENT_SEARCH_HISTORY;
        super.onResume();
    }
}
