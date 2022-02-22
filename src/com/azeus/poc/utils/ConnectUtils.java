package com.azeus.poc.utils;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ConnectUtils {
  public static final Map<String, String> DEFAULT_HEADERS;
  
  static {
    DEFAULT_HEADERS = new HashMap<>();
    DEFAULT_HEADERS.put("Accept", "application/json;odata=verbose");
    DEFAULT_HEADERS.put("Content-Type", "application/json;odata=verbose");
  }

  public static OkHttpClient createClient(String username, String password) throws Exception {
    OkHttpClient.Builder builder = new OkHttpClient().newBuilder();
    builder.addInterceptor(new BasicAuthInterceptor(username, password));
    return builder.build();
  }

  public static Request createGetRequest(String url) {
    return createRequest(url, null, null);
  }

  public static Request createGetRequest(String url, Map<String, String> headers) {
    return createRequest(url, headers, null);
  }

  public static Request createPostRequest(String url, RequestBody requestBody) {
    return createRequest(url, null, requestBody);
  }

  public static Request createPostRequest(String url, Map<String, String> headers, RequestBody requestBody) {
    return createRequest(url, headers, requestBody);
  }

  private static Request createRequest(String url, Map<String, String> headers, RequestBody requestBody) {
    Request.Builder builder = new Request.Builder();
    Map<String, String> allHeaders = new HashMap<>(DEFAULT_HEADERS);
    if (headers != null) {
      allHeaders.putAll(headers);
    }

    for (Entry<String, String> header : allHeaders.entrySet()) {
      builder.addHeader(header.getKey(), header.getValue());
    }

    builder.url(url);
    if (requestBody != null) {
      builder.post(requestBody);
    }
    return builder.build();
  }

  public static RequestBody createRequestBodyWithFile(File file) throws Exception {
    MultipartBody.Builder builder = new MultipartBody.Builder();
    builder.setType(MultipartBody.FORM);
    builder.addFormDataPart("file", file.getName(), RequestBody.create(file, TempFileUtils.getMediaType(file)));
    return builder.build();
  }

  public static void checkSuccessResponse(Response response) throws Exception {
    if (!response.isSuccessful()) {
      throw new IllegalStateException(String.format("Call not successful. Response = {code: %s, message: %s, body: %s}",
          response.code(), response.message(), response.body().string()));
    }
  }

  static class BasicAuthInterceptor implements Interceptor {
    private String credentials;

    public BasicAuthInterceptor(String user, String password) {
      this.credentials = Credentials.basic(user, password);
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
      Request request = chain.request();
      Request authenticatedRequest = request.newBuilder().header("Authorization", this.credentials).build();
      return chain.proceed(authenticatedRequest);
    }
  }

}
