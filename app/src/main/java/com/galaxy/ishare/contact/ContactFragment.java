package com.galaxy.ishare.contact;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.galaxy.ishare.R;

public class ContactFragment extends Fragment {

    private View mRoot;
    private ListView mListView;
    private View mEmptyHint;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = LayoutInflater.from(getActivity());
        mRoot = lf.inflate(R.layout.share_item_fragment, container, false);


        return mRoot;
    }

}
