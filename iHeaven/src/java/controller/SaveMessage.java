/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.HibernateUtile;
import Hibernate.User;
import Hibernate.UserFeedback;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADHIII
 */
@WebServlet(name = "SaveMessage", urlPatterns = {"/SaveMessage"})
public class SaveMessage extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject msgData = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = msgData.get("firstName").getAsString();
        String lastName = msgData.get("lastName").getAsString();
        String email = msgData.get("email").getAsString();
        String fMessage = msgData.get("fMessage").getAsString();

        JsonObject responseObject = new JsonObject();

        if (firstName.isEmpty()) {
            responseObject.addProperty("message", "Please enter your First name!");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "Please enter your Last name!");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "Please enter your Email!");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter valid Email!");
        } else if (fMessage.isEmpty()) {
            responseObject.addProperty("message", "Please enter your Message!");
        } else {
//            HttpSession ses = request.getSession();
            SessionFactory sf = HibernateUtile.getSessionFactory();
            Session s = sf.openSession();

            UserFeedback userFeedback = new UserFeedback();
            userFeedback.setFirst_name(firstName);
            userFeedback.setLast_name(lastName);
            userFeedback.setEmail(email);
            userFeedback.setText(fMessage);

            s.save(userFeedback);
            s.beginTransaction().commit();
            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Message added!");
            s.close();
        }
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);

    }
}
