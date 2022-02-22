package com.azeus.poc;

import java.io.File;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.azeus.poc.obj.SPFile;
import com.azeus.poc.utils.ConnectUtils;
import com.azeus.poc.utils.SPUtils;
import com.azeus.poc.utils.Settings;

import okhttp3.OkHttpClient;

public class Test {
  private static final Logger LOG = LoggerFactory.getLogger(Test.class);

  public static void main(String[] args) throws Exception {
    OkHttpClient client = ConnectUtils.createClient(Settings.getUsername(), Settings.getPassword());
    SharePointOP spOp = new SharePointOP(client);

    demoListAndDownload(spOp);
    demoUploadOrUpdate(spOp);

    LOG.info("Done!");
  }

  private static void demoListAndDownload(SharePointOP spOp) throws Exception {
    LOG.info("*** List and Download Demo ***");

    List<SPFile> spDocList = spOp.listDocuments();
    LOG.info("Total number of files: {}", spDocList.size());

    for (SPFile spFile : spDocList) {
      LOG.info("File [{}]{}{}", spFile.getName(), SPUtils.NEW_LINE, spFile);
      File file = spFile.downloadFile();
      LOG.info("File download at {}{}", file.getAbsolutePath(), SPUtils.NEW_LINE);
    }
  }

  private static void demoUploadOrUpdate(SharePointOP spOp) throws Exception {
    LOG.info("*** Upload or Update Demo ***");

    SPFile uploadedSPFile = spOp.uploadDocument();
    LOG.info("Successfully uploaded file [{}]{}{}", uploadedSPFile.getName(), SPUtils.NEW_LINE, uploadedSPFile);
    File file = uploadedSPFile.downloadFile();
    LOG.info("File download at {}{}", file.getAbsolutePath(), SPUtils.NEW_LINE);
  }

}
