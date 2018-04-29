package idev.com.drawline;

import android.content.Context;
import android.database.Cursor;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

import idev.com.drawline.Database.TestAdapter;
import idev.com.drawline.Model.Building;
import idev.com.drawline.Model.Corridor;
import idev.com.drawline.Model.Edge;
import idev.com.drawline.Model.Latitude;
import idev.com.drawline.Model.Longitude;
import idev.com.drawline.Model.Room;
import idev.com.drawline.Model.Segments;
import idev.com.drawline.Model.Vertex;

public class Application {
    private Context context;
    ArrayList<Room> roomlist = new ArrayList<>();
    ArrayList<Corridor> corridorlist = new ArrayList<>();
    ArrayList<Segments> segmentList = new ArrayList<>();
    ArrayList<Building> buildinglist = new ArrayList<>();
    List<Vertex> nodes = new ArrayList<>();
    Cursor data;

    public Application(final Context context) {
        this.context = context;
        TestAdapter mDbHelper = new TestAdapter(context);
        mDbHelper.createDatabase();
        mDbHelper.open();

        data = mDbHelper.getAllRoomData();
        if (data != null) {
            if (data.moveToFirst()) {
                do {
                    // get the data into array, or class variable
                    Latitude lati = new Latitude(data.getDouble(11), data.getDouble(12), data.getDouble(13), data.getString(14));
                    Longitude longi = new Longitude(data.getDouble(15), data.getDouble(16), data.getDouble(17), data.getString(18));
                    Room room = new Room(data.getString(0), data.getString(1), data.getString(2), data.getDouble(8), data.getDouble(9), data.getDouble(10), lati, longi);
                    roomlist.add(room);
                } while (data.moveToNext());
            }
            data.close();
        }

        data = mDbHelper.getAllCorridoreData();
        if (data != null) {
            if (data.moveToFirst()) {
                do {
                    // get the data into array, or class variable
                    Latitude lati = new Latitude(data.getDouble(6), data.getDouble(7), data.getDouble(8), data.getString(9));
                    Longitude longi = new Longitude(data.getDouble(10), data.getDouble(11), data.getDouble(12), data.getString(13));
                    Corridor corridor = new Corridor(data.getString(0), data.getString(5), data.getDouble(2), data.getDouble(3), data.getDouble(4), lati, longi);
                    corridorlist.add(corridor);
                } while (data.moveToNext());
            }
            data.close();
        }

        data = mDbHelper.getAllBuildingData();
        if (data != null) {
            if (data.moveToFirst()) {
                do {
                    // get the data into array, or class variable
                    Building building = new Building(data.getString(0), data.getString(1));
                    buildinglist.add(building);
                } while (data.moveToNext());
            }
            data.close();
        }

        data = mDbHelper.getAllSegment();
        if (data != null) {
            if (data.moveToFirst()) {
                do {
                    // get the data into array, or class variable
                    Segments segments = new Segments(data.getString(0), data.getString(1), data.getString(2), data.getDouble(3));
                    if (!segments.getSource().substring(0, 1).equals("S") && !segments.getDestination().substring(0, 1).equals("S")) {
                        segmentList.add(segments);
                    }
                } while (data.moveToNext());
            }
            data.close();
        }
        mDbHelper.close();
        clusterRoomandCorridor();
    }

    public ArrayList<Room> getRoombyLantai(String noLantai) {
        ArrayList<Room> roomlistLantai = new ArrayList<>();
        TestAdapter mDbHelper = new TestAdapter(context);
        mDbHelper.createDatabase();
        mDbHelper.open();
        data = mDbHelper.getRoombyFloor(noLantai);
        if (data != null) {
            if (data.moveToFirst()) {
                do {
                    // get the data into array, or class variable
                    Latitude lati = new Latitude(data.getDouble(11), data.getDouble(12), data.getDouble(13), data.getString(14));
                    Longitude longi = new Longitude(data.getDouble(15), data.getDouble(16), data.getDouble(17), data.getString(18));
                    Room room = new Room(data.getString(0), data.getString(1), data.getString(2), data.getDouble(8), data.getDouble(9), data.getDouble(10), lati, longi);
                    roomlistLantai.add(room);
                } while (data.moveToNext());
            }
            data.close();
        }
        return roomlistLantai;
    }

    public ArrayList<Room> getRoomlist() {
        return roomlist;
    }

    private void clusterRoomandCorridor() {
        TestAdapter mDbHelper = new TestAdapter(context);
        mDbHelper.createDatabase();
        mDbHelper.open();
        for (Building building : buildinglist) {
            data = mDbHelper.getRoombyBuilding(building.getIDBuilding());
            if (data != null) {
                if (data.moveToFirst()) {
                    do {
                        // get the data into array, or class variable
                        Latitude lati = new Latitude(data.getDouble(11), data.getDouble(12), data.getDouble(13), data.getString(14));
                        Longitude longi = new Longitude(data.getDouble(15), data.getDouble(16), data.getDouble(17), data.getString(18));
                        Room room = new Room(data.getString(0), data.getString(1), data.getString(2), data.getDouble(8), data.getDouble(9), data.getDouble(10), lati, longi);
                        building.addRoom(room);
                    } while (data.moveToNext());
                }
            }
            data.close();
            StringBuilder sb = new StringBuilder(building.getIDBuilding());
            if (sb.charAt(2) == '-')
                sb.deleteCharAt(2);
            System.out.println("building_ID Corridor : " + sb.toString());
            data = mDbHelper.getCorridorbyBuilding(sb.toString());
            if (data != null) {
                if (data.moveToFirst()) {
                    do {
                        // get the data into array, or class variable
                        // get the data into array, or class variable
                        Latitude lati = new Latitude(data.getDouble(6), data.getDouble(7), data.getDouble(8), data.getString(9));
                        Longitude longi = new Longitude(data.getDouble(10), data.getDouble(11), data.getDouble(12), data.getString(13));
                        Corridor corridor = new Corridor(data.getString(0), data.getString(5), data.getDouble(2), data.getDouble(3), data.getDouble(4), lati, longi);
                        building.addCorridor(corridor);
                    } while (data.moveToNext());
                }
            }
            data.close();
        }
        mDbHelper.close();
    }

    public ArrayList<Corridor> getCorridorlist() {
        return corridorlist;
    }

    public ArrayList<Building> getBuildinglist() {
        return buildinglist;
    }

    public Building getBuildingbyID(String idBuilding) {
        for (Building building : buildinglist) {
            if (building.getIDBuilding().equals(idBuilding))
                return building;
        }
        return null;
    }

    public List<Vertex> getVertex() {
        for (Room room : roomlist) {
            nodes.add(new Vertex(room.getRoom_code(), room.getRoom_name()));
        }
        for (Corridor corridor : corridorlist) {
            nodes.add(new Vertex(corridor.getCorridor_ID(), corridor.getCorridor_ID()));
        }
        return nodes;
    }

    public List<Edge> getEdges() {
        List<Edge> edges = new ArrayList<>();
        for (Segments segments : segmentList) {
            edges.add(new Edge(segments.getNo(), getVertexyID(segments.getSource()), getVertexyID(segments.getDestination()), segments.getLength()));
            edges.add(new Edge(segments.getNo(), getVertexyID(segments.getDestination()), getVertexyID(segments.getSource()), segments.getLength()));
        }


        return edges;
    }

    private Vertex getVertexyID(String verId) {
        for (Vertex vertex : nodes) {
            if (vertex.getId().equals(verId))
                return vertex;
        }
        return null;
    }
}
