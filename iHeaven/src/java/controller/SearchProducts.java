/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.Brand;
import Hibernate.Color;
import Hibernate.HibernateUtile;
import Hibernate.Model;
import Hibernate.Product;
import Hibernate.Quality;
import Hibernate.Status;
import Hibernate.Storage;
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
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADHIII
 */
@WebServlet(name = "SearchProducts", urlPatterns = {"/SearchProducts"})
public class SearchProducts extends HttpServlet {

    private static final int MAX_RESULT = 6;
    private static final int ACTIVE_ID = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        JsonObject requestJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        SessionFactory sf = HibernateUtile.getSessionFactory();
        Session s = sf.openSession();

        Criteria c1 = s.createCriteria(Product.class); // get all products for the filtering

        if (requestJsonObject.has("brandName")) {
            String brandName = requestJsonObject.get("brandName").getAsString();

            Criteria c2 = s.createCriteria(Brand.class);
            c2.add(Restrictions.eq("name", brandName));
            Brand brand = (Brand) c2.uniqueResult();

            if (brand != null) {
                Criteria c3 = s.createCriteria(Model.class);
                c3.add(Restrictions.eq("brand", brand));
                List<Model> modelList = c3.list();

                if (!modelList.isEmpty()) {
                    c1.add(Restrictions.in("model", modelList));
                } else {
                    c1.add(Restrictions.sqlRestriction("1 = 0")); // Prevent invalid IN ()
                }
            } else {
                c1.add(Restrictions.sqlRestriction("1 = 0")); // Prevent query if brand not found
            }
        }

        if (requestJsonObject.has("conditionName")) {
            String qualityValue = requestJsonObject.get("conditionName").getAsString();

            Criteria c4 = s.createCriteria(Quality.class);
            c4.add(Restrictions.eq("value", qualityValue));
            Quality quality = (Quality) c4.uniqueResult();

            if (quality != null) {
                c1.add(Restrictions.eq("quality", quality));
            }
        }

        if (requestJsonObject.has("colorName")) {
            String colorName = requestJsonObject.get("colorName").getAsString();

            Criteria c5 = s.createCriteria(Color.class);
            c5.add(Restrictions.eq("name", colorName));
            Color color = (Color) c5.uniqueResult();

            if (color != null) {
                c1.add(Restrictions.eq("color", color));
            }
        }

        if (requestJsonObject.has("storageName")) {
            String storageValue = requestJsonObject.get("storageName").getAsString();

            Criteria c6 = s.createCriteria(Storage.class);
            c6.add(Restrictions.eq("value", storageValue));
            Storage storage = (Storage) c6.uniqueResult();

            if (storage != null) {
                c1.add(Restrictions.eq("storage", storage));
            }
        }

        if (requestJsonObject.has("priceStart") && requestJsonObject.has("priceEnd")) {
            double priceStart = requestJsonObject.get("priceStart").getAsDouble();
            double priceEnd = requestJsonObject.get("priceEnd").getAsDouble();

            c1.add(Restrictions.ge("price", priceStart));
            c1.add(Restrictions.le("price", priceEnd));
        }

        if (requestJsonObject.has("sortValue")) {
            String sortValue = requestJsonObject.get("sortValue").getAsString();
            if (sortValue.equals("Sort by Latest")) {
                c1.addOrder(Order.desc("id"));
            } else if (sortValue.equals("Sort by Oldest")) {
                c1.addOrder(Order.asc("id"));
            } else if (sortValue.equals("Sort by Name")) {
                c1.addOrder(Order.asc("title"));
            } else if (sortValue.equals("Sort by Price")) {
                c1.addOrder(Order.asc("price"));
            }
        }
        
        Status status = (Status) s.get(Status.class, SearchProducts.ACTIVE_ID);
        c1.add(Restrictions.eq("status", status));

        responseObject.addProperty("allProductCount", c1.list().size());

        if (requestJsonObject.has("firstResult")) {
            int firstResult = requestJsonObject.get("firstResult").getAsInt();
            c1.setFirstResult(firstResult);
            c1.setMaxResults(SearchProducts.MAX_RESULT);
        }

        List<Product> productList = c1.list();
        for (Product product : productList) {
            product.setUser(null);
        }

        s.close();

        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.addProperty("status", true);
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);

    }
}
