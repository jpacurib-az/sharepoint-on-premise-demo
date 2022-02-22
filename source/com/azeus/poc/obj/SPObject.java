package com.azeus.poc.obj;

import org.apache.http.util.Asserts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azeus.poc.utils.SPUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;

import okhttp3.OkHttpClient;

public abstract class SPObject {
  private static final Logger LOG = LoggerFactory.getLogger(SPObject.class);

  @JsonIgnore
  private OkHttpClient client;

  @JsonIgnore
  private Object rawObject;

  public SPObject(OkHttpClient client, Object rawObject) {
    Asserts.check(client != null, "Client cannot be null");
    Asserts.check(rawObject != null, "Raw object cannot be null");

    this.client = client;
    this.rawObject = rawObject;

    try {
      init();
    } catch (Exception e) {
      LOG.error("Error while initializing " + getClass().getSimpleName(), e);
      throw new IllegalArgumentException(e);
    }
  }

  protected OkHttpClient getClient() {
    return client;
  }

  public Object getRawObject() {
    return rawObject;
  }

  @Override
  public String toString() {
    return SPUtils.jsonPrettyPrint(this);
  }

  protected abstract void init() throws Exception;
}
