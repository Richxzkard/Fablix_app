package parse;

import java.util.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;

public class parseMain {

    List movies;
    List genres;
    Document doc;


    public parseMain(){
        // Create a list to hold the employee objects
        movies = new ArrayList();
        genres = new ArrayList();
        parse_file("src/parse/mains243.xml");
        parse_document();
    }


    private void parse_file(String file){

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = factory.newDocumentBuilder();
            doc = db.parse(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    private Movie get_movie(Element elements, String director) {
        Movie movie = null;

        String id = get_value(elements,"fid");
        String title = get_value(elements,"t");

        int year = 0;
        String tempYear = null;

        if (true){
            try {
                tempYear = get_value(elements,"year").trim();
                year = Integer.parseInt(tempYear);
            }
            catch(Exception e) {
                year = -1;
                System.err.println("Invalid year: <" + tempYear + "> for movie, please enter again");
            }

            if (year != -1 && id != null && title != null ||  director != null) {
                movie = new Movie(id, title, year, director);

                get_genres(elements, id);
            }

        }


        return movie;
    }


    private void parse_document(){
        Element elements = doc.getDocumentElement();
        NodeList films = elements.getElementsByTagName("directorfilms");

        if (1==1){
            if(films.getLength() > 0) {
                for(int i = 0 ; i < films.getLength();i++) {

                    Element dfelmt = (Element)films.item(i);
                    Element directorelement = (Element)dfelmt.getElementsByTagName("director").item(0);

                    String director = get_value(directorelement, "dirname");
                    NodeList filmslist = dfelmt.getElementsByTagName("film");

                    if(filmslist.getLength() > 0) {
                        for (int j = 0; j < filmslist.getLength(); j++) {
                            Element filmelement = (Element)filmslist.item(j);

                            Movie movie = get_movie(filmelement, director);

                            if (movie != null) {
                                movies.add(movie);
                            }
                        }
                    }
                }
            }
        }


    }


    private Genre get_genres(Element elements, String movie) {
        Genre genreInMovie = null;

        NodeList catNL = elements.getElementsByTagName("cat");

        if(catNL.getLength() > 0) {
            for (int j = 0; j < catNL.getLength(); j++) {
                Element catEL = (Element)catNL.item(j);

                String genreId = null;

                if (catEL.hasChildNodes()) {
                    genreId = catEL.getChildNodes().item(0).getNodeValue();
                }

                if (genreId != null) {
                    genreInMovie = new Genre(genreId, movie);
                    genres.add(genreInMovie);
                }
            }
        }

        return genreInMovie;
    }




    private String get_value(Element elements, String tag) {
        String textVal = null;
        NodeList nodelist = elements.getElementsByTagName(tag);

        try {
            if(nodelist.getLength() > 0) {
                Element el = (Element)nodelist.item(0);
                textVal = el.getFirstChild().getNodeValue();
            }
        }
        catch(Exception e) {
            System.err.println("There's no value for tag <" + tag + ">, please enter a valid one.");
            textVal = null;
        }
        return textVal;
    }




    public List get_movies() {
        return movies;
    }

    public static void get_movie_genres(parseMain parser) {
        System.out.println("Number of movies: " + parser.get_movies().size());
        System.out.println("Number of genre in movies: " + parser.genres.size());
    }

    public static void main(String[] args){
        parseMain parser = new parseMain();
        get_movie_genres(parser);
    }
}