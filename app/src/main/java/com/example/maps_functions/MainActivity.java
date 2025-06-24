package com.example.maps_functions;

import static android.content.ContentValues.TAG;
import static com.google.android.material.internal.ContextUtils.getActivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Telephony;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.mapbox.geojson.Feature;
import com.mapbox.geojson.FeatureCollection;
import com.mapbox.geojson.Geometry;
import com.mapbox.geojson.LineString;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.annotations.Polyline;
import com.mapbox.mapboxsdk.annotations.PolylineOptions;
import com.mapbox.mapboxsdk.geometry.LatLng;
import com.mapbox.mapboxsdk.geometry.LatLngBounds;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.MapboxMapOptions;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;
import com.mapbox.mapboxsdk.offline.OfflineManager;
import com.mapbox.mapboxsdk.offline.OfflineRegion;
import com.mapbox.mapboxsdk.offline.OfflineRegionError;
import com.mapbox.mapboxsdk.offline.OfflineRegionStatus;
import com.mapbox.mapboxsdk.offline.OfflineTilePyramidRegionDefinition;
import com.mapbox.mapboxsdk.plugins.annotation.LineOptions;
import com.mapbox.mapboxsdk.style.layers.CircleLayer;
import com.mapbox.mapboxsdk.style.layers.LineLayer;
import com.mapbox.mapboxsdk.style.layers.Property;
import com.mapbox.mapboxsdk.style.layers.PropertyFactory;
import com.mapbox.mapboxsdk.style.layers.RasterLayer;
import com.mapbox.mapboxsdk.style.layers.SymbolLayer;
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource;
import com.mapbox.mapboxsdk.style.sources.RasterSource;
import com.mapbox.mapboxsdk.style.sources.Source;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    TextView distance;
    LineOptions polylineOptionsY = null;
    MapView mapView;
    Polyline polyline;
    private List<LatLng> low_speed = new ArrayList<>();
    com.mapbox.mapboxsdk.maps.Style pubStyle = null;
    private List<Point> routeCoordinates = new ArrayList<>();
    private LocationComponent locationComponent;
    private MapboxMap map;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Mapbox.getInstance(this, "pk.eyJ1IjoiYm91Y2hyYTc3IiwiYSI6ImNreTFyczljejAyNHAybm4xMGE1bjZxdDEifQ.4GB_h_R5cY9G7Fh7S1jNiA");
        MapboxMapOptions options=new MapboxMapOptions().pixelRatio(2);

// Initialize the MapView with the options
        mapView = new MapView(this, options);
        mapView.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mapView = findViewById(R.id.mapView);
        final MapboxMap[] map = new MapboxMap[1];
        // Set up the OfflineManager
        OfflineManager offlineManager = OfflineManager.getInstance(MainActivity.this);
        // Create a bounding box for the offline region
      //  mapView.onCreate(savedInstanceState);
        LatLngBounds latLngBounds = new LatLngBounds.Builder()
                .include(new LatLng(32.365157139961234, -84.97099257511302)) // Northeast
                .include(new LatLng(32.314282288977296, -85.01381581263415)) // Southwest
                .build();


//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull MapboxMap mapboxMap) {
//                mapboxMap.setStyle(new Style.Builder().fromUri("mapbox://styles/mapbox/satellite-streets-v11")
//                       , new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//                        mapboxMap.setLatLngBoundsForCameraTarget(latLngBounds);
//                        mapboxMap.setMinZoomPreference(13);
//                        mapboxMap.setMaxZoomPreference(19);
//                        RasterSource rasterSource = new RasterSource("maptiler-source",
//                                "https://api.maptiler.com/maps/hybrid/256/{z}/{x}/{y}.jpg?key=whtNBjTt7P1zWynk43pF");
//
//                        style.addSource(rasterSource);
//
//                        // Add a raster layer to display the tiles
//                        RasterLayer rasterLayer = new RasterLayer("maptiler-layer", "maptiler-source");
//                        style.addLayer(rasterLayer);
//
//                    }
//                });
//            }
//        });

