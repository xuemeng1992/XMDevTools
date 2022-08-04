package com.xuemeng.xmdevtools.net;

import android.content.Context;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.xuemeng.xmdevtools.bean.KeyValue;
import com.xuemeng.xmdevtools.utils.LoggerUtils;
import com.xuemeng.xmdevtools.utils.Preconditions;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 需要在application中初始化 上下文
 */
public class OkHttpUtil {
    private static final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");
    private static final String METHOD_GET = "GET";
    private static final String METHOD_POST_BODY = "POST_BODY";
    private static final String METHOD_POST_QUERY = "POST_QUERY";
    private static final String METHOD_PUT = "PUT";
    private static final String METHOD_PATCH = "PATCH";
    private static final String METHOD_NO_PARAM_PATCH = "PATCH_NO_PARAM";
    private static final String METHOD_DELETE = "DELETE";
    private static final String CHARSET_NAME = "UTF-8";
    public static final int TIME_OUT = 120;//网络超时时间 单位秒

    private static final OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
            .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT, TimeUnit.SECONDS)
            .addInterceptor(new HttpLoggingInterceptor()
                    .setLevel(false
                            ? HttpLoggingInterceptor.Level.BASIC
                            : HttpLoggingInterceptor.Level.BODY))
//                    //链路复用
            .connectionPool(new ConnectionPool())
