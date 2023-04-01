package parse;

import java.sql.*;
import java.util.HashMap;
import java.util.HashSet;


public class Main {

	   public static void run(){
//		   int count = 1;

		   try {
			   Statement slct;
			   String query;
			   ResultSet res;
			   Class.forName("com.mysql.jdbc.Driver").newInstance();


			   // Create connection
			   Connection connection = DriverManager.getConnection("jdbc:mysql:///moviedb?autoReconnect=true&useSSL=false&allowPublicKeyRetrieval=true",
					   "mytestuser", "122b");
			   slct = connection.createStatement();

//			   query = "insert into genres values(24,\"" +"XML"+ "\"" + ")";
////						System.out.println(query);
//			   slct.executeUpdate(query);

			   parseMain mainParser = new parseMain();


			   for (int i = 0; i < mainParser.get_movies().size(); i++) {


				   Movie movie = (Movie) mainParser.get_movies().get(i);
				   query = "select * from movies where id=\""+movie.getId()+"\"";
				   res = slct.executeQuery(query);

//				   System.out.println(query);
//					UNCOMMENT LATER

				   if (!res.next()) {
    					query = "insert into movies(id,title,year,director) values(\"" + movie.getId() + "\",\"" + movie.getTitle()
    							+ "\"," + movie.getYear() + ",\"" + movie.getDirector() + "\")";
						slct.executeUpdate(query);
//						System.out.println(query);
						query = "insert into ratings(movieId,rating,numVotes) values(\"" + movie.getId() + "\",0,0" + ")";
//						System.out.println(query);
					    slct.executeUpdate(query);
					    query = "insert into genres_in_movies values(24,\"" + movie.getId() + "\"" + ")";
//						System.out.println(query);
					    slct.executeUpdate(query);
				   }
//				   else{
//					   System.out.println("Current ID exists in the database! Skipped.");
//				   }
			   }



			   parseActor actorParser = new parseActor();
			   for (int i = 0; i < actorParser.get_stars().size(); i++) {
				   Star star = (Star) actorParser.get_stars().get(i);
				   query = "select * from stars where name=\"" + star.getName() + "\"" ;
				   res=slct.executeQuery(query);

				   //					UNCOMMENT LATER

    				if (!res.next()) {
						query = "select id from stars order by id desc limit 1;";
						PreparedStatement statement = connection.prepareStatement(query);
						ResultSet rs = statement.executeQuery();

						String new_id = "";
						while (rs.next()) {
							new_id = rs.getString("id");
						}
						rs.close();
						statement.close();

						String newid="nm"+  String.valueOf(Integer.parseInt(new_id.substring(2))+1);



    					query = "insert into stars(id,name,birthYear) values(\"" + newid + "\",\"" + star.getName() + "\","
    							+ star.getBirthYear() + ")";
//						System.out.println(query);

    					slct.executeUpdate(query);
    				}

//					else{
//						System.out.println("Current ID exists in the database! Skipped.");
//					}
			   }



			   parseCat catParser = new parseCat();
			   System.out.println("Constructing Dictionary");
//			   PREPARE FOR STAR NAME HASHMAP
			   query = "select stars.name, stars.id from stars;";
			   res = slct.executeQuery(query);
			   HashMap<String, String> star_names = new HashMap<>();
			   while (res.next()) {
				   star_names.put(res.getString("name"), res.getString("id"));
			   }



			   query="SET FOREIGN_KEY_CHECKS=0";
			   res = slct.executeQuery(query);

			   for (int i = 0; i < catParser.get_moviestars().size(); i++) {


//				   Boolean error = false;
				   MovieStar star = (MovieStar) catParser.get_moviestars().get(i);
//				   query = "select * from stars where name=\"" + star.getName() + "\";";
//
////				   System.out.println(query);
//				   try {
//					   res = slct.executeQuery(query);
//				   }catch(Exception e){
//					   error = true;
//				   }
				   String temp_star = star.getName();
				   if (!star_names.containsKey(temp_star)){
//					   System.out.println("Star not found.");
					   continue;
				   }

				   String current_id = star_names.get(temp_star);

				   query = "select * from stars_in_movies where starId=\"" + current_id + "\" and movieId=\"" + star.getMovieId()+"\"";
				   res = slct.executeQuery(query);

				   //					UNCOMMENT LATER

    				if (!res.next()) {
    					query = "insert into stars_in_movies(starId,movieId) values(\"" + current_id + "\",\""
    							+ star.getMovieId() + "\")";
    					slct.executeUpdate(query);
//						System.out.println(query);
    				}

//					else{
//						System.out.println("Current ID exists in the database! Skipped.");
//					}
			   }

			   query="SET FOREIGN_KEY_CHECKS=1";
			   res = slct.executeQuery(query);

			   System.out.println("All File Processed.");
		   }catch(Exception e) {
			   e.printStackTrace();
		   }
	   }


       public static void main(String[] args) {
    	   run();
//		   System.out.println("insert into ratings(movieId,rating,numVotes) values(\"" + ",0,0" + ")");
	}
}
