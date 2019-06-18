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
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.Datastore;
import com.google.codeu.data.Message;
import com.google.gson.Gson;

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
    List<String> imageBlobUrls = getUploadUrl(request, "image");
    //add image tag for uploaded image url at the end of message text
    if(imageBlobUrls!=null ) {
      for(String url:imageBlobUrls)
      {
        textWithImagesReplaced += "<img src=\"" + url + "\" />";   
      }
    }

    Message message = new Message(user, textWithImagesReplaced);
    datastore.storeMessage(message);

    response.sendRedirect("/user-page.html?user=" + user);
  }
  
  private List<String> getUploadUrl(HttpServletRequest request, String formInputElementName) {
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
    Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(request);
    List<BlobKey> blobKeys = blobs.get(formInputElementName);

    // User submitted form without selecting a file, so we can't get a URL. (devserver)
    if(blobKeys == null || blobKeys.isEmpty()) {
      return null;
    }

    for(BlobKey blobKey: blobKeys) {
    	BlobInfo blobInfo = new BlobInfoFactory().loadBlobInfo(blobKey);
    	if(blobInfo.getSize() == 0) {
    		blobstoreService.delete(blobKey);
    	}
    }
    
    ImagesService imagesService = ImagesServiceFactory.getImagesService();
    List<String> imageBlobUrls = new ArrayList<String>();
    
    for(BlobKey blobKey: blobKeys) {
    	String fileType = new BlobInfoFactory().loadBlobInfo(blobKey).getContentType().toString().toLowerCase();
    	if(!(fileType.equals("image/png") ||fileType.equals("image/jpg") || fileType.equals("image/gif") || fileType.equals("image/jpeg"))) {
    		//not yet supported so deleted
    		blobstoreService.delete(blobKey);
    	}
    	else {
    		ServingUrlOptions urlOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);
    		String imageUrl = imagesService.getServingUrl(urlOptions);
    		imageBlobUrls.add(imageUrl);
    	}
    	
    }
  
    return imageBlobUrls;
  }
}
