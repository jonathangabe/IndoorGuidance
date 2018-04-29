package idev.com.drawline;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.SyncParams;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.ArrayList;
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

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, SensorEventListener, StepListener {

    private GoogleMap mMap;
    private Application application;
    List<Vertex> vertex = new ArrayList<>();
    List<Edge> edges = new ArrayList<>();
    int stat = 0;
    Marker marker;
    private StepDetector simpleStepDetector;
    private SensorManager sensorManager;
    private Sensor accel, mRotationV, mAccelerometer, mMagnetometer;
    float[] rMat = new float[9];
    float[] orientation = new float[9];
    private float[] mLastAccelerometer = new float[3];
    private float[] mLastMagnetoometer = new float[3];
    private boolean haveSensor = false, haveSensor2 = false;
    private boolean mLastAccelerometerset = false;
    private boolean mLastMagnetometerset = false;
    private static final String TEXT_NUM_STEPS = "Number of Steps: ";
    private int numSteps;
    private FusedLocationProviderClient mFusedLocationClient;
    private TextView steptx;
    int mAzimuth;
    String where = "NO";
    TextView txt_azimuth;
    TextView instructionTxt;
    Path inputPath = new Path();
    boolean isStart = false;
    ImageView compass_arrow;

//    LocationListener mLocationListener = new LocationListener() {
//        @Override
//        public void onLocationChanged(Location location) {
//            if (marker != null)
//                marker.remove();
//            LatLng currLoc = new LatLng(location.getLatitude(), location.getLongitude());
//            marker = mMap.addMarker(new MarkerOptions().position(currLoc).title("Its Me :)"));
//            mMap.moveCamera(CameraUpdateFactory.newLatLng(currLoc));
//        }
//
//        @Override
//        public void onStatusChanged(String s, int i, Bundle bundle) {
//
//        }
//
//        @Override
//        public void onProviderEnabled(String s) {
//
//        }
//
//        @Override
//        public void onProviderDisabled(String s) {
//
//        }
//    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        txt_azimuth = (TextView) findViewById(R.id.txt_azimuth);
        compass_arrow = (ImageView) findViewById(R.id.arrow);
        instructionTxt = (TextView) findViewById(R.id.instructionText);
        steptx = (TextView) findViewById(R.id.stepTxt);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        simpleStepDetector = new StepDetector();
        accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        simpleStepDetector.registerListener(MapsActivity.this);
        inputPath = null;
        isStart = false;
//        Dexter.withActivity(this)
//                .withPermissions(
//                        Manifest.permission.ACCESS_FINE_LOCATION,
//                        Manifest.permission.ACCESS_COARSE_LOCATION
//                ).withListener(new MultiplePermissionsListener() {
//            @Override
//            public void onPermissionsChecked(MultiplePermissionsReport report) {
//                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                        && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                    // TODO: Consider calling
//                    //    ActivityCompat#requestPermissions
//                    // here to request the missing permissions, and then overriding
//                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//                    //                                          int[] grantResults)
//                    // to handle the case where the user grants the permission. See the documentation
//                    // for ActivityCompat#requestPermissions for more details.
//                    return;
//                }
//                mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000 * 0,
//                        0, mLocationListener);
//            }
//
//            @Override
//            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
//        }).check();

//        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        application = new Application(this);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        findViewById(R.id.btnMulai).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                numSteps = 0;
                isStart = true;
                sensorManager.registerListener(MapsActivity.this, accel, SensorManager.SENSOR_DELAY_FASTEST);
            }
        });

        findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showInputPath();
            }
        });

        findViewById(R.id.btnStop).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
//        LatLng sydney = new LatLng(-34, 151);
//        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
//        Building if1 = application.getBuildingbyID("IF-1");
//        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (Room room : application.getRoombyLantai("1")) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            System.out.println("Latitude : " + room.getLati() + " Longitude : " + room.getLongi());
            LatLng point = new LatLng(room.getLati() * -1, room.getLongi());
//            options.add(point);
            mMap.addCircle(new CircleOptions()
                    .center(point)
                    .radius(0.5)
                    .strokeColor(color)
                    .fillColor(color));
        }

        for (Corridor corridor : application.getCorridorlist()) {
            LatLng point = new LatLng(corridor.getLati() * -1, corridor.getLongi());
//            options.add(point);
            mMap.addCircle(new CircleOptions()
                    .center(point)
                    .radius(0.1)
                    .strokeColor(Color.BLACK)
                    .fillColor(Color.BLACK));
        }
        LatLng startLatLng = new LatLng(application.getRoombyLantai("1").get(0).getLati() * -1, application.getRoombyLantai("1").get(0).getLongi());