//                    //失败重连
            .retryOnConnectionFailure(true)
            .build();
    private static Context applicationCtx;

    /**
     * 统一获取配置好了的 OKhttpClient实例
     *
     * @param context
     * @param isNeedHandleErrorBySelf
     * @return
     */
    public static OkHttpClient getOKHttpClient(Context context, boolean isNeedHandleErrorBySelf) {
        return getOKHttpClientInterceptor(context, isNeedHandleErrorBySelf);
    }

    /**
     * * @param context
     *
     * @param isNeedHandleErrorBySelf
     * @return
     */
    public static OkHttpClient getOKHttpClientInterceptor(Context context, boolean isNeedHandleErrorBySelf) {
        return mOkHttpClient
                .newBuilder()
                .sslSocketFactory(SSLSocketFactory.getSSLstrategy().getSocketFactory(), SSLSocketFactory.trustManager)
                .build();
    }


    /**
     * 全局初始化 context
     *
     * @param ctx
     */
    public static void initContext(Context ctx) {
        applicationCtx = ctx;
    }

    //同步get请求
    public static <T> T get(Class<T> cls, String url, Map<String, Object> params) {
        return doGet(cls, url, params, true, true, null);
    }

    public static <T> T get(Class<T> cls, String url, Map<String, Object> params, ResponseCallback callback) {
        return doGet(cls, url, params, true, true, callback);
    }

    public static <T> T delete(Class<T> cls, String url, Map<String, Object> params, ResponseCallback callback) {
        return doDelete(cls, url, params, true, true, callback);
    }

    public static <T> T delete(Class<T> cls, String url, Map<String, Object> params) {
        return doDelete(cls, url, params, true, true, null);
    }

    public static <T> T patch(Class<T> cls, String url) {
        return doPatch(cls, url, true, true, null);
    }

    public static <T> T patch(Class<T> cls, String url, ResponseCallback callback) {
        return doPatch(cls, url, true, true, null);
    }

    public
    static <T> T patch(Class<T> cls, String url, Map<String, Object> params) {
        return doPatch(cls, url, params, true, true, null);
    }

    public
    static <T> T patch(Class<T> cls, String url, Map<String, Object> params, ResponseCallback callback) {
        return doPatch(cls, url, params, true, true, callback);
    }

    public static <T> T put(Class<T> cls, String url, Map<String, Object> params) {
        return doPut(cls, url, params, true, true, null);
    }

    public static <T> T get(Class<T> cls, String url) {
        return get(cls, url, "");
    }

    public static <T> T get(Class<T> cls, String url, String simpleParam) {
        String urlStr;
        if (TextUtils.isEmpty(simpleParam)) {
            urlStr = url;
        } else {
            urlStr = url + "?" + simpleParam.replaceAll(" ", "%20");
        }
        return doGet(cls, urlStr, null, true, true, null);
    }

    //同步get请求，且返回数据大
    public static <T> T getHeavy(Class<T> cls, String url, Map<String, Object> params) {
        return doGet(cls, url, params, false, true, null);
    }

    //异步get请求
    public static <T> void getAsync(Class<T> cls, String url, Map<String, Object> params, ResponseCallback callback) {
        doGet(cls, url, params, false, true, callback);
    }

    //post
    public static <T> T post(Class<T> cls, String url, String params) {
        String urlStr = url + "?" + params.replaceAll(" ", "%20");
        return post(cls, urlStr);
    }

    public static <T> T post(Class<T> cls, String url) {
        return post(cls, url, new HashMap<String, Object>());
    }

    //同步post
    public static <T> T post(Class<T> cls, String url, Map<String, Object> params) {
        return doPost(cls, url, params, null, true, true, null);
    }

    public static <T> T post(Class<T> cls, String url, Map<String, Object> params, Object o) {
        return doPost(cls, url, params, null, true, true, null, o);
    }

    public static <T> T post(Class<T> cls, String url, Map<String, Object> params, boolean isData) {
        return doPost(cls, url, params, true, true, null);
    }

    //同步post 但返回数据很大
    public static <T> T postHeavy(Class<T> cls, String url, Map<String, Object> params) {
        return doPost(cls, url, params, null, false, true, null);
    }

    //异步post
    public static <T> void postAsync(Class<T> cls, String url, Map<String, Object> params, ResponseCallback callback) {
        doPost(cls, url, params, null, true, false, callback);
    }

    //请求byte[]
    public static byte[] postReturnByte(String url, Map<String, Object> params) {
        return doRequestByte(METHOD_POST_BODY, url, params, null, true, null, null);
    }

    /**
     * 上传单个文件
     *
     * @param url
     * @param params
     * @param multiPartKey
     * @param filepath
     * @return
     */
    public static <T> T postFile(Class<T> cls, String url, Map<String, Object> params, String multiPartKey,
                                 final String filepath) {
        List<String> filepathList = new ArrayList<>();
        filepathList.add(filepath);
        return postMultiFile(cls, url, params, TextUtils.isEmpty(multiPartKey) ? "uploadIcon" : multiPartKey,
                filepathList);
    }

    /**
     * 上传文件列表
     *
     * @param url
     * @param params
     * @param multiPartKey
     * @param filepaths
     * @return
     */
    public static <T> T postMultiFile(Class<T> cls, String url, Map<String, Object> params, String multiPartKey,
                                      final List<String> filepaths) {
        if (Preconditions.isNullOrEmpty(filepaths)) {
            return null;
        }
        return postMultiFile(cls, url, params, getMutilFilePart(multiPartKey, filepaths), true, null);
    }

    /**
     * 上传文件列表
     *
     * @param url
     * @param params
     * @param multilKeyFile key  -- file map
     * @return
     */
    public static <T> T postMultiFile(Class<T> cls, String url, Map<String, Object> params, ArrayList<KeyValue> multilKeyFile) {
        return postMultiFile(cls, url, params, multilKeyFile, true, null);
    }

    /**
     * @param url
     * @param params
     * @param keyFileMap
     * @return
     */
    public static <T> T postMultiFile(Class<T> cls, String url, Map<String, Object> params, ArrayList<KeyValue> keyFileMap, boolean isSyn, ResponseCallback callback) {

        if (Preconditions.isNullOrEmpty(keyFileMap)) {
            return null;
        }
        return doPost(cls, url, params, keyFileMap, true, isSyn, callback);
    }

    //异步post带文件
    public static <T> void postMultiFileAsync(Class<T> cls, String url, Map<String, Object> params, String multiPartKey,
                                              final List<String> filepaths, ResponseCallback callback) {
        if (Preconditions.isNullOrEmpty(filepaths)) {
            return;
        }
        doPost(cls, url, params, getMutilFilePart(multiPartKey, filepaths), true, false, callback);
    }

    /**
     * 只传一个 相同的 key，和 文件列表
     *
     * @param multiPartKey
     * @param filepaths
     * @return
     */
    private static ArrayList<KeyValue> getMutilFilePart(String multiPartKey, final List<String> filepaths) {
        String key;
        if (TextUtils.isEmpty(multiPartKey)) {
            key = "picFileList";
        } else {
            key = multiPartKey;
        }
        ArrayList<KeyValue> keyFileList = new ArrayList<>();
        for (String filePath : filepaths) {
            keyFileList.add(new KeyValue(key, filePath));
        }
        return keyFileList;
    }

    /**
     * 执行请求
     *
     * @param method     请求方式 GET或者POST
     * @param url        地址
     * @param params     路径
     * @param isLiteResp 同步请求 ：返回数据很小吗，如果为true用string解析结果， 否则用流
     *                   异步请求 ：随便传 不受影响
     * @param isSync     是否是同步请求
     * @param callback   如果上面是false,需要传入回调
     * @return
     * @throws IOException
     */
    private static <T> T doRequst(Class<T> cls,
                                  String method, String url, Map<String, Object> params,
                                  ArrayList<KeyValue> keyFileList, boolean isLiteResp,
                                  boolean isSync, ResponseCallback callback, Object o) {
        T resultStr = null;
        Response response = getResponse(method, url, params, keyFileList, isSync, callback, o);
        //如果响应体比较小，使用String()方法来得到String, 否则需要用流的方式
        try {
            if (isLiteResp) {
                resultStr = new Gson().fromJson(response.body().string(), cls);
            } else {
                resultStr = new Gson().fromJson(convertInputStream2Str(response.body().byteStream()), cls);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.body().close();
        return resultStr;
    }

    /**
     * 请求字节码
     *
     * @param method
     * @param url
     * @param params
     * @param keyFileList
     * @param isSync
     * @param callback
     * @return
     */
    private static byte[] doRequestByte(
            String method, String url, Map<String, Object> params,
            ArrayList<KeyValue> keyFileList,
            boolean isSync, ResponseCallback callback, Object o) {
        byte[] byteResult = new byte[]{};
        //构造请求体
        Response response = getResponse(method, url, params, keyFileList, isSync, callback, o);
        if (response == null) {
            return byteResult;
        }
        try {
            byteResult = readStream(response.body().byteStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        response.body().close();
        return byteResult;
    }

    private static Response getResponse(
            String method, String url, Map<String, Object> params,
            ArrayList<KeyValue> keyFileMap,
            boolean isSync, ResponseCallback callback, Object o) {
        //构造请求体
        Request request = buildRequest(method, url, params, keyFileMap, o);
        //返回response
        Response response = null;
        try {
            if (isSync) {
                response = getOKHttpClientInterceptor(applicationCtx, false).newCall(request).execute();
            } else {
                getOKHttpClientInterceptor(applicationCtx, false).newCall(request).execute();
            }
            if (!Preconditions.isNullOrEmpty(callback)) {
                callback.onResponse(response);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
            callback.onFailure(e);
        } catch (IOException e) {
            e.printStackTrace();
            callback.onFailure(e);
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure(e);
        }
        return response;
    }

    /**
     * 流转换成字节数组
     *
     * @param in 输入流
     * @return 字节数组
     * @throws
     */
    public static byte[] readStream(InputStream in) throws Exception {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        while ((len = in.read(buffer)) != -1) {
            outputStream.write(buffer, 0, len);
        }
        outputStream.close();
        in.close();
        return outputStream.toByteArray();
    }

    private static <T> T doGet(Class<T> cls,
                               String url, Map<String, Object> params, boolean isLiteResp,
                               boolean isSync, ResponseCallback callback) {
        return doRequst(cls, METHOD_GET, url, params, null, isLiteResp, isSync, callback, null);
    }

    private static <T> T doPut(Class<T> cls,
                               String url, Map<String, Object> params, boolean isLiteResp,
                               boolean isSync, ResponseCallback callback) {
        return doRequst(cls, METHOD_PUT, url, params, null, isLiteResp, isSync, callback, null);
    }

    private static <T> T doPatch(Class<T> cls,
                                 String url, Map<String, Object> params, boolean isLiteResp,
                                 boolean isSync, ResponseCallback callback) {
        return doRequst(cls, METHOD_PATCH, url, params, null, isLiteResp, isSync, callback, null);
    }

    private static <T> T doPatch(Class<T> cls,
                                 String url, boolean isLiteResp,
                                 boolean isSync, ResponseCallback callback) {
        return doRequst(cls, METHOD_NO_PARAM_PATCH, url, null, null, isLiteResp, isSync, callback, null);
    }

    private static <T> T doDelete(Class<T> cls,
                                  String url, Map<String, Object> params, boolean isLiteResp,
                                  boolean isSync, ResponseCallback callback) {
        return doRequst(cls, METHOD_DELETE, url, params, null, isLiteResp, isSync, callback, null);
    }

    private static <T> T doPost(Class<T> cls,
                                String url, Map<String, Object> params, ArrayList<KeyValue> keyFileMap,
                                boolean isLiteResp, boolean isSync, ResponseCallback callback) {
        return doRequst(cls, METHOD_POST_BODY, url, params, keyFileMap, isLiteResp, isSync, callback, null);
    }

    private static <T> T doPost(Class<T> cls,
                                String url, Map<String, Object> params, ArrayList<KeyValue> keyFileMap,
                                boolean isLiteResp, boolean isSync, ResponseCallback callback, Object o) {
        return doRequst(cls, METHOD_POST_BODY, url, params, keyFileMap, isLiteResp, isSync, callback, o);
    }

    private static <T> T doPost(Class<T> cls,
                                String url, Map<String, Object> params,
                                boolean isLiteResp, boolean isSync, ResponseCallback callback) {
        return doRequst(cls, METHOD_POST_QUERY, url, params, null, isLiteResp, isSync, callback, null);
    }

    /**
     * 构造请求头
     *
     * @param method
     * @param url
     * @param params
     * @param keyFileList
     * @return
     */
    private static Request buildRequest(
            String method, String url, Map<String, Object> params,
            ArrayList<KeyValue> keyFileList, Object o) {
        //构造请求体
        Request request = null;
        if (method.equals(METHOD_GET)) {  //GET请求
            String reqStr;
            if (!Preconditions.isNullOrEmpty(params)) {
                reqStr = attachHttpGetParams(url, params);
            } else {
                reqStr = url;
            }
            LoggerUtils.Log().i("执行Get请求---" + reqStr);
            request = new Request.Builder().url(reqStr).build();
        } else if (method.equals(METHOD_DELETE)) {  //Delete请求
            String reqStr;
            if (!Preconditions.isNullOrEmpty(params)) {
                reqStr = attachHttpGetParams(url, params);
            } else {
                reqStr = url;
            }
            LoggerUtils.Log().i("执行Delete请求---" + reqStr);
            request = new Request.Builder().url(reqStr).delete().build();
        } else if (method.equals(METHOD_PATCH)) {  //Patch请求
            if (!Preconditions.isNullOrEmpty(params)) {
                request = buildSimplePathRequest(url, params);
            }
            LoggerUtils.Log().i("执行Patch请求---" + url + "参数:" + params);
        } else if (method.equals(METHOD_NO_PARAM_PATCH)) {  //Patch请求
            request = buildSimplePathRequest(url, params);
            LoggerUtils.Log().i("执行Patch请求---" + url);
        } else if (method.equals(METHOD_POST_QUERY)) {  //Patch请求
            if (!Preconditions.isNullOrEmpty(params)) {
                request = buildSimplePostRequest(url, params);
            }
            LoggerUtils.Log().i("执行Post请求---" + url + "参数:" + params);
        } else if (method.equals(METHOD_PUT)) {  //Put请求
            if (!Preconditions.isNullOrEmpty(params)) {
                request = buildSimplePutJsonRequest(url, params);
            }
            LoggerUtils.Log().i("执行Put请求---地址:" + url + "参数:" + params);
        } else { //Post请求
            LoggerUtils.Log().i("执行Post请求---地址:" + url + "参数:" + params);
            if (o == null) {
                if (Preconditions.isNullOrEmpty(keyFileList)) {
                    request = buildSimplePostJsonRequest(url, params);
                } else {
                    request = buildMultiFileRequest(url, params, keyFileList);
                }
            } else {
                request = buildSimplePostJsonRequest(url, o);
            }
        }
        return request;
    }

    /**
     * 构造一个简单的Path请求体
     */
    private static Request buildSimplePathRequest(String url, Map<String, Object> params) {

        FormBody.Builder formbuilder = new FormBody.Builder();
        if (!Preconditions.isNullOrEmpty(params)) {
            for (String key : params.keySet()) {
                String value = params.get(key).toString();
                if (!Preconditions.isNullOrEmpty(value)) {
                    formbuilder.add(key, value);
                }
            }
        }
        // Create RequestBody
        RequestBody build = formbuilder.build();
        return new Request.Builder().url(url).patch(build).build();
    }

    /**
     * 构造一个简单的PUT Json请求体
     */
    private static Request buildSimplePutJsonRequest(String url, Map<String, Object> params) {
        //JSON字符串
        RequestBody requestBody = FormBody.create(MEDIA_TYPE_JSON, JSON.toJSONString(params));
        return new Request.Builder().url(url).put(requestBody).build();
    }

    /**
     * 构造一个简单的Post Json请求体
     */
    private static Request buildSimplePostJsonRequest(String url, Map<String, Object> params) {
        //JSON字符串
        RequestBody requestBody = FormBody.create(MEDIA_TYPE_JSON, JSON.toJSONString(params));
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private static Request buildSimplePostJsonRequest(String url, Object params) {
        //JSON字符串
        RequestBody requestBody = FormBody.create(MEDIA_TYPE_JSON, JSON.toJSONString(params));
        return new Request.Builder().url(url).post(requestBody).build();
    }

    /**
     * 构造一个简单的Post请求体
     */
    private static Request buildSimplePostRequest(String url, Map<String, Object> params) {

        FormBody.Builder formbuilder = new FormBody.Builder();
        if (!Preconditions.isNullOrEmpty(params)) {
            for (String key : params.keySet()) {
                String value = params.get(key).toString();
                if (!Preconditions.isNullOrEmpty(value)) {
                    formbuilder.add(key, value);
                }
            }
        }

        // Create RequestBody
        RequestBody build = formbuilder.build();
        return new Request.Builder().url(url).post(build).build();
    }

    /**
     * 构造一个复杂的请求体，供同步和异步Post批量上传使用
     *
     * @param url
     * @param params
     * @param fileKeyPathList key--filePath
     * @return
     */
    private static Request buildMultiFileRequest(String url, Map<String, Object> params, ArrayList<KeyValue> fileKeyPathList) {
        MultipartBody.Builder builder = new MultipartBody.Builder();
        builder.setType(MultipartBody.FORM);
        //添加参数
        if (!Preconditions.isNullOrEmpty(params)) {
            for (String key : params.keySet()) {
                String value = params.get(key).toString();
                if (!Preconditions.isNullOrEmpty(value)) {
                    builder.addFormDataPart(key, params.get(key).toString());
                }
            }
        }

        //生成一个fileList
        if (!Preconditions.isNullOrEmpty(fileKeyPathList)) {
            for (KeyValue filePath : fileKeyPathList) {
                String key = filePath.getKey();
                String path = filePath.getValue();
                if (!Preconditions.isNullOrEmpty(key) && !Preconditions.isNullOrEmpty(path)) {
                    builder.addFormDataPart(
                            key, path, RequestBody.create(MEDIA_TYPE_PNG, new File(path)));
                }
            }
        }
        RequestBody requestBody = builder.build();
        return new Request.Builder().url(url).post(requestBody).build();
    }

    private static String attachHttpGetParams(String url, Map<String, Object> params) {
        StringBuilder sb = new StringBuilder(url);
        if (!Preconditions.isNullOrEmpty(params)) {
            sb.append("?");
            sb.append(formatParams(params));
        }
        return sb.toString();
    }

    private static String attachHttpGetParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        if (!Preconditions.isNullOrEmpty(params)) {
            sb.append("?");
            sb.append(formatParams(params));
        }
        return sb.toString();
    }

    /**
     * 对http map参数 值进行编码
     *
     * @param params
     * @return
     */
    private static String formatParams(Map<String, Object> params) {
        StringBuilder sb = new StringBuilder();
        for (String str : params.keySet()) {
            try {
                String value = params.get(str).toString();
                if (!Preconditions.isNullOrEmpty(value)) {
                    sb.append(str).append("=").append(URLEncoder.encode(params.get(str).toString(), CHARSET_NAME)).append("&");
                }
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        if (sb.length() > 1) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * 如果请求得到的响应体太大 用此方法读取输入流,默认UTF-8
     *
     * @param is
     * @return
     */
    private static String convertInputStream2Str(InputStream is) {
        //默认都用UTF-8转
        return convertInputStream2Str(is, CHARSET_NAME);
    }

    /**
     * 输入流转String
     *
     * @param is
     * @param encode
     * @return
     */
    public static String convertInputStream2Str(InputStream is, String encode) {
        String str = "";
        try {
            if (TextUtils.isEmpty(encode)) {
                encode = CHARSET_NAME;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, encode));
            StringBuffer sb = new StringBuffer();
            while ((str = reader.readLine()) != null) {
                sb.append(str).append("\n");
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }
}