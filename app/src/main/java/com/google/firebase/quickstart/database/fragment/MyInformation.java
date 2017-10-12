package com.google.firebase.quickstart.database.fragment;


import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.quickstart.database.Map.MapsActivity;
import com.google.firebase.quickstart.database.Map.PermissionUtils;
import com.google.firebase.quickstart.database.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.quickstart.database.models.Post;

import java.util.ArrayList;


/**
 * Created by Jan on 2017-10-11.
 * 2017_10_12 이재인 프로필 사진을 추가할 수 있도록 추가
 * 2017_10_12 이재인 지도 추가
 * 2017_10_12 이재인 DB에서 정보를 받아와 마커로 찍음 -> 내일 할 일 - > 지도 선 긋기
 */

public class MyInformation extends android.support.v4.app.Fragment implements OnMapReadyCallback,ActivityCompat.OnRequestPermissionsResultCallback {
    private static final String TAG = "PostDetailActivity";
    private FirebaseAuth mAuth;
    FirebaseUser user;
    String StringUid;
    String Stringemail;
    Marker selectedMarker;
    View marker_root_view;
    TextView tv_marker;
    private GoogleMap mMap;

    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private String mlon;
    private String mlat;

    String TitleView;

    public static double dLat =0;
    public static double dLon =0;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private  static  final  int LOCATION_PERMISSION_REQUEST_CODE=1;
    private  boolean mPermissionDenied = false;





    public MyInformation(){

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.myinformation, container, false);
        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        Stringemail = user.getEmail();
        StringUid = user.getUid();
        TextView hello = (TextView)v.findViewById(R.id.hello);
        hello.setText("안녕하십니까 "+Stringemail+"님");
        mTitleView = (TextView)v.findViewById(R.id.post_title);


        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager().findFragmentById(R.id.AllMap);
        mapFragment.getMapAsync(this);

        /*
            2017_10_12 이재인 일단 데이터를 불러와서 좌표에 저장한다.

         */

        enableMyLocation();



            return v;
    }//END onCreateView

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
         2017_10_12 이재인 할 일 - > 좌표값을 받아와 여러개 마커를 찍는다.
         */
        mMap = googleMap;
        enableMyLocation();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(37.537523, 126.96558), 17));

        getSampleMarkerItems();

    }

    /*
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
     */


    private void getSampleMarkerItems() {

        databaseReference.child("posts").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Post post = dataSnapshot.getValue(Post.class);

                TitleView = post.title;
                mlat=post.lat;
                mlon=post.lon;
                Log.d("Tag2","DB:title "+TitleView);
                Log.d("Tag3","DB:lat "+mlat);
                Log.d("Tag3","DB:lon "+mlon);

                if (mlat != null || mlon !=null){

                    dLat = Double.parseDouble(mlat);
                    dLon = Double.parseDouble(mlon);


                    Log.d("Tag4","DB Lat:"+dLat);
                    Log.d("Tag4","DB Lon:"+dLon);

                }
                if (dLat != 0 || dLon !=0){
                    for(int i=0; i<100;i++){
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions
                                .position(new LatLng(dLat,dLon))
                                .title(TitleView)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_phone));
                            mMap.addMarker(markerOptions);


                    }
                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });






        }


    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission to access the location is missing.
            PermissionUtils.requestPermission((MapsActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }













    }


