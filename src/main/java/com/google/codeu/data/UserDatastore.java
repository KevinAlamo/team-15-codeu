package com.google.codeu.data;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class UserDatastore {
  private DatastoreService userDatastore;
  private int id = 0;

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

}
