<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<% BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
String uploadUrl = blobstoreService.createUploadUrl("/image-analysis"); %>

<!DOCTYPE html>
<html>
<head>
  <meta charset="UTF-8">
  <title>Image Upload Analysis</title>
</head>
<body>
<h1>Image Upload Analysis</h1>
<p>This page allows you to upload images and see what type of labels are associated with the image. In the future, we hope to make these labels clickable so that users can see which other users have posted specific types of images.</p>

<form method="POST" enctype="multipart/form-data" action="<%= uploadUrl %>">
  <p>Upload an image:</p>
  <input type="file" name="image">
  <br/><br/>
  <button>Submit</button>
</form>
</body>
</html>