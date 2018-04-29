package idev.com.drawline;

import android.Manifest;
import android.graphics.Color;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.EntryXComparator;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import idev.com.drawline.Model.Building;
import idev.com.drawline.Model.Corridor;
import idev.com.drawline.Model.Edge;
import idev.com.drawline.Model.Graph;
import idev.com.drawline.Model.JalurRute;
import idev.com.drawline.Model.Path;
import idev.com.drawline.Model.Room;
import idev.com.drawline.Model.Vertex;

public class MainActivity extends AppCompatActivity {

    private LineChart chart;
    private CustomView customView;
    Application application;
    List<Vertex> vertex = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chart = findViewById(R.id.chart);

        Dexter.withActivity(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse response) {
                        application = new Application(MainActivity.this);
//                        cobaAja();
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse response) {
                        Toast.makeText(MainActivity.this, "Ijin membaca file tidak diberikan !", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {

                    }
                }).check();

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputPath();
            }
        });

//        findViewById(R.id.btnCoba).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                vertex = application.getVertex();
//                edges = application.getEdges();
//                System.out.println("vertex Size " + vertex.size());
//                System.out.println("edge Size " + edges.size());
//                System.out.println("Segment");
////                for (Edge edge : edges) {
////                    System.out.println("Source : " + edge.getSource().getName() + " Destination : " + edge.getDestination().getName());
////                }
//                Graph graph = new Graph(vertex, edges);
//                DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
//                dijkstra.execute(vertex.get(0));
//                LinkedList<Vertex> path = dijkstra.getPath(vertex.get(7));
//                for (Vertex vertex : path) {
//                    System.out.println(vertex);
//                }
//            }
//        });
    }

    private Vertex getvertexByID(String idVert) {
        for (Vertex vertex1 : vertex) {
            if (vertex1.getId().equals(idVert))
                return vertex1;
        }
        return null;
    }

    private void showInputPath() {
        Bundle bundle = new Bundle();
        ArrayList<Room> data = application.getRoombyLantai("1");
        bundle.putParcelableArrayList("data", data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        InputPath inputPath = new InputPath();
        inputPath.setRequestCode(300);
        inputPath.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.add(android.R.id.content, inputPath).addToBackStack(null).commit();
        inputPath.setOnCallbackResult(new InputPath.CallbackResult() {
            @Override
            public void sendResult(int requestCode, Object obj) {
                Path path = (Path) obj;
                System.out.println("Asal : " + path.getAsal());
                System.out.println("Tujuan : " + path.getTujuan());
                handleResult(path);
            }
        });

    }

    private void handleResult(Path path) {
        vertex = application.getVertex();
        edges = application.getEdges();
        System.out.println("vertex Size " + vertex.size());
        System.out.println("edge Size " + edges.size());
        Graph graph = new Graph(vertex, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(getvertexByID(path.getAsal()));
        LinkedList<Vertex> rute = dijkstra.getPath(getvertexByID(path.getTujuan()));
        buildRoute1Building(rute);
    }

    private Corridor getCorridorbyID(String ID) {
        for (Corridor corridor : application.getCorridorlist()) {
            if (corridor.getCorridor_ID().equals(ID))
                return corridor;
        }
        return null;
    }

    private Room getRoomByID(String id) {
        for (Room room : application.getRoomlist()) {
            if (room.getRoom_code().equals(id))
                return room;
        }
        return null;
    }

    private void buildRoute() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<Entry> jalur = new ArrayList<>();
        for (int i = 0; i < application.getRoomlist().size(); i++) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            ArrayList<Entry> garis = new ArrayList<>();
            float y = Float.valueOf(String.valueOf(application.getRoomlist().get(i).getLongi()));
            float x = Float.valueOf(String.valueOf(application.getRoomlist().get(i).getLati()));
            garis.add(new Entry(x, y, application.getRoomlist().get(i).getRoom_name()));
            jalur.add(new Entry(x, y, application.getRoomlist().get(i).getRoom_name()));
            LineDataSet dataset = new LineDataSet(garis, application.getRoomlist().get(i).getRoom_name());
            dataset.setCircleColor(color);
            dataset.setColor(color);
            dataset.setDrawValues(false);
            dataSets.add(dataset);
        }
        LineDataSet datasetjalur = new LineDataSet(jalur, "");
        datasetjalur.setDrawValues(false);
        datasetjalur.setDrawCircles(false);
        dataSets.add(datasetjalur);
        ArrayList<Entry> garis = new ArrayList<>();
        for (Corridor corridor : application.getCorridorlist()) {
            float y = Float.valueOf(String.valueOf(corridor.getLongi()));
            float x = Float.valueOf(String.valueOf(corridor.getLati()));
            garis.add(new Entry(x, y));
        }
//                        chart.getXAxis().setEnabled(false);
//                        chart.getAxisLeft().setEnabled(false);
//                        chart.getAxisRight().setEnabled(false);
        LineDataSet datasetcoridor = new LineDataSet(garis, "");
        datasetcoridor.setColor(Color.BLACK);
        datasetcoridor.setDrawValues(false);
        dataSets.add(datasetcoridor);
        chart.setData(new LineData(dataSets));
        chart.invalidate();
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                try {
                    Toast.makeText(MainActivity.this, e.getData().toString(), Toast.LENGTH_SHORT).show();
                } catch (NullPointerException raono) {

                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        Description description = new Description();
        description.setText("");
        chart.setGridBackgroundColor(128);
        chart.setBorderColor(255);
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        chart.setDrawGridBackground(true);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.setPinchZoom(false);
        chart.setDescription(description);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.setDrawGridBackground(true);//enable this too
        chart.getXAxis().setDrawGridLines(true);//enable for grid line
        chart.invalidate();
    }

    private void cobaAja() {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        ArrayList<Room> dataRuang = application.getRoombyLantai("1");
        Collections.sort(dataRuang, new Comparator<Room>() {
            @Override
            public int compare(Room room, Room t1) {
                if (room.getLati() > t1.getLati())
                    return -1;
                else if (room.getLati() < t1.getLati())
                    return 1;
                return 0;
            }
        });
        ArrayList<Entry> room = new ArrayList<>();
        for (Room ruang : dataRuang) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            float y = Float.valueOf(String.valueOf(ruang.getLongi()));
            float x = Float.valueOf(String.valueOf(ruang.getLati()));
            room.add(new Entry(x, y, ruang.getRoom_name()));
        }
        LineDataSet datasetjalur = new LineDataSet(room, "");
        datasetjalur.setDrawValues(false);
        datasetjalur.setDrawCircles(false);
        dataSets.add(datasetjalur);
        chart.setData(new LineData(dataSets));
        chart.setGridBackgroundColor(128);
        chart.setBorderColor(255);
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        chart.setDrawGridBackground(true);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.setPinchZoom(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.setDrawGridBackground(true);//enable this too
        chart.getXAxis().setDrawGridLines(true);//enable for grid line
        chart.invalidate();
    }

    private void buildRoute1Building(LinkedList<Vertex> rute) {
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        Building if1 = application.getBuildingbyID("IF-1");
        Collections.sort(if1.getRoom(), new Comparator<Room>() {
            @Override
            public int compare(Room room, Room t1) {
                if (room.getLati() > t1.getLati())
                    return -1;
                else if (room.getLati() < t1.getLati())
                    return 1;
                return 0;
            }
        });
        System.out.println("sebelum sort");
        for (Corridor corridor : if1.getCorridor()) {
            System.out.println("longitude : " + corridor.getLongi());
        }
        Collections.sort(if1.getCorridor(), new Comparator<Corridor>() {
            @Override
            public int compare(Corridor corridor, Corridor t1) {
                if (corridor.getLati() > t1.getLati())
                    return -1;
                else if (corridor.getLati() < t1.getLati())
                    return 1;
                return 0;
            }
        });
        System.out.println("sesudah sort");
        for (Corridor corridor : if1.getCorridor()) {
            System.out.println("longitude : " + corridor.getLongi());
        }
        ArrayList<Entry> jalur = new ArrayList<>();
        for (Room room1 : if1.getRoom()) {
            ArrayList<Entry> room = new ArrayList<>();
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            float y = Float.valueOf(String.valueOf(room1.getLongi()));
            float x = Float.valueOf(String.valueOf(room1.getLati()));
            room.add(new Entry(x, y, room1.getRoom_name()));
            jalur.add(new Entry(x, y, room1.getRoom_name()));
            LineDataSet dataset = new LineDataSet(room, room1.getRoom_name());
            dataset.setCircleColor(color);
            dataset.setColor(color);
            dataset.setDrawValues(false);
            dataSets.add(dataset);
        }
        LineDataSet datasetjalur = new LineDataSet(jalur, "");
        datasetjalur.setDrawValues(false);
        datasetjalur.setDrawCircles(false);
        dataSets.add(datasetjalur);
        ArrayList<Entry> garis = new ArrayList<>();
        for (Corridor corridor : if1.getCorridor()) {
            float y = Float.valueOf(String.valueOf(corridor.getLongi()));
            float x = Float.valueOf(String.valueOf(corridor.getLati()));
            garis.add(new Entry(x, y));
        }
        LineDataSet datasetcoridor = new LineDataSet(garis, "");
        datasetcoridor.setColor(Color.BLACK);
        datasetcoridor.setDrawValues(false);
        dataSets.add(datasetcoridor);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                try {
                    Toast.makeText(MainActivity.this, e.getData().toString(), Toast.LENGTH_SHORT).show();
                } catch (NullPointerException raono) {

                }
            }

            @Override
            public void onNothingSelected() {

            }
        });
        ArrayList<JalurRute> jalan = new ArrayList<>();
        for (Vertex vertex : rute) {
            System.out.println("id vertex " + vertex.getId());
            if (vertex.getId().substring(0, 1).equals("C"))
                jalan.add(new JalurRute(getCorridorbyID(vertex.getId()).getCorridor_ID(),
                        getCorridorbyID(vertex.getId()).getLati(), getCorridorbyID(vertex.getId()).getLongi()));
            else
                jalan.add(new JalurRute(getRoomByID(vertex.getId()).getRoom_code(),
                        getRoomByID(vertex.getId()).getLati(), getRoomByID(vertex.getId()).getLongi()));
        }
        Collections.sort(jalan, new Comparator<JalurRute>() {
            @Override
            public int compare(JalurRute jalurRute, JalurRute t1) {
                if (jalurRute.getLatitude() > t1.getLatitude())
                    return -1;
                else if (jalurRute.getLatitude() < t1.getLatitude())
                    return 1;
                return 0;
            }
        });
        ArrayList<Entry> jalur1 = new ArrayList<>();
        for (int i = 0; i < jalan.size(); i++) {
//            System.out.println("Jalan ke " + i + " : " + jalan.get(i).getRoom_name());
            float y = Float.valueOf(String.valueOf(jalan.get(i).getLongitude()));
            float x = Float.valueOf(String.valueOf(jalan.get(i).getLatitude()));
            jalur1.add(new Entry(x, y, jalan.get(i).getNama()));
        }
        LineDataSet datasetjalur1 = new LineDataSet(jalur, "");
        datasetjalur.setDrawValues(false);
        datasetjalur.setDrawCircles(false);
        dataSets.add(datasetjalur1);
        chart.setData(new LineData(dataSets));
        chart.setGridBackgroundColor(128);
        chart.setBorderColor(255);
        chart.getAxisRight().setEnabled(false);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setEnabled(false);
        chart.setDrawGridBackground(true);
        chart.getAxisRight().setDrawLabels(false);
        chart.getAxisLeft().setDrawLabels(false);
        chart.getLegend().setEnabled(false);
        chart.setPinchZoom(false);
        Description description = new Description();
        description.setText("");
        chart.setDescription(description);
        chart.setDoubleTapToZoomEnabled(false);
        chart.getXAxis().setEnabled(false);
        chart.setDrawGridBackground(true);//enable this too
        chart.getXAxis().setDrawGridLines(true);//enable for grid line
        chart.invalidate();
    }
}
