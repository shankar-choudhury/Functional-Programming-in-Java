import java.util.Objects;

public record ReturnMultipleValues(String name, int houseNumber,
                                   String streetName, String cityName,
                                   String stateName, int zipCode, long phoneNumber) {
    /**
     * To return multiple values at once, a class can be created that holds multiple values
     * within its fields. It can be programmed to return all the values (fields) that it
     * contains such as calling the overridden toString() method.
     */

    public ReturnMultipleValues {
        Objects.requireNonNull(name);
        Objects.requireNonNull(houseNumber);
        Objects.requireNonNull(streetName);
        Objects.requireNonNull(cityName);
        Objects.requireNonNull(stateName);
        Objects.requireNonNull(zipCode);
        Objects.requireNonNull(phoneNumber);
    }

    @Override
    public String toString() {
        return "Name: " + name +"\n" +
                "Address: " + houseNumber + " " + streetName + ", " +
                cityName + " " + stateName + " " + zipCode + "\n" +
                "Phone number: " + phoneNumber + "\n";
    }

    public static void main(String[] args) {
        ReturnMultipleValues r = new ReturnMultipleValues("Shankar",
                2737, "Hampshire Road", "Cleveland Heights",
                "Ohio", 44106, 6036677069L);
        System.out.println(r.toString());
    }

}
