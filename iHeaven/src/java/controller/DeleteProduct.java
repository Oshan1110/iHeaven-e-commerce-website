/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.Cart;
import Hibernate.HibernateUtile;
import Hibernate.Product;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADHIII
 */
@WebServlet(name = "DeleteProduct", urlPatterns = {"/DeleteProduct"})
public class DeleteProduct extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        try {
            JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);
            int productId = requestJsonObject.get("id").getAsInt();

            SessionFactory sf = HibernateUtile.getSessionFactory();
            Session s = sf.openSession();
            Transaction tr = s.beginTransaction();

            User user = (User) request.getSession().getAttribute("user");

            // Find the Cart item with given product and user
            Criteria cartCriteria = s.createCriteria(Cart.class);
            cartCriteria.add(Restrictions.eq("product.id", productId)); // or "product" if object mapped
            cartCriteria.add(Restrictions.eq("user", user));
            Cart cartItem = (Cart) cartCriteria.uniqueResult();

            if (cartItem != null) {
                s.delete(cartItem);
                tr.commit();
                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Product removed from cart successfully");
            } else {
                responseObject.addProperty("message", "Product not found in cart");
            }

            s.close();
        } catch (Exception e) {
            e.printStackTrace();
            responseObject.addProperty("message", "An error occurred: " + e.getMessage());
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));

    }

}
