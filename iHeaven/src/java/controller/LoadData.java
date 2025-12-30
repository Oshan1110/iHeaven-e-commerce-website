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


@WebServlet(name = "LoadData", urlPatterns = {"/LoadData"})
public class LoadData extends HttpServlet {

@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        
        SessionFactory sf = HibernateUtile.getSessionFactory();
        Session s = sf.openSession();

        Criteria c1 = s.createCriteria(Brand.class);
        List<Brand> brandList = c1.list();

        Criteria c2 = s.createCriteria(Model.class);
        List<Model> modelList = c2.list();

        Criteria c3 = s.createCriteria(Quality.class);
        List<Quality> qualityList = c3.list();

        Criteria c4 = s.createCriteria(Color.class);
        List<Color> colorList = c4.list();

        Criteria c5 = s.createCriteria(Storage.class);
        List<Storage> storageList = c5.list();
        
        //load-product-data
        Status status = (Status) s.get(Status.class, 2);
        Criteria c6 = s.createCriteria(Product.class);
        c6.addOrder(Order.desc("id"));
        c6.add(Restrictions.eq("status", status));
        responseObject.addProperty("allProductCount", c6.list().size());
        
        c6.setFirstResult(0);
        c6.setMaxResults(6);
        
        List<Product> productList = c6.list();
        for (Product product : productList) {
            product.setUser(null);
        }

        //load-product-data
        
        Gson gson = new Gson();
        
        
        responseObject.add("brandList", gson.toJsonTree(brandList));
        responseObject.add("modelList", gson.toJsonTree(modelList));
        responseObject.add("qualityList", gson.toJsonTree(qualityList));
        responseObject.add("colorList", gson.toJsonTree(colorList));
        responseObject.add("storageList", gson.toJsonTree(storageList));
        responseObject.addProperty("allProductCount", productList.size());
        responseObject.add("productList", gson.toJsonTree(productList));
        responseObject.addProperty("status", true);
        
        String toJson = gson.toJson(responseObject);
        response.setContentType("application/json");
        response.getWriter().write(toJson);
        s.close();
    }

}
