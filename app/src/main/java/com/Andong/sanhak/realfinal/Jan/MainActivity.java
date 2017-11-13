/*
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.Andong.sanhak.realfinal.Jan;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.Andong.sanhak.realfinal.Jan.fragment.PoliceFragment;
import com.google.firebase.quickstart.Jan.R;
import com.Andong.sanhak.realfinal.Jan.fragment.ChatFragment;
import com.Andong.sanhak.realfinal.Jan.fragment.MainFragment;
import com.Andong.sanhak.realfinal.Jan.fragment.MyPostsFragment;
import com.Andong.sanhak.realfinal.Jan.fragment.MyTopPostsFragment;
import com.Andong.sanhak.realfinal.Jan.fragment.RecentPostsFragment;


/*
    2017_10_18 이재인 계정 아이콘을 누를 시 계정으로 이동
    2017_11_07 이재인 채팅 에딧텍스트 누를 시 화면 위로 이동하도록 수정
 */
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";

    private FragmentPagerAdapter mPagerAdapter;
    private ViewPager mViewPager;
    long lastPressed;//뒤로가기 버튼 누를 때 시간

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*
        2017_10_18 이재인 커스텀 타이틀 바
         */
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        /*
        2017_10_20 이재인 네비게이션 메뉴
        2017_10_24 이재인 네비게이션 메뉴바 기능 추가

         */








        // Create the adapter that will return a fragment for each section
        mPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            private final Fragment[] mFragments = new Fragment[] {
                    new MainFragment(),
                    new MyPostsFragment(),
                    new RecentPostsFragment(),
                    new MyTopPostsFragment(),
                    new ChatFragment(),
                    new PoliceFragment(),


            };
            private final String[] mFragmentNames = new String[] {
                    getString(R.string.heading_my_information),
                    getString(R.string.heading_recent),
                    getString(R.string.heading_my_posts),
                    getString(R.string.heading_my_top_posts),
                    getString(R.string.heading_my_chat),
                    getString(R.string.heading_my_police),


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


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int i = item.getItemId();
        if (i == R.id.action_logout) {
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(this, SignInActivity.class));
//            finish();
            Intent intent = new Intent(MainActivity.this,UserActivity.class);
            startActivity(intent);
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
