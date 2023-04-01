import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

@WebServlet(name = "SearchServlet", urlPatterns = "/api/search")
public class SearchServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Create a dataSource which registered in web.
    private DataSource dataSource;

    public void init(ServletConfig config) {
        try {
            dataSource = (DataSource) new InitialContext().lookup("java:comp/env/jdbc/slave");
        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        String title = request.getParameter("movie_title");
        String year = request.getParameter("movie_year");
        String director = request.getParameter("movie_director");
        String star = request.getParameter("movie_star");


        System.out.println("title: " + title);
        System.out.println("year: " + year);
        System.out.println("director: " + director);
        System.out.println("star: " + star);

        response.setContentType("application/json"); // Response mime type

        // Output stream to STDOUT
        PrintWriter out = response.getWriter();

        // Get a connection from dataSource and let resource manager close the connection after usage.
        try (out; Connection conn = dataSource.getConnection()) {
            if (conn == null) {
                out.println("conn is null.");
            } else {
                StringBuilder sb = new StringBuilder(" where true ");
                int counter = 0;
                String[] array = new String[4];

                if (!title.isEmpty()) {
                    array[counter] = "%" + title + "%";
                    title = " and m.title like ? ";
                    sb.append(title);
                    counter++;
                }
                if (!year.isEmpty()) {
                    array[counter] = new String(year);
                    year = " and m.year = ? ";
                    sb.append(year);
                    counter++;
                }
                if (!director.isEmpty()) {
                    array[counter] = "%" + director + "%";
                    director = " and m.director like ? ";
                    sb.append(director);
                    counter++;
                }

                String s = sb.toString();

                String query = "select l.id,l.title,l.year,l.director,substring_index(l.genres, ',', 3)as genres,substring_index(l.genres_id, ',', 3)as genres_id,l.rating,substring_index(l.star_name, ',', 3)as star_name,substring_index(l.star_id, ',', 3)as star_id from\n" +
                        "(select l.id,l.title,l.year,l.director,l.genres,l.genres_id,l.rating,group_concat(s.name)as star_name,group_concat(s.id)as star_id from\n" +
                        "(select l.id,l.title,l.year,l.director,group_concat(g.name order by g.name asc) as genres,group_concat(g.id order by g.name asc) as genres_id,max(l.rating)as rating from\n" +
                        "(select m.*,r.rating from movies as m inner join ratings as r\n" +
                        "on m.id=r.movieId " + s + " order by r.rating desc ,m.title asc limit 20" +
                        " ) as l,\n" +
                        "genres as g, genres_in_movies as gim where l.id=gim.movieId and gim.genreId=g.id\n" +
                        "group by l.id order by max(l.rating) desc, l.title asc ) as l,\n" +
                        "stars as s, stars_in_movies as sim where l.id=sim.movieId and sim.starId=s.id\n" +
                        "group by l.id order by max(l.rating) desc, l.title asc ) as l";

                if (!star.isEmpty()) {
                    array[counter] = "%" + star + "%";
                    query = query + " where l.star_name like ?;";
                    counter++;
                } else {
                    query = query + ";";
                }

                System.out.println(query);
                PreparedStatement statement = conn.prepareStatement(query);

                for (int i = 0; i < counter; i++) {
                    statement.setString(i + 1, array[i]);
                }

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

                // Log to localhost log
                request.getServletContext().log("getting " + jsonArray.size() + " results");

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

            // Set response status to 500 (Internal Server Error)
            response.setStatus(500);
        } finally {
            out.close();
        }

        // Always remember to close db connection after usage. Here it's done by try-with-resources

    }
}
