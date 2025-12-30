/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package controller;

import Hibernate.Address;
import Hibernate.Cart;
import Hibernate.City;
import Hibernate.HibernateUtile;
import Hibernate.OrderItem;
import Hibernate.OrderStatus;
import Hibernate.Orders;
import Hibernate.Product;
import Hibernate.User;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import model.Mail;
import model.PayHere;
import model.Util;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author ADHIII
 */
@WebServlet(name = "CheckOut", urlPatterns = {"/CheckOut"})
public class CheckOut extends HttpServlet {

    private static final int SELECTOR_DEFAULT_VALUE = 0;
    private static final int ORDER_PENDING = 5;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Gson gson = new Gson();
        JsonObject requJsonObject = gson.fromJson(request.getReader(), JsonObject.class);

        boolean isCurrentAddress = requJsonObject.get("isCurrentAddress").getAsBoolean();
        String firstName = requJsonObject.get("firstName").getAsString();
        String lastName = requJsonObject.get("lastName").getAsString();
        String citySelect = requJsonObject.get("citySelect").getAsString();
        String lineOne = requJsonObject.get("lineOne").getAsString();
        String lineTwo = requJsonObject.get("lineTwo").getAsString();
        String postalCode = requJsonObject.get("postalCode").getAsString();
        String mobile = requJsonObject.get("mobile").getAsString();

        SessionFactory sf = HibernateUtile.getSessionFactory();
        Session s = sf.openSession();
        Transaction tr = s.beginTransaction();

        JsonObject responseObject = new JsonObject();
        responseObject.addProperty("status", false);
        User user = (User) request.getSession().getAttribute("user");

        if (user == null) {
            responseObject.addProperty("message", "Session expired! Please log in again");
        } else {
            if (isCurrentAddress) {
                Criteria c1 = s.createCriteria(Address.class);
                c1.add(Restrictions.eq("user", user));
                c1.addOrder(Order.desc("id"));
                if (c1.list().isEmpty()) {
                    responseObject.addProperty("message",
                            "You current address is not found. Please add a new address");
                } else {
                    Address address = (Address) c1.list().get(0);
                    processCheckout(s, tr, user, address, responseObject, request);
                }
            } else {
                if (firstName.isEmpty()) {
                    responseObject.addProperty("message", "First Name is required.");
                } else if (lastName.isEmpty()) {
                    responseObject.addProperty("message", "Last Name is required.");
                } else if (!Util.isIntegers(citySelect)) {
                    responseObject.addProperty("message", "Invalid city");
                } else if (Integer.parseInt(citySelect) == CheckOut.SELECTOR_DEFAULT_VALUE) {
                    responseObject.addProperty("message", "Invalid city");
                } else {
                    City city = (City) s.get(City.class, Integer.valueOf(citySelect));
                    if (city == null) {
                        responseObject.addProperty("message", "Invalid city name");
                    } else {
                        if (lineOne.isEmpty()) {
                            responseObject.addProperty("message", "Address line one is required");
                        } else if (lineTwo.isEmpty()) {
                            responseObject.addProperty("message", "Address line two is required");
                        } else if (postalCode.isEmpty()) {
                            responseObject.addProperty("message", "Your postal code is required");
                        } else if (!Util.isCodeValid(postalCode)) {
                            responseObject.addProperty("message", "Invalid postal code number");
                        } else if (mobile.isEmpty()) {
                            responseObject.addProperty("message", "Mobile number is required");
                        } else if (!Util.isMobileValid(mobile)) {
                            responseObject.addProperty("message", "Invalid mobile number");
                        } else {
                            Address address = new Address();
                            address.setFirst_name(firstName);
                            address.setLast_name(lastName);
                            address.setLine_1(lineOne);
                            address.setLine_2(lineTwo);
                            address.setCity(city);
                            address.setPostalCode(postalCode);
                            address.setMobile(mobile);
                            address.setUser(user);
                            s.save(address);

                            processCheckout(s, tr, user, address, responseObject, request);
                        }
                    }
                }
            }
        }

