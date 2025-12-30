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
import net.sf.ehcache.hibernate.HibernateUtil;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import sun.awt.windows.ThemeReader;

@WebServlet(name = "SignUp", urlPatterns = {"/SignUp"})
public class SignUp extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject user = gson.fromJson(request.getReader(), JsonObject.class);

        String fname = user.get("fristName").getAsString();
        String lname = user.get("lastName").getAsString();
        final String email = user.get("email").getAsString();
        String password = user.get("password").getAsString();
        String password2 = user.get("password2").getAsString();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        if (fname.isEmpty()) {
            responseObject.addProperty("message", "Frist Name Can Not Be Empty");
        } else if (lname.isEmpty()) {
            responseObject.addProperty("message", "Last Name Can Not Be Empty");
        } else if (email.isEmpty()) {
            responseObject.addProperty("message", "Email Can Not Be Empty");
        } else if (!Util.isEmailValid(email)) {
            responseObject.addProperty("message", "Please Enter Valid Email");
        } else if (password.isEmpty()) {
            responseObject.addProperty("message", "Password Can Not Be Empty");
        } else if (!Util.isPasswordValid(password)) {
            responseObject.addProperty("message", "The password must contains at least Uppercase,Lowecase Number,"
                    + "Special Character and to be minimum 8 Characters ");
        } else if (password2.isEmpty()) {
            responseObject.addProperty("message", "Re-Type Your Password");
        } else if (!password2.equals(password)) {
            responseObject.addProperty("message", "Password Does Not Match!");
        } else {
            //hibernate save
            SessionFactory sf = HibernateUtile.getSessionFactory();
            Session s = sf.openSession();

            Criteria c = s.createCriteria(User.class);
            c.add(Restrictions.eq("email", email));

            if (!c.list().isEmpty()) {
                responseObject.addProperty("message", "User With the Email Alredy Exsists");
            } else {
                User u = new User();
                u.setFname(fname);
                u.setLname(lname);
                u.setEmail(email);
                u.setPassword(password);

                //verification code
                final String Verificationcode = Util.genarateCode();
                u.setVerificationCode(Verificationcode);
                u.setCreatedAt(new Date());

                s.save(u);
                s.beginTransaction().commit();
                //hibernate save

                //send email
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(email, "iHeaven Log In Verification Code", "<!DOCTYPE html>\n"
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
                                + "        <div class=\"verification-code\">"+Verificationcode+"</div>\n"
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

                HttpSession session = request.getSession();
                session.setAttribute("email", email);

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Registration Success.Please check youer Email for the Verification Code. ");
            }

            s.close();
        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);

//            
    }

}
