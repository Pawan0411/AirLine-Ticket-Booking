package pp.model;

public class Passenger {
   private int luggage;
   private String gender;
   private int size;
    private String key;

    public Passenger(int luggage, String gender) {
        this.gender = gender;
        this.luggage = luggage;
        size = 0;
    }

    public Passenger (){

    }
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }


    public int getLuggage() {
        return luggage;
    }

    public void setLuggage(int luggage) {
        this.luggage = luggage;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public int size(){
        return size;
    }
}
