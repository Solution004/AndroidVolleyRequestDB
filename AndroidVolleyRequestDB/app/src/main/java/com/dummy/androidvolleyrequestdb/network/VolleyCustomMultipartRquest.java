package com.dummy.androidvolleyrequestdb.network;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

public class VolleyCustomMultipartRquest {
    public static int GET = 0;
    public static int POST = 1;
    private ServiceResultListener serviceResultListener;
    private Context context;
    private int method;
    private String url;
    private int timeOut = 20000;
    private Map<String, String> params = null;
    private String parameterName;
    //parameterName = "userfile"
    private String fileName = "";
    private Map<String, VolleyMultipartRequest.DataPart> image = new HashMap<>();
    private String fileType = "image/jpeg";
    private byte[] dataParts;
    private String token;

    public VolleyCustomMultipartRquest(Context context, String token) {
        this.context = context;
        this.token = token;
    }

    public void setMethodAndUrl(int method, String url) {
        this.method = method;
        this.url = url;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public void setFile(String parameterName, String fileName, Uri uri) {
        this.parameterName = parameterName;
        this.fileName = fileName;
        dataParts = getFileDataFromPath(getPath(context, uri), 80);
    }

    public void setMultipleFiles(String parameterName, String fileName, Uri uri) {
        byte[] dataParts = getFileDataFromPath(getPath(context, uri), 80);
        image.put(parameterName, new VolleyMultipartRequest.DataPart(fileName, dataParts, fileType));
    }

    public void setFile(String parameterName, String fileName, Uri uri, int uploadQuality) {
        this.parameterName = parameterName;
        this.fileName = fileName;
        dataParts = getFileDataFromPath(getPath(context, uri), uploadQuality);
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public void executeRequest(final int requestCode) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(method, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                serviceResultListener.onVolleyMultipartResult(resultResponse, "null", "null", "null", 0, requestCode);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage1 = "Unknown error";
                String errorMessage2 = "Unknown message";
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
                        String status = response.getString("status");
                        String message = response.getString("message");
                        if (networkResponse.statusCode == 404) {
                            errorMessage2 = "Resource not found";
                            statusCode = 404;
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage2 = message + " Please login again";
                            statusCode = 401;
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage2 = message + " Check your inputs";
                            statusCode = 400;
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage2 = message + " Something is getting wrong";
                            statusCode = 500;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                serviceResultListener.onVolleyMultipartResult("null", error.toString(), errorMessage1, errorMessage2, statusCode, requestCode);
                Log.i("Error", errorMessage1 + errorMessage2);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (image != null && image.size() > 0) {
                    params = image;
                } else {
                    // image name could found image base or direct access from real path
                    // for now just get bitmap data from ImageView
                    params.put(parameterName, new DataPart(fileName, dataParts, fileType));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(timeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
    }

    public void executeRequestBankVerification(final int requestCode) {
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(method, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                serviceResultListener.onVolleyMultipartResult(resultResponse, "null", "null", "null", 0, requestCode);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage1 = "Unknown error";
                String errorMessage2 = "Unknown message";
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
                        String message = response.getString("message");
                        JSONArray errors = response.optJSONArray("errors");
                        if (type.equals("error")) {
                            Toast.makeText(context, errors.get(0).toString(), Toast.LENGTH_SHORT).show();
                        }
//                        if (networkResponse.statusCode == 404) {
//                            errorMessage2 = "Resource not found";
//                            statusCode = 404;
//                        } else if (networkResponse.statusCode == 401) {
//                            errorMessage2 = message + " Please login again";
//                            statusCode = 401;
//                        } else if (networkResponse.statusCode == 400) {
//                            errorMessage2 = message + " Check your inputs";
//                            statusCode = 400;
//                        } else if (networkResponse.statusCode == 500) {
//                            errorMessage2 = message + " Something is getting wrong";
//                            statusCode = 500;
//                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                serviceResultListener.onVolleyMultipartResult("null", error.toString(), errorMessage1, errorMessage2, statusCode, requestCode);
                Log.i("Error", errorMessage1 + errorMessage2);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                if (image != null && image.size() > 0) {
                    params = image;
                } else {
                    // image name could found image base or direct access from real path
                    // for now just get bitmap data from ImageView
                    params.put(parameterName, new DataPart(fileName, dataParts, fileType));
                }
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(timeOut,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        VolleySingleton.getInstance(context).addToRequestQueue(multipartRequest);
    }

    public void setServiceResultListener(ServiceResultListener serviceResultListener) {
        this.serviceResultListener = serviceResultListener;
    }

    private byte[] getFileDataFromPath(String filePath, int uploadQuality) {
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, bmOptions);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, uploadQuality, byteArrayOutputStream);
        return byteArrayOutputStream.toByteArray();
    }

    private String getPath(final Context context, final Uri uri) {
        final boolean isKitKatOrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (isKitKatOrAbove && DocumentsContract.isDocumentUri(context, uri)) {
                // ExternalStorageProvider
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        return Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                    // TODO handle non-primary volumes
                }
                // DownloadsProvider
                else if (isDownloadsDocument(uri)) {

                    final String id = DocumentsContract.getDocumentId(uri);
                    final Uri contentUri = ContentUris.withAppendedId(
                            Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                    return getDataColumn(context, contentUri, null, null);
                }
                // MediaProvider
                else if (isMediaDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    Uri contentUri = null;
                    if ("image".equals(type)) {
                        contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                    } else if ("video".equals(type)) {
                        contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                    } else if ("audio".equals(type)) {
                        contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                    }

                    final String selection = "_id=?";
                    final String[] selectionArgs = new String[]{
                            split[1]
                    };

                    return getDataColumn(context, contentUri, selection, selectionArgs);
                }
            }
            // MediaStore (and general)
            else if ("content".equalsIgnoreCase(uri.getScheme())) {
                return getDataColumn(context, uri, null, null);
            }
            // File
            else if ("image".equalsIgnoreCase(uri.getScheme())) {
                return uri.getPath();
            } else {
                return uri.getPath();
            }
        }

        return null;
    }

    private String getDataColumn(Context context, Uri uri, String selection,
                                 String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    private boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    private boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    private boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public Intent getImages() {
        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.setType("image/*");
        Intent chooserIntent = Intent.createChooser(pickIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, pickIntent);
        return chooserIntent;
    }

    public interface ServiceResultListener {
        void onVolleyMultipartResult(String resultResponse, String errorResponse, String errormessage1, String errormessage2, int errorCode, int requestCode);
    }
    /**
     * Simple data container use for passing byte image
     */
    public static class DataPart {
        private String fileName;
        private byte[] content;
        private String type;

        /**
         * Constructor with mime data type.
         *
         * @param name     label of data
         * @param data     byte data
         * @param mimeType mime data like "image/jpeg"
         */
        public DataPart(String name, byte[] data, String mimeType) {
            fileName = name;
            content = data;
            type = mimeType;
        }

        /**
         * Getter image name.
         *
         * @return image name
         */
        public String getFileName() {
            return fileName;
        }

        /**
         * Setter image name.
         *
         * @param fileName string image name
         */
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        /**
         * Getter content.
         *
         * @return byte image data
         */
        public byte[] getContent() {
            return content;
        }

        /**
         * Setter content.
         *
         * @param content byte image data
         */
        public void setContent(byte[] content) {
            this.content = content;
        }

        /**
         * Getter mime type.
         *
         * @return mime type
         */
        public String getType() {
            return type;
        }

        /**
         * Setter mime type.
         *
         * @param type mime type
         */
        public void setType(String type) {
            this.type = type;
        }
    }
}
