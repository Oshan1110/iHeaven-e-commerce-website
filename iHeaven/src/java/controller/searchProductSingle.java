/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.HibernateUtile;
import Hibernate.Product;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADHIII
 */
@WebServlet(name = "searchProductSingle", urlPatterns = {"/searchProductSingle"})
public class searchProductSingle extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject resJson = new JsonObject();

        try {
            JsonObject reqJson = gson.fromJson(request.getReader(), JsonObject.class);
            String search = reqJson.get("search").getAsString();

            SessionFactory factory = HibernateUtile.getSessionFactory();
            Session session = factory.openSession();

            Criteria c1 = session.createCriteria(Product.class);
            c1.add(Restrictions.ilike("title", search, MatchMode.ANYWHERE));

            List<Product> productList = c1.list();

            JsonArray productArray = new JsonArray();
            for (Product p : productList) {
                JsonObject prodJson = new JsonObject();
                prodJson.addProperty("id", p.getId());
                prodJson.addProperty("title", p.getTitle());
                prodJson.addProperty("price", p.getPrice());
                prodJson.addProperty("color", p.getColor() != null ? p.getColor().getName(): "Unknown");
                prodJson.addProperty("storage", p.getStorage() != null ? p.getStorage().getValue() : "Unknown");
                productArray.add(prodJson);
            }

            resJson.addProperty("status", true);
            resJson.add("products", productArray);
        } catch (Exception e) {
            resJson.addProperty("status", false);
            resJson.addProperty("error", e.getMessage());
        }

        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(resJson));
    }
}
