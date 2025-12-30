/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.Brand;
import Hibernate.HibernateUtile;
import Hibernate.Product;
import Hibernate.Status;
import Hibernate.UserFeedback;
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
@WebServlet(name = "LoadHomeData", urlPatterns = {"/LoadHomeData"})
public class LoadHomeData extends HttpServlet {
private static final int ACTIVE_STATUS_ID = 2;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        SessionFactory sf = HibernateUtile.getSessionFactory();
        Session s = sf.openSession();
        
        Criteria c1 = s.createCriteria(Brand.class);
        List<Brand> brandList = c1.list();
        responseObject.add("brandList", gson.toJsonTree(brandList));
        
        Criteria c2 = s.createCriteria(Product.class);
        c2.addOrder(Order.desc("id"));
        
        Status status = (Status)s.get(Status.class, LoadHomeData.ACTIVE_STATUS_ID);
        c2.add(Restrictions.eq("status", status));
        
        c2.setFirstResult(0);
        c2.setMaxResults(4);
        
        List<Product> productList = c2.list();
        for (Product product : productList) {
            product.setUser(null);
        }
        
        Criteria c3 = s.createCriteria(UserFeedback.class);
        List<UserFeedback> userFeedbacksList = c3.list();
        responseObject.add("userFeedbacksList", gson.toJsonTree(userFeedbacksList));
        
        responseObject.add("productList", gson.toJsonTree(productList));
        
responseObject.addProperty("status", true);
        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }

}
