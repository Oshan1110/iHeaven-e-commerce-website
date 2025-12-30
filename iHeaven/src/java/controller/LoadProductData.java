/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.Brand;
import Hibernate.Color;
import Hibernate.HibernateUtile;
import Hibernate.Model;
import Hibernate.Quality;
import Hibernate.Storage;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

/**
 *
 * @author ADHIII
 */
@WebServlet(name = "LoadProductData", urlPatterns = {"/LoadProductData"})
public class LoadProductData extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

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
        
        Gson gson = new Gson();
        
        responseObject.addProperty("status", true);
        
        responseObject.add("brandList", gson.toJsonTree(brandList));
        responseObject.add("modelList", gson.toJsonTree(modelList));
        responseObject.add("qualityList", gson.toJsonTree(qualityList));
        responseObject.add("colorList", gson.toJsonTree(colorList));
        responseObject.add("storageList", gson.toJsonTree(storageList));
        
        resp.setContentType("application/json");
        resp.getWriter().write(gson.toJson(responseObject));
    }

}
