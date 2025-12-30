/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.HibernateUtile;
import Hibernate.Model;
import Hibernate.Product;
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
import model.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADHIII
 */
@WebServlet(name = "LoadSingleProduct", urlPatterns = {"/LoadSingleProduct"})
public class LoadSingleProduct extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        String productId = request.getParameter("id");

        if (Util.isIntegers(productId)) {

            SessionFactory sf = HibernateUtile.getSessionFactory();
            Session s = sf.openSession();

            try {

                Product product = (Product) s.get(Product.class, Integer.valueOf(productId));

                if (product.getStatus().getValue().equals("Active")) {

                    product.getUser().setEmail(null);
                    product.getUser().setPassword(null);
                    product.getUser().setVerificationCode(null);
                    product.getUser().setId(-1);
                    product.getUser().setCreatedAt(null);

                    //load Similer Product
                    Criteria c1 = s.createCriteria(Model.class);
                    c1.add(Restrictions.eq("brand", product.getModel().getBrand()));
                    List<Model> modelList = c1.list();
                    
                    Criteria c2 = s.createCriteria(Product.class);
                    c2.add(Restrictions.in("model", modelList));
                    c2.add(Restrictions.ne("id", product.getId()));
                    c2.setMaxResults(6);
                    
                    List<Product> productList = c2.list();
                    
                    
                    
                     responseObject.add("productList", gson.toJsonTree(productList));
                    //ens Similer Product
                    

                    responseObject.add("product", gson.toJsonTree(product));
                   
                    responseObject.addProperty("status", true);

                } else {
                    responseObject.addProperty("message", "Product not Found");
                }

            } catch (NumberFormatException | HibernateException e) {
                responseObject.addProperty("message", "Product not Found");
            }

        }

        String responseText = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(responseText);
    }


}
