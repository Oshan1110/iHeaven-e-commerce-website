package controller;

import Hibernate.Brand;
import Hibernate.Color;
import Hibernate.HibernateUtile;
import Hibernate.Model;
import Hibernate.Product;
import Hibernate.Quality;
import Hibernate.Status;
import Hibernate.Storage;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.Part;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

@MultipartConfig
@WebServlet(name = "SaveProducts", urlPatterns = {"/SaveProducts"})
public class SaveProducts extends HttpServlet {

    private static final int PENDING_STAUTUS_ID = 2;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String brandId = request.getParameter("brandId");
        String modelId = request.getParameter("modelId");
        String title = request.getParameter("title");
        String desc = request.getParameter("desc");
        String storageId = request.getParameter("storageId");
        String colorId = request.getParameter("colorId");
        String conditionId = request.getParameter("conditionId");
        String price = request.getParameter("price");
        String qty = request.getParameter("qty");

        Part part1 = request.getPart("img1");
        Part part2 = request.getPart("img2");
        Part part3 = request.getPart("img3");
//
        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);

        SessionFactory sf = HibernateUtile.getSessionFactory();
        Session s = sf.openSession();

        if (request.getSession().getAttribute("user") == null) {
            responseObject.addProperty("message", "Please sign in!");
        } else if (!Util.isIntegers(brandId)) {
            responseObject.addProperty("message", "Invalid Category!");
        } else if (Integer.parseInt(brandId) == 0) {
            responseObject.addProperty("message", "Please select a Category!");
        } else if (!Util.isIntegers(modelId)) {
            responseObject.addProperty("message", "Invalid model!");
        } else if (Integer.parseInt(modelId) == 0) {
            responseObject.addProperty("message", "Please select a model!");
        } else if (title.isEmpty()) {
            responseObject.addProperty("message", "Product title can not be empty");
        } else if (desc.isEmpty()) {
            responseObject.addProperty("message", "Product description can not be empty");
        } else if (!Util.isIntegers(storageId)) {
            responseObject.addProperty("message", "Invalid storage");
        } else if (Integer.parseInt(storageId) == 0) {
            responseObject.addProperty("message", "Please select a valid storage");
        } else if (!Util.isIntegers(colorId)) {
            responseObject.addProperty("message", "Invalid color");
        } else if (Integer.parseInt(colorId) == 0) {
            responseObject.addProperty("message", "Please select a valid color");
        } else if (!Util.isIntegers(conditionId)) {
            responseObject.addProperty("message", "Invalid condition");
        } else if (Integer.parseInt(conditionId) == 0) {
            responseObject.addProperty("message", "Please select a valid condition");
        } else if (!Util.isDoubles(price)) {
            responseObject.addProperty("message", "Invalid price");
        } else if (Double.parseDouble(price) <= 0) {
            responseObject.addProperty("message", "Price must be greater than 0");
        } else if (!Util.isIntegers(qty)) {
            responseObject.addProperty("message", "Invalid quantity");
        } else if (Integer.parseInt(qty) <= 0) {
            responseObject.addProperty("message", "Quantity must be greater than 0");
        } else if (part1.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image one is required");
        } else if (part2.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image two is required");
        } else if (part3.getSubmittedFileName() == null) {
            responseObject.addProperty("message", "Product image three is required");
        } else {

            Brand brand = (Brand) s.get(Brand.class, Integer.valueOf(brandId));
            if (brand == null) {
                responseObject.addProperty("message", "Please select a valid Brand Name!");
            } else {

                Model model = (Model) s.get(Model.class, Integer.parseInt(modelId));
                if (model == null) {
                    responseObject.addProperty("message", "Please select a valid Model Name!");
                } else {
                    if (model.getBrand().getId() != brand.getId()) {
                        responseObject.addProperty("message", "Please select a suitable Model!");
                    } else {
                        Storage storage = (Storage) s.get(Storage.class, Integer.valueOf(storageId));
                        if (storage == null) {
                            responseObject.addProperty("message", "Please select a valid Storage!");
                        } else {
                            Color color = (Color) s.get(Color.class, Integer.valueOf(colorId));
                            if (color == null) {
                                responseObject.addProperty("message", "Please select a valid Color!");
                            } else {
                                Quality quality = (Quality) s.get(Quality.class, Integer.valueOf(conditionId));
                                if (quality == null) {
                                    responseObject.addProperty("message", "Please select a valid Quality!");
                                } else {
//        Storage storage = (Storage) s.load(Storage.class, Integer.parseInt(storageId));
//        Model model = (Model) s.load(Model.class, Integer.parseInt(modelId));
//        Color color = (Color) s.load(Color.class, Integer.parseInt(colorId));
//        Quality quality = (Quality) s.load(Quality.class, Integer.parseInt(conditionId));
//        Status status = (Status) s.load(Status.class, 1);
//        User user = (User) request.getSession().getAttribute("user");

                                    Product p = new Product();
                                    p.setModel(model);
                                    p.setTitle(title);
                                    p.setDescription(desc);
                                    p.setStorage(storage);
                                    p.setColor(color);
                                    p.setQuality(quality);
                                    p.setPrice(Double.parseDouble(price));
                                    p.setQty(Integer.parseInt(qty));

                                    Status status = (Status) s.get(Status.class, SaveProducts.PENDING_STAUTUS_ID);
                                    p.setStatus(status);
                                    User user = (User) request.getSession().getAttribute("user");

                                    Criteria c1 = s.createCriteria(User.class);
                                    c1.add(Restrictions.eq("email", user.getEmail()));
                                    User u1 = (User) c1.uniqueResult();
                                    p.setUser(u1);
                                    p.setAdded_date(new Date());

                                    int id = (int) s.save(p);
                                    s.beginTransaction().commit();
                                    s.close();
//
                                    //image uploading
                                    String appPath = getServletContext().getRealPath("");
                                    String newPath = appPath.replace("build" + File.separator + "web", "web" + File.separator + "product-images");

                                    File productFolder = new File(newPath, String.valueOf(id));
                                    productFolder.mkdir();

                                    File file1 = new File(productFolder, "image1.png");
                                    Files.copy(part1.getInputStream(), file1.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                    File file2 = new File(productFolder, "image2.png");
                                    Files.copy(part2.getInputStream(), file2.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                    File file3 = new File(productFolder, "image3.png");
                                    Files.copy(part3.getInputStream(), file3.toPath(), StandardCopyOption.REPLACE_EXISTING);

                                    responseObject.addProperty("status", true);
                                }
                            }
                        }
                    }
                }
            }
        }

        //send Response
        Gson gson = new Gson();
        response.setContentType("application/json");
        response.getWriter().write(gson.toJson(responseObject));
        //send Response

    }
}
