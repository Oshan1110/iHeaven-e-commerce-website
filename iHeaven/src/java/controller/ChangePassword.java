/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.HibernateUtile;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
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
@WebServlet(name = "ChangePassword", urlPatterns = {"/ChangePassword"})
public class ChangePassword extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject fp = gson.fromJson(request.getReader(), JsonObject.class);

        String newPassword = fp.get("newPassword").getAsString();
        String vPassword = fp.get("vPassword").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        Object emailObj = request.getSession().getAttribute("email");
        if (emailObj == null) {
            responseObject.addProperty("message", "Session expired or email not found.");
        } else {
            String email = emailObj.toString();

            if (newPassword.isEmpty()) {
                responseObject.addProperty("message", "New password cannot be empty.");
            } else if (!Util.isPasswordValid(newPassword)) {
                responseObject.addProperty("message", "Password must include uppercase, lowercase, number, special character, and be at least 8 characters long.");
            } else if (vPassword.isEmpty()) {
                responseObject.addProperty("message", "Re-type your password.");
            } else if (!vPassword.equals(newPassword)) {
                responseObject.addProperty("message", "Passwords do not match.");
            } else {
                // Hibernate operations with try-catch
                SessionFactory sf = HibernateUtile.getSessionFactory();
                Session s = null;
                Transaction tx = null;

                try {
                    s = sf.openSession();
                    Criteria c = s.createCriteria(User.class);
                    c.add(Restrictions.eq("email", email));

                    if (c.list().isEmpty()) {
                        responseObject.addProperty("message", "Can't change password. No user found.");
                    } else {
                        User user = (User) c.list().get(0);
                        user.setPassword(vPassword); // Consider hashing here

                        tx = s.beginTransaction();
                        s.update(user);
                        tx.commit();

                        responseObject.addProperty("message", "Password changed successfully!");
                        responseObject.addProperty("status", true);
                    }
                } catch (Exception e) {
                    if (tx != null) tx.rollback();
                    e.printStackTrace();
                    responseObject.addProperty("message", "Server error while updating password.");
                } finally {
                    if (s != null) s.close();
                }
            }
        }

        String jsonResponse = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(jsonResponse);
    }

}
