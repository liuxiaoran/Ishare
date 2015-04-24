package com.galaxy.ishare.usercenter;

import java.util.HashMap;
import java.util.Random;

import com.galaxy.ishare.R;
import com.galaxy.util.utils.*;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Toast;

public class MeFragment extends Fragment {

    private View mRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LayoutInflater lf = LayoutInflater.from(getActivity());
        mRoot = lf.inflate(R.layout.me_fragment, container, false);

        return mRoot;
    }
}
