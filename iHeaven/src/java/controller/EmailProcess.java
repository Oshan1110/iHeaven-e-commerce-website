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
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
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
@WebServlet(name = "EmailProcess", urlPatterns = {"/EmailProcess"})
public class EmailProcess extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);
        final String email = user.get("email").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (email.isEmpty()) {
            responseObject.addProperty("message", "Email cannot be empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please enter a valid email");
        } else {
            SessionFactory sf = HibernateUtile.getSessionFactory();
            Session s = sf.openSession();

            try {
                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", email));
                List<User> users = c.list();

                if (users.isEmpty()) {
                    responseObject.addProperty("message", "Invalid Email");
                } else {
                    User existingUser = users.get(0);

                    final String otpCode = Util.genarateCode();
                    existingUser.setOtp(otpCode);

                    Transaction tx = s.beginTransaction();
                    s.update(existingUser); // update existing user with new OTP
                    tx.commit();

                    // Send email in a new thread
                    new Thread(() -> {
                        String htmlContent = "<!DOCTYPE html>\n"
                                + "<html lang=\"en\">\n"
                                + "<head>\n"
                                + "  <meta charset=\"UTF-8\">\n"
                                + "  <title>iHeaven Email Verification</title>\n"
                                + "  <style>\n"
                                + "    body { font-family: 'Segoe UI', sans-serif; background-color: #f2f4f6; margin: 0; padding: 0; }\n"
                                + "    .email-wrapper { width: 100%; padding: 20px; background-color: #f2f4f6; }\n"
                                + "    .email-content { max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; overflow: hidden; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }\n"
                                + "    .email-header { background-color: #111827; color: white; padding: 20px; text-align: center; }\n"
                                + "    .email-header h1 { margin: 0; font-size: 24px; }\n"
                                + "    .email-body { padding: 30px 20px; color: #333333; text-align: center; }\n"
                                + "    .verification-code { font-size: 36px; font-weight: bold; color: #2563eb; margin: 20px 0; letter-spacing: 8px; }\n"
                                + "    .email-footer { font-size: 12px; color: #999999; text-align: center; padding: 20px; }\n"
                                + "  </style>\n"
                                + "</head>\n"
                                + "<body>\n"
                                + "  <div class=\"email-wrapper\">\n"
                                + "    <div class=\"email-content\">\n"
                                + "      <div class=\"email-header\">\n"
                                + "        <h1>iHeaven OTP Code</h1>\n"
                                + "      </div>\n"
                                + "      <div class=\"email-body\">\n"
                                + "        <p>Hello,</p>\n"
                                + "        <p>Use the 6-digit otp code below to verify your email address for iHeaven:</p>\n"
                                + "        <div class=\"verification-code\">" + otpCode + "</div>\n"
                                + "        <p>This code is valid for 5 minutes. Please do not share it with anyone.</p>\n"
                                + "        <p>Thank you,<br><strong>iHeaven Team</strong></p>\n"
                                + "      </div>\n"
                                + "      <div class=\"email-footer\">&copy; 2025 iHeaven. All rights reserved.</div>\n"
                                + "    </div>\n"
                                + "  </div>\n"
                                + "</body>\n"
                                + "</html>";

                        Mail.sendMail(email, "iHeaven OTP Code", htmlContent);
                    }).start();

                    // Save email in session
                    HttpSession session = request.getSession();
                    session.setAttribute("email", email);

                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Registration successful. Please check your email for the verification code.");
                }
            } catch (Exception e) {
                e.printStackTrace();
                responseObject.addProperty("message", "Something went wrong. Please try again.");
            } finally {
                s.close();
            }
        }

// Send JSON response
        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }
}
