package idev.com.drawline.Model;

import java.util.ArrayList;

public class Building {
    private String IDBuilding;
    private String name;
    private ArrayList<Room> rooms;
    private ArrayList<Corridor> corridors;

    public Building(String IDBuilding, String name, ArrayList<Room> rooms, ArrayList<Corridor> corridors) {
        this.IDBuilding = IDBuilding;
        this.name = name;
        this.rooms = new ArrayList<>();
        this.corridors = new ArrayList<>();
    }

    public Building(String IDBuilding, String name) {
        this.IDBuilding = IDBuilding;
        this.name = name;
        this.rooms = new ArrayList<>();
        this.corridors = new ArrayList<>();
    }

    public void addRoom(String room_code, String room_name, String type, Double x, Double y, Double z, Latitude lati, Longitude longi) {
        rooms.add(new Room(room_code, room_name, type, x, y, z, lati, longi));
    }

    public void addRoom(Room room) {
        rooms.add(room);
    }

    public void addCorridor(String corridor_ID, String roofed, Double x, Double y, Double z, Latitude lati, Longitude longi) {
        corridors.add(new Corridor(corridor_ID, roofed, x, y, z, lati, longi));
    }

    public void addCorridor(Corridor corridor) {
        corridors.add(corridor);
    }

    public void setIDBuilding(String IDBuilding) {
        this.IDBuilding = IDBuilding;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRooms(ArrayList<Room> rooms) {
        this.rooms = rooms;
    }

    public void setCorridors(ArrayList<Corridor> corridors) {
        this.corridors = corridors;
    }

    public String getIDBuilding() {
        return IDBuilding;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Room> getRoom() {
        return rooms;
    }

    public ArrayList<Corridor> getCorridor() {
        return corridors;
    }

}
