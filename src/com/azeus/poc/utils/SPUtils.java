package com.azeus.poc.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;

import com.azeus.poc.obj.SPObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.jayway.jsonpath.JsonPath;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class SPUtils {

  public static final String DEFAULT_DOC_FOLDER = "Documents";
  // public static final String SHARED_DOC_FOLDER = "Shared Documents";

  public static final String API_URL = Settings.getSharepointBaseUrl() + Settings.getSharepointSiteUrl() + "/_api";
  public static final String CONTEXT_INFO_URL = API_URL + "/contextinfo";

  public static final String SP_ITEMS_URL = API_URL
      + String.format("/lists/GetByTitle('%s')/items", DEFAULT_DOC_FOLDER);

  // using lists api
  public static final String SP_FILES_URL = API_URL
      + String.format("/lists/GetByTitle('%s')/files", DEFAULT_DOC_FOLDER);

  // using lists api
  public static final String SP_FILES_UPLOAD_URL = API_URL
      + String.format("/lists/GetByTitle('%s')/files/add(overwrite=true, name='@{FILENAME}')", DEFAULT_DOC_FOLDER);

  // using web api
  // public static final String SP_FILES_UPLOAD_URL = API_URL
  // + String.format("/web/GetFolderByServerRelativeUrl('%s')/files/add(overwrite=true, url='@{FILENAME}')",
  // SITE_URL + "/" + SHARED_DOC_FOLDER);

  public static final String HEADER_REQUEST_DIGEST = "X-RequestDigest";
  public static final String NEW_LINE = System.getProperty("line.separator");
  public static final String RESP_ROOT = "$.d"; // wtf is 'd'

  private static final DateTimeFormatter SP_DATE_TIME_FORMATTER;
  private static final String LOGIN_NAME_PREF = "i:0#.w|";
  private static final ObjectMapper JSON_MAPPER;

  static {
    SP_DATE_TIME_FORMATTER = DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.systemDefault());
    JSON_MAPPER = JsonMapper.builder().addModule(new JavaTimeModule()).build();
  }

  public static LocalDateTime toLocalDateTime(String rawDateTime) {
    if (StringUtils.isBlank(rawDateTime)) {
      return null;
    }
    return LocalDateTime.from(SP_DATE_TIME_FORMATTER.parse(rawDateTime));
  }

  /**
   * Returns the Request Digest value. This is used in all POST calls to
   * SharePoint as <code>X-RequestDigest</code> header.
   * 
   * @param client
   * @return
   * @throws Exception
   */
  public static String getSPFormDigestValue(OkHttpClient client) throws Exception {
    RequestBody emptyBody = new FormBody.Builder().build();
    Request request = ConnectUtils.createPostRequest(CONTEXT_INFO_URL, emptyBody);
    try (Response response = client.newCall(request).execute()) {
      ConnectUtils.checkSuccessResponse(response);
      String respBody = response.body().string();
      Asserts.check(StringUtils.isNotBlank(respBody), "Response body cannot be empty");
      return JsonPath.read(respBody, RESP_ROOT + ".GetContextWebInformation.FormDigestValue");
    }
  }

  public static String cleanLoginName(String loginName) {
    return StringUtils.removeStart(loginName, LOGIN_NAME_PREF);
  }

  public static String jsonPrettyPrint(SPObject obj) {
    try {
      return JSON_MAPPER.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    } catch (Exception e) {
      throw new IllegalArgumentException(e);
    }
  }

}
