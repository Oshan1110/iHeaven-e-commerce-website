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
import java.util.Date;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADHIII
 */
@WebServlet(name = "ResendCode", urlPatterns = {"/ResendCode"})
public class ResendCode extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        String Email = request.getSession().getAttribute("email").toString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtile.getSessionFactory();
        Session s = sf.openSession();

        Criteria c = s.createCriteria(User.class);
        c.add(Restrictions.eq("email", Email));

        if (c.list().isEmpty()) {

        } else {
            User u = (User) c.list().get(0);
            final String vCode = Util.genarateCode();

            u.setVerificationCode(vCode);
            s.update(u);
            s.beginTransaction().commit();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    Mail.sendMail(Email, "iHeaven Log In Verification Code", "<!DOCTYPE html>\n"
                            + "<html lang=\"en\">\n"
                            + "<head>\n"
                            + "  <meta charset=\"UTF-8\">\n"
                            + "  <title>iHeaven Email Verification</title>\n"
                            + "  <style>\n"
                            + "    body {\n"
                            + "      font-family: 'Segoe UI', sans-serif;\n"
                            + "      background-color: #f2f4f6;\n"
                            + "      margin: 0;\n"
                            + "      padding: 0;\n"
                            + "    }\n"
                            + "    .email-wrapper {\n"
                            + "      width: 100%;\n"
                            + "      padding: 20px;\n"
                            + "      background-color: #f2f4f6;\n"
                            + "    }\n"
                            + "    .email-content {\n"
                            + "      max-width: 600px;\n"
                            + "      margin: 0 auto;\n"
                            + "      background-color: #ffffff;\n"
                            + "      border-radius: 8px;\n"
                            + "      overflow: hidden;\n"
                            + "      box-shadow: 0 4px 6px rgba(0,0,0,0.05);\n"
                            + "    }\n"
                            + "    .email-header {\n"
                            + "      background-color: #111827;\n"
                            + "      color: white;\n"
                            + "      padding: 20px;\n"
                            + "      text-align: center;\n"
                            + "    }\n"
                            + "    .email-header h1 {\n"
                            + "      margin: 0;\n"
                            + "      font-size: 24px;\n"
                            + "    }\n"
                            + "    .email-body {\n"
                            + "      padding: 30px 20px;\n"
                            + "      color: #333333;\n"
                            + "      text-align: center;\n"
                            + "    }\n"
                            + "    .verification-code {\n"
                            + "      font-size: 36px;\n"
                            + "      font-weight: bold;\n"
                            + "      color: #2563eb;\n"
                            + "      margin: 20px 0;\n"
                            + "      letter-spacing: 8px;\n"
                            + "    }\n"
                            + "    .email-footer {\n"
                            + "      font-size: 12px;\n"
                            + "      color: #999999;\n"
                            + "      text-align: center;\n"
                            + "      padding: 20px;\n"
                            + "    }\n"
                            + "  </style>\n"
                            + "</head>\n"
                            + "<body>\n"
                            + "  <div class=\"email-wrapper\">\n"
                            + "    <div class=\"email-content\">\n"
                            + "      <div class=\"email-header\">\n"
                            + "        <h1>iHeaven Verification</h1>\n"
                            + "      </div>\n"
                            + "      <div class=\"email-body\">\n"
                            + "        <p>Hello,</p>\n"
                            + "        <p>Use the 6-digit verification code below to verify your email address for iHeaven:</p>\n"
                            + "        <div class=\"verification-code\">" + vCode + "</div>\n"
                            + "        <p>This code is valid for 5 minutes. Please do not share it with anyone.</p>\n"
                            + "        <p>Thank you,<br><strong>iHeaven Team</strong></p>\n"
                            + "      </div>\n"
                            + "      <div class=\"email-footer\">\n"
                            + "        &copy; 2025 iHeaven. All rights reserved.\n"
                            + "      </div>\n"
                            + "    </div>\n"
                            + "  </div>\n"
                            + "</body>\n"
                            + "</html>");
                }
            }).start();

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Registration Success.Please check youer Email for the Verification Code. ");
        }

    }

}
