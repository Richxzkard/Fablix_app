package parse;

import java.util.*;

import javax.xml.parsers.*;

import org.w3c.dom.*;

public class parseActor {

    List stars;
    Document doc;


    public parseActor(){
        // Create a list to hold the star objects
        stars = new ArrayList();
        parse_file("src/parse/actors63.xml");
        parse_document();

    }


    private void parse_file(String file){
        DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder db = fac.newDocumentBuilder();
            doc = db.parse(file);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    private void parse_document(){

        Element elements = doc.getDocumentElement();
        NodeList actors = elements.getElementsByTagName("actor");

        if(actors.getLength() > 0) {
            for(int i = 0 ; i < actors.getLength();i++) {
                Element actorEL = (Element)actors.item(i);

                Star s = return_star(actorEL);
                if (s != null) {
                    stars.add(s);
                }
            }
        }
    }




    private Star return_star(Element elements) {
        Star star = null;
        String id = null;
        String name = null;
        int year = 0;

        try {
            name = get_value(elements, "stagename");
            year = Integer.parseInt(get_value(elements,"dob"));
            star = new Star(name, year);
        }
        catch(Exception e) {
            System.err.println("Error found for star: <" + name + ">, enter a valid one.");
        }

        return star;
    }



    private String get_value(Element elements, String tag) {
        String res = null;
        NodeList nodel = elements.getElementsByTagName(tag);

        try {
            if(nodel.getLength() > 0) {
                Element el = (Element)nodel.item(0);
                res = el.getFirstChild().getNodeValue();
            }
        }
        catch(Exception e) {
            System.err.println("There is no value for tag: <" + tag + ">, enter a new one.");
            res = null;
        }

        return res;
    }



    public List get_stars() {
        return stars;
    }

    public static void printstars(parseActor parser){
        System.out.println(parser.get_stars().size());
    }

    public static void main(String[] args){
        parseActor parser = new parseActor();
        printstars(parser);

    }
}