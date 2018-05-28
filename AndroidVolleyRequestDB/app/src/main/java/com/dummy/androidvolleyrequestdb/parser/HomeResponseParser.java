package com.dummy.androidvolleyrequestdb.parser;

import com.dummy.androidvolleyrequestdb.model.Data;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class HomeResponseParser {

    /**
     * @param response
     * @return
     */
    public static ArrayList<Data> parseMessageListingResponse(String response) {
        ArrayList<Data> quickMessageArrayList = new ArrayList<>();
        Data quickMessageData;
        try {
            JSONObject jsonObject = new JSONObject(response);
            String responseType = jsonObject.optString("type");
                if (jsonObject.has("contacts")) {
                    JSONArray jsonArraydata = jsonObject.optJSONArray("contacts");
                    for (int i = 0; i < jsonArraydata.length(); i++) {
                        quickMessageData = new Data();
                        JSONObject jsonObjectInner = jsonArraydata.optJSONObject(i);
                        String email = jsonObjectInner.optString("email");
                        quickMessageData.setEmail(email);
                        String address = jsonObjectInner.optString("address");
                        quickMessageData.setAddress(address);
                        quickMessageArrayList.add(quickMessageData);
                    }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return quickMessageArrayList;
    }
}