package com.example.maps_functions;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.service.quicksettings.Tile;
import android.util.Log;
import android.widget.TextView;

import com.mapbox.android.telemetry.TelemetryEnabler;
import com.mapbox.android.telemetry.TelemetryListener;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.annotation.LineManager;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.plugins.offline.model.NotificationOptions;
import com.mapbox.mapboxsdk.plugins.offline.model.OfflineDownloadOptions;
import com.mapbox.mapboxsdk.plugins.offline.offline.OfflinePlugin;
import com.mapbox.mapboxsdk.plugins.offline.utils.OfflineUtils;
import com.mapbox.mapboxsdk.utils.ColorUtils;
import com.mapbox.maps.Style;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;

public class MainActivity extends AppCompatActivity {
    TextView distance;
    LineOptions polylineOptionsY = null;
    MapView mapView;
    private List<LatLng> low_speed=new ArrayList<>();
    com.mapbox.mapboxsdk.maps.Style pubStyle = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "sk.eyJ1IjoiYm91Y2hyYTc3IiwiYSI6ImNsZndhdGk1dDA2MHkzZnBvaTRoZGxzbTQifQ.knys52gVrIMsPetvZ8V7zQ");
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        final MapboxMap[] map = new MapboxMap[1];
//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull MapboxMap mapboxMap) {
//                mapboxMap.setStyle(com.mapbox.mapboxsdk.maps.Style.SATELLITE, new com.mapbox.mapboxsdk.maps.Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull com.mapbox.mapboxsdk.maps.Style style) {
//                        pubStyle = style;
////                        low_speed.add(new LatLng(33.85942541058388, 35.9972819059255));
////                        low_speed.add(new LatLng(33.85917054020412, 35.99694341507842));
////                       // low_speed.add(new LatLng(33.85847056164691, 35.996129881206));
////                        map[0] =mapboxMap;
////                    //    mapboxMap.getUiSettings().setScrollGesturesEnabled(false);
////                        if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
////                            // TODO: Consider calling
////                            //    ActivityCompat#requestPermissions
////                            return;
////                        }
////                        mapboxMap.setMinZoomPreference(15);
////                        mapboxMap.getUiSettings().setAttributionEnabled(false);
////                        mapboxMap.getUiSettings().setLogoEnabled(false);
////                        LineManager lineManager1 = new LineManager(mapView, map[0], pubStyle);
////                        polylineOptionsY = new LineOptions()
////                                .withLatLngs(low_speed)
////                                .withLineColor(ColorUtils.colorToRgbaString(getColor(R.color.white)))
////                                .withLineOpacity(0.5f)
////                                .withLineWidth(16f);
////                        lineManager1.create(polylineOptionsY);
////                     //   mapboxMap.setMinPitchPreference(60);
////                        boolean tilesCount = mapboxMap.getPrefetchesTiles();
////                        System.out.println( "Tiles count: " + tilesCount);
//
//                    }
//        });
//    }});
        // Set up the OfflineManager
        OfflineManager offlineManager = OfflineManager.getInstance(MainActivity.this);
        // Create a bounding box for the offline region
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng( 33.827675498183716, 35.50420874230868)) // Northeast
                .include(new LatLng(33.79770425424735, 35.47803849631031)) // Southwest
                .build();
        // Define the offline region
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
             Style.SATELLITE,
                latLngBounds,
                14,
                17,
                MainActivity.this.getResources().getDisplayMetrics().density);
        // Implementation that uses JSON to store Yosemite National Park as the offline region name.
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Beirut1", "airbase");
            String json = jsonObject.toString();
            metadata = json.getBytes();
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }
// Create the region asynchronously
        offlineManager.createOfflineRegion(definition, metadata,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion) {
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);

// Monitor the download progress using setObserver
                        offlineRegion.setObserver(new OfflineRegion.OfflineRegionObserver() {
                            @Override
                            public void onStatusChanged(OfflineRegionStatus status) {

// Calculate the download percentage
                                double percentage = status.getRequiredResourceCount() >= 0
                                        ? (100.0 * status.getCompletedResourceCount() / status.getRequiredResourceCount()) :
                                        0.0;
                                Log.d("downloaded ", percentage+"");
                                System.out.println(" downloaded "+percentage);
                                if (status.isComplete()) {
// Download complete
                                    Log.d(TAG, "Region downloaded successfully.");
                                } else if (status.isRequiredResourceCountPrecise()) {
                                    Log.d(TAG, String.valueOf(percentage));
                                }
                            }

                            @Override
                            public void onError(OfflineRegionError error) {
// If an error occurs, print to logcat
                                Log.e(TAG, "onError reason: " + error.getReason());
                                Log.e(TAG, "onError message: " + error.getMessage());
                            }

                            @Override
                            public void mapboxTileCountLimitExceeded(long limit) {
// Notify if offline region exceeds maximum tile count
                                System.out.println( "Mapbox tile count limit exceeded: " + limit);
                            }
                        });
                    }

                    @Override
                    public void onError(String error) {
                        Log.e(TAG, "Error: " + error);
                    }
                });
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }
}