//        mMap.addPolyline(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
    }


    private void refreshMap() {
        for (Room room : application.getRoombyLantai("1")) {
            Random rnd = new Random();
            int color = Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256));
            System.out.println("Latitude : " + room.getLati() + " Longitude : " + room.getLongi());
            LatLng point = new LatLng(room.getLati() * -1, room.getLongi());
//            options.add(point);
            mMap.addCircle(new CircleOptions()
                    .center(point)
                    .radius(1)
                    .strokeColor(color)
                    .fillColor(color));
        }

        for (Corridor corridor : application.getCorridorlist()) {
            LatLng point = new LatLng(corridor.getLati() * -1, corridor.getLongi());
//            options.add(point);
            mMap.addCircle(new CircleOptions()
                    .center(point)
                    .radius(0.1)
                    .strokeColor(Color.BLACK)
                    .fillColor(Color.BLACK));
        }
        LatLng startLatLng = new LatLng(application.getRoombyLantai("1").get(0).getLati() * -1, application.getRoombyLantai("1").get(0).getLongi());
//        mMap.addPolyline(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startLatLng));
        mMap.animateCamera(CameraUpdateFactory.zoomTo(25));
    }


    private void showInputPath() {
        Bundle bundle = new Bundle();
        ArrayList<Room> data = application.getRoombyLantai("1");
        bundle.putParcelableArrayList("data", data);
        FragmentManager fragmentManager = getSupportFragmentManager();
        final InputPath inputpath = new InputPath();
        inputpath.setRequestCode(300);
        inputpath.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.add(android.R.id.content, inputpath).addToBackStack(null).commit();
        inputpath.setOnCallbackResult(new InputPath.CallbackResult() {
            @Override
            public void sendResult(int requestCode, Object obj) {
                Path path = (Path) obj;
                inputPath = path;
                System.out.println("Asal : " + path.getAsal());
                System.out.println("Tujuan : " + path.getTujuan());
                handleResult(path);
            }
        });
    }

    private void handleResult(Path path) {
        stat++;
        if (stat > 0) {
            mMap.clear();
            refreshMap();
        }
        vertex = application.getVertex();
        edges = application.getEdges();
        System.out.println("vertex Size " + vertex.size());
        System.out.println("edge Size " + edges.size());
        Graph graph = new Graph(vertex, edges);
        DijkstraAlgorithm dijkstra = new DijkstraAlgorithm(graph);
        dijkstra.execute(getvertexByID(path.getAsal()));
        LinkedList<Vertex> rute = dijkstra.getPath(getvertexByID(path.getTujuan()));
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
        LatLng awal = new LatLng(jalan.get(0).getLatitude() * -1, jalan.get(0).getLongitude());
        marker = mMap.addMarker(new MarkerOptions().position(awal).title("Its Me :)"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(awal));
        PolylineOptions options = new PolylineOptions().width(5).color(Color.BLUE).geodesic(true);
        for (JalurRute jalurRute : jalan) {
            LatLng point = new LatLng(jalurRute.getLatitude() * -1, jalurRute.getLongitude());
            options.add(point);
        }
        mMap.addPolyline(options);
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

    private Vertex getvertexByID(String idVert) {
        for (Vertex vertex1 : vertex) {
            if (vertex1.getId().equals(idVert))
                return vertex1;
        }
        return null;
    }

    //Sensor event reading
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            simpleStepDetector.updateAccel(
                    event.timestamp, event.values[0], event.values[1], event.values[2]);
        }

        if (event.sensor.getType() == Sensor.TYPE_ROTATION_VECTOR) {
            SensorManager.getRotationMatrixFromVector(rMat, event.values);
            mAzimuth = (int) ((Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360);

        }
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, mLastAccelerometer, 0, event.values.length);
            mLastAccelerometerset = true;
        } else if (event.sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, mLastMagnetoometer, 0, event.values.length);
            mLastMagnetometerset = true;
        }
        if (mLastMagnetometerset && mLastAccelerometerset) {
            sensorManager.getRotationMatrix(rMat, null, mLastAccelerometer, mLastMagnetoometer);
            sensorManager.getOrientation(rMat, orientation);
            mAzimuth = (int) ((Math.toDegrees(SensorManager.getOrientation(rMat, orientation)[0]) + 360) % 360);
        }

        mAzimuth = Math.round(mAzimuth);

        if (mAzimuth >= 350 || mAzimuth <= 10) {
            where = "N";
        }
        if (mAzimuth < 350 && mAzimuth > 280) {
            where = "NW";
        }
        if (mAzimuth <= 280 && mAzimuth > 260) {
            where = "W";
        }
        if (mAzimuth <= 260 && mAzimuth > 190) {
            where = "SW";
        }
        if (mAzimuth <= 190 && mAzimuth > 170) {
            where = "S";
        }
        if (mAzimuth <= 170 && mAzimuth > 100) {
            where = "SE";
        }
        if (mAzimuth <= 100 && mAzimuth > 80) {
            where = "E";
        }
        if (mAzimuth <= 80 && mAzimuth > 10) {
            where = "NE";
        }

        txt_azimuth.setText(where);

        if (inputPath != null && isStart) {
//            System.out.println("is start :");
            if (inputPath.getAsal().equals("IF1.01.01") && inputPath.getTujuan().equals("IF1.01.08")) { //Academy service room to meeting room
                System.out.println("ke " + where);
                if (numSteps < 3) {
                    if (!where.equals("W")) {
                        instructionTxt.setText("HEAD WEST");
                    } else {
                        instructionTxt.setText("Move 2 step foward");
                    }
                } else if (numSteps < 29) {
                    if (!where.equals("S")) {
                        instructionTxt.setText("HEAD SOUTH");
                    } else {
                        instructionTxt.setText("Move 27 step foward");
                    }
                } else if (numSteps < 31) {
                    if (!where.equals("W")) {
                        instructionTxt.setText("HEAD WEST");
                    } else {
                        instructionTxt.setText("Move 2 step foward");
                    }
                } else if (numSteps > 31) {
                    instructionTxt.setText("You have arrived at the destination");
                }

            } else if (inputPath.getAsal().equals("IF2.01.01") && inputPath.getTujuan().equals("IF2.01.10")) { //Computing lab to multimedia lab
                if (numSteps < 3) {
                    if (!where.equals("N")) {
                        instructionTxt.setText("HEAD NORTH");
                    } else {
                        instructionTxt.setText("Move 2 step foward");
                    }
                } else if (numSteps < 98) {
                    if (!where.equals("W")) {
                        instructionTxt.setText("HEAD WEST");
                    } else {
                        instructionTxt.setText("Move 96 step foward");
                    }
                } else if (numSteps < 31) {
                    if (!where.equals("N")) {
                        instructionTxt.setText("HEAD NORTH");
                    } else {
                        instructionTxt.setText("Move 2 step foward");
                    }
                } else if (numSteps > 31) {
                    instructionTxt.setText("You have arrived at the destination");
                }

            } else if (inputPath.getAsal().equals("PS.01.01") && inputPath.getTujuan().equals("PS.01.13")) { //Pascasarjana academic and administration room to Thesis mentoring room
                if (numSteps < 1) {
                    if (!where.equals("S")) {
                        instructionTxt.setText("HEAD SOUTH");
                    } else {
                        instructionTxt.setText("Move 1 step foward");
                    }
                } else if (numSteps < 5) {
                    if (!where.equals("W")) {
                        instructionTxt.setText("HEAD WEST");
                    } else {
                        instructionTxt.setText("Move 4 step foward");
                    }
                } else if (numSteps < 7) {
                    if (!where.equals("S")) {
                        instructionTxt.setText("HEAD SOUTH");
                    } else {
                        instructionTxt.setText("Move 2 step foward");
                    }
                } else if (numSteps < 16) {
                    if (!where.equals("W")) {
                        instructionTxt.setText("HEAD WEST");
                    } else {
                        instructionTxt.setText("Move 9 step foward");
                    }
                } else if (numSteps < 39) {
                    if (!where.equals("S")) {
                        instructionTxt.setText("HEAD SOUTH");
                    } else {
                        instructionTxt.setText("Move 23 step foward");
                    }
                } else if (numSteps < 45) {
                    if (!where.equals("E")) {
                        instructionTxt.setText("HEAD EAST");
                    } else {
                        instructionTxt.setText("Move 6 step foward");
                    }
                } else if (numSteps < 51) {
                    if (!where.equals("S")) {
                        instructionTxt.setText("HEAD SOUTH");
                    } else {
                        instructionTxt.setText("Move 7 step foward");
                    }
                } else if (numSteps < 55) {
                    if (!where.equals("E")) {
                        instructionTxt.setText("HEAD EAST");
                    } else {
                        instructionTxt.setText("Move 4 step foward");
                    }
                } else if (numSteps > 55){
                    instructionTxt.setText("You have arrived at the destination");
                }

            } else if (inputPath.getAsal().equals("A108") && inputPath.getTujuan().equals("B101")) { //EPRT room to B101 classroom
                if (numSteps < 2) {
                    if (!where.equals("E")) {
                        instructionTxt.setText("HEAD EAST");
                    } else {
                        instructionTxt.setText("Move 2 step foward");
                    }
                } else if (numSteps < 15) {
                    if (!where.equals("S")) {
                        instructionTxt.setText("HEAD SOUTH");
                    } else {
                        instructionTxt.setText("Move 13 step foward");
                    }
                } else if (numSteps < 25) {
                    if (!where.equals("E")) {
                        instructionTxt.setText("HEAD EAST");
                    } else {
                        instructionTxt.setText("Move 10 step foward");
                    }
                } else if (numSteps < 106) {
                    if (!where.equals("S")) {
                        instructionTxt.setText("HEAD SOUTH");
                    } else {
                        instructionTxt.setText("Move 81 step foward");
                    }
                } else if (numSteps < 116) {
                    if (!where.equals("W")) {
                        instructionTxt.setText("HEAD WEST");
                    } else {
                        instructionTxt.setText("Move 10 step foward");
                    }
                } else if (numSteps < 130) {
                    if (!where.equals("S")) {
                        instructionTxt.setText("HEAD SOUTH");
                    } else {
                        instructionTxt.setText("Move 24 step foward");
                    }
                } else if (numSteps < 135) {
                    if (!where.equals("W")) {
                        instructionTxt.setText("HEAD WEST");
                    } else {
                        instructionTxt.setText("Move 24 step foward");
                    }
                } else if (numSteps < 137) {
                    if (!where.equals("S")) {
                        instructionTxt.setText("HEAD SOUTH");
                    } else {
                        instructionTxt.setText("Move 2 step foward");
                    }
                } else if (numSteps > 137) {
                    instructionTxt.setText("You have arrived at the destination");
                }
            }

        }
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    //method to start the sensor reading
    public void start() {
        if (sensorManager != null) {
            if (sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR) == null) {
                if (sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) == null || sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER) == null) {
                    noSensorAlert();
                } else {
                    mAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                    mMagnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

                    haveSensor = sensorManager.registerListener(this, mAccelerometer, sensorManager.SENSOR_DELAY_UI);
                    haveSensor2 = sensorManager.registerListener(this, mMagnetometer, sensorManager.SENSOR_DELAY_UI);
                }
            } else {
                mRotationV = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
                haveSensor = sensorManager.registerListener(this, mRotationV, sensorManager.SENSOR_DELAY_UI);
            }
        }
    }

    public void noSensorAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setMessage("Your Device Doesn't Support The Compass.")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                });
    }

    public void stop() {
        if (haveSensor && haveSensor2) {
            sensorManager.unregisterListener(this, mAccelerometer);
            sensorManager.unregisterListener(this, mMagnetometer);
        } else {
            if (haveSensor) {
                sensorManager.unregisterListener(this, mRotationV);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }

    @Override
    protected void onResume() {
        super.onResume();
        start();
    }

    //step count when the sensor detect a step
    @Override
    public void step(long timeNs) {
        numSteps++;
        steptx.setText(numSteps);

        /*Toast.makeText(MapsActivity.this, String.valueOf(numSteps), Toast.LENGTH_SHORT).show();
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport report) {
                if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                mFusedLocationClient.getLastLocation()
                        .addOnSuccessListener(MapsActivity.this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {

                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {

                                    // Logic to handle location object
                                    if (marker != null)
                                        marker.remove();
                                    LatLng currLoc = new LatLng(location.getLatitude(), location.getLongitude());
                                    marker = mMap.addMarker(new MarkerOptions().position(currLoc).title("Its Me :)"));
                                    mMap.moveCamera(CameraUpdateFactory.newLatLng(currLoc));
                                }
                            }
                        });
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) { ... }
        }).check();*/
    }
}
