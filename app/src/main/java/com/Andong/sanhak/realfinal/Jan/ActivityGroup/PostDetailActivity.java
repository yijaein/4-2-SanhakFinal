package com.Andong.sanhak.realfinal.Jan.ActivityGroup;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.Andong.sanhak.realfinal.Jan.models.Comment;
import com.Andong.sanhak.realfinal.Jan.models.Post;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.Andong.sanhak.realfinal.Jan.models.User;
import com.google.firebase.quickstart.Jan.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PostDetailActivity extends BaseActivity implements View.OnClickListener,OnMapReadyCallback {
/*
    2017_09_28 이재인 맵뷰 추가
    2017_09_28 이재인 맵 프래그먼트 추가 디비에서 직접 좌표값을 불러와야함
    2017_10_01 이재인 글에 있는 좌표값을 불러와서 지도에 띄우기는 성공했으나 바로바로 뜨지는 않는다  -> 나중에 수정
    2017_10_11 이재인 글을 등록 시 좌표값과 사진을 저장해서 띄우기 성공 -> 글을 등록하면 바로 볼 수 있도록 수정
    2017_11_07 이재인 피카소 라이브러리 centerinside -> crop
 */
    private static final String TAG = "PostDetailActivity";

    public static final String EXTRA_POST_KEY = "post_key";

    private DatabaseReference mPostReference;
    private DatabaseReference mCommentsReference;
    private ValueEventListener mPostListener;
    private String mPostKey;
    private CommentAdapter mAdapter;

    private TextView mAuthorView;
    private TextView mTitleView;
    private TextView mBodyView;
    private EditText mCommentField;
    private Button mCommentButton;
    private RecyclerView mCommentsRecycler;
    private String mAuthor;
    private TextView mPostTime;

    /*
    2017_09_29 이재인 일단 디비에서 좌표값을 받아와 PostDetailActivity에 뿌려주기 위해 쓰이는 변수
    2017_10_02 이재인 Post.class로 downloadUri를 받아온다.
    2017_10_23 이재인 글을 삭제할 경우 일어나는 nullPointException 오류 try/catch로  수정 글쓴이와 로그인한 아이디와 비교하여 같을 경우 버튼이 생성 버튼을 누르면 게시글이 삭제됨 -> 내일 할 일 사진도 같이 지운다.
    2017_11_02 이재인 작성일 추가
     */
    private String mlon;
    private String mlat;
    private MapView mapView = null;

    public static double dLat =0;
    public static double dLon =0;

    private StorageReference mStorageRef;

    String downloadUri;//이미지 저장경로
/*
        2017_10_11 이재인 이미지 다운로드 추가
 */
    String StringUid;
    String StringEmail;
    FirebaseUser user;
    ImageView mPostDetail ;
    Button findedBtn;
    private DatabaseReference mDatabase;








    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        // Get post key from intent
        mPostKey = getIntent().getStringExtra(EXTRA_POST_KEY);
        if (mPostKey == null) {
            throw new IllegalArgumentException("Must pass EXTRA_POST_KEY");
        }

        // Initialize Database
        mPostReference = FirebaseDatabase.getInstance().getReference()
                .child("posts").child(mPostKey);
        mCommentsReference = FirebaseDatabase.getInstance().getReference()
                .child("post-comments").child(mPostKey);

        // Initialize Views
        mAuthorView = (TextView) findViewById(R.id.post_author);
        mTitleView = (TextView) findViewById(R.id.post_title);
        mBodyView = (TextView) findViewById(R.id.post_body);
        mPostTime = (TextView)findViewById(R.id.post_time);
        mCommentField = (EditText) findViewById(R.id.field_comment_text);
        mCommentButton = (Button) findViewById(R.id.button_post_comment);
        mCommentsRecycler = (RecyclerView) findViewById(R.id.recycler_comments);
        mCommentButton.setOnClickListener(this);
        mCommentsRecycler.setLayoutManager(new LinearLayoutManager(this));


        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager().findFragmentById(R.id.mapfrag2);
        mapFragment.getMapAsync(this);

        mStorageRef = FirebaseStorage.getInstance().getReference();

        /*
        일단 post액티비티에서 값을 불러와서 그 값을 PostDetail에 저장한다
        mlon은 불러온 값이고 String으로 저장되어 있어서 그 값을 다시 float으로 변환해서 사용한다 =--> mapfragment에 좌표값으로 사용

        2017_10_11 이재인 추가
         */
        user = FirebaseAuth.getInstance().getCurrentUser();
        StringUid = user.getUid();
        StringEmail = user.getEmail();
        mPostDetail =(ImageView)findViewById(R.id.postdetailImgView);




    }//oncreate end



    @Override
    public void onStart() {
        super.onStart();

        // Add value event listener to the post
        // [START post_value_event_listener]
        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get Post object and use the values to update the UI
                Post post = dataSnapshot.getValue(Post.class);
                /*
                2017_10_23 이재인 글이 삭제되었을 때 오류 try/catch로 묶어버림
                2017_11_02 이재인 작성일 추가
                 */

                try{
                    // [START_EXCLUDE]
                    mAuthorView.setText(post.author);
                    mTitleView.setText(post.title);
                    mBodyView.setText(post.body);
                    mAuthor = post.author;
                    mPostTime.setText("작성일:"+post.postTime);

                    Log.d("Tag1", "Author " + mAuthor);

                    if(post.lat != null && post.lon!=null) {
                        mlat = post.lat;
                        mlon = post.lon;

                /*
                2017_09_28 이재인 글을 쓸 때 DB에 저장은 완료했는데 불러와서 지도에 뿌려줘야함
                 */
                        Log.d("Tag1", "DB:lat " + mlat);
                        Log.d("Tag1", "DB:lon " + mlon);
                        // [END_EXCLUDE]

                        dLat = Double.parseDouble(mlat);
                        dLon = Double.parseDouble(mlon);

                        Log.d("Tag2", "DB Lat:" + dLat);
                        Log.d("Tag2", "DB Lon:" + dLon);
                    }
                    downloadUri = post.photoUri;
                    Log.d("Tag2","photoUri:"+downloadUri);

                /*
                2017_10_23 이재인 작성자와 로그인한 아이디가 같으면 찾음 버튼이 뜬다.
                equals() 메소드는 string 객체의 문자열을 비교 체크하는 반면, == 연산자는 객체의 참조값이 같은지를 체크합니다.
                */

                    int indexOf=StringEmail.indexOf("@");
                    System.out.println(indexOf);
                    String emailTrim = StringEmail.substring(0,indexOf);
                    System.out.println(emailTrim);

                    Log.d("Tag1","userEmail:"+StringEmail);
                    Log.d("Tag1","userUid:"+StringUid);
                    Log.d("Tag1","userEmailTrim:"+emailTrim);


                    System.out.println(emailTrim.equals(mAuthor));


                    if (mAuthor!=null &&mAuthor.equals(emailTrim)){
                        findedBtn = (Button)findViewById(R.id.findedBtn);
                        findedBtn.setVisibility(View.VISIBLE);
                        Log.d("Tag1","userTrim"+emailTrim);
                        Toast.makeText(PostDetailActivity.this,"작성자임",Toast.LENGTH_SHORT).show();
                    /*
                    2017_10_23 이재인 만약 찾음 버튼을 누르면 글을 삭제한다.
                     */
                        findedBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                AlertDialog.Builder builder = new AlertDialog.Builder(PostDetailActivity.this);     // 여기서 this는 Activity의 this

                                    // 여기서 부터는 알림창의 속성 설정
                                builder.setTitle("확인을 누르면 게시글이 삭제됩니다.")        // 제목 설정
                                        .setMessage("물건을 찾으셨습니까?")        // 메세지 설정
                                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                                            // 확인 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton){
                                                final String userId = getUid();
                                                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/user-posts/"+userId+"/").child(mPostKey);
                                                final DatabaseReference databaseRaference1 = FirebaseDatabase.getInstance().getReference("posts").child(mPostKey);
                                                databaseReference.removeValue();
                                                databaseRaference1.removeValue();
                                                Toast.makeText(PostDetailActivity.this,"버튼 눌림",Toast.LENGTH_SHORT).show();



                                            }
                                        })
                                        .setNegativeButton("취소", new DialogInterface.OnClickListener(){
                                            // 취소 버튼 클릭시 설정
                                            public void onClick(DialogInterface dialog, int whichButton){
                                                dialog.cancel();
                                            }
                                        });

                                         AlertDialog dialog = builder.create();    // 알림창 객체 생성
                                         dialog.show();    // 알림창 띄우기

                            }
                        });

                    }else{
                        findedBtn = (Button)findViewById(R.id.findedBtn);
                        findedBtn.setVisibility(View.INVISIBLE);
                        Toast.makeText(PostDetailActivity.this,"작성자와 다름",Toast.LENGTH_SHORT).show();
                    }

                }catch (NullPointerException e){
                    Toast.makeText(PostDetailActivity.this,"글이 삭제되었습니다.",Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(PostDetailActivity.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
                // [START_EXCLUDE]
                Toast.makeText(PostDetailActivity.this, "Failed to load post.",
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        };
        mPostReference.addValueEventListener(postListener);
        // [END post_value_event_listener]

        // Keep copy of post listener so we can remove it when app stops
        mPostListener = postListener;

        // Listen for comments
        mAdapter = new CommentAdapter(this, mCommentsReference);
        mCommentsRecycler.setAdapter(mAdapter);

        /*
        2017_10_02 이재인 시작시 사진을 받아옴
        2017_10_11 이재인 사진 받아오기 완료 피카소 이용
         */

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();


        myRef.child("users").child(StringUid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue().toString();
                String stPhoto = downloadUri;

                if (TextUtils.isEmpty(stPhoto)){
                        Toast.makeText(PostDetailActivity.this,"이미지를 불러오지 못했습니다.",Toast.LENGTH_SHORT).show();
                        /*
                        2017_10_24 이재인 만약 저장된 이미지가 없다면 기본 이미지로 설정
                         */

                        Picasso.with(PostDetailActivity.this).load(R.drawable.parbin).fit().centerCrop().into(mPostDetail);
                }else{
                    Picasso.with(PostDetailActivity.this).load(stPhoto).fit().centerCrop().into(mPostDetail, new Callback.EmptyCallback() {
                        // Picasso.with(PostDetailActivity.this).load(stPhoto).fit().centerInside().into(mPostDetail, new Callback.EmptyCallback() {

                        @Override public void onSuccess() {
                            // Index 0 is the image view.
                           Log.d(TAG,"SUCCESS");
                        }
                    });
                }



                Log.d(TAG, "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });




        //  findedBtn =(Button)findViewById(R.id.findedBtn);
    }//onStartEnd
/*
    2017_09_28 이재인 구글맵 프래그먼트 추가
 */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        /*
        2017_09_29 이재인 왜 안되는 거지
         */
            LatLng SEOUL = new LatLng(dLon,dLat);
            Log.d("Tag3","DB Lat:"+dLat);
            Log.d("Tag3","DB Lon:"+dLon);
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(SEOUL);
            markerOptions.title("잃어버린 물건");
            markerOptions.snippet("요깅");
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(SEOUL));
            googleMap.animateCamera(CameraUpdateFactory.zoomTo(15));
            Toast.makeText(PostDetailActivity.this,"위치를 불러오는데 성공했습니다"+"위도"+dLat+"경도:"+dLon,Toast.LENGTH_SHORT).show();


    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);


    }

    @Override
    public void onStop() {
        super.onStop();

        // Remove post value event listener
        if (mPostListener != null) {
            mPostReference.removeEventListener(mPostListener);
        }

        // Clean up comments listener

    }



    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.button_post_comment) {
            postComment();
        }
    }

    private void postComment() {
        final String uid = getUid();
        FirebaseDatabase.getInstance().getReference().child("users").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Get user information
                        User user = dataSnapshot.getValue(User.class);
                        String authorName = user.username;

                        // Create new comment object
                        String commentText = mCommentField.getText().toString();
                        Comment comment = new Comment(uid, authorName, commentText);

                        // Push the comment, it will appear in the list
                        mCommentsReference.push().setValue(comment);

                        // Clear the field
                        mCommentField.setText(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    private static class CommentViewHolder extends RecyclerView.ViewHolder {

        public TextView authorView;
        public TextView bodyView;


        public CommentViewHolder(View itemView) {
            super(itemView);

            authorView = (TextView) itemView.findViewById(R.id.comment_author);
            bodyView = (TextView) itemView.findViewById(R.id.comment_body);


        }
    }

    private static class CommentAdapter extends RecyclerView.Adapter<CommentViewHolder> {

        private Context mContext;
        private DatabaseReference mDatabaseReference;
        private ChildEventListener mChildEventListener;

        private List<String> mCommentIds = new ArrayList<>();
        private List<Comment> mComments = new ArrayList<>();

        public CommentAdapter(final Context context, DatabaseReference ref) {
            mContext = context;
            mDatabaseReference = ref;

            // Create child event listener
            // [START child_event_listener_recycler]
            ChildEventListener childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildAdded:" + dataSnapshot.getKey());

                    // A new comment has been added, add it to the displayed list
                    Comment comment = dataSnapshot.getValue(Comment.class);

                    // [START_EXCLUDE]
                    // Update RecyclerView
                    mCommentIds.add(dataSnapshot.getKey());
                    mComments.add(comment);
                    notifyItemInserted(mComments.size() - 1);
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildChanged:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so displayed the changed comment.
                    Comment newComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Replace with the new data
                        mComments.set(commentIndex, newComment);

                        // Update the RecyclerView
                        notifyItemChanged(commentIndex);
                    } else {
                        Log.w(TAG, "onChildChanged:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Log.d(TAG, "onChildRemoved:" + dataSnapshot.getKey());

                    // A comment has changed, use the key to determine if we are displaying this
                    // comment and if so remove it.
                    String commentKey = dataSnapshot.getKey();

                    // [START_EXCLUDE]
                    int commentIndex = mCommentIds.indexOf(commentKey);
                    if (commentIndex > -1) {
                        // Remove data from the list
                        mCommentIds.remove(commentIndex);
                        mComments.remove(commentIndex);

                        // Update the RecyclerView
                        notifyItemRemoved(commentIndex);
                    } else {
                        Log.w(TAG, "onChildRemoved:unknown_child:" + commentKey);
                    }
                    // [END_EXCLUDE]
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String previousChildName) {
                    Log.d(TAG, "onChildMoved:" + dataSnapshot.getKey());

                    // A comment has changed position, use the key to determine if we are
                    // displaying this comment and if so move it.
                    Comment movedComment = dataSnapshot.getValue(Comment.class);
                    String commentKey = dataSnapshot.getKey();

                    // ...
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Log.w(TAG, "postComments:onCancelled", databaseError.toException());
                    Toast.makeText(mContext, "Failed to load comments.",
                            Toast.LENGTH_SHORT).show();
                }
            };
            ref.addChildEventListener(childEventListener);
            // [END child_event_listener_recycler]

            // Store reference to listener so it can be removed on app stop
            mChildEventListener = childEventListener;
        }

        @Override
        public CommentViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            View view = inflater.inflate(R.layout.item_comment, parent, false);
            return new CommentViewHolder(view);
        }

        @Override
        public void onBindViewHolder(CommentViewHolder holder, int position) {
            Comment comment = mComments.get(position);
            holder.authorView.setText(comment.author);
            holder.bodyView.setText(comment.text);

        }

        @Override
        public int getItemCount() {
            return mComments.size();
        }

        public void cleanupListener() {
            if (mChildEventListener != null) {
                mDatabaseReference.removeEventListener(mChildEventListener);
            }
        }

    }
/*
    2017_10_02 이재인 일단 downloadUri값을 NewPostActivity에서 받아와서 picasso로 출력한다.
 */





}