//        mapView.getMapAsync(new OnMapReadyCallback() {
//            @Override
//            public void onMapReady(@NonNull MapboxMap mapboxMap) {
//                mapboxMap.setStyle(Style.SATELLITE, new Style.OnStyleLoaded() {
//                    @Override
//                    public void onStyleLoaded(@NonNull Style style) {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                mapboxMap.setLatLngBoundsForCameraTarget(latLngBounds);
////                                Feature polygonFeature = Feature.fromGeometry(Polygon.fromLngLats());
////                                List<Point> points=new ArrayList<>();
////                                points.add(Point.fromLngLat(40.51523822894434, -3.5743362175545283));
////                                Feature lineStringFeature = Feature.fromGeometry(LineString.fromLngLats(points));
////                                Feature pointFeature = Feature.fromGeometry(location());
////
////                                FeatureCollection featureCollection = FeatureCollection.fromFeatures(new Feature[]{
////                                        pointFeature,lineStringFeature
////                                });
////
////                                GeoJsonSource geoJsonSource = new GeoJsonSource("source-id", featureCollection);
////
////                                style.addSource(geoJsonSource);
////
////                                LineLayer lineLayer = new LineLayer("line-layer-id", "source-id");
////                                lineLayer.setProperties(
////                                        PropertyFactory.lineColor(Color.RED),
////                                        PropertyFactory.lineWidth(70F),
////                                        PropertyFactory.circleColor(Color.RED),
////                                        PropertyFactory.circleStrokeWidth(700F),
////                                        PropertyFactory.backgroundColor(Color.RED),
////                                        PropertyFactory.circleRadius(800f)
////                                );
////                                style.addLayer(lineLayer);
////                                Feature customFeature = Feature.fromGeometry(location());
////
////// Create a GeoJsonSource with your custom feature
////                                GeoJsonSource customSource = new GeoJsonSource("my-custom-source", customFeature);
////                                style.addSource(customSource);
////                                CircleLayer lineLayer = new CircleLayer("my-line-layer", "1")
////                                        .withProperties(
//////                                                PropertyFactory.lineWidth(5f), // Set the line width in pixels
//////                                                PropertyFactory.lineColor(Color.parseColor("#FF0000")),// Set the line color
////                                                PropertyFactory.circleRadius(300f),
////                                                PropertyFactory.circleColor(Color.RED)
////                                        );
////
////// Add the layer to the map
////                                mapboxMap.getStyle().addLayer(lineLayer);
//                                initRouteCoordinates();
//                                locationComponent = mapboxMap.getLocationComponent();
//
//                                locationComponent.activateLocationComponent(MainActivity.this, style);
//                                locationComponent.setLocationComponentEnabled(true);
//                                locationComponent.setCameraMode(CameraMode.TRACKING_GPS);
//                                locationComponent.setRenderMode(RenderMode.GPS);
//                                mapboxMap.setMinZoomPreference(17);
//                                mapboxMap.setMaxZoomPreference(17);
//                                mapboxMap.setMinPitchPreference(60);
//                                mapboxMap.setMaxPitchPreference(60);
//                                double groundResolution = 156543.03392 * Math.cos(Math.toRadians(mapboxMap.getCameraPosition().target.getLatitude())) / Math.pow(2, 17);
//                                double desiredWidthPixels = distance(33.83788850060048, 35.987967588886875, 33.8379085515794, 35.98812650976308) * 3.77953 * 1609.34 / groundResolution;
//                                polyline=mapboxMap.addPolyline(new PolylineOptions()
//                                        .width((float) desiredWidthPixels-10)
//                                        .color(Color.RED)
//                                        .alpha(0.4f).add(new LatLng(33.83788850060048, 35.987967588886875)));
//                                style.addSource(new GeoJsonSource("line-source",
//                                        FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
//                                                LineString.fromLngLats(routeCoordinates)
//                                        )})));
//
//                                // Create the LineString from the list of coordinates and then make a GeoJSON
//                                // FeatureCollection so we can add the line to our map as a layer.
//
//                                // The layer properties for our line. This is where we make the line dotted, set the
//                                // color, etc.
////                                style.addLayer(new LineLayer("linelayer", "line-source").withProperties(
//////                                        PropertyFactory.lineDasharray(new Float[] {2f, 2f}),
//////                                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//////                                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
////                                        PropertyFactory.lineWidth((float) desiredWidthPixels-40),
////                                        PropertyFactory.lineColor(Color.RED),
////                                        PropertyFactory.lineOpacity(0.4f)
////
////                                        ));
//                                style.addLayer(new LineLayer("linelayer1", "line-source").withProperties(
//                                        PropertyFactory.lineDasharray(new Float[]{2f, 2f}),
//                                        PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//                                        PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
//                                        PropertyFactory.lineWidth(5f),
//                                        PropertyFactory.lineColor(Color.GREEN),
//                                        PropertyFactory.lineOpacity(0.5f)
//                                ));
//                                monitorLocation(mapboxMap);
//                            }
//
////                                List<Feature> markerCoordinates = new ArrayList<>();
////                                markerCoordinates.add(Feature.fromGeometry(
////                                        Point.fromLngLat(40.528051998926884, -3.5767554607059475))); // Boston Common Park
////                                markerCoordinates.add(Feature.fromGeometry(
////                                        Point.fromLngLat(40.51820459727463, -3.5641910043388996))); // Fenway Park
////                                markerCoordinates.add(Feature.fromGeometry(
////                                        Point.fromLngLat(40.509898435216314, -3.56122548047587))); // The Paul Revere House
////                                FeatureCollection featureCollection = FeatureCollection.fromFeatures(markerCoordinates);
////
////                                Source geoJsonSource = new GeoJsonSource("marker-source", featureCollection);
////                                style.addSource(geoJsonSource);
//
////                                Bitmap icon = BitmapFactory.decodeResource(
////                                       getResources(), R.drawable.ic_launcher_background);
//
//                            // Add the marker image to map
////                                style.addImage("my-marker-image", icon);
//
////                                SymbolLayer markers = new SymbolLayer("marker-layer", "marker-source")
////                                        .withProperties(PropertyFactory.iconImage("my-marker-image"),
////                                                PropertyFactory.backgroundColor(Color.RED)
////                                        );
////                                style.addLayer(markers);
////
////                                // Add the selected marker source and layer
////                                FeatureCollection emptySource = FeatureCollection.fromFeatures(new Feature[]{});
////                                Source selectedMarkerSource = new GeoJsonSource("selected-marker", emptySource);
////                                style.addSource(selectedMarkerSource);
////
////                                SymbolLayer selectedMarker = new SymbolLayer("selected-marker-layer", "selected-marker")
////                                        .withProperties(PropertyFactory.iconImage("my-marker-image"));
////                                style.addLayer(selectedMarker);
//
//
////                                mapboxMap.addPolyline(new PolylineOptions()
////                                        .width(60f)
////                                        .color(Color.GREEN)
////                                        .alpha(1f)
////                                        .add(new LatLng(40.51191574060161, -3.5776139018241926),new LatLng(40.49690316093934, -3.5748044581644804)));
//
//                        });
//                    }
//                });
//            }
//        });
        // Define the offline region
        OfflineTilePyramidRegionDefinition definition = new OfflineTilePyramidRegionDefinition(
           Style.SATELLITE,
                latLngBounds,
                15,
                19,
                4.0f,false)
                ;
        // Implementation that uses JSON to store Yosemite National Park as the offline region name.
        byte[] metadata;
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("Lawson Army Airfield (KLSF)", "Airport");
            String json = jsonObject.toString();
            metadata = json.getBytes();
        } catch (Exception exception) {
            Log.e(TAG, "Failed to encode metadata: " + exception.getMessage());
            metadata = null;
        }
        offlineManager.setOfflineMapboxTileCountLimit(200000);
 //Create the region asynchronously
        offlineManager.createOfflineRegion(definition, metadata,
                new OfflineManager.CreateOfflineRegionCallback() {
                    @Override
                    public void onCreate(OfflineRegion offlineRegion) {
                        offlineRegion.setDownloadState(OfflineRegion.STATE_ACTIVE);
                     //   offlineManager.setOfflineMapboxTileCountLimit(20000);
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
                                long totalSize = status.getCompletedResourceSize();

                                // Number of completed resources (tiles)
                                long completedTileCount = status.getCompletedResourceCount();

                                // Calculate the average size of each tile
                                long averageTileSize = totalSize / completedTileCount;

                                Log.d("TileSize", "Average tile size: " + averageTileSize + " bytes");
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

//    private double distance(double lat1, double lon1, double lat2, double lon2) {
//        double theta = lon1 - lon2;
//        double dist = Math.sin(deg2rad(lat1))
//                * Math.sin(deg2rad(lat2))
//                + Math.cos(deg2rad(lat1))
//                * Math.cos(deg2rad(lat2))
//                * Math.cos(deg2rad(theta));
//        dist = Math.acos(dist);
//        dist = rad2deg(dist);
//        dist = dist * 60 * 1.1515;
//        return (dist);
//    }
//
//    private double deg2rad(double deg) {
//        return (deg * Math.PI / 180.0);
//    }
//
//    private double rad2deg(double rad) {
//        return (rad * 180.0 / Math.PI);
//    }
//
//    public void setPoints() {
//
//    }
//
//    @NonNull
//    public Point location() {
//        return Point.fromLngLat(40.50438020164894, -3.5747264180628213);
//    }

//    private void initRouteCoordinates() {
//        // Create a list to store our line coordinates.
//        routeCoordinates = new ArrayList<>();
//        routeCoordinates.add(Point.fromLngLat(midPoint(33.83814065532409, 35.988412787138806, 33.83790981279798, 35.9879419621058).getLongitude(), midPoint(33.83814065532409, 35.988412787138806, 33.83790981279798, 35.9879419621058).getLatitude()));
//        routeCoordinates.add(Point.fromLngLat(midPoint(33.82671257288266, 35.97718130715028, 33.82676634996836, 35.977042588994614).getLongitude(), midPoint(33.82671257288266, 35.97718130715028, 33.82676634996836, 35.977042588994614).getLatitude()));
//        Log.d("Midpoint",midPoint(33.83814065532409, 35.988412787138806, 33.83790981279798, 35.9879419621058)+"");
//        routeCoordinates.add(Point.fromLngLat(40.509898435216314, -3.56122548047587));
//        routeCoordinates.add(Point.fromLngLat(40.50176926625812, -3.5696538114550074));
//        routeCoordinates.add(Point.fromLngLat(-118.39372033447427, 33.39728514560042));
//        routeCoordinates.add(Point.fromLngLat(-118.3930882271826, 33.39756875508861));
//        routeCoordinates.add(Point.fromLngLat(-118.3928216241072, 33.39759029501192));
//        routeCoordinates.add(Point.fromLngLat(-118.39227981785722, 33.397234885594564));
//        routeCoordinates.add(Point.fromLngLat(-118.392021814881, 33.397005125197666));
//        routeCoordinates.add(Point.fromLngLat(-118.39090810203379, 33.396814854409186));
//        routeCoordinates.add(Point.fromLngLat(-118.39040499623022, 33.39696563506828));
//        routeCoordinates.add(Point.fromLngLat(-118.39005669221234, 33.39703025527067));
//        routeCoordinates.add(Point.fromLngLat(-118.38953208616074, 33.39691896489222));
//        routeCoordinates.add(Point.fromLngLat(-118.38906338075398, 33.39695127501678));
//        routeCoordinates.add(Point.fromLngLat(-118.38891287901787, 33.39686511465794));
//        routeCoordinates.add(Point.fromLngLat(-118.38898167981154, 33.39671074380141));
//        routeCoordinates.add(Point.fromLngLat(-118.38984598978178, 33.396064537239404));
//        routeCoordinates.add(Point.fromLngLat(-118.38983738968255, 33.39582400356976));
//        routeCoordinates.add(Point.fromLngLat(-118.38955358640874, 33.3955978295119));
//        routeCoordinates.add(Point.fromLngLat(-118.389041880506, 33.39578092284221));
//        routeCoordinates.add(Point.fromLngLat(-118.38872797688494, 33.3957916930261));
//        routeCoordinates.add(Point.fromLngLat(-118.38817327048618, 33.39561218978703));
//        routeCoordinates.add(Point.fromLngLat(-118.3872530598711, 33.3956265500598));
//        routeCoordinates.add(Point.fromLngLat(-118.38653065153775, 33.39592811523983));
//        routeCoordinates.add(Point.fromLngLat(-118.38638444985126, 33.39590657490452));
//        routeCoordinates.add(Point.fromLngLat(-118.38638874990086, 33.395737842093304));
//        routeCoordinates.add(Point.fromLngLat(-118.38723155962309, 33.395027006653244));
//        routeCoordinates.add(Point.fromLngLat(-118.38734766096238, 33.394441819579285));
//        routeCoordinates.add(Point.fromLngLat(-118.38785936686516, 33.39403972556368));
//        routeCoordinates.add(Point.fromLngLat(-118.3880743693453, 33.393616088784825));
//        routeCoordinates.add(Point.fromLngLat(-118.38791956755958, 33.39331092541894));
//        routeCoordinates.add(Point.fromLngLat(-118.3874852625497, 33.39333964672257));
//        routeCoordinates.add(Point.fromLngLat(-118.38686605540683, 33.39387816940854));
//        routeCoordinates.add(Point.fromLngLat(-118.38607484627983, 33.39396792286514));
//        routeCoordinates.add(Point.fromLngLat(-118.38519763616081, 33.39346171215717));
//        routeCoordinates.add(Point.fromLngLat(-118.38523203655761, 33.393196040109466));
//        routeCoordinates.add(Point.fromLngLat(-118.3849955338295, 33.393023711860515));
//        routeCoordinates.add(Point.fromLngLat(-118.38355931726203, 33.39339708930139));
//        routeCoordinates.add(Point.fromLngLat(-118.38323251349217, 33.39305243325907));
//        routeCoordinates.add(Point.fromLngLat(-118.3832583137898, 33.39244928189641));
//        routeCoordinates.add(Point.fromLngLat(-118.3848751324406, 33.39108499551671));
//        routeCoordinates.add(Point.fromLngLat(-118.38522773650804, 33.38926830725471));
//        routeCoordinates.add(Point.fromLngLat(-118.38508153482152, 33.38916777794189));
//        routeCoordinates.add(Point.fromLngLat(-118.38390332123025, 33.39012280171983));
//        routeCoordinates.add(Point.fromLngLat(-118.38318091289693, 33.38941192035707));
//        routeCoordinates.add(Point.fromLngLat(-118.38271650753981, 33.3896129783018));
//        routeCoordinates.add(Point.fromLngLat(-118.38275090793661, 33.38902416443619));
//        routeCoordinates.add(Point.fromLngLat(-118.38226930238106, 33.3889451769069));
//        routeCoordinates.add(Point.fromLngLat(-118.38258750605169, 33.388420985121336));
//        routeCoordinates.add(Point.fromLngLat(-118.38177049662707, 33.388083490107284));
//        routeCoordinates.add(Point.fromLngLat(-118.38080728551597, 33.38836353925403));
//        routeCoordinates.add(Point.fromLngLat(-118.37928506795642, 33.38717870977523));
//        routeCoordinates.add(Point.fromLngLat(-118.37898406448423, 33.3873079646849));
//        routeCoordinates.add(Point.fromLngLat(-118.37935386875012, 33.38816247841951));
//        routeCoordinates.add(Point.fromLngLat(-118.37794345248027, 33.387810620840135));
//        routeCoordinates.add(Point.fromLngLat(-118.37546662390886, 33.38847843095069));
//        routeCoordinates.add(Point.fromLngLat(-118.37091717142867, 33.39114243958559));

 //   }
//
//    public static LatLng getDestinationLatLng(double lat, double longt, double phi, double distance) {
//        double r = 6378100;
//        double bearing = Math.toRadians(phi);
//        double d = distance;
//        double lat1 = Math.toRadians(lat);
//        double long1 = Math.toRadians(longt);
//        double lat2 = Math.asin(Math.sin(lat1) * Math.cos(d / r) + Math.cos(lat1) * Math.sin(d / r) * Math.cos(bearing));
//        double long2 = long1 + Math.atan2(Math.sin(bearing) * Math.sin(d / r) * Math.cos(lat1), Math.cos(d / r) - Math.sin(lat1) * Math.sin(lat2));
//        lat2 = Math.toDegrees(lat2);
//        long2 = Math.toDegrees(long2);
//        return new LatLng(lat2, long2);
//    }
//
//    public static double getPathLength(double lat1, double lng1, double lat2, double lng2) {
//        int r = 6371000;
//        double lat1rads = Math.toRadians(lat1);
//        double lat2rads = Math.toRadians(lat2);
//        double deltaLat = Math.toRadians((lat2 - lat1));
//        double deltaLng = Math.toRadians((lng2 - lng1));
//        double a = Math.sin(deltaLat / 2) * Math.sin(deltaLat / 2) + Math.cos(lat1rads) * Math.cos(lat2rads) * Math.sin(deltaLng / 2) * Math.sin(deltaLng / 2);
//        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
//        double d = r * c;
//        return d;
//    }
//
//    public static LatLng midPoint(double  startLat,double startlong, double endlat,double endlong) {
//        double lat1 = Math.toRadians(startLat);
//        double lon1 = Math.toRadians(startlong);
//        double lat2 = Math.toRadians(endlat);
//        double lon2 = Math.toRadians(endlong);
//
//        // Calculate the midpoint
//        double dLon = lon2 - lon1;
//        double bx = Math.cos(lat2) * Math.cos(dLon);
//        double by = Math.cos(lat2) * Math.sin(dLon);
//        double lat3 = Math.atan2(Math.sin(lat1) + Math.sin(lat2), Math.sqrt((Math.cos(lat1) + bx) * (Math.cos(lat1) + bx) + by * by));
//        double lon3 = lon1 + Math.atan2(by, Math.cos(lat1) + bx);
//
//        // Convert back to degrees
//        double midpointLat = Math.toDegrees(lat3);
//        double midpointLon = Math.toDegrees(lon3);
//
//        return new LatLng(midpointLat, midpointLon);
//    }
//
//    public void monitorLocation(MapboxMap map) {
//        LocationRequest locationRequest = new LocationRequest();
//        locationRequest.setPriority(Priority.PRIORITY_HIGH_ACCURACY);
//        locationRequest.setInterval(400);
//        locationRequest.setFastestInterval(400);
//        locationRequest.setWaitForAccurateLocation(true);
//        LocationCallback locationCallback = new LocationCallback() {
//            @Override
//            public void onLocationAvailability(@NonNull LocationAvailability locationAvailability) {
//                super.onLocationAvailability(locationAvailability);
//            }
//
//            @Override
//            public void onLocationResult(@NonNull LocationResult locationResult) {
//                super.onLocationResult(locationResult);
//                for (Location location : locationResult.getLocations()) {
//                    if (location != null) {
//                        polyline.addPoint(new LatLng(location.getLatitude(), location.getLongitude()));
//                        double distanceToS1 = getPathLength(location.getLatitude(), location.getLongitude(), 33.83788850060048, 35.987967588886875);
//                        double distanceToS2 = getPathLength(location.getLatitude(), location.getLongitude(), 33.8379085515794, 35.98812650976308);
//                        if (distanceToS1 != distanceToS2) {
//                            if (distanceToS1 - distanceToS2 > 10 || distanceToS2 - distanceToS1 > 10) {
//                                Toast.makeText(MainActivity.this, "You are not operating in the middle", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    }
//                }
//            }
//        };
//        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return;
//        }
//        LocationServices.getFusedLocationProviderClient(this).requestLocationUpdates(locationRequest, locationCallback, Looper.getMainLooper());
//    }
}