        response.setContentType("application/json");
        String toJson = gson.toJson(responseObject);
        response.getWriter().write(toJson);
    }

    private void processCheckout(Session s,
            Transaction tr,
            User user,
            Address address,
            JsonObject responseObject,
            HttpServletRequest request) {

        try {
            final String Verificationcode = Util.generateOrderId();
            Orders orders = new Orders();
            orders.setId(Verificationcode);
            orders.setAddress(address);
            orders.setAdded_date(new Date());
            orders.setUser(user);

            String orderId =  (String) s.save(orders);

            Criteria c1 = s.createCriteria(Cart.class);
            c1.add(Restrictions.eq("user", user));
            List<Cart> cartList = c1.list();

            OrderStatus orderStatus = (OrderStatus) s.get(OrderStatus.class, CheckOut.ORDER_PENDING);

            double amount = 0;
            String items = "";

            for (Cart cart : cartList) {
                amount += cart.getQty() * cart.getProduct().getPrice();

                OrderItem orderItem = new OrderItem();

                items += cart.getProduct().getTitle() + " x " + cart.getQty() + ", ";

                Product product = cart.getProduct();
                orderItem.setOrderStatus(orderStatus);
                orderItem.setOrder(orders);
                orderItem.setProduct(product);
                orderItem.setQty(cart.getQty());

                s.save(orderItem);

                //update product qty
                product.setQty(product.getQty() - cart.getQty());
                s.update(product);

                // delete cart item
                s.delete(cart);
                //send mail
                User sessionUser = (User) request.getSession().getAttribute("user");

                // Build product rows for the email
                StringBuilder productRows = new StringBuilder();

                for (Cart cart1 : cartList) {
                    Product product1 = cart1.getProduct();
                    String productTitle = product1.getTitle();

                    productRows.append(
                            "<tr>\n"
                            + "  <td>\n"
                            + "    <strong>" + productTitle + "</strong><br>\n"
                            + "    <span style=\"font-size: 13px; color: gray;\">128GB - Space Black</span>\n"
                            + "  </td>\n"
                            + "  <td>$999.00</td>\n"
                            + "  <td>1</td>\n"
                            + "  <td>$999.00</td>\n"
                            + "</tr>\n"
                    );
                }

                // HTML content with placeholder for product rows
                String htmlContent = "<!DOCTYPE html>\n"
                        + "<html lang=\"en\">\n"
                        + "<head>\n"
                        + "  <meta charset=\"UTF-8\">\n"
                        + "  <title>iHeaven Invoice</title>\n"
                        + "  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n"
                        + "  <style>\n"
                        + // ... keep all your CSS as is ...
                        "    body {\n"
                        + "      margin: 0;\n"
                        + "      padding: 0;\n"
                        + "      background-color: #f5f5f7;\n"
                        + "      font-family: Arial, sans-serif;\n"
                        + "      color: #333;\n"
                        + "    }\n"
                        + // ... (keep rest of CSS unchanged) ...
                        "  </style>\n"
                        + "</head>\n"
                        + "<body>\n"
                        + "  <div class=\"container\">\n"
                        + "    <div class=\"header\">\n"
                        + "      <div class=\"header-top\">\n"
                        + "        <div>\n"
                        + "          <h1>iHeaven</h1>\n"
                        + "          <p>Your Premium Apple Store</p>\n"
                        + "        </div>\n"
                        + "        <div style=\"text-align: right;\">\n"
                        + "          <h2 style=\"margin: 0;\">INVOICE</h2>\n"
                        + "        </div>\n"
                        + "      </div>\n"
                        + "    </div>\n"
                        + "    <div class=\"summary\">\n"
                        + "      <div>\n"
                        + "        <p class=\"section-title\">Order Summary</p>\n"
                        + "        <p>Order #: ORD-2023-0567</p>\n"
                        + "        <p>Date: May 15, 2023</p>\n"
                        + "      </div>\n"
                        + "      <div style=\"text-align: right;\">\n"
                        + "        <p class=\"section-title\">Amount Due</p>\n"
                        + "        <p style=\"font-size: 24px; font-weight: bold; color: #0071e3;\">$1,899.00</p>\n"
                        + "      </div>\n"
                        + "    </div>\n"
                        + "    <div class=\"billing\">\n"
                        + "      <div style=\"display: flex; flex-wrap: wrap; gap: 24px;\">\n"
                        + "        <div style=\"flex: 1;\">\n"
                        + "          <p class=\"section-title\">Bill To</p>\n"
                        + "          <div class=\"info-box\">\n"
                        + "            <p><strong>John Appleseed</strong></p>\n"
                        + "            <p>" + sessionUser.getEmail() + "</p>\n"
                        + "            <p>123 Apple Street</p>\n"
                        + "            <p>Cupertino, CA 95014</p>\n"
                        + "            <p>United States</p>\n"
                        + "          </div>\n"
                        + "        </div>\n"
                        + "        <div style=\"flex: 1;\">\n"
                        + "          <p class=\"section-title\">Payment Method</p>\n"
                        + "          <div class=\"info-box\">\n"
                        + "            <p><strong>Visa</strong></p>\n"
                        + "            <p>•••• •••• ••••</p>\n"
                        + "            <p style=\"margin-top: 8px;\">Payment will be processed automatically</p>\n"
                        + "          </div>\n"
                        + "        </div>\n"
                        + "      </div>\n"
                        + "    </div>\n"
                        + "    <div class=\"products\">\n"
                        + "      <p class=\"section-title\">Products Ordered</p>\n"
                        + "      <table>\n"
                        + "        <thead>\n"
                        + "          <tr>\n"
                        + "            <th>Product</th>\n"
                        + "            <th>Price</th>\n"
                        + "            <th>Qty</th>\n"
                        + "            <th>Total</th>\n"
                        + "          </tr>\n"
                        + "        </thead>\n"
                        + "        <tbody>\n"
                        + productRows.toString()
                        + "        </tbody>\n"
                        + "      </table>\n"
                        + "    </div>\n"
                        + "    <div class=\"totals\">\n"
                        + "      <div class=\"totals-box\" style=\"width: 100%;\">\n"
                        + "        <div><span>Subtotal</span><span>$1,977.00</span></div>\n"
                        + "        <div><span>Shipping</span><span>FREE</span></div>\n"
                        + "        <div class=\"divider\"></div>\n"
                        + "        <div><span class=\"total\">Total</span><span class=\"total\">$1,899.00</span></div>\n"
                        + "      </div>\n"
                        + "    </div>\n"
                        + "    <div class=\"footer\">\n"
                        + "      <div class=\"footer-bottom\">\n"
                        + "        <div>\n"
                        + "          <h3>iHeaven</h3>\n"
                        + "          <p style=\"font-size: 13px; color: #ccc;\">The Ultimate Apple Experience</p>\n"
                        + "        </div>\n"
                        + "        <div>\n"
                        + "          <a href=\"#\">🌐 www.iheaven.com</a>\n"
                        + "          <a href=\"#\">✉️ support@iheaven.com</a>\n"
                        + "          <a href=\"#\">📞 +1 (800) APPLE-00</a>\n"
                        + "        </div>\n"
                        + "      </div>\n"
                        + "      <div class=\"divider\"></div>\n"
                        + "      <p class=\"center-text\">Thank you for shopping with iHeaven. Your Apple products will be shipped within 1-2 business days.</p>\n"
                        + "    </div>\n"
                        + "  </div>\n"
                        + "</body>\n"
                        + "</html>";

                // Send email in background
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Mail.sendMail(sessionUser.getEmail(), "iHeaven Log In Verification Code", htmlContent);
                    }
                }).start();

                responseObject.addProperty("status", true);
                responseObject.addProperty("message", "Registration Success.Please check youer Email for the Verification Code. ");
            }

            tr.commit();

            //PayHere process
            String merahantID = "1226875";
            String merchantSecret = "MTU0ODI4Mjk5MjExMTY4MzkyNjQzOTAyOTI3ODUyMzgyMTgxMzc0OQ==";
            String orderID = "#000" + orderId;
            String currency = "LKR";
            String formattedAmount = new DecimalFormat("0.00").format(amount);
            String merchantSecretMD5 = PayHere.generateMD5(merchantSecret);

            String hash = PayHere.generateMD5(merahantID + orderID + formattedAmount + currency + merchantSecretMD5);

            JsonObject payHereJson = new JsonObject();
            payHereJson.addProperty("sandbox", true);
            payHereJson.addProperty("merchant_id", merahantID);

            payHereJson.addProperty("return_url", "");
            payHereJson.addProperty("cancel_url", "");
            payHereJson.addProperty("notify_url", "https://bfab0a212cfd.ngrok-free.app/iHeaven/VerifyPayments");

            payHereJson.addProperty("order_id", orderID);
            payHereJson.addProperty("items", items);
            payHereJson.addProperty("amount", formattedAmount);
            payHereJson.addProperty("currency", currency);
            payHereJson.addProperty("hash", hash);

            payHereJson.addProperty("first_name", user.getFname());
            payHereJson.addProperty("last_name", user.getLname());
            payHereJson.addProperty("email", user.getEmail());

            payHereJson.addProperty("phone", address.getMobile());
            payHereJson.addProperty("address", address.getLine_1() + ", " + address.getLine_2());
            payHereJson.addProperty("city", address.getCity().getName());
            payHereJson.addProperty("country", "Sri Lanka");

            responseObject.addProperty("status", true);
            responseObject.addProperty("message", "Cechkout completed");
            responseObject.add("payhereJson", new Gson().toJsonTree(payHereJson));

        } catch (Exception e) {
            tr.rollback();
        }
    }

}
