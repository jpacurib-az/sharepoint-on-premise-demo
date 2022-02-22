package com.azeus.poc.utils;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public final class Settings {
  private static final Settings instance;

  private String username;
  private String password;
  private String sharepointBaseUrl;
  private String sharepointSiteUrl;
  private String uploadDirectory;
  private String downloadDirectory;

  static {
    instance = new Settings();
  }

  private Settings() {
    try (InputStream input = new FileInputStream("settings.properties")) {
      Properties prop = new Properties();
      prop.load(input);

      this.username = prop.getProperty("username");
      this.password = prop.getProperty("password");
      this.sharepointBaseUrl = prop.getProperty("sharepoint.base.url");
      this.sharepointSiteUrl = prop.getProperty("sharepoint.site.url");
      this.uploadDirectory = prop.getProperty("work.dir") + prop.getProperty("work.dir.upload");
      this.downloadDirectory = prop.getProperty("work.dir") + prop.getProperty("work.dir.download");

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public static String getUsername() {
    return instance.username;
  }

  public static String getPassword() {
    return instance.password;
  }

  public static String getSharepointBaseUrl() {
    return instance.sharepointBaseUrl;
  }

  public static String getSharepointSiteUrl() {
    return instance.sharepointSiteUrl;
  }

  public static String getUploadDirectory() {
    return instance.uploadDirectory;
  }

  public static String getDownloadDirectory() {
    return instance.downloadDirectory;
  }

}
