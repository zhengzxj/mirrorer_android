package com.smart.mirrorer.history;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.smart.mirrorer.R;
import com.smart.mirrorer.adapter.OrderListAdapter;
import com.smart.mirrorer.base.BaseApplication;
import com.smart.mirrorer.base.BaseTitleActivity;
import com.smart.mirrorer.bean.home.OrderListBean;
import com.smart.mirrorer.net.GsonCallbackListener;
import com.smart.mirrorer.net.PerfectJsonObjectRequest;
import com.smart.mirrorer.util.MirrorSettings;
import com.smart.mirrorer.util.TipsUtils;
import com.smart.mirrorer.util.Urls;
import com.smart.mirrorer.util.mUtil.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzm on 16/3/26.
 */
public class HistoryActivity extends BaseTitleActivity {

    private OrderListAdapter mAdapter;
    private String mUid;
    private List<OrderListBean.ResultBean> mHistoryOrderList = new ArrayList<OrderListBean.ResultBean>();
    private MirrorSettings mSettings;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_layout);
        setupNavigationBar(R.id.navigation_bar);
        setCommonTitle(R.string.navigation_history_text);
        mSettings = new MirrorSettings(this);
        mUid = mSettings.APP_UID.getValue();
        initView();
        getHistoryOrder();
    }

    private void initView() {

        RecyclerView historyListview = (RecyclerView) findViewById(R.id.history_recycle_view);
        final LinearLayoutManager llm = new LinearLayoutManager(this);
        historyListview.setLayoutManager(llm);
        mAdapter = new OrderListAdapter();
        historyListview.setAdapter(mAdapter);
        historyListview.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visible  = llm.getChildCount();
                int total = llm.getItemCount();
                int past=llm.findFirstCompletelyVisibleItemPosition();
                if ((visible + past) >= total && canRequest){
                    page++;
                    getHistoryOrder();
                }
            }
        });

    }

    private boolean canRequest = true;
    private int page = 1;
    private void getHistoryOrder()
    {
        canRequest = false;
        String tag_json_obj = "json_obj_get_file_req";
        showLoadDialog();
        JSONObject param = new JSONObject();
        try {
            param.put("limit",10);
            param.put("page",page);
            L.i("evaluate page = "+page);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        PerfectJsonObjectRequest jsonObjReq = new PerfectJsonObjectRequest(mUid, Request.Method.POST,
                Urls.URL_ORDER_LIST, param, mCallBack, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                TipsUtils.showShort(getApplicationContext(), error.getMessage());
                dismissLoadDialog();
                canRequest = true;
            }
        });
        // Adding request to request queue
        BaseApplication.getInstance().addToRequestQueue(jsonObjReq, tag_json_obj);

    }

    private ArrayList<OrderListBean.ResultBean> orderList = new ArrayList<OrderListBean.ResultBean>();
    private GsonCallbackListener<OrderListBean> mCallBack = new GsonCallbackListener<OrderListBean>() {
        @Override
        public void onResultSuccess(OrderListBean orderListBean) {
            super.onResultSuccess(orderListBean);
            dismissLoadDialog();
            canRequest = true;
            if (orderListBean == null || orderListBean.getResult() == null) {
                return;
            }
            orderList.addAll(orderListBean.getResult());
            mAdapter.setListData(orderList,HistoryActivity.this);
        }

        @Override
        public void onFailed(String errorMsg) {
            super.onFailed(errorMsg);
            dismissLoadDialog();
            canRequest = true;
            TipsUtils.showShort(getApplicationContext(), errorMsg);
        }
    };
}
