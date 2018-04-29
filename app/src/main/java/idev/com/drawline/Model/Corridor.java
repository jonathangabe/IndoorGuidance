package idev.com.drawline.Model;

public class Corridor {
    private String corridor_ID;
    private String building_ID;
    private Double x;
    private Double y;
    private Double z;
    private String roofed;
    private Double lati;
    private Double longi;
    private Latitude latitude;
    private Longitude longitude;

    public Corridor(String corridor_ID, String roofed, Double x, Double y, Double z, Latitude lati, Longitude longi) {
        this.corridor_ID = corridor_ID;
        this.x = x;
        this.y = y;
        this.z = z;
        this.roofed = roofed;
        this.latitude = lati;
        this.longitude = longi;
        this.lati = latitude.getDegree() + latitude.getMinute() / 60 + latitude.getSecond() / 3600;
        this.longi = longitude.getDegree() + longitude.getMinute() / 60 + longitude.getSecond() / 3600;
    }


    public String getCorridor_ID() {
        return corridor_ID;
    }

    public void setCorridor_ID(String corridor_ID) {
        this.corridor_ID = corridor_ID;
    }

    public String getBuilding_ID() {
        return building_ID;
    }

    public void setBuilding_ID(String building_ID) {
        this.building_ID = building_ID;
    }

    public Double getX() {
        return x;
    }

    public void setX(Double x) {
        this.x = x;
    }

    public Double getY() {
        return y;
    }

    public void setY(Double y) {
        this.y = y;
    }

    public Double getZ() {
        return z;
    }

    public void setZ(Double z) {
        this.z = z;
    }

    public String getRoofed() {
        return roofed;
    }

    public void setRoofed(String roofed) {
        this.roofed = roofed;
    }

    public Double getLati() {
        return lati;
    }

    public void setLati(Double lati) {
        this.lati = lati;
    }

    public Double getLongi() {
        return longi;
    }

    public void setLongi(Double longi) {
        this.longi = longi;
    }

    public Latitude getLatitude() {
        return latitude;
    }

    public void setLatitude(Latitude latitude) {
        this.latitude = latitude;
    }

    public Longitude getLongitude() {
        return longitude;
    }

    public void setLongitude(Longitude longitude) {
        this.longitude = longitude;
    }
}
