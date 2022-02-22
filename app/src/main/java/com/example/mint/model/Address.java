package com.example.mint.model;

/**
 * Describe an address with a name and two coordinates (latitude and longitude)
 */
public class Address {
    private String locationName;
    private Coordinates coordinates;

    /**
     * Constructor without parameters
     */
    public Address() {
        this.locationName = null;
        this.coordinates = new Coordinates();
    }

    /**
     * Constructor with 2 parameters
     *
     * @param locationName String
     * @param coordinates  Coordinates
     */
    public Address(String locationName, Coordinates coordinates) {
        this.locationName = locationName;
        this.coordinates = coordinates;
    }

    /**
     * Constructor with parameter locationName
     *
     * @param locationName String
     */
    public Address(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Constructor with parameters coordinates
     *
     * @param coordinates Coordinates
     */
    public Address(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Access location name of the Address
     *
     * @return String locationName
     */
    public String getLocationName() {
        return locationName;
    }

    /**
     * Set the locationName of the Address
     *
     * @param locationName String
     */
    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    /**
     * Access the coordinates of the Address
     *
     * @return Coordinates coordinates
     */
    public Coordinates getCoordinates() {
        return coordinates;
    }

    /**
     * Set the coordinates of the Address from existing coordinates
     *
     * @param coordinates Coordinates
     */
    public void setCoordinates(Coordinates coordinates) {
        this.coordinates = coordinates;
    }

    /**
     * Set the coordinates of the Address from latitude et longitude
     *
     * @param latitude  Double
     * @param longitude Double
     */
    public void setCoordinates(double latitude, double longitude) {
        this.coordinates.setLatitude(latitude);
        this.coordinates.setLongitude(longitude);
    }

}
