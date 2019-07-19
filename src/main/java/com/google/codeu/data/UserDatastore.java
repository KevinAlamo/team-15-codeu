package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import java.util.ArrayList;
import java.util.List;

public class UserDatastore {
  private DatastoreService userDatastore;
  private int id = 1;

  public UserDatastore() {
    userDatastore = DatastoreServiceFactory.getDatastoreService();
  }

  /** Stores the User in userDatastore. */
  /** TODO:
   * make sure id is different for every user
   * autofill info on submission page of editing profile with everything you know
   * CSS for editing profile .. so ugly right now
   * connect submission to servlet somehow
   * display updated user info on user page
   * exception when user enters in non numberical data for age?
   * @param user
   */
  public void storeUser(User user) {
    Entity userEntity = new Entity("User", id);
    userEntity.setProperty("name", user.getDisplayName());
    userEntity.setProperty("gender", user.getGender());
    userEntity.setProperty("age", user.getAge());
    userDatastore.put(userEntity);
    id += 1;
  }

  public List<User> getUsers() {
    Query query = new Query("User");
    PreparedQuery results = userDatastore.prepare(query);

    List<User> users = getUserInformation(results);
    return users;
  }

  public List<User> getUserInformation(PreparedQuery results) {
    List<User> users = new ArrayList<>();
    for (Entity entity : results.asIterable()) {
      try {
        String name = (String) entity.getProperty("name");
        String gender = (String) entity.getProperty("gender");
        int age = (int) entity.getProperty("age");

        User user = new User(name);
        users.add(user);
      } catch (Exception e) {
        System.err.println("Error reading message.");
        System.err.println(entity.toString());
        e.printStackTrace();
      }
    }
    return users;
  }
}
