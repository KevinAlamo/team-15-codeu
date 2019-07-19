package com.google.codeu.data;

public class User {

  private int id;
  private String displayName;
  private String gender;
  private int age;

  public User(String user) {
    this.displayName = user;
  }

  public int getId() {
    return id;
  }

  public String getDisplayName() {
    return displayName;
  }

  public String getGender() {
    return gender;
  }

  public int getAge() {
    return age;
  }

  public void updateDisplayName(String name) {
    this.displayName = name;
  }

  public void updateAge(int x) {
    this.age = x;
  }

  public void updateGender(String gender) {
    this.gender = gender;
  }

}
