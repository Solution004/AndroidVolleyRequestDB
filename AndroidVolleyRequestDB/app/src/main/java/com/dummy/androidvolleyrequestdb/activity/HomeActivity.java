package com.dummy.androidvolleyrequestdb.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.dummy.androidvolleyrequestdb.R;
import com.dummy.androidvolleyrequestdb.model.Data;
import com.dummy.androidvolleyrequestdb.parser.HomeResponseParser;
import com.dummy.androidvolleyrequestdb.utils.ConnectionStatus;
import com.dummy.androidvolleyrequestdb.network.QueryApi;
import com.dummy.androidvolleyrequestdb.utils.HomeAdapter;
import com.dummy.androidvolleyrequestdb.utils.OnApiCompleteListner;
import com.dummy.androidvolleyrequestdb.utils.VerticalItemDecoration;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity implements OnApiCompleteListner, SwipeRefreshLayout.OnRefreshListener {
    private Context mContext;
    private RecyclerView mRecycleView;
    private HomeAdapter mHomeAdapter;
    private ArrayList<Data> mArrayList;
    private TextView mInfoTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private QueryApi mQueryApi;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initializeView();
        setUpRecyclerView();
    }

    private void initializeView() {
        mContext = HomeActivity.this;
        mQueryApi = new QueryApi(this, this);
        setContentView(R.layout.activity_home);
        mInfoTextView = findViewById(R.id.tv_info);
        mSwipeRefreshLayout = findViewById(R.id.home_swipeRefreshLayout);
        mRecycleView = (RecyclerView) findViewById(R.id.recycler_view);
        mArrayList = new ArrayList<>();
        mSwipeRefreshLayout.setOnRefreshListener(this);
    }

    /**
     * The purpose of this method is to setup the recycler view
     */
    private void setUpRecyclerView() {
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(mContext);
        mRecycleView.setLayoutManager(mLayoutManager);
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mHomeAdapter = new HomeAdapter(HomeActivity.this, mArrayList);
        mRecycleView.addItemDecoration(new VerticalItemDecoration(20, false));
        mRecycleView.setAdapter(mHomeAdapter);

    }

    /**
     * The purpose of this method is to set the visibility of info text
     */
    public void setVisibility() {
        if (mArrayList.size() > 0) {
            mInfoTextView.setVisibility(View.GONE);
        } else {
            mInfoTextView.setVisibility(View.VISIBLE);
        }
    }


    /**
     * close swipe refresh progress or pagination progress bar
     *
     * @param isRefreshing
     */
    private void setRefreshStatus(boolean isRefreshing) {
        if (isRefreshing) {
            mSwipeRefreshLayout.setRefreshing(true);
        } else {
            if (mSwipeRefreshLayout.isRefreshing()) {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    public void callHomeListingApi(boolean isloader) {
        if (ConnectionStatus.isInternetOn(mContext)) {
            JSONObject myJobsJsonObject = getListingJsonObject();
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(myJobsJsonObject.toString());
            mQueryApi.commonApiCallGet("https://api.androidhive.info/contacts/", Request.Method.POST, 22, isloader);
        } else {
            setRefreshStatus(false);
            Toast.makeText(mContext, mContext.getResources().getString(R.string.sorry_no_internet_connection), Toast.LENGTH_LONG).show();
            setVisibility();
        }
    }

    private JSONObject getListingJsonObject() {
        JSONObject myJobsJsonObject = new JSONObject();
        try {
            myJobsJsonObject.put("key", "");
            myJobsJsonObject.put("userid", "");
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return myJobsJsonObject;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (mArrayList.size() <= 0) {
            callHomeListingApi(false);
        }

    }


    @Override
    public void successApiResponse(String s, int responseId) {
        ArrayList<Data> mTempList;
        switch (responseId) {
            case 22:
                setRefreshStatus(false);
                mTempList = HomeResponseParser.parseMessageListingResponse(s);
                if (mTempList != null && mTempList.size() > 0) {
                    mInfoTextView.setVisibility(View.GONE);
                    mArrayList.clear();
                    mArrayList.addAll(mTempList);
                    mHomeAdapter.notifyDataSetChanged();
                } else {
                    mArrayList.clear();
                    mHomeAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    @Override
    public void errorApiResponse(String s, String errorMessage1, String errorMessage2, ArrayList<String> errorMessageList, int statusCode, int responseId) {
        if (responseId == 22) {
            setRefreshStatus(false);
            setVisibility();
        }
    }

    @Override
    public void onRefresh() {
        setRefreshStatus(true);
        callHomeListingApi(false);
    }
}
