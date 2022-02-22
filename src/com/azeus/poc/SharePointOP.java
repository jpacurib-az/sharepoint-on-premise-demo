package com.azeus.poc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azeus.poc.obj.SPFile;
import com.azeus.poc.utils.ConnectUtils;
import com.azeus.poc.utils.SPUtils;
import com.azeus.poc.utils.TempFileUtils;
import com.jayway.jsonpath.JsonPath;

import net.minidev.json.JSONArray;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SharePointOP {
  private static final Logger LOG = LoggerFactory.getLogger(SharePointOP.class);

  private OkHttpClient client;

  public SharePointOP(OkHttpClient client) {
    this.client = client;
  }

  public List<SPFile> listDocuments() throws Exception {
    List<SPFile> spDocList = new ArrayList<>();
    Request request = ConnectUtils.createGetRequest(SPUtils.SP_FILES_URL);

    try (Response response = this.client.newCall(request).execute()) {
      ConnectUtils.checkSuccessResponse(response);
      String respBody = response.body().string();
      Asserts.check(StringUtils.isNotBlank(respBody), "Response body cannot be empty");

      JSONArray results = JsonPath.read(respBody, SPUtils.RESP_ROOT + ".results");
      results.forEach(item -> {
        spDocList.add(new SPFile(this.client, item));
      });

    }
    return spDocList;
  }

  public SPFile uploadDocument() throws Exception {
    String requestDigest = SPUtils.getSPFormDigestValue(this.client);
    Asserts.check(StringUtils.isNotBlank(requestDigest), "Request Digest cannot be empty");
    LOG.info("{}: {}", SPUtils.HEADER_REQUEST_DIGEST, requestDigest);

    File fileUpload = TempFileUtils.getRandomFile();
    LOG.info("Uploading [{}]", fileUpload.getAbsolutePath());

    RequestBody requestBody = ConnectUtils.createRequestBodyWithFile(fileUpload);
    String uploadUrl = StringUtils.replace(SPUtils.SP_FILES_UPLOAD_URL, "@{FILENAME}", fileUpload.getName());
    Request request = ConnectUtils.createPostRequest(uploadUrl,
        Collections.singletonMap(SPUtils.HEADER_REQUEST_DIGEST, requestDigest), requestBody);

    try (Response response = this.client.newCall(request).execute()) {
      ConnectUtils.checkSuccessResponse(response);
      String respBody = response.body().string();
      Asserts.check(StringUtils.isNotBlank(respBody), "Response body cannot be empty");

      return new SPFile(this.client, JsonPath.read(respBody, SPUtils.RESP_ROOT));
    }
  }
}
