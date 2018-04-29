/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package idev.com.drawline.Model;


import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class Room implements Parcelable {

    private String room_code;
    private String room_name;
    private String type;
    private Double x;
    private Double y;
    private Double z;
    private Double lati;
    private Double longi;
    private Latitude latitude;
    private Longitude longitude;

    public Room(String room_code, String room_name, String type, Double x, Double y, Double z, Latitude lati, Longitude longi) {
        this.room_code = room_code;
        this.room_name = room_name;
        this.type = type;
        this.x = x;
        this.y = y;
        this.z = z;
        this.latitude = lati;
        this.longitude = longi;
        this.lati = latitude.getDegree() + ((latitude.getMinute() * 60 + latitude.getSecond()) / 3600);
        this.longi = longitude.getDegree() + ((longitude.getMinute() * 60 + longitude.getSecond()) / 3600);
    }

    protected Room(Parcel in) {
        room_code = in.readString();
        room_name = in.readString();
        type = in.readString();
        if (in.readByte() == 0) {
            x = null;
        } else {
            x = in.readDouble();
        }
        if (in.readByte() == 0) {
            y = null;
        } else {
            y = in.readDouble();
        }
        if (in.readByte() == 0) {
            z = null;
        } else {
            z = in.readDouble();
        }
        if (in.readByte() == 0) {
            lati = null;
        } else {
            lati = in.readDouble();
        }
        if (in.readByte() == 0) {
            longi = null;
        } else {
            longi = in.readDouble();
        }
    }

    public static final Creator<Room> CREATOR = new Creator<Room>() {
        @Override
        public Room createFromParcel(Parcel in) {
            return new Room(in);
        }

        @Override
        public Room[] newArray(int size) {
            return new Room[size];
        }
    };

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Room() {
    }

    public String getRoom_code() {
        return room_code;
    }

    public void setRoom_code(String room_code) {
        this.room_code = room_code;
    }

    public String getRoom_name() {
        return room_name;
    }

    public void setRoom_name(String room_name) {
        this.room_name = room_name;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(room_code);
        parcel.writeString(room_name);
        parcel.writeString(type);
        if (x == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(x);
        }
        if (y == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(y);
        }
        if (z == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(z);
        }
        if (lati == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(lati);
        }
        if (longi == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeDouble(longi);
        }
    }
}
