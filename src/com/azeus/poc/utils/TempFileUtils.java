package com.azeus.poc.utils;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.http.util.Asserts;
import org.apache.tika.Tika;

import okhttp3.MediaType;

public class TempFileUtils {
  private static final Tika MIME_DETECTOR = new Tika();

  public static final File streamToFile(InputStream stream, String filename) throws Exception {
    File outputFile = new File(Settings.getDownloadDirectory(), filename);
    FileUtils.deleteQuietly(outputFile); // ensure file doesn't exist

    try (OutputStream outputStream = Files.newOutputStream(Path.of(outputFile.getAbsolutePath()))) {
      IOUtils.copy(stream, outputStream);
      outputStream.flush();
    }
    return outputFile;
  }

  public static final InputStream streamFromFile(File inputFile) throws Exception {
    Asserts.check(inputFile != null && inputFile.exists() && inputFile.isFile(), "Input file must be a valid file");
    return Files.newInputStream(Path.of(inputFile.getAbsolutePath()));
  }

  public static File getRandomFile() {
    File uploadDir = new File(Settings.getUploadDirectory());
    Asserts.check(uploadDir.exists() && uploadDir.isDirectory(),
        "Upload directory must be a valid directory: " + uploadDir.getAbsolutePath());

    File[] files = uploadDir.listFiles();
    Asserts.check(files != null && files.length > 0, "No file found at " + uploadDir.getAbsolutePath());

    int index = (int) Math.floor(Math.random() * files.length);
    return files[index];
  }

  public static MediaType getMediaType(File inputFile) throws Exception {
    Asserts.check(inputFile != null && inputFile.exists() && inputFile.isFile(), "Input file must be a valid file");
    return MediaType.parse(MIME_DETECTOR.detect(inputFile));
  }

}
