package com.bektasoguzhan.gezbul;

/**
 * Created by Bektas on 2.04.2018.
 */

public class User {
    private String kullaniciID, title, comment, numberOfReviews;
    private double lat, lon;

    public User(String kullaniciID, String title, String comment, String numberOfReviews, double lat, double lon) {
        this.kullaniciID = kullaniciID;
        this.title = title;
        this.comment = comment;
        this.numberOfReviews = numberOfReviews;
        this.lat = lat;
        this.lon = lon;
    }

    public User(String title, String comment, String numberOfReviews, double lat, double lon) {
        this.title = title;
        this.comment = comment;
        this.numberOfReviews = numberOfReviews;
        this.lat = lat;
        this.lon = lon;
    }

    public String getkullaniciID() {
        return kullaniciID;
    }

    public void setkullaniciID(String kullaniciID) {
        kullaniciID = kullaniciID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getNumberOfReviews() {
        return numberOfReviews;
    }

    public void setNumberOfReviews(String numberOfReviews) {
        this.numberOfReviews = numberOfReviews;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }
}
