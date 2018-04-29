package idev.com.drawline.Model;

public class JalurRute {
    private String nama;
    private Double latitude;
    private Double longitude;

    public JalurRute(String nama, Double latitude, Double longitude) {
        this.nama = nama;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getNama() {
        return nama;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }
}
