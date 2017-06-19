package app.dto;

public class TwoStrings {

    public String string1;
    public String string2;

    public TwoStrings(){}

    public TwoStrings(String string1, String string2){
        this.string1 = string1;
        this.string2 = string2;
    }

    public String getString1() {
        return string1;
    }

    public void setString1(String string1) {
        this.string1 = string1;
    }

    public String getString2() {
        return string2;
    }

    public void setString2(String string2) {
        this.string2 = string2;
    }
}
