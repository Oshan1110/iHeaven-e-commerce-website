/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.Address;
import Hibernate.City;
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
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADHIII
 */
@WebServlet(name = "SaveChanges", urlPatterns = {"/SaveChanges"})
public class SaveChanges extends HttpServlet {

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject userData = gson.fromJson(request.getReader(), JsonObject.class);

        String firstName = userData.get("firstName").getAsString();
        String lastName = userData.get("lastName").getAsString();
        String lineOne = userData.get("lineOne").getAsString();
        String lineTwo = userData.get("lineTwo").getAsString();
        String postaCode = userData.get("postalCode").getAsString();
        int cityId = userData.get("cityId").getAsInt();
        String currentPassword = userData.get("currentPassword").getAsString();
        String newPassword = userData.get("newPassword").getAsString();
        String confirmPassword = userData.get("confirmPassword").getAsString();
        String mobile = userData.get("mobile").getAsString();

        JsonObject responseObject = new JsonObject();

        if (firstName.isEmpty()) {
            responseObject.addProperty("message", "first name Can Not Be Empty");
        } else if (lastName.isEmpty()) {
            responseObject.addProperty("message", "last name Can Not Be Empty");
        } else if (mobile.isEmpty()) {
            responseObject.addProperty("message", "Mobile Can Not Be Empty");
        }else if(!Util.isMobileValid(mobile)){
            responseObject.addProperty("message", "Enter valid mobile");
        }else if (lineOne.isEmpty()) {
            responseObject.addProperty("message", "Enter Address line one");
        } else if (lineTwo.isEmpty()) {
            responseObject.addProperty("message", "Enter Address line two");
        } else if (postaCode.isEmpty()) {
            responseObject.addProperty("message", "Enter your postal code");
        } else if (!Util.isCodeValid(postaCode)) {
            responseObject.addProperty("message", "Enter your postal code");
        } else if (cityId == 0) {
            responseObject.addProperty("message", "Select a city");
        } else if (currentPassword.isEmpty()) {
            responseObject.addProperty("message", "Enter your current password");
        } else if (!newPassword.isEmpty() && !Util.isPasswordValid(newPassword)) {
            responseObject.addProperty("message", "The password must contains at least Uppercase,Lowecase Number,"
                    + "Special Character and to be minimum 8 Characters ");
        } else if (!newPassword.isEmpty() && newPassword.equals(currentPassword)) {
            responseObject.addProperty("message", "The new password cannot be the current password");
        } else if (!confirmPassword.isEmpty() && !Util.isPasswordValid(confirmPassword)) {
            responseObject.addProperty("message", "The password must contains at least Uppercase,Lowecase Number,"
                    + "Special Character and to be minimum 8 Characters ");
        } else if (!confirmPassword.equals(newPassword)) {
            responseObject.addProperty("message", "Confirm password does not matching entered new password");
        } else {
            HttpSession ses = request.getSession();
            if (ses.getAttribute("user") != null) {
                User u = (User) ses.getAttribute("user");

                SessionFactory sf = HibernateUtile.getSessionFactory();
                Session s = sf.openSession();

                Criteria c = s.createCriteria(User.class);
                c.add(Restrictions.eq("email", u.getEmail()));
                if (!c.list().isEmpty()) {
                    User u1 = (User) c.list().get(0);

                    u1.setFname(firstName);
                    u1.setLname(lastName);
                    if (!confirmPassword.isEmpty()) {
                        u1.setPassword(confirmPassword);
                    } else {
                        u1.setPassword(currentPassword);
                    }

                    City city = (City) s.load(City.class, cityId);
                    Address address = new Address();
                    address.setLine_1(lineOne);
                    address.setLine_2(lineTwo);
                    address.setPostalCode(postaCode);
                    address.setCity(city);
                    address.setUser(u1);
                    address.setMobile(mobile);

                    ses.setAttribute("user", u1);

                    s.merge(u1);
                    s.save(address);

                    s.beginTransaction().commit();
                    responseObject.addProperty("status", true);
                    responseObject.addProperty("message", "Use profile details update successfully!");
                    s.close();

                }
            }
        }

        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
    }

}
