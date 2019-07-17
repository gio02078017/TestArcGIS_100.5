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
import com.esri.arcgisruntime.arcgisservices.LabelDefinition;
import com.esri.arcgisruntime.concurrent.ListenableFuture;
import com.esri.arcgisruntime.data.Feature;
import com.esri.arcgisruntime.data.FeatureEditResult;
import com.esri.arcgisruntime.data.FeatureQueryResult;
import com.esri.arcgisruntime.data.FeatureTemplate;
import com.esri.arcgisruntime.data.FeatureType;
import com.esri.arcgisruntime.data.Field;
import com.esri.arcgisruntime.geometry.Point;
import com.esri.arcgisruntime.geometry.SpatialReference;
import com.esri.arcgisruntime.io.RequestConfiguration;
import com.esri.arcgisruntime.loadable.LoadStatus;
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
    List<Field> fields;
    List<FeatureType> types;

    private void setupMap() {
        if (mMapView != null) {
            Basemap.Type basemapType = Basemap.Type.OPEN_STREET_MAP;
            double latitude = 6.234703;
            double longitude = -75.5514745;
            int levelOfDetail = 12;
            map = new ArcGISMap(basemapType, latitude, longitude, levelOfDetail);
            mMapView.setMap(map);
        }
    }

    private void addTrailheadsLayer() {

        Portal portal = new Portal("URL_Base", true);
        UserCredential credentials = new UserCredential("User","Password");
        portal.setCredential(credentials);
        portal.loadAsync();
        portal.addDoneLoadingListener(() -> {
            LicenseInfo licenseInfo = portal.getPortalInfo().getLicenseInfo();
            ArcGISRuntimeEnvironment.setLicense(licenseInfo);
        });

        RequestConfiguration request = RequestConfiguration.getGlobalRequestConfiguration();
        request.setForcePost(true);
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Referer", "NameReferer");
        request.setHeaders(headers);

        credentials = UserCredential.createFromToken("Token", "Referer");

        String url = "URL_Feature";
        serviceFeatureTable = new ServiceFeatureTable(url);
        serviceFeatureTable.setFeatureRequestMode(ServiceFeatureTable.FeatureRequestMode.MANUAL_CACHE);
        serviceFeatureTable.setCredential(credentials);
        serviceFeatureTable.addDoneLoadingListener(() ->{
            if (serviceFeatureTable.getLoadStatus() == LoadStatus.LOADED) {
                fields = serviceFeatureTable.getFields();
                Toast.makeText(this, "fields "+fields.toString(), Toast.LENGTH_LONG).show();
                if (fields != null) {
                    types = serviceFeatureTable.getFeatureTypes();
                    System.out.println("types " + types);
                    Toast.makeText(this, "types "+types.toString(), Toast.LENGTH_LONG).show();
                }
            }else if(serviceFeatureTable.getLoadStatus() == LoadStatus.FAILED_TO_LOAD){
                String error = "Service feature table failed to load: " + serviceFeatureTable.getLoadError().getCause();
                System.out.println("error "+error);
                return;
            }

        });
        featureLayer = new FeatureLayer(serviceFeatureTable);
        map = mMapView.getMap();
        map.getOperationalLayers().add(featureLayer);
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

    public void event(){
        Toast.makeText(this, "fields "+fields.toString(), Toast.LENGTH_LONG).show();
        if (fields != null) {
            //FeatureType[] types = arcGISFeatureLayer.getTypes();
            List<FeatureType> typeslocal = serviceFeatureTable.getFeatureTypes();
            Toast.makeText(this, "types local "+typeslocal.toString(), Toast.LENGTH_LONG).show();
            Toast.makeText(this, "types global "+types.toString(), Toast.LENGTH_LONG).show();
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
