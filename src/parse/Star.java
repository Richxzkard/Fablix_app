package parse;

public class Star {
    private String id;
    private String name;
    private int birthYear;

    public Star(String name, int birthYear) {
        this.name = name;
        this.birthYear = birthYear;
    }


    public String getName() {
        return name;
    }

    public int getBirthYear() {
        return birthYear;
    }


    public void setName(String name) {
        this.name = name;
    }



    @Override
    public String toString() {
        return "Star{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", birthYear=" + birthYear +
                '}';
    }
}
