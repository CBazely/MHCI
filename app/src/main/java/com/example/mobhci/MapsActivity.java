package com.example.mobhci;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Polygon mMutablePolygon;
    final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button logOutButton = (Button) findViewById(R.id.setRangeButton);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMap2();
            }
        });
    }
    private void goToMap2() {
        Intent intent = new Intent(this, MapsActivity2.class);
        startActivity(intent);
    }

    public static double distance(LatLng x, LatLng x2) {

        double lat1 = x.latitude;
        double lat2 = x2.latitude;
        double lon1 = x.longitude;
        double lon2 = x2.longitude;
        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = 0;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
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

        mMap.setMinZoomPreference(15);
        final LatLng gla2 = new LatLng( 55.873724, -4.292538);
        mMap.addMarker(new MarkerOptions().position(gla2).title("Start"));
        final int radius = 500;
        mMap.addCircle(new CircleOptions()
                .center(gla2)
                .radius(radius));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            LatLng first = gla2;
            PolylineOptions options = new PolylineOptions().width(7).color(Color.GREEN).geodesic(true);
            Boolean canclick = Boolean.TRUE;
            Boolean intersect = Boolean.FALSE;
            ArrayList<LatLng> list = new ArrayList<LatLng>();
            Boolean firstLoop = Boolean.TRUE;
            double lastx1 = 0;
            double lasty1 = 0;
            double lastx2 = 0;
            double lasty2 = 0;
            double newx1 = 0;
            double newy1 = 0;
            double newx2 = 0;
            double newy2 = 0;
            @Override
            public void onMapClick(LatLng latLng) {
                list.add(latLng);
                int size = list.size();

                for (LatLng x:list){
                    if(firstLoop){
                        firstLoop = Boolean.FALSE;
                        continue;
                    }
                    if (x!=latLng){
                        if(distance(x, latLng)<5) {
                            intersect = Boolean.TRUE;
                        }
                    }
                    }

                if(canclick){
                    options.add(latLng);
                    mMap.addPolyline(options);
                    if ((distance(gla2, latLng) > radius) || intersect) {
                        //goToMap2();
                        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
                        alertDialogBuilder.setMessage("              GAME OVER");
                        final EditText et = new EditText(context);

                        // set prompts.xml to alertdialog builder
                        alertDialogBuilder.setView(et);

                        // set dialog message
                        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                options.color(Color.GREEN);
                                canclick = false;
                                mMap.addPolyline(options);
                            }
                        });

                        // create alert dialog
                        AlertDialog alertDialog = alertDialogBuilder.create();
                        // show it
                        alertDialog.show();
                    }

                }

            }
            });
        mMap.moveCamera(CameraUpdateFactory.newLatLng(gla2));
        googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                        this, R.raw.style_json));


    }
}
