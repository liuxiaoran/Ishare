package com.galaxy.ishare;

import java.util.List;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class DiscoverFragment extends Fragment{

	private View mRoot;
	private ListView mListView;
	private View mEmptyHint;

	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
		Bundle savedInstanceState) {
        LayoutInflater lf = LayoutInflater.from(getActivity());
        mRoot = lf.inflate(R.layout.discover_fragment, container, false);


        return mRoot;
	}

}
