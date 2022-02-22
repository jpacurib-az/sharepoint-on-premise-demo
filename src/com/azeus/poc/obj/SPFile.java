package com.azeus.poc.obj;

import java.io.File;
import java.io.InputStream;
import java.time.LocalDateTime;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azeus.poc.utils.ConnectUtils;
import com.azeus.poc.utils.SPUtils;
import com.azeus.poc.utils.TempFileUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.jayway.jsonpath.JsonPath;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class SPFile extends SPObject {
  private static final Logger LOG = LoggerFactory.getLogger(SPFile.class);

  // file data
  private String id;
  private String name;
  private Integer size;
  private String eTag;
  private String url;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timeCreated;

  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private LocalDateTime timeLastModified;

  private SPUserInfo createdBy;
  private SPUserInfo lastModifiedBy;

  // computed values
  @JsonIgnore
  private File cachedFile;

  public SPFile(OkHttpClient client, Object rawObject) {
    super(client, rawObject);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Integer getSize() {
    return size;
  }

  public String geteTag() {
    return eTag;
  }

  public String getUrl() {
    return url;
  }

  public LocalDateTime getTimeCreated() {
    return timeCreated;
  }

  public LocalDateTime getTimeLastModified() {
    return timeLastModified;
  }

  public SPUserInfo getCreatedBy() {
    return createdBy;
  }

  public SPUserInfo getLastModifiedBy() {
    return lastModifiedBy;
  }

  public File downloadFile() throws Exception {
    if (cachedFile == null) {
      Asserts.check(StringUtils.isNotBlank(this.url), "URL cannot be empty");

      LOG.info("Downloading [{}]", this.url);
      Request request = ConnectUtils.createGetRequest(this.url);
      try (Response response = getClient().newCall(request).execute()) {
        try (InputStream inputStream = response.body().byteStream()) {
          this.cachedFile = TempFileUtils.streamToFile(inputStream, this.name);
        }
      }
    }
    return cachedFile;
  }

  @Override
  protected void init() throws Exception {
    Object obj = getRawObject();
    this.id = JsonPath.read(obj, "$.Id");
    this.name = JsonPath.read(obj, "$.Name");
    this.size = JsonPath.read(obj, "$.Size");
    this.eTag = JsonPath.read(obj, "$.ETag");
    this.url = JsonPath.read(obj, "$.Url");
    this.timeCreated = SPUtils.toLocalDateTime(JsonPath.read(obj, "$.TimeCreated"));
    this.timeLastModified = SPUtils.toLocalDateTime(JsonPath.read(obj, "$.TimeLastModified"));
    this.createdBy = new SPUserInfo(getClient(), JsonPath.read(obj, "$.CreatedBy"));
    this.lastModifiedBy = new SPUserInfo(getClient(), JsonPath.read(obj, "$.LastModifiedBy"));
  }

}
