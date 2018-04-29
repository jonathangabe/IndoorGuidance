package idev.com.drawline.Model;

import java.io.Serializable;

public class Path implements Serializable {
    private String asal, tujuan;

    public Path(String asal, String tujuan) {
        this.asal = asal;
        this.tujuan = tujuan;
    }

    public Path() {

    }

    public String getAsal() {
        return asal;
    }

    public String getTujuan() {
        return tujuan;
    }
}
