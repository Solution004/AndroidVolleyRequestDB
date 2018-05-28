package com.dummy.androidvolleyrequestdb.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.widget.Toast;

import com.android.volley.Request;
import com.dummy.androidvolleyrequestdb.R;
import com.dummy.androidvolleyrequestdb.utils.ConnectionStatus;
import com.dummy.androidvolleyrequestdb.network.QueryApi;
import com.dummy.androidvolleyrequestdb.utils.OnApiCompleteListner;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity implements OnApiCompleteListner {

    private QueryApi queryApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        queryApi = new QueryApi(this, this);
        callLoginApi();
    }

    // call Login Api
    private void callLoginApi() {
        if (ConnectionStatus.isInternetOn(this)) {
           JSONObject loginJsonObject= getJsonLoginObject();
            JsonObject jsonObject = (JsonObject) new JsonParser().parse(loginJsonObject.toString());
            queryApi.jsonCommonApiCall("LoginUrl", Request.Method.POST, 22, jsonObject, true);
        } else {
            Toast.makeText(this, "No Internet Connection.", Toast.LENGTH_SHORT).show();
        }
    }

    private JSONObject getJsonLoginObject() {
       JSONObject loginJsonObject = new JSONObject();
       String password="123";
        try {
            byte[] encodeValue = Base64.encode(password.getBytes(), Base64.DEFAULT);
            loginJsonObject.put("username", "");
            loginJsonObject.put("password", new String(encodeValue).trim());
            JSONObject loginDeviceJsonObject = new JSONObject();
            loginDeviceJsonObject.put("type", "android");
            loginJsonObject.put("device", loginDeviceJsonObject);
        } catch (
                JSONException e) {
            e.printStackTrace();
        }
        return loginJsonObject;
    }

    @Override
    public void successApiResponse(String s, int responseId) {

    }

    @Override
    public void errorApiResponse(String s, String errorMessage1, String errorMessage2, ArrayList<String> errorMessageList, int statusCode, int responseId) {

    }
}
