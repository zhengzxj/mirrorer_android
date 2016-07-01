package com.smart.mirrorer.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.QuestionRecordAdapter;
import com.smart.mirrorer.base.BaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzm on 16/4/6.
 */
public class RecordFragment extends BaseFragment {

    public static final String REQUEST_TYPE = "request_type";
    public static final String TYPE_RECORD = "3";

    private QuestionRecordAdapter mAdapter;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mAdapter = new QuestionRecordAdapter();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_single_listview_layout, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recordRecyclerView = (RecyclerView) getView().findViewById(R.id.fragment_single_listview);
        recordRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recordRecyclerView.setAdapter(mAdapter);

        List<String> testDatas = new ArrayList<>();
        testDatas.add("1");
        testDatas.add("2");
        mAdapter.setListData(testDatas);
    }

}
