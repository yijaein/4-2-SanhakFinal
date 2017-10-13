/*
2017_10_11 이재인 MYinformation 프래그먼트 추가
 */

package com.google.firebase.quickstart.database;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.quickstart.database.Notification.MyFirebaseMessagingService;
import com.google.firebase.quickstart.database.fragment.MyPostsFragment;
import com.google.firebase.quickstart.database.fragment.MyTopPostsFragment;
import com.google.firebase.quickstart.database.fragment.MyInformation;
import com.google.firebase.quickstart.database.fragment.RecentPostsFragment;

public class  MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    long lastPressed;//뒤로가기 버튼 누를 때 시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new MyInformation(),
                    new MyPostsFragment(),
                    new RecentPostsFragment(),
                    new MyTopPostsFragment(),


            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.heading_my_information),
                    getString(R.string.heading_recent),
                    getString(R.string.heading_my_posts),
                    getString(R.string.heading_my_top_posts)


            };
            @Override
            public Fragment getItem(int position) {
                return mFragments[position];
            }
            @Override
            public int getCount() {
                return mFragments.length;
            }
            @Override
            public CharSequence getPageTitle(int position) {
                return mFragmentNames[position];
            }
        };
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mPagerAdapter);
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Button launches NewPostActivity
        findViewById(R.id.fab_new_post).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, NewPostActivity.class));
            }
        });
        /*
        2017_10_12 이재인 firebase notification 가능하도록
         */

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG,"token: "+token);
        //Toast.makeText(MainActivity.this,token,Toast.LENGTH_LONG).show();


    }//oncreate end

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }
/*
    2017_09_28 이재인 뒤로가기
 */
    @Override
    public void onBackPressed() {

        if(System.currentTimeMillis() - lastPressed<1500){
            finish();
        }else{
            Toast.makeText(this,"한번 더 누르면 종료됩니다.",Toast.LENGTH_LONG).show();
            lastPressed= System.currentTimeMillis();


        }

    }


}
