package com.google.codeu.data;

public class User {

  private int id;
  private String displayName;
  private String gender;
  private int age;

  /**
   * Constructs a new {@link User}.
   */
  public User(String user) {
    this.displayName = user;
  }

  /**
   * Get User ID.
   * @return ID
   */
  public int getId() {
    return id;
  }

  /**
   * Get display name.
   * @return current display name
   */
  public String getDisplayName() {
    return displayName;
  }

  /**
   * Get gender of user.
   * @return current gender of user
   */
  public String getGender() {
    return gender;
  }

  /**
   * Get age.
   * @return current age of user
   */
  public int getAge() {
    return age;
  }

  /**
   * Update display name.
   * @param name is the new name
   */
  public void updateDisplayName(String name) {
    this.displayName = name;
  }

  /**
   * Update age.
   * @param x is the new age
   */
  public void updateAge(int x) {
    this.age = x;
  }

  /**
   * Update gender.
   * @param gender is the new gender
   */
  public void updateGender(String gender) {
    this.gender = gender;
  }

}
