package com.Andong.sanhak.realfinal.Jan;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.Andong.sanhak.realfinal.Jan.Map.MapsActivity;
import com.Andong.sanhak.realfinal.Jan.models.Post;
import com.Andong.sanhak.realfinal.Jan.models.User;
import com.google.firebase.quickstart.Jan.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

/*
2017 _09_30 이재인 일단 이미지 올리는 것 완료
2017_11_02 이재인 작성일 추가
 */

public class NewPostActivity extends BaseActivity implements OnMapReadyCallback {

    private static final String TAG = "NewPostActivity";
    private static final String REQUIRED = "Required";

    // [START declare_database_ref]
    private DatabaseReference mDatabase;
    // [END declare_database_ref]

    private EditText mTitleField;
    private EditText mBodyField;
    private Button mLocationBtn;
    private FloatingActionButton mSubmitButton;
    private Button mImageBtn;
    private ImageView mImageView;
    private float lon;
    private float lat;
    private String stLon;
    private String stLat;
    private  static  final  int GALLERY_INTENT = 2;
    private ProgressDialog mProgressDialog;
    StorageReference filepath;
    private StorageReference mStorageRef;
    String StringUid;
    FirebaseUser user;

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;
    String StringEmail;
    String photoUri;
    int countPost;
    String postTime;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        mStorageRef = FirebaseStorage.getInstance().getReference();

        // [START initialize_database_ref]
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // [END initialize_database_ref]
        user = FirebaseAuth.getInstance().getCurrentUser();

        StringUid = user.getUid();
        StringEmail = user.getEmail();


        mTitleField = (EditText) findViewById(R.id.field_title);
        mBodyField = (EditText) findViewById(R.id.field_body);
        mLocationBtn= (Button)findViewById(R.id.gpsBtn);
        mImageBtn =(Button)findViewById(R.id.imgBtn);
        mSubmitButton = (FloatingActionButton) findViewById(R.id.fab_submit_post);
        mImageView = (ImageView)findViewById(R.id.lostThingImgView);
        mProgressDialog = new ProgressDialog(NewPostActivity.this);


        /*
        2017_09_28 이재인 위치 등록 버튼 ,이미지 버튼
         */

        mLocationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(NewPostActivity.this, MapsActivity.class);
                startActivity(intent);

            }
        });
        /*
        이미지 넣기
         */
        mImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
                startActivityForResult(intent,GALLERY_INTENT);

            }
        });

        mSubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitPost();

            }
        });








        SharedPreferences pref = getSharedPreferences("GPS", Activity.MODE_PRIVATE);
        lon = pref.getFloat("lon",0);
        lat = pref.getFloat("lat",0);

        stLon =Float.toString(lon);
        stLat =Float.toString(lat);


        /*
        2017_09_28 이재인 구글맵 프래그먼트 추가
         */
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapfrag1);
        mapFragment.getMapAsync(this);

//        checkPermissionREAD_EXTERNAL_STORAGE(getApplicationContext());




                /*
        2017_10_20 이재인 뒤로가기 버튼
         */
//        ImageButton backBtn = (ImageButton)findViewById(R.id.action_back);
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(NewPostActivity.this,MainActivity.class);
//                startActivity(intent);
//                finish();
//            }

    showDialog(1);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formattedDate = df.format(c.getTime());

        postTime = formattedDate;


    }//oncreate end



    /*
        구글맵
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {



        LatLng SEOUL = new LatLng(lat, lon);
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(SEOUL);
        markerOptions.title("서울");
        markerOptions.snippet("수도");
        googleMap.addMarker(markerOptions);
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));




    }

    private void submitPost() {
        final String title = mTitleField.getText().toString();
        final String body = mBodyField.getText().toString();

        // Title is required
        if (TextUtils.isEmpty(title)) {
            mTitleField.setError(REQUIRED);
            return;
        }

        // Body is required
        if (TextUtils.isEmpty(body)) {
            mBodyField.setError(REQUIRED);
            return;
        }

        // Disable button so there are no multi-posts
        setEditingEnabled(false);
        Toast.makeText(this, "Posting...", Toast.LENGTH_SHORT).show();

        // [START single_value_read]
        final String userId = getUid();


        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user value
                        User user = dataSnapshot.getValue(User.class);

                        // [START_EXCLUDE]
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userId + " is unexpectedly null");
                            Toast.makeText(NewPostActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            // Write new post
                            writeNewPost(userId, user.username, title, body,stLat,stLon, photoUri,postTime);

                        }

                        // Finish this Activity, back to the stream
                        setEditingEnabled(true);
                        finish();
                        // [END_EXCLUDE]
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.w(TAG, "getUser:onCancelled", databaseError.toException());
                        // [START_EXCLUDE]
                        setEditingEnabled(true);
                        // [END_EXCLUDE]
                    }
                });
        // [END single_value_read]

    }







    private void setEditingEnabled(boolean enabled) {
        mTitleField.setEnabled(enabled);
        mBodyField.setEnabled(enabled);
        if (enabled) {
            mSubmitButton.setVisibility(View.VISIBLE);
        } else {
            mSubmitButton.setVisibility(View.GONE);
        }
    }

    // [START write_fan_out]
    private void writeNewPost(String userId, String username, String title, String body,String lat,String lon ,String photoUri,String postTime) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mDatabase.child("posts").push().getKey();
        Post post = new Post(userId, username, title, body,lat,lon,photoUri,postTime);
        Map<String, Object> postValues = post.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/posts/" + key, postValues);
        childUpdates.put("/user-posts/" + userId + "/" + key, postValues);


        mDatabase.updateChildren(childUpdates);
    }
    // [END write_fan_out]






    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode ==GALLERY_INTENT &&resultCode == RESULT_OK ){
            mProgressDialog.setMessage("uploading....");
            mProgressDialog.show();

            final Uri uri = data.getData();
            filepath = mStorageRef.child("users").child(user.getEmail()).child(uri.getLastPathSegment());

            filepath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(NewPostActivity.this ,"Upload Done",Toast.LENGTH_SHORT).show();
                    try{
                        mProgressDialog.dismiss();
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(NewPostActivity.this.getContentResolver(),uri);
                        mImageView.setImageBitmap(bitmap);//이미지뷰에 이미지 저장

                        @SuppressWarnings("VisibleForTests")  Uri downloadUri = taskSnapshot.getDownloadUrl();
                        photoUri = String.valueOf(downloadUri);
                        Log.d("photoUri",String.valueOf(downloadUri));


                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference("photo");


                        Hashtable<String,String> lostThingPhoto = new Hashtable<String, String>();

                        lostThingPhoto.put("email",StringUid);
                        lostThingPhoto.put("photouri",photoUri);

                        myRef.child(StringUid).setValue(lostThingPhoto);
                        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String s = dataSnapshot.getValue().toString();
                                Log.d("Profile",s);
                                if (dataSnapshot!= null){
                                    Toast.makeText(NewPostActivity.this,"업로드 완료",Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });

                    }catch (IOException e ){
                        e.printStackTrace();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });


        }
    }//end onResult




}
