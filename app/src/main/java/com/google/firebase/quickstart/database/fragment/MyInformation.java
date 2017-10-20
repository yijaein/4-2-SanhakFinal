package com.google.firebase.quickstart.database.fragment;


import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
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

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.quickstart.database.MainActivity;
import com.google.firebase.quickstart.database.Map.MapsActivity;
import com.google.firebase.quickstart.database.Map.PermissionUtils;
import com.google.firebase.quickstart.database.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.quickstart.database.models.Post;


/**
 * Created by Jan on 2017-10-11.
 * 2017_10_12 이재인 프로필 사진을 추가할 수 있도록 추가
 * 2017_10_12 이재인 지도 추가
 * 2017_10_12 이재인 DB에서 정보를 받아와 마커로 찍음
 * 2017_10_13 이재인 내 위치를 좌표값으로 받아오기 ->locationManager 사용
 */

public class MyInformation extends android.support.v4.app.Fragment implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback, LocationListener,GoogleMap.OnMarkerClickListener{
    private static final String TAG = "MyInformation";
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

    public static double dLat = 0;
    public static double dLon = 0;

    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private boolean mPermissionDenied = false;

    /*
    2017_10_13 이재인 추가
    2017_10_14 이재인 내 위치 좌표값 불러와서 마커 찍힌 곳과 거리를 구하여 일정 거리 이하에 들어오면 notification을 띄운다
     */
    public static LocationManager locationManager;
    public static Location location;

    private static String bestProvider;

    public static String mapTitle = "니 위치 ";

    final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    double distance;

    Location point;

    String app_server_url = "http://jilee317.dothome.co.kr/php/fcm_insert.php";



    public MyInformation() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.myinformation, container, false);
        mAuth = FirebaseAuth.getInstance();

        user = FirebaseAuth.getInstance().getCurrentUser();
        Stringemail = user.getEmail();
        StringUid = user.getUid();
        TextView hello = (TextView) v.findViewById(R.id.hello);
        hello.setText("안녕하십니까 " + Stringemail + "님");
        mTitleView = (TextView) v.findViewById(R.id.post_title);




        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.AllMap);
        mapFragment.getMapAsync(this);

        /*
            2017_10_12 이재인 일단 데이터를 불러와서 좌표에 저장한다.

         */
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return v;
            }




//++++++++++++++++++++++++++++++++++++++++++++++++++++++==================================================================

        LocationManager locationManager = (LocationManager)getActivity().getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER,
                8000,
                10, this);




        return v;
    }//END onCreateView

    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
         2017_10_12 이재인 할 일 - > 좌표값을 받아와 여러개 마커를 찍는다.
         */
        mMap = googleMap;
        enableMyLocation();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(36.544049, 128.795994), 17));

        getSampleMarkerItems();

        mMap.setOnMarkerClickListener(this);

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

                if (mlat != null && mlon !=null){

                    dLat = Double.parseDouble(mlat);
                    dLon = Double.parseDouble(mlon);


                    Log.d("Tag4","DB Lat:"+dLat);
                    Log.d("Tag4","DB Lon:"+dLon);

                }
                if (dLat != 0 && dLon !=0 &&TitleView !=null){
                    for(int i=0; i<100;i++){
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions
                                .position(new LatLng(dLon,dLat))
                                .title(TitleView)
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker_phone));
                        mMap.addMarker(markerOptions);



                        point = new Location("point");
                        point.setLatitude(dLon);
                        point.setLongitude(dLat);






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
            if (getActivity() instanceof MapsActivity)
            PermissionUtils.requestPermission((MapsActivity) getActivity(), LOCATION_PERMISSION_REQUEST_CODE,
                    Manifest.permission.ACCESS_FINE_LOCATION, true);
        } else if (mMap != null) {
            // Access to the location has been granted to the app.
            mMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onLocationChanged(Location p_location) {
        if (location!=null)

            mapTitle = "당신의 위치";
            location = p_location;

            String msg = "New Latitude: " + location.getLatitude()
                    + "New Longitude: " + location.getLongitude();

            //Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
       /*
       2017_10_14 이재인 내 위치 저장해서 distanceTo 메소드 활용 거리를 구한다
        */
            Location mylocation = new Location("mylocation");
            mylocation.setLatitude(location.getLatitude());
            mylocation.setLongitude(location.getLongitude());

            Log.d("Tag8", "myLocation" + mylocation.getLatitude());

            distance = mylocation.distanceTo(point);

            distance = distance/1000f;


            Log.d(TAG, "거리" + distance);
            Log.d("Tag7", "distance" + distance);
            Toast.makeText(getActivity(), "거리:" + distance, Toast.LENGTH_LONG).show();



        /*
        2017_10_19 이재인 노티피케이션 거리 수정
         */

        if (distance<1){
            NotificationSomethings();

        }



    }
//    //위치 정보
//    private Location getPointLocation(){
//        /*
//        2017_10_13 이재인 for 문으로 point 좌표 생성
//         */
//
//        if (dLat != 0 && dLon !=0){
//            for(int i=0; i<100;i++){
//
//                Location point = new Location("point");
//                point.setLatitude(dLon);
//                point.setLongitude(dLat);
//
//
//
//            }
//        }

//                Location point = new Location("point");
//                point.setLatitude(dLon);
//                point.setLongitude(dLat);
//
//            Toast.makeText(getActivity(),"포인트값"+point,Toast.LENGTH_SHORT).show();
//




//        Location point = new Location("point");
//        point.setLatitude(36.545886);
//        point.setLongitude(128.792530);
//        return point;
    //}


    @Override
    public void onStatusChanged(String provider, int status, Bundle bundle) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onProviderDisabled(String provider) {
        /*if(LocationManager.GPS_PROVIDER.equalsIgnoreCase(provider)){
            location = null;
        }*/
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        /*
        2017_10_13 이재인  나중에 마커 클릭 시 해당 글로 이동하도록 만들어야함
         */

        Toast.makeText(getActivity(),"마커 클릭 ",Toast.LENGTH_SHORT).show();

        return false;
    }

    public void NotificationSomethings() {


        Resources res = getResources();

        Intent notificationIntent = new Intent(getActivity(), MainActivity.class);
        notificationIntent.putExtra("notificationId", 9999); //전달할 값
        PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getActivity());

        builder.setContentTitle("근처에 잃어버린 물건이 있습니다.")
                .setContentText("확인해보시겠습니까?")
                .setTicker("찾아주세용")
                .setSmallIcon(R.drawable.ic_find_loc)
                .setLargeIcon(BitmapFactory.decodeResource(res, R.drawable.ic_find_loc))
                .setContentIntent(contentIntent)
                .setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setDefaults(Notification.DEFAULT_ALL);



        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            builder.setCategory(Notification.CATEGORY_MESSAGE)
                    .setPriority(Notification.PRIORITY_HIGH)
                    .setVisibility(Notification.VISIBILITY_PUBLIC);
        }

        NotificationManager nm = (NotificationManager)getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
        nm.notify(1234, builder.build());
    }




}


