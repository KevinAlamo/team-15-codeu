package com.google.codeu.servlets;

import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.codeu.data.User;
import com.google.codeu.data.UserDatastore;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.List;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/users")
public class UserInfoServlet extends HttpServlet {

  private UserDatastore datastore;

  @Override
  public void init() {
    datastore = new UserDatastore();
  }

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

    response.setContentType("application/json");

    String user = request.getParameter("name");

    if (user == null || user.equals("")) {
      // Request is invalid, return empty array
      response.getWriter().println("[]");
      return;
    }

    List<User> users = datastore.getUsers();
    Gson gson = new Gson();
    String json = gson.toJson(users);

    response.getWriter().println(json);

  }

  /**
   * Stores a new {@link User}.
   */
  @Override
  public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

    UserService userService = UserServiceFactory.getUserService();
    if (!userService.isUserLoggedIn()) {
      response.sendRedirect("/index.html");
      return;
    }

    String user = request.getParameter("name");

    User profile = new User(user);
    datastore.storeUser(profile);

    response.sendRedirect("user-page.html?user=" + user);
  }


}
