package com.azeus.poc.obj;

import com.jayway.jsonpath.JsonPath;

import okhttp3.OkHttpClient;

public class SPUserInfo extends SPObject {

  private String id;
  private String name;
  private String puid;

  public SPUserInfo(OkHttpClient client, Object rawObject) throws Exception {
    super(client, rawObject);
  }

  public String getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public String getPuid() {
    return puid;
  }

  @Override
  protected void init() throws Exception {
    Object obj = getRawObject();
    this.id = JsonPath.read(obj, "$.Id");
    this.name = JsonPath.read(obj, "$.Name");
    this.puid = JsonPath.read(obj, "$.Puid");
  }

}
