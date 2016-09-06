import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.webkit.MimeTypeMap;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.iid.InstanceID;
import com.google.gson.Gson;
import com.pwc.talentexchange.common.BaseApplication;
import com.pwc.talentexchange.common.constant.GlobalConstant;
import com.pwc.talentexchange.common.httptool.JsonStringRequest;
import com.pwc.talentexchange.common.httptool.MyJsonArrayRequest;
import com.pwc.talentexchange.common.httptool.NetCallBack;
import com.pwc.talentexchange.common.httptool.VolleyErrorHelper;
import com.pwc.talentexchange.common.httptool.VolleyQueue;
import com.pwc.talentexchange.common.models.BaseBean;
import com.pwc.talentexchange.common.models.RexPerson;
import com.pwc.talentexchange.common.utils.CacheUtils.ACacheHelper;
import com.pwc.talentexchange.common.utils.DateUtils;
import com.pwc.talentexchange.common.utils.LogUtils;
import com.pwc.talentexchange.common.utils.MD5Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
public class NetUtils {
    //The data records of single page
    public static final int PAGE_SIZE = 10;

    private static final String CLIENT_ID = "MobileApp";
    private static final String CLIENT_SECRET = "Test@123";

    public static final int DEFAULT_TIMEOUT_MS = 15000;
    public static final int DEFAULT_MAX_RETRIES = 2;
    public static final float DEFAULT_BACKOFF_MULT = 1.0F;
    public static final int DEFAULT_MIN_RETRIES = 1;


    private static String getSuffix(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return null;
        }
        String fileName = file.getName();
        if (fileName.equals("") || fileName.endsWith(".")) {
            return null;
        }
        int index = fileName.lastIndexOf(".");
        if (index != -1) {
            return fileName.substring(index + 1).toLowerCase(Locale.US);
        } else {
            return null;
        }
    }

    public static String getMimeType(File file) {
        String suffix = getSuffix(file);
        if (suffix == null) {
            return "file/*";
        }
        String type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(suffix);
        if (type != null || !TextUtils.isEmpty(type)) {
            return type;
        }
        return "file/*";
    }

    /**
     * @param context  must be current activity context
     * @param url      url
     * @param auth     token
     * @param callBack success and failed callback
     */
//    public static void doGet(final Context context, final String url, final String auth, final NetCallBack callBack) {
//        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
//            @Override
//            public void onResponse(String s) {
//                callBack.onResponse(s);
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//                checkToken(context, volleyError, callBack);
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
//        VolleyQueue.getInstance().addToRequestQueue(stringRequest);
//    }


    /**
     * @param context  must be current activity context
     * @param url      url
     * @param callBack success and failed callback
     */
    public static void doGet(final Context context, final String url, final NetCallBack callBack) {
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                callBack.onResponse(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                checkToken(context, volleyError, callBack);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = BaseApplication.getInstance().getTokenAuth();
                params.put("Authorization", auth);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(stringRequest);
    }

    /**
     * @param context
     * @param url
     * @param callBack
     */
    public static void doGetWithoutToken(final Context context, final String url, final NetCallBack callBack) {
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                callBack.onResponse(s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
            }
        });
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(stringRequest);
    }

    public static void doGetWithCache(final Context context, final String url, final NetCallBack callBack) {

        final String key = BaseApplication.getInstance().getUserId() + MD5Utils.encode(url);
        LogUtils.e(key + " " + ACacheHelper.hasCache(context, key));
        if (key != null && ACacheHelper.hasCache(context, key)) {
            callBack.onResponse(ACacheHelper.getJsonFromCache(context, key, true));
            return;
        }

        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                callBack.onResponse(s);
                ACacheHelper.setJsonToCache(context, key, s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError != null) {
                    String message = VolleyErrorHelper.getMessage(volleyError);
                    LogUtils.e(message);
                }

                if (ACacheHelper.hasCache(context, key)) {
                    callBack.onResponse(ACacheHelper.getJsonFromCache(context, key, true));
                }
                checkToken(context, volleyError, callBack);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = BaseApplication.getInstance().getTokenAuth();
                params.put("Authorization", auth);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(stringRequest);


    }


    public static void doGetWithErrorCache(final Context context, final String url, final NetCallBack callBack) {

        final String key = BaseApplication.getInstance().getUserId() + MD5Utils.encode(url);
        StringRequest stringRequest = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String s) {
                callBack.onResponse(s);
                ACacheHelper.setJsonToCache(context, key, s);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError != null) {
                    String message = VolleyErrorHelper.getMessage(volleyError);
                    LogUtils.e(message);
                    LogUtils.e(message);
                }
                if (ACacheHelper.hasCache(context, key)) {
                    callBack.onResponse(ACacheHelper.getJsonFromCache(context, key, true));
                } else {
                    callBack.onErrorResponse(volleyError);
                }
                checkToken(context, volleyError, callBack);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = BaseApplication.getInstance().getTokenAuth();
                params.put("Authorization", auth);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(stringRequest);


    }


    /**
     * @param context     must be current activity context
     * @param URL         url
     * @param auth        token
     * @param jsonRequest json data you submit
     * @param callBack    success and failed callback
     */

