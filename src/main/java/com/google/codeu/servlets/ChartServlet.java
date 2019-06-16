package com.google.codeu.servlets;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import java.io.IOException;
import java.util.Scanner;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/** Handles fetching all messages for the public feed. */
@WebServlet("/new-residents-charts")
public class ChartServlet extends HttpServlet {

  private JsonArray bookRatingArray;

  // This class could be its own file if we needed it outside this servlet
  private static class BookRating {
    int freedom;
    int happy;

    private BookRating(int freedom, int happy) {
      this.freedom = freedom;
      this.happy = happy;
    }
  }

  @Override
  public void init() {
    bookRatingArray = new JsonArray();
    Gson gson = new Gson();
    Scanner scanner = new Scanner(getServletContext().getResourceAsStream("/WEB-INF/happiness.csv"));
    scanner.nextLine(); // skips first line (the csv header)
    while (scanner.hasNextLine()) {
      String line = scanner.nextLine();
      String[] cells = line.split(",");

      int freedom = 0;
      if (!cells[5].equals("")) {
        freedom = Integer.parseInt(cells[5]);
      }
      int happy = Integer.parseInt(cells[1]);

      bookRatingArray.add(gson.toJsonTree(new BookRating(freedom, happy)));
    }
    scanner.close();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
    response.setContentType("application/json");
    response.getOutputStream().println(bookRatingArray.toString());
  }
}