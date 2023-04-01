package parse;

import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;


public class parseCat {

    List stars;
    Document doc;


    public parseCat(){
        stars = new ArrayList();
        parse_file("src/parse/casts124.xml");

        parse_document();
    }


    private void parse_file(String file){
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            doc = db.parse(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }



    private void parse_document(){
        Element docEle = doc.getDocumentElement();

        NodeList movielist = docEle.getElementsByTagName("m");

        if(movielist.getLength() > 0) {
            for(int i = 0 ; i < movielist.getLength();i++) {
                Element mEL = (Element)movielist.item(i);

                MovieStar starInMovie = get_star(mEL);

                if (starInMovie != null) {
                    stars.add(starInMovie);
                }
            }
        }
    }



    private MovieStar get_star(Element elements) {
        MovieStar starInMovie = null;
        String name = null;
        String movieId = null;

        try {
            name = get_value(elements, "a");
            movieId = get_value(elements, "f");

            starInMovie = new MovieStar(movieId, name);
        }
        catch(Exception e) {
            System.err.println(e);
        }

        return starInMovie;
    }


    private String get_value(Element elements, String tag) {
        String textVal = null;
        NodeList nl = elements.getElementsByTagName(tag);

        try {
            if(nl.getLength() > 0) {
                Element el = (Element)nl.item(0);
                textVal = el.getFirstChild().getNodeValue();
            }
        }
        catch(Exception e) {
            System.err.println("There's no value for tag: <" + tag + ">, please enter a valid one.");
        }

        return textVal;
    }

    public List get_moviestars() {
        return stars;
    }

    public static void printSize(parseCat parser){

        System.out.println(parser.get_moviestars().size());
    }

    public static void main(String[] args){
        parseCat parser = new parseCat();
        printSize(parser);
    }
}