//    public static void doPost(final Context context, final String URL, final String auth, JSONObject jsonRequest, final NetCallBack callBack) {
//        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonRequest, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                if (jsonObject != null) {
//                    callBack.onResponse(jsonObject.toString());
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//                checkToken(context, volleyError, callBack);
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//
//        jsonObjectRequest.setRetryPolicy(new
//                DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
//        VolleyQueue.getInstance().addToRequestQueue(jsonObjectRequest);
//
//    }


    /**
     * @param context     must be current activity context
     * @param URL         url
     * @param jsonRequest json data you submit
     * @param callBack    success and failed callback
     */
    public static void doPost(final Context context, final String URL, JSONObject jsonRequest, final NetCallBack callBack) {

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                callBack.onResponse(jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                checkToken(context, volleyError, callBack);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = BaseApplication.getInstance().getTokenAuth();
                params.put("Authorization", auth);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new
                DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public static void doPost(final Context context, final String URL, BaseBean bean, final NetCallBack callBack) {
        JSONObject jsonObject = null;
        Gson gson = new Gson();
        try {
            jsonObject = new JSONObject(gson.toJson(bean));
        } catch (JSONException e) {
            LogUtils.e(bean.getClass().getName() + e);
        }
        doPost(context, URL, jsonObject, callBack);
    }


    public static void doPostWithCache(final Context context, final String URL, JSONObject jsonRequest, final NetCallBack callBack) {

        final String key = BaseApplication.getInstance().getUserId() + MD5Utils.encode(URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                callBack.onResponse(jsonObject.toString());
                ACacheHelper.setMemoryCache(context, key, jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError != null) {
                    String message = VolleyErrorHelper.getMessage(volleyError);
                    LogUtils.e(message);
                }
                if (ACacheHelper.getMemoryCache(context, key) != null) {
                    callBack.onResponse(ACacheHelper.getMemoryCache(context, key));
                }
                checkToken(context, volleyError, callBack);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = BaseApplication.getInstance().getTokenAuth();
                params.put("Authorization", auth);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new
                DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(jsonObjectRequest);


    }


    public static void doPostWithErrorCache(final Context context, final String URL, JSONObject jsonRequest, final NetCallBack callBack) {

        final String key = BaseApplication.getInstance().getUserId() + MD5Utils.encode(URL);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, jsonRequest, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject jsonObject) {
                callBack.onResponse(jsonObject.toString());
                ACacheHelper.setMemoryCache(context, key, jsonObject.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                if (volleyError != null) {
                    String message = VolleyErrorHelper.getMessage(volleyError);
                    LogUtils.e(message);
                }
                if (ACacheHelper.getMemoryCache(context, key) != null) {
                    callBack.onResponse(ACacheHelper.getMemoryCache(context, key));
                } else {
                    callBack.onErrorResponse(volleyError);
                }
                checkToken(context, volleyError, callBack);

            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = BaseApplication.getInstance().getTokenAuth();
                params.put("Authorization", auth);
                return params;
            }
        };

        jsonObjectRequest.setRetryPolicy(new
                DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(jsonObjectRequest);


    }

    /**
     * if have cache data then do not invoke web request
     *
     * @param context     must be current activity context
     * @param URL         url
     * @param tag         tag for cache key
     * @param jsonRequest json data you submit
     * @param callBack    success and failed callback
     */
    public static void doPostWithCache(final Context context, String URL, String tag, String jsonRequest, final NetCallBack callBack) {
        if (tag != null && ACacheHelper.hasCache(context, tag)) {
            callBack.onResponse(ACacheHelper.getJsonFromCache(context, tag, true));
            return;
        }
        JsonStringRequest jsonObjectRequest = new JsonStringRequest(Request.Method.POST, URL, jsonRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                callBack.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                checkToken(context, volleyError, callBack);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = BaseApplication.getInstance().getTokenAuth();
                params.put("Authorization", auth);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new
                DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    public static void doPostWithoutCheckToeken(final Context context, String URL, String tag, String jsonRequest, final NetCallBack callBack) {
        if (tag != null && ACacheHelper.hasCache(context, tag)) {
            callBack.onResponse(ACacheHelper.getJsonFromCache(context, tag, true));
            return;
        }
        JsonStringRequest jsonObjectRequest = new JsonStringRequest(Request.Method.POST, URL, jsonRequest, new Response.Listener<String>() {
            @Override
            public void onResponse(String jsonObject) {
                callBack.onResponse(jsonObject);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
                callBack.onErrorResponse(volleyError);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = BaseApplication.getInstance().getTokenAuth();
                params.put("Authorization", auth);
                return params;
            }
        };
        jsonObjectRequest.setRetryPolicy(new
                DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(jsonObjectRequest);

    }

    /**
     * @param context          must be current activity context
     * @param URL              url
     * @param auth             token
     * @param jsonArrayRequest jsonarray data you submit
     * @param callBack         success and failed callback
     */
//    public static void doPostArray(final Context context, final String URL, final String auth, JSONArray jsonArrayRequest, final NetCallBack callBack) {
//        MyJsonArrayRequest myJsonArrayRequest = new MyJsonArrayRequest(Request.Method.POST, URL, jsonArrayRequest, new Response.Listener<JSONArray>() {
//            @Override
//            public void onResponse(JSONArray jsonArray) {
//                callBack.onResponse(jsonArray.toString());
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError volleyError) {
//
//                checkToken(context, volleyError, callBack);
//            }
//        }) {
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> params = new HashMap<String, String>();
//                params.put("Authorization", auth);
//                return params;
//            }
//        };
//        myJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
//        VolleyQueue.getInstance().addToRequestQueue(myJsonArrayRequest);
//    }


    /**
     * @param context          must be current activity context
     * @param URL              url
     * @param jsonArrayRequest jsonarray data you submit
     * @param callBack         success and failed callback
     */

    public static void doPostArray(final Context context, final String URL, JSONArray jsonArrayRequest, final NetCallBack callBack) {
        MyJsonArrayRequest myJsonArrayRequest = new MyJsonArrayRequest(Request.Method.POST, URL, jsonArrayRequest, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray jsonArray) {
                callBack.onResponse(jsonArray.toString());
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

                checkToken(context, volleyError, callBack);
            }
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<String, String>();
                String auth = BaseApplication.getInstance().getTokenAuth();
                params.put("Authorization", auth);
                return params;
            }
        };
        myJsonArrayRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(myJsonArrayRequest);
    }

    private static void checkToken(final Context context, VolleyError volleyError, NetCallBack callBack) {
        if (volleyError.networkResponse != null && volleyError.networkResponse.statusCode == 401) {
            try {
                if (BaseApplication.getInstance().getExpireTime() != null) {
                    if (DateUtils.isExpireHours(BaseApplication.getInstance().getExpireTime())) {
                        doRefreshToken(context, null);
                    }

                } else {
                    doRefreshToken(context, null);
                }

            } catch (Exception e) {
                LogUtils.e(e.getMessage());
            }
        }

        callBack.onErrorResponse(volleyError);
    }

    /**
     * if you stop progress dialog or destroy activity
     * cancel all request by volley
     */
    public static void cancelRequest() {
        VolleyQueue.getInstance().getRequestQueue().cancelAll(new RequestQueue.RequestFilter() {
            @Override
            public boolean apply(Request<?> request) {
                return true;
            }
        });
    }

    public static void doRefreshToken(final Context context, final NetCallBack callBack) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, GlobalConstant.URL_HOST + GlobalConstant.URL_SIGN_IN, new Response.Listener<String>() {
            @Override
            public void onResponse(String result) {
                try {
                    JSONObject array = new JSONObject(result);
                    if (array.optString("access_token", "").length() > 1) {
                        RexPerson oldAccount = BaseApplication.getInstance().getAccount();
                        if (oldAccount != null) {
                            oldAccount.setExpires(array.optString(".expires"));
                            oldAccount.setAccess_token(array.optString("access_token"));
                            oldAccount.setRefresh_token(array.optString("refresh_token"));
                            ACacheHelper.setBeanToCache(context, "account", oldAccount);
                        }
                    }

                } catch (JSONException e) {
                    LogUtils.e("" + e.getMessage());
                }
                if (callBack != null) {
                    callBack.onResponse(result);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {
//                try {
//                    String errCode = "[" + volleyError.networkResponse.statusCode + "] ";
//                    doLogout(context, errCode + context.getString(R.string.authentication_failed));
//                } catch (NullPointerException e) {
//                    LogUtils.e(e.getMessage());
//                }
                if (callBack != null) {
                    callBack.onErrorResponse(volleyError);
                }
            }
        }) {
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> map = new HashMap<String, String>();
                map.put("grant_type", "refresh_token");
                if (BaseApplication.getInstance().getAccount() != null) {
                    map.put("username", BaseApplication.getInstance().getAccount().getUserName());
                    map.put("refresh_token", BaseApplication.getInstance().getAccount().getRefresh_token());
                }
                //Todd Add for Refresh token
                map.put("client_id", CLIENT_ID);
                map.put("client_secret", CLIENT_SECRET);

                return map;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, DEFAULT_MAX_RETRIES, DEFAULT_BACKOFF_MULT));
        VolleyQueue.getInstance().addToRequestQueue(stringRequest);
    }

//    public static void doLogout(final Context context, String msg) {
//        LogUtils.i("doLogout");
//        if(context instanceof Activity){
//        //Login again
//            DialogUtil.getConfirmDialogWithoutNegavite("",
//                    msg, context, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    Intent intent = new Intent("com.talentexchange.LOGIN_ACTION");
//                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//                    context.startActivity(intent);
//                }
//            });
//        }else{
//            LogUtils.e("context not activity");
//            Utility.showToast(context,msg);
//            Intent intent = new Intent("com.talentexchange.LOGIN_ACTION");
//            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
//        }
//
//        sendUnRegisterToServer(context);
//        ACacheHelper.clearAllCache(context, true);
//        BadgeDelegator.getInstance().resetBadgeCount();
//        ACacheHelper.setMemoryCache(context, GlobalConstant.SIGNIN_FLAG, GlobalConstant.SIGNIN_FLAG_LOGIN, true);
//        //Clear notification
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.cancelAll();
//    }

    public static void sendRegistrationToServer(Context context, String token) {
        // Add custom implementation, as needed.
        LogUtils.i(token);
        //Invoke the india service to register token again
        //TODO
        Map<String, Object> mapParams = new HashMap<>();
        if (BaseApplication.getInstance().isTokenAuth()) {
            RexPerson person = BaseApplication.getInstance().getAccount();
            LogUtils.e(person.getUserName());
            mapParams.put("userName", person.getUserName());
            mapParams.put("deviceType", "AndroidPhone"); //Android Device
            mapParams.put("deviceToken", token);


            NetUtils.doPostWithCache(context, GlobalConstant.URL_UPDATE_DEVICE, null, new Gson().toJson(mapParams), new NetCallBack() {
                @Override
                public void onResponse(String result) {
                    LogUtils.i(" SUCCESS>>" + result);
                }

                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    LogUtils.e(" ERR>>" + volleyError.networkResponse);
                }
            });
        }

    }

    public static void sendUnRegisterToServer(final Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(BaseApplication.getApplication());
        String token = sp.getString(GlobalConstant.GCM_TOKEN, "");
        Map<String, Object> mapParams = new HashMap<>();
        if (BaseApplication.getInstance().isTokenAuth()) {
            RexPerson person = BaseApplication.getInstance().getAccount();
            LogUtils.e(person.getUserName());
            mapParams.put("userName", person.getUserName());
            mapParams.put("deviceType", "AndroidPhone"); //Android Device
            mapParams.put("deviceToken", token);

            NetUtils.doPostWithoutCheckToeken(context,
                    GlobalConstant.URL_UNREGISTER_DEVICE, null, new Gson().toJson(mapParams), new NetCallBack() {
                        @Override
                        public void onResponse(String result) {

                            new Thread(new Runnable() {
                                public void run() {
                                    InstanceID instanceID = InstanceID.getInstance(context);
                                    try {
                                        instanceID.deleteInstanceID();
                                    } catch (IOException e) {
                                        LogUtils.e(e);
                                    }
                                }
                            }).start();

                        }

                        @Override
                        public void onErrorResponse(VolleyError volleyError) {
                            LogUtils.e("Unregister ERR>>" + volleyError.networkResponse);
                        }
                    });
        }
    }

    public static boolean downloadFile(String url, String filePath) {
        OutputStream os = null;
        InputStream is = null;
        boolean success = false;

        try {
            File file = new File(filePath);
            if (!file.exists()) {
                file.createNewFile();
            }

            URL uri = new URL(url);
            URLConnection con = uri.openConnection();
            is = con.getInputStream();
            byte[] bs = new byte[1024];
            int len;
            os = new FileOutputStream(filePath);
            while ((len = is.read(bs)) != -1) {
                os.write(bs, 0, len);
            }
            success = true;
        } catch (FileNotFoundException e) {
            LogUtils.e(e);
        } catch (MalformedURLException e) {
            LogUtils.e(e);
        } catch (IOException e) {
            LogUtils.e(e);
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                LogUtils.e("close stream error:" + e);
            }
        }
        return success;
    }
}
