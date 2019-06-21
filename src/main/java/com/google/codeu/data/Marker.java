package com.google.codeu.data;

public class Marker {

  private double lat;
  private double lng;
  private String content;

  /**
   * Constructor.
   * @param lat is the location latitude
   * @Param lng is the location longitude
   * @content is the description of the marker
   */
  public Marker(double lat, double lng, String content) {
    this.lat = lat;
    this.lng = lng;
    this.content = content;
  }
  
  /**
   * getter for latitude.
   * @return latitude as a double
   */
  public double getLat() {
    return lat;
  }

  /**
   * getter for longitude.
   * @return longitude as a double
   */
  public double getLng() {
    return lng;
  }

  /**
   * getter for marker content.
   * @return content as a String
   */
  public String getContent() {
    return content;
  }
}