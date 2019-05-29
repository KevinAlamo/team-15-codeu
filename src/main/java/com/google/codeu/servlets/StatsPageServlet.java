package com.google.codeu.servlets;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
/**
 * Responds with a hard-coded message for testing purposes.
 */
@WebServlet("/stats")
public class StatsPageServlet extends HttpServlet{
  
 @Override
 public void doGet(HttpServletRequest request, HttpServletResponse response)
   throws IOException {
  
  response.getOutputStream().println("Total number of Messages: " + getTotalMessageCount());
 }
 
 /** Returns the total number of messages for all users. */
 public int getTotalMessageCount(){
	 DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	 Query query = new Query("Message");
	 PreparedQuery results = datastore.prepare(query);
	 return results.countEntities(FetchOptions.Builder.withLimit(1000));
 }
}