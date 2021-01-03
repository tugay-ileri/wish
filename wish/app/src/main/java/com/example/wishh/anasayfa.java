package com.example.wishh;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hms.location.FusedLocationProviderClient;
import com.huawei.hms.location.LocationAvailability;
import com.huawei.hms.location.LocationCallback;
import com.huawei.hms.location.LocationRequest;
import com.huawei.hms.location.LocationResult;
import com.huawei.hms.location.LocationServices;
import com.huawei.hms.location.LocationSettingsRequest;
import com.huawei.hms.maps.CameraUpdateFactory;
import com.huawei.hms.maps.HuaweiMap;
import com.huawei.hms.maps.MapView;
import com.huawei.hms.maps.MapsInitializer;
import com.huawei.hms.maps.OnMapReadyCallback;
import com.huawei.hms.maps.model.LatLng;
import com.huawei.hms.site.api.SearchResultListener;
import com.huawei.hms.site.api.SearchService;
import com.huawei.hms.site.api.SearchServiceFactory;
import com.huawei.hms.site.api.model.AddressDetail;
import com.huawei.hms.site.api.model.Coordinate;
import com.huawei.hms.site.api.model.HwLocationType;
import com.huawei.hms.site.api.model.LocationType;
import com.huawei.hms.site.api.model.SearchStatus;
import com.huawei.hms.site.api.model.Site;
import com.huawei.hms.site.api.model.TextSearchRequest;
import com.huawei.hms.site.api.model.TextSearchResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.Permissions;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACCESS_WIFI_STATE;

public class anasayfa extends AppCompatActivity implements OnMapReadyCallback {
    //map
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LocationRequest mLocationRequest;
    private static final String TAG = "MapViewDemoActivity";
    private HuaweiMap hMap;
    private MapView mMapView;
    private static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";
    private Bundle savedInstanceState;
    private Object Permissions;
    private SearchService searchService;
    SeekBar seek ;
    TextView seektext;
    Button arama ;
    double enlem,boylam;
    LocationCallback mLocationCallback;
    LocationSettingsRequest.Builder builder;
    EditText search;
    private TextView resultTextView;
    private EditText queryInput;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_anasayfa);
        searchService = SearchServiceFactory.create(this, "CgB6e3x9IxH270OTUJpxzi4sJPbM1hFGRKXtequsm80wXXtU+QprTJXcvyEIVrY8KmEKR3y7R9meaml58ZrM/31M");
        tanımla();
        bundle_bilgi();

       seek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                seektext.setText( String.valueOf(progress)+" Metre");


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


    }
    public void arama()
    {

// Create a request body.
        Toast.makeText(this, ""+ searchService.toString(), Toast.LENGTH_LONG).show();
        String searchh = search.getText().toString();
        TextSearchRequest request = new TextSearchRequest();
        request.setQuery("Migros");
        request.setRadius(1000);
        request.setPoiType(LocationType.GROCERY_OR_SUPERMARKET);
        request.setCountryCode("TR");
        request.setLanguage("tr");
        request.setPageIndex(1);
        request.setPageSize(5);
        request.setChildren(false);
// Create a search result listener.
        SearchResultListener<TextSearchResponse> resultListener = new SearchResultListener<TextSearchResponse>() {
            // Return search results upon a successful search.
            @Override
            public void onSearchResult(TextSearchResponse results) {
                if (results == null || results.getTotalCount() <= 0) {
                    Toast.makeText(anasayfa.this, "arama yapılamıyor", Toast.LENGTH_SHORT).show();
                    return;
                }
                List<Site> sites = results.getSites();
                if(sites == null || sites.size() == 0){
                    Toast.makeText(anasayfa.this, "arama yapıldı", Toast.LENGTH_SHORT).show();
                    return;
                }
                for (Site site : sites) {
                    Toast.makeText(anasayfa.this, "arama yapıldı", Toast.LENGTH_SHORT).show();
                    Log.i("TAG5678", String.format("siteId: '%s', name: %s\r\n", site.getSiteId(), site.getName()));
                }
            }
            // Return the result code and description upon a search exception.
            @Override
            public void onSearchError(SearchStatus status) {
                Log.i("TAG", "Error : " + status.getErrorCode() + " " + status.getErrorMessage());
                Toast.makeText(anasayfa.this, ""+ status.getErrorCode() + " " + status.getErrorMessage(), Toast.LENGTH_SHORT).show();
            }
        };
// Call the keyword search API.
        searchService.textSearch(request, resultListener);

    }


    public void tanımla()
    {   search = findViewById(R.id.search);
        arama = findViewById(R.id.arama);
        Object permissions = Permissions;
        seek = findViewById(R.id.seek);
        seektext = findViewById(R.id.seektext);
        mMapView = findViewById(R.id.mapView);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }
    public void konum_guncelle()
    {
         builder = new LocationSettingsRequest.Builder();
         mLocationRequest = new LocationRequest();
         builder.addAllLocationRequests(Collections.singleton(mLocationRequest));
        LocationSettingsRequest locationSettingsRequest = builder.build();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult != null) {
                    List<Location> locations = locationResult.getLocations();
                    if (!locations.isEmpty()) {
                        for (Location location : locations) {
                            Toast.makeText(anasayfa.this, ""+location.getLatitude(), Toast.LENGTH_SHORT).show();
                            enlem = locationResult.getLastLocation().getLatitude();
                            boylam = locationResult.getLastLocation().getLongitude();
                            hMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(enlem,boylam),15));
                            Log.i(TAG,"onLocationResult location[Longitude,Latitude,Accuracy]:" + location.getLongitude() + "," + location.getLatitude() + "," + location.getAccuracy());
                        }
                    }
                }
            }

            @Override
            public void onLocationAvailability(LocationAvailability locationAvailability) {
                if (locationAvailability != null) {
                    boolean flag = locationAvailability.isLocationAvailable();
                    Log.i(TAG, "onLocationAvailability isLocationAvailable:" + flag);
                }
            }
        };
        fusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                    }
                });

    }

    public void bundle_bilgi()
    {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        MapsInitializer.setApiKey("CgB6e3x9IxH270OTUJpxzi4sJPbM1hFGRKXtequsm80wXXtU+QprTJXcvyEIVrY8KmEKR3y7R9meaml58ZrM/31M");
        mMapView.onCreate(mapViewBundle);
        //get map instance
        mMapView.getMapAsync(this);
    }
    public void izin()
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.i(TAG, "sdk >= 23 M");
            if (ActivityCompat.checkSelfPermission(this,
                    ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
            else
            {
                hMap.setMyLocationEnabled(true);
                hMap.getUiSettings().setMyLocationButtonEnabled(true);
            }

        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {

                hMap.setMyLocationEnabled(true);
                hMap.getUiSettings().setMyLocationButtonEnabled(true);
                konum_guncelle();

                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSION successful");
            }
            else
                {

                Log.i(TAG, "onRequestPermissionsResult: apply LOCATION PERMISSSION  failed");
                finish();
            }
        }

    }

    // activity balşladığında ilk önce map'i yükler.
    @Override
    public void onMapReady(HuaweiMap huaweiMap) {
        hMap = huaweiMap;
        izin();
        konum_guncelle();
        arama.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                arama();

            }
        });



    }
}
