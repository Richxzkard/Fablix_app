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

// Declaring a WebServlet called SingleStarServlet, which maps to url "/api/single-star"
@WebServlet(name = "SingleMovieServlet", urlPatterns = "/api/single-movie")
public class SingleMovieServlet extends HttpServlet {
    private static final long serialVersionUID = 2L;

    // Create a dataSource which registered in web.xml
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slave");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
     * response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json"); // Response mime type

        // Retrieve parameter id from url request.
        String id = request.getParameter("id");

        // The log message can be found in localhost log
        request.getServletContext().log("getting id: " + id);

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {
            if (conn == null) {
                out.println("conn is null.");
            } else {

                // Construct a query with parameter represented by "?"
                String query = "select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,l.star_name as star_name,l.star_id as star_id from\n" +
                        "(select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,group_concat(s.name)as star_name,group_concat(s.id)as star_id from\n" +
                        "(select l.id,l.title,l.year,l.director,group_concat(g.name order by g.name asc) as genres,group_concat(g.id order by g.name asc) as genres_id,max(l.rating)as rating from\n" +
                        "(select m.*,r.rating from movies as m inner join ratings as r\n" +
                        "on m.id=r.movieId where m.id=? order by r.rating desc limit 20) as l,\n" +
                        "genres as g, genres_in_movies as gim where l.id=gim.movieId and gim.genreId=g.id\n" +
                        "group by l.id order by max(l.rating) desc ) as l,\n" +
                        "stars as s, stars_in_movies as sim where l.id=sim.movieId and sim.starId=s.id\n" +
                        "group by l.id order by max(l.rating) desc) as l;";

                // Declare our statement
                PreparedStatement statement = conn.prepareStatement(query);

                // Set the parameter represented by "?" in the query to the id we get from url,
                // num 1 indicates the first "?" in the query
                statement.setString(1, id);

                // Perform the query
                ResultSet rs = statement.executeQuery();

                JsonArray jsonArray = new JsonArray();

                // Iterate through each row of rs
                while (rs.next()) {
                    String movie_id = rs.getString("id");
                    String movie_title = rs.getString("title");
                    String movie_year = rs.getString("year");
                    String movie_director = rs.getString("director");
                    String movie_genre = rs.getString("genres");
                    String movie_genre_id = rs.getString("genres_id");
                    String movie_rating = rs.getString("rating");
                    String movie_star_name = rs.getString("star_name");
                    String movie_star_id = rs.getString("star_id");

                    String[] id_list = movie_star_id.split(",");


                    String query_stars = "select group_concat(l.id order by l.c desc,l.name asc) as star_id,group_concat(l.name order by l.c desc,l.name asc) as star_name from(\n" +
                            "select * from(\n";

                    String query1 = "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                            "                where sim.starId=s.id and sim.movieId=m.id and s.id=?\n" +
                            "union\n";

                    String query2 =
                            "select s.id,s.name,count(*)as c from stars_in_movies as sim, movies as m, stars as s\n" +
                                    "                where sim.starId=s.id and sim.movieId=m.id and s.id=?)as l order by c desc) as l;";

                    for (int i = 0; i < id_list.length - 1; i++) {
                        query_stars += query1;
                    }
                    query_stars += query2;

                    PreparedStatement statement_stars = conn.prepareStatement(query_stars);

                    for (int i = 0; i < id_list.length; i++) {
                        statement_stars.setString(i + 1, id_list[i]);
                    }

                    ResultSet rs_stars = statement_stars.executeQuery();
                    while (rs_stars.next()) {
                        movie_star_name = rs_stars.getString("star_name");
                        movie_star_id = rs_stars.getString("star_id");
                    }


                    // Create a JsonObject based on the data we retrieve from rs
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("movie_id", movie_id);
                    jsonObject.addProperty("movie_title", movie_title);
                    jsonObject.addProperty("movie_year", movie_year);
                    jsonObject.addProperty("movie_director", movie_director);
                    jsonObject.addProperty("movie_genre", movie_genre);
                    jsonObject.addProperty("movie_genre_id", movie_genre_id);
                    jsonObject.addProperty("movie_rating", movie_rating);
                    jsonObject.addProperty("movie_star_name", movie_star_name);
                    jsonObject.addProperty("movie_star_id", movie_star_id);
                    jsonArray.add(jsonObject);
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
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }

}
