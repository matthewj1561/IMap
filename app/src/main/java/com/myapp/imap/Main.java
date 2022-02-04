//Entry point in Imap


package com.myapp.imap;

//Imports the necessary classes
import android.Manifest;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;


public class Main extends AppCompatActivity
        implements
        OnMyLocationButtonClickListener,
        OnMyLocationClickListener,
        OnMapReadyCallback,
        GoogleMap.OnMyLocationChangeListener,
        ActivityCompat.OnRequestPermissionsResultCallback,
        OnMapAndViewReadyListener.OnGlobalLayoutAndMapReadyListener
        {

//  Important variables used throughout the program
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean permissionDenied = false;
    public Double lat;
    public Double lon;
    private GoogleMap map;
    private Dialog dialog;
    private FrameLayout framelayout;
    private String comment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

//      Initializes the app layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
//      Brings the map into view
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

//      Makes a button and attaches an eventlistener to it
        Button btn = findViewById(R.id.btnAddMarker);
        framelayout = (FrameLayout) findViewById(R.id.layout);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//              Makes the dialog and shows what frame to pop in
                dialog = new Dialog(Main.this);
//              Removes the default title of the dialog
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//              Ensures the user can't leave the dialog window without pressing the submit button
                dialog.setCancelable(false);
//              Sets the new view to the dialog.xml design
                dialog.setContentView(R.layout.dialog);


//              Attaches an eventlistener to the button in the dialog. When pressed, it makes a marker with the user input
//              and closes the dialog window
                final EditText commentDialog = dialog.findViewById(R.id.comment);
                Button submit = dialog.findViewById(R.id.dialogButton);

                submit.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {
                        comment = commentDialog.getText().toString();
                        Marker marker = map.addMarker(
                                new MarkerOptions()
                                        .position(new LatLng(lat, lon))
                                        .title(comment)
                        );
                        dialog.dismiss();

                    }
                });
                dialog.show();



            }
        });
    }

//  Provides functionality for the even listeners on the location button and blue location dot
    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
        map.setOnMyLocationChangeListener(this);

        enableMyLocation();
    }

    /**
     * Enables the My Location layer if the fine location permission has been granted.
     */
    private void enableMyLocation() {
        // [START maps_check_location_permission]
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            if (map != null) {
                map.setMyLocationEnabled(true);
            }
        } else {
            // Permission to access the location is missing. Show rationale and request permission
            PermissionUtils.requestPermission(this, LOCATION_PERMISSION_REQUEST_CODE,
                Manifest.permission.ACCESS_FINE_LOCATION, true);
        }
        // [END maps_check_location_permission]
    }

//  Displays a message when the location button is pressed
    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT).show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
//  Updates the latitude and longitude of the user when ever it moves
    @Override
    public void onMyLocationChange(Location location){
        lat = location.getLatitude();
        lon = location.getLongitude();
    }
    //  Displays a message when the blue location dot is pressed
    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG).show();


    }

    // [START maps_check_location_permission_result]
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode != LOCATION_PERMISSION_REQUEST_CODE) {
            return;
        }

        if (PermissionUtils.isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Enable the my location layer if the permission has been granted.
            enableMyLocation();
        } else {
            // Permission was denied. Display an error message
            // [START_EXCLUDE]
            // Display the missing permission error dialog when the fragments resume.
            permissionDenied = true;
            // [END_EXCLUDE]
        }
    }
    // [END maps_check_location_permission_result]

    @Override
    protected void onResumeFragments() {
        super.onResumeFragments();
        if (permissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            permissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getSupportFragmentManager(), "dialog");
    }

}
