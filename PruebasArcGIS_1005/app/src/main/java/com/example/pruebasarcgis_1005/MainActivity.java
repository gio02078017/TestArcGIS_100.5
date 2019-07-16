package com.example.pruebasarcgis_1005;

import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.esri.arcgisruntime.ArcGISRuntimeEnvironment;
import com.esri.arcgisruntime.LicenseInfo;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.mapping.ArcGISMap;
import com.esri.arcgisruntime.mapping.Basemap;
import com.esri.arcgisruntime.mapping.view.DefaultMapViewOnTouchListener;
import com.esri.arcgisruntime.mapping.view.MapView;
import com.esri.arcgisruntime.layers.FeatureLayer;
import com.esri.arcgisruntime.data.ServiceFeatureTable;
import com.esri.arcgisruntime.portal.Portal;
import com.esri.arcgisruntime.security.UserCredential;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {

    private MapView mMapView;
    private static final String TAG = MainActivity.class.getSimpleName();
    ServiceFeatureTable serviceFeatureTable;
    FeatureLayer featureLayer;
    ArcGISMap map;

    private void setupMap() {
        if (mMapView != null) {
            Basemap.Type basemapType = Basemap.Type.OPEN_STREET_MAP;
            double latitude = 6.234703;
            double longitude = -75.5514745;
            int levelOfDetail = 12;
            /*Basemap.Type basemapType = Basemap.Type.TOPOGRAPHIC_VECTOR;
            /*double latitude = 34.09042;
            double longitude = -118.71511;
            int levelOfDetail = 11;*/
            map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
            mMapView.setMap(map);
        }
    }

    private void addTrailheadsLayer() {

        /*Portal portal = new Portal("http://grupoepm.maps.arcgis.com", false);
        UserCredential credentials = new UserCredential("AppEPM","4pp3pm*-");
        portal.setCredential(credentials);
        portal.loadAsync();
        portal.addDoneLoadingListener(() -> {
            LicenseInfo licenseInfo = portal.getPortalInfo().getLicenseInfo();
            ArcGISRuntimeEnvironment.setLicense(licenseInfo);
        });*/

        //String url = "https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trailheads/FeatureServer/0";
        /*String url = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";
        serviceFeatureTable = new ServiceFeatureTable(url);
        featureLayer = new FeatureLayer(serviceFeatureTable);
        map = mMapView.getMap();
        map.getOperationalLayers().add(featureLayer);*/

        //String url = "https://utility.arcgis.com/usrsvcs/servers/19ba8b632aed49a3a8141dc20eb1a8ef/rest/services/Danios_Energia_PUBLICO/FeatureServer/0";
        //String url = "https://services3.arcgis.com/GVgbJbqm8hXASVYi/arcgis/rest/services/Trailheads/FeatureServer/0";
        String url = "http://sampleserver6.arcgisonline.com/arcgis/rest/services/DamageAssessment/FeatureServer/0";
        serviceFeatureTable = new ServiceFeatureTable(url);
        featureLayer = new FeatureLayer(serviceFeatureTable);
        map = mMapView.getMap();
        map.getOperationalLayers().add(featureLayer);
        mMapView.setOnTouchListener(new DefaultMapViewOnTouchListener(this, mMapView) {
            @Override public boolean onSingleTapConfirmed(MotionEvent event) {
                // create a point from where the user clicked
                android.graphics.Point point = new android.graphics.Point((int) event.getX(), (int) event.getY());

                // create a map point from a point
                Point mapPoint = mMapView.screenToLocation(point);

                // add a new feature to the service feature table
                addFeature(mapPoint, serviceFeatureTable);
                return super.onSingleTapConfirmed(event);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mMapView = findViewById(R.id.mapView);
        setupMap();
        addTrailheadsLayer();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();*/
                event();
            }
        });
    }

    @Override
    protected void onPause() {
        if (mMapView != null) {
            mMapView.pause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.resume();
        }
    }

    @Override
    protected void onDestroy() {
        if (mMapView != null) {
            mMapView.dispose();
        }
        super.onDestroy();
    }

    /**
     * Adds a new Feature to a ServiceFeatureTable and applies the changes to the
     * server.
     *
     * @param mapPoint     location to add feature
     * @param featureTable service feature table to add feature
     */
    private void addFeature(Point mapPoint, final ServiceFeatureTable featureTable) {

        // create default attributes for the feature
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("typdamage", "Destroyed");
        attributes.put("primcause", "Earthquake");

        // creates a new feature using default attributes and point
        Feature feature = featureTable.createFeature(attributes, mapPoint);

        // check if feature can be added to feature table
        if (featureTable.canAdd()) {
            // add the new feature to the feature table and to server
            featureTable.addFeatureAsync(feature).addDoneListener(() -> applyEdits(featureTable));
        } else {
            runOnUiThread(() -> logToUser(true, getString(R.string.error_cannot_add_to_feature_table)));
        }
    }

    /**
     * Sends any edits on the ServiceFeatureTable to the server.
     *
     * @param featureTable service feature table
     */
    private void applyEdits(ServiceFeatureTable featureTable) {

        // apply the changes to the server
        final ListenableFuture<List<FeatureEditResult>> editResult = featureTable.applyEditsAsync();
        editResult.addDoneListener(() -> {
            try {
                List<FeatureEditResult> editResults = editResult.get();
                // check if the server edit was successful
                if (editResults != null && !editResults.isEmpty()) {
                    if (!editResults.get(0).hasCompletedWithErrors()) {
                        runOnUiThread(() -> logToUser(false, getString(R.string.feature_added)));
                    } else {
                        throw editResults.get(0).getError();
                    }
                }
            } catch (InterruptedException | ExecutionException e) {
                runOnUiThread(() -> logToUser(true, getString(R.string.error_applying_edits, e.getCause().getMessage())));
            }
        });
    }

    /**
     * Shows a Toast to user and logs to logcat.
     *
     * @param isError whether message is an error. Determines log level.
     * @param message message to display
     */
    private void logToUser(boolean isError, String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        if (isError) {
            Log.e(TAG, message);
        } else {
            Log.d(TAG, message);
        }
    }

    public void event(){
        if (serviceFeatureTable.getFields() != null) {
            //FeatureType[] types = arcGISFeatureLayer.getTypes();
            List<FeatureType> types = serviceFeatureTable.getFeatureTypes();
            System.out.println("types " + types);
            for (FeatureType featureType : types) {
                //if (verifyFeatureTypeDanios(hasCoberturEnergia, hasCoberturaIluminaria, featureType))
                for (FeatureTemplate featureTemplate : featureType.getTemplates()) {
                    //TypeDanioOrFraude typeDanioOrFraude = new TypeDanioOrFraude();
                    System.out.println("featureTemplate" + featureTemplate);
                    //typeDanioOrFraude.setId(featureType.getId().toString());
                    //typeDanioOrFraude.setNameType(featureTemplate.getName());
                    //System.out.println("typeDanioOrFraude" +typeDanioOrFraude);
                    //listNameTypeDanio.add(typeDanioOrFraude);
                    //System.out.println("typeDanioOrFraude" +typeDanioOrFraude);
                }
                //}
            }
        }
    }
}
