package com.google.codeu.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import java.io.IOException;
import java.util.Scanner;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * Returns map data as a JSON array, e.g. [{"lat": 38.4404675, "lng": -122.7144313}]
 */
@WebServlet("/boba_spots")
public class MapServlet extends HttpServlet {

  private JsonArray bobaArray;

  @Override
  public void init() {
    bobaArray = new JsonArray();
    Gson gson = new Gson();

    Scanner scanner = new 
        Scanner(getServletContext().getResourceAsStream("/WEB-INF/boba_spots.csv"));
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      double lat = Double.parseDouble(cells[0]);
      double lng = Double.parseDouble(cells[1]);

      bobaArray.add(gson.toJsonTree(new BobaSpot(lat, lng)));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.getOutputStream().println(bobaArray.toString());
  }

  // This class could be its own file if we needed it outside this servlet
  private static class BobaSpot {

    double lat;
    double lng;

    private BobaSpot(double lat, double lng) {
      this.lat = lat;
      this.lng = lng;
    }
  }
}
