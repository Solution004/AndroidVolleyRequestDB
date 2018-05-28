package com.dummy.androidvolleyrequestdb.network;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dummy.androidvolleyrequestdb.utils.OnApiCompleteListner;
import com.google.gson.JsonObject;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QueryApi {
    private Context context;
    private OnApiCompleteListner onApiCompleteListner;
    private ProgressDialog dialog;
//    private SharedPreferenceUtility sharedPreferenceUtility;
    private ProgressDialog kProgressHUD;

    public QueryApi(Context context, OnApiCompleteListner onApiCompleteListner) {
        this.context = context;
        this.onApiCompleteListner = onApiCompleteListner;
        dialog = new ProgressDialog(context);
//        sharedPreferenceUtility = new SharedPreferenceUtility(context);
    }

    public void commonApiCall(String content, int requestType, final int responseId, boolean isDialog) {
        if (isDialog) {
            showDialog("Please wait...");
        }

        StringRequest stringRequest = new StringRequest(requestType, content, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                closeDialog();
                onApiCompleteListner.successApiResponse(response, responseId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                closeDialog();
                Toast.makeText(context, "Server error..", Toast.LENGTH_LONG).show();
//                onApiCompleteListner.errorApiResponse(error);
            }
        });
        NetworkHelper.getInstance(context).addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
    }

    public void commonApiCallGet(String content, int requestType, final int responseId, boolean isDialog) {
        if (isDialog) {
            showDialog("Please wait...");
        }

        StringRequest stringRequest = new StringRequest(requestType, content, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                closeDialog();
                onApiCompleteListner.successApiResponse(response, responseId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                closeDialog();
//                onApiCompleteListner.errorApiResponse(error);
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage1 = "Unknown error";
                String errorMessage2 = "Unknown message";
                ArrayList<String> errorMessage = new ArrayList();
                int statusCode = 0;
                if (networkResponse == null) {
                    if (error instanceof TimeoutError) {
                        errorMessage1 = "Request timeout";
                    } else if (error instanceof NoConnectionError) {
                        errorMessage1 = "Failed to connect server";
                    } else if (error instanceof NetworkError) {
                        errorMessage1 = "Network Error";
                    } else if (error instanceof ServerError) {
                        errorMessage1 = "Server Error";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String type = response.getString("type");
                        JSONArray jsonArray = response.getJSONArray("errors");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMessage.add(jsonArray.optString(i));
                        }
                        if (networkResponse.statusCode == 404) {
                            errorMessage2 = "Resource not found";
                            statusCode = 404;
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage2 = type + " Please login again";
                            statusCode = 401;
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage2 = type + " Check your inputs";
                            statusCode = 400;
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage2 = type + " Something is getting wrong";
                            statusCode = 500;
                        } else if (networkResponse.statusCode == 412) {
                            errorMessage2 = type + " Something is getting wrong";
                            statusCode = 500;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onApiCompleteListner.errorApiResponse(error.toString(), errorMessage1, errorMessage2, errorMessage, statusCode, responseId);
                Log.i("Error", errorMessage1 + errorMessage2);
                error.printStackTrace();
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
//                headers.put("Authorization", "Bearer " + sharedPreferenceUtility.getToken());
                return headers;
            }
        };
        NetworkHelper.getInstance(context).addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
    }

    public void paramsCommonApiCall(String content, int requestType, final int responseId, final String parameters[], final String values[], boolean isDialog) {
        if (isDialog) {
            showDialog("Please wait...");
        }
        StringRequest stringRequest = new StringRequest(requestType, content, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                closeDialog();
                onApiCompleteListner.successApiResponse(response, responseId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                closeDialog();
//                onApiCompleteListner.errorApiResponse(error);
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage1 = "Unknown error";
                String errorMessage2 = "Unknown message";
                ArrayList<String> errorMessage = new ArrayList();
                int statusCode = 0;
                if (networkResponse == null) {
                    if (error instanceof TimeoutError) {
                        errorMessage1 = "Request timeout";
                    } else if (error instanceof NoConnectionError) {
                        errorMessage1 = "Failed to connect server";
                    } else if (error instanceof NetworkError) {
                        errorMessage1 = "Network Error";
                    } else if (error instanceof ServerError) {
                        errorMessage1 = "Server Error";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String type = response.getString("type");
                        JSONArray jsonArray = response.getJSONArray("errors");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMessage.add(jsonArray.optString(i));
                        }
                        if (networkResponse.statusCode == 404) {
                            errorMessage2 = "Resource not found";
                            statusCode = 404;
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage2 = type + " Please login again";
                            statusCode = 401;
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage2 = type + " Check your inputs";
                            statusCode = 400;
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage2 = type + " Something is getting wrong";
                            statusCode = 500;
                        } else if (networkResponse.statusCode == 412) {
                            errorMessage2 = type + " Something is getting wrong";
                            statusCode = 500;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onApiCompleteListner.errorApiResponse(error.toString(), errorMessage1, errorMessage2, errorMessage, statusCode, responseId);
                Log.i("Error", errorMessage1 + errorMessage2);
                error.printStackTrace();
            }
        }) {
            @Override
            public String getBodyContentType() {
                return "application/x-www-form-urlencoded";
            }

            @Override
            protected Map<String, String> getParams() {
                return createHashMap(parameters, values);
            }
        };
        NetworkHelper.getInstance(context).addToRequestQueue(stringRequest);
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(100 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, 0, 0));
    }

    public void jsonCommonApiCall(final String content, int requestType, final int responseId, final JsonObject map, boolean isDialog) {
        if (isDialog) {
            showDialog("Please wait...");
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestType, content, map.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                closeDialog();
//                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                onApiCompleteListner.successApiResponse(response.toString(), responseId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                closeDialog();
//                Toast.makeText(context, "Server error..", Toast.LENGTH_SHORT).show();
//                onApiCompleteListner.errorApiResponse(error.toString(), "", "", null, 1, responseId);

                closeDialog();
//                onApiCompleteListner.errorApiResponse(error);
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage1 = "Unknown error";
                String errorMessage2 = "Unknown message";
                ArrayList<String> errorMessage = new ArrayList();
                int statusCode = 0;
                if (networkResponse == null) {
                    if (error instanceof TimeoutError) {
                        errorMessage1 = "Request timeout";
                    } else if (error instanceof NoConnectionError) {
                        errorMessage1 = "Failed to connect server";
                    } else if (error instanceof NetworkError) {
                        errorMessage1 = "Network Error";
                    } else if (error instanceof ServerError) {
                        errorMessage1 = "Server Error";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String type = response.getString("type");
                        String refresh = response.optString("refresh");
                        if (refresh.equals("true")) {
                            errorMessage1 = refresh;
                        }
                        JSONArray jsonArray = response.getJSONArray("errors");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMessage.add(jsonArray.optString(i));
                        }
                        if (networkResponse.statusCode == 404) {
                            errorMessage2 = "Resource not found";
                            statusCode = 404;
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage2 = type + " Please login again";
                            statusCode = 401;
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage2 = type + " Check your inputs";
                            statusCode = 400;
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage2 = type + " Something is getting wrong";
                            statusCode = 500;
                        } else if (networkResponse.statusCode == 412) {
                            errorMessage2 = type + " Something is getting wrong";
                            statusCode = 500;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onApiCompleteListner.errorApiResponse(error.toString(), errorMessage1, errorMessage2, errorMessage, statusCode, responseId);
                Log.i("Error", errorMessage1 + errorMessage2);
                error.printStackTrace();

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
//                headers.put("Authorization", "Bearer " + sharedPreferenceUtility.getToken());
                return headers;
            }
        };
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(25 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    public void jsonCommonApiWithOutTokenCall(final String content, int requestType, final int responseId, final JsonObject map, boolean isDialog) {
        if (isDialog) {
            showDialog("Please wait...");
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(requestType, content, map.toString(), new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                closeDialog();
//                Toast.makeText(context, "success", Toast.LENGTH_SHORT).show();
                onApiCompleteListner.successApiResponse(response.toString(), responseId);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                closeDialog();
//                Toast.makeText(context, "Server error..", Toast.LENGTH_SHORT).show();
//                onApiCompleteListner.errorApiResponse(error.toString(), "", "", null, 1, responseId);

                closeDialog();
//                onApiCompleteListner.errorApiResponse(error);
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage1 = "Unknown error";
                String errorMessage2 = "Unknown message";
                ArrayList<String> errorMessage = new ArrayList();
                int statusCode = 0;
                if (networkResponse == null) {
                    if (error instanceof TimeoutError) {
                        errorMessage1 = "Request timeout";
                    } else if (error instanceof NoConnectionError) {
                        errorMessage1 = "Failed to connect server";
                    } else if (error instanceof NetworkError) {
                        errorMessage1 = "Network Error";
                    } else if (error instanceof ServerError) {
                        errorMessage1 = "Server Error";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String type = response.getString("type");
                        JSONArray jsonArray = response.getJSONArray("errors");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            errorMessage.add(jsonArray.optString(i));
                        }
                        if (networkResponse.statusCode == 404) {
                            errorMessage2 = "Resource not found";
                            statusCode = 404;
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage2 = type + " Please login again";
                            statusCode = 401;
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage2 = type + " Check your inputs";
                            statusCode = 400;
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage2 = type + " Something is getting wrong";
                            statusCode = 500;
                        } else if (networkResponse.statusCode == 412) {
                            errorMessage2 = type + " Something is getting wrong";
                            statusCode = 500;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                onApiCompleteListner.errorApiResponse(error.toString(), errorMessage1, errorMessage2, errorMessage, statusCode, responseId);
                Log.i("Error", errorMessage1 + errorMessage2);
                error.printStackTrace();

            }
        });
        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(5 * DefaultRetryPolicy.DEFAULT_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        Volley.newRequestQueue(context).add(jsonObjectRequest);
    }

    private Map<String, String> createHashMap(String parameters[], String[] values) {
        Map<String, String> params = new HashMap<String, String>();
        if (parameters != null && values != null && parameters.length == values.length) {
            int paramsLength = parameters.length;
            for (int i = 0; i < paramsLength; i++) {
                params.put(parameters[i], values[i]);
            }
        }
        return params;
    }

    public String createUrl(String parameter[], String values[]) {
        String urlParameters = "";
        for (int i = 0; i < parameter.length; i++) {

            if (i != parameter.length - 1) {
                urlParameters += parameter[i] + "=" + values[i] + "&";
            } else {
                urlParameters += parameter[i] + "=" + values[i];
            }
        }

        return urlParameters;
    }

    private void showDialog(String message) {
//        dialog.setCancelable(false);
        if (dialog != null && !dialog.isShowing()) {
            dialog.setCanceledOnTouchOutside(false);
            dialog.setMessage(message);
            dialog.show();
        }
    }
    private void closeDialog() {
        if (kProgressHUD != null && kProgressHUD.isShowing()) {
            kProgressHUD.dismiss();
        }
    }

}
