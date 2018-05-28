package com.dummy.androidvolleyrequestdb.utils;

import java.util.ArrayList;

public interface OnApiCompleteListner {
    void successApiResponse(String s, int responseId);

//    void errorApiResponse(VolleyError error);

    void errorApiResponse(String s, String errorMessage1, String errorMessage2, ArrayList<String> errorMessageList, int statusCode, int responseId);
}
