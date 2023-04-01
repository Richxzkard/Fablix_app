import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * This IndexServlet is declared in the web annotation below,
 * which is mapped to the URL pattern /api/index.
 */
@WebServlet(name = "CartServlet", urlPatterns = "/api/cart")
public class CartServlet extends HttpServlet {

    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/master");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }
    /**
     * handles GET requests to store session information
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        HttpSession session = request.getSession();
        String sessionId = session.getId();
        long lastAccessTime = session.getLastAccessedTime();

        JsonObject responseJsonObject = new JsonObject();
        responseJsonObject.addProperty("sessionID", sessionId);
        responseJsonObject.addProperty("lastAccessTime", new Date(lastAccessTime).toString());

        ArrayList<HashMap> previousItems = (ArrayList<HashMap>) session.getAttribute("previousItems");
        if (previousItems == null) {
            previousItems = new ArrayList<>();
        }
        // Log to localhost log
        request.getServletContext().log("getting " + previousItems.size() + " items");
        JsonArray previousItemsJsonArray = new JsonArray();

        ArrayList<String> stringitems = new ArrayList<>() ;
        for (int i = 0; i < previousItems.size(); i++) {
            HashMap<String,String> map=previousItems.get(i);
            stringitems.add(map.get("id")+","+map.get("title")+","+map.get("price")+","+map.get("number"));
        }
        stringitems.forEach(previousItemsJsonArray::add);
        responseJsonObject.add("previousItems", previousItemsJsonArray);

        // write all the data into the jsonObject
        response.getWriter().write(responseJsonObject.toString());
    }

    /**
     * handles POST requests to add and show the item list information
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String item = request.getParameter("item");
        String action = request.getParameter("action");
        System.out.println(item);
        HttpSession session = request.getSession();
        PrintWriter out = response.getWriter();
        try (out; Connection conn = dataSource.getConnection()) {
            if (conn == null) {
                out.println("conn is null.");
            } else {
                // Construct a query with parameter represented by "?"
                String query = "select * from movies where movies.id=?;";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, item);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                // Iterate through each row of rs
                while (rs.next()) {
                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year = rs.getString("year");

                    ArrayList<HashMap> previousItems = (ArrayList<HashMap>) session.getAttribute("previousItems");
                    if (previousItems == null) {
                        previousItems = new ArrayList<>();
                        Map<String, String> map = new HashMap<String, String>();
                        String price = String.valueOf((int) (Math.random() * Integer.parseInt(movie_year)) + 1);
                        map.put("id", movie_id);
                        map.put("title", movie_title);
                        map.put("price", price);
                        map.put("number", "1");
                        previousItems.add((HashMap) map);
                        session.setAttribute("previousItems", previousItems);
                    } else {
                        // prevent corrupted states through sharing under multi-threads
                        // will only be executed by one thread at a time
                        synchronized (previousItems) {
//                        previousItems.add(item);
                            boolean inside = false;
                            for (int i = 0; i < previousItems.size(); i++) {
                                HashMap<String, String> map = previousItems.get(i);
                                String id = map.get("id");
                                if (id.equals(movie_id)) {
                                    inside = true;
                                    String number;
                                    if (action == null) {
                                        number = String.valueOf(Integer.parseInt(map.get("number")) + 1);
                                    } else {
                                        if (action.equals("delete")) {
                                            number = "0";
                                        } else if (action.equals("minus")) {
                                            number = String.valueOf(Integer.parseInt(map.get("number")) - 1);
                                        } else {
                                            number = String.valueOf(Integer.parseInt(map.get("number")) + 1);
                                        }
                                    }
                                    map.put("number", number);
                                    previousItems.set(i, map);
                                    session.setAttribute("previousItems", previousItems);
                                    break;
                                }
                            }
                            if (!inside) {
                                Map<String, String> map = new HashMap<String, String>();
                                String price = String.valueOf((int) (Math.random() * Integer.parseInt(movie_year)) + 1);
                                map.put("id", movie_id);
                                map.put("title", movie_title);
                                map.put("price", price);
                                map.put("number", "1");
                                previousItems.add((HashMap) map);
                                session.setAttribute("previousItems", previousItems);
                                session.setAttribute("previousItems", previousItems);
                            }
                        }
                    }

                    JsonObject responseJsonObject = new JsonObject();

                    JsonArray previousItemsJsonArray = new JsonArray();
                    ArrayList<String> stringitems = new ArrayList<>();
                    for (int i = 0; i < previousItems.size(); i++) {
                        HashMap<String, String> map = previousItems.get(i);
                        stringitems.add(map.get("id") + "," + map.get("title") + "," + map.get("price") + "," + map.get("number"));
                    }
                    stringitems.forEach(previousItemsJsonArray::add);

                    responseJsonObject.add("previousItems", previousItemsJsonArray);

                    response.getWriter().write(responseJsonObject.toString());
                }

                rs.close();
                statement.close();

                // Write JSON string to output
                out.write(jsonArray.toString());
                // Set response status to 200 (OK)
                response.setStatus(200);
            }
        } catch (Exception e) {
            // Write error message JSON object to output
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("errorMessage", e.getMessage());
            out.write(jsonObject.toString());

            // Log error to localhost log
            request.getServletContext().log("Error:", e);
            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        }
        finally {
            out.close();
        }
        // get the previous items in a ArrayList

    }
}
