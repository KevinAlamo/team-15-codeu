    
/*
 * Copyright 2019 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.codeu.servlets;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.blobstore.FileInfo;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.cloud.vision.v1.AnnotateImageRequest;
import com.google.cloud.vision.v1.AnnotateImageResponse;
import com.google.cloud.vision.v1.BatchAnnotateImagesResponse;
import com.google.cloud.vision.v1.EntityAnnotation;
import com.google.cloud.vision.v1.Feature;
import com.google.cloud.vision.v1.Image;
import com.google.cloud.vision.v1.ImageAnnotatorClient;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;

import com.google.protobuf.ByteString;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * Handles fetching and saving {@link Message} instances.
 */
@WebServlet("/messages")
public class MessageServlet extends HttpServlet {

  private Datastore datastore;

  @Override
  public void init() {
    datastore = new Datastore();
  }

  /**
   * Responds with a JSON representation of {@link Message} data for a specific user. Responds with
   * an empty array if the user is not provided.
   */
  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    String user = request.getParameter("user");

    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    List<Message> messages = datastore.getMessages(user);
    Gson gson = new Gson();
    String json = gson.toJson(messages);

    response.getWriter().println(json);
  }

  /**
   * Stores a new {@link Message}.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String user = userService.getCurrentUser().getEmail();
    String userText = Jsoup.clean(request.getParameter("text"), Whitelist.none());
    String regex = "(https?://\\S+\\.(png|jpg|gif|jpeg))";
    String replacement = "<img src=\"$1\" />";
    String textWithImagesReplaced = userText.replaceAll(regex, replacement);
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<FileInfo>> fileName = blobstoreService.getFileInfos(request);
    List<FileInfo> file = fileName.get("image");
    
    if ((file != null) && (!file.get(0).getFilename().isEmpty())) {
      List<BlobKey> blobKeys = getBlobKeys(request, "image");
      List<String> imageBlobUrls = null;

      List<byte[]> blobBytes;
      List<List<EntityAnnotation>> imageLabels = null;
      if (blobKeys != null) {
        imageBlobUrls = getUploadUrl(blobstoreService, blobKeys);
        blobBytes = getBlobBytes(blobKeys,blobstoreService);
        imageLabels = getImageLabels(blobBytes);
      }

      //add image tag on uploads
      if (imageBlobUrls != null) {
        for (int i = 0; i < imageBlobUrls.size(); i++) {
          String url = imageBlobUrls.get(i);
          textWithImagesReplaced += "<img src=\"" + url + "\" />";
          textWithImagesReplaced += "<p>";
          int lastIndexCheck = 0;
          for (EntityAnnotation label : imageLabels.get(i)) {
            textWithImagesReplaced = textWithImagesReplaced + label.getDescription();
            if (lastIndexCheck != imageLabels.get(i).size() - 1) {
              textWithImagesReplaced += ", ";
            }
            lastIndexCheck++;
          }
          textWithImagesReplaced += "</p>";
        }
      }
    }

    Message message = new Message(user, textWithImagesReplaced);
    datastore.storeMessage(message);
    response.sendRedirect(request.getHeader("referer"));
  }

  private List<BlobKey> getBlobKeys(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);


    if (blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    for (BlobKey blobKey: blobKeys) {
      BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
      if (blobInfo.getSize() == 0) {
        blobstoreService.delete(blobKey);
      }
    }
    return blobKeys;
  }

  private List<String> getUploadUrl(BlobstoreService blobstoreService, List<BlobKey> blobKeys) {
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    List<String> imageBlobUrls = new ArrayList<String>();

    for (BlobKey blobKey: blobKeys) {
      BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
      String contentType = blobInfo.getContentType();
      String fileType = contentType.toString();
      fileType = fileType.toLowerCase();
      if (!(fileType.equals("image/png") || fileType.equals("image/jpg")
          || fileType.equals("image/gif") || fileType.equals("image/jpeg"))) {
        //not yet supported so deleted
        blobstoreService.delete(blobKey);
      } else {
        ServingUrlOptions urlOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
        String imageUrl = imagesService.getServingUrl(urlOptions);
        imageBlobUrls.add(imageUrl);
      }
    }
    return imageBlobUrls;
  }

  /**
   * Blobstore stores files as binary data. This function retrieves the
   * binary data stored at the BlobKey parameter.
   */
  private List<byte[]> getBlobBytes(List<BlobKey> bk, BlobstoreService bss) throws IOException {
    List<byte[]> bytes = new ArrayList<>();

    for (BlobKey key : bk) {
      ByteArrayOutputStream outputBytes = new ByteArrayOutputStream();
      int fetchSize = bss.MAX_BLOB_FETCH_SIZE;
      long curr = 0;
      boolean continueReading = true;
      while (continueReading) {
        // end index is inclusive, so we have to subtract 1 to get fetchSize bytes
        byte[] b = bss.fetchData(key, curr, curr + fetchSize - 1);
        outputBytes.write(b);

        // if we read fewer bytes than we requested, then we reached the end
        if (b.length < fetchSize) {
          continueReading = false;
        }

        curr += fetchSize;
      }
      bytes.add(outputBytes.toByteArray());
    }
    return bytes;
  }

  /**
   * Uses the Google Cloud Vision API to generate a list of labels that apply to the image
   * represented by the binary data stored in imgBytes.
   */
  private List<List<EntityAnnotation>> getImageLabels(List<byte[]> imgBytes) throws IOException {
    List<List<EntityAnnotation>> annotations = new ArrayList<>();
    for (byte[] imgByte : imgBytes) {
      ByteString byteString = ByteString.copyFrom(imgByte);
      Image image = Image.newBuilder().setContent(byteString).build();

      Feature feature = Feature.newBuilder().setType(Feature.Type.LABEL_DETECTION).build();
      AnnotateImageRequest request =
          AnnotateImageRequest.newBuilder().addFeatures(feature).setImage(image).build();
      List<AnnotateImageRequest> requests = new ArrayList<>();
      requests.add(request);

      ImageAnnotatorClient client = ImageAnnotatorClient.create();
      BatchAnnotateImagesResponse batchResponse = client.batchAnnotateImages(requests);
      client.close();
      List<AnnotateImageResponse> imageResponses = batchResponse.getResponsesList();
      AnnotateImageResponse imageResponse = imageResponses.get(0);

      if (imageResponse.hasError()) {
        System.err.println("Error getting image labels: " + imageResponse.getError().getMessage());
        return null;
      }
      annotations.add(imageResponse.getLabelAnnotationsList());
    }
    return annotations;
  }
}