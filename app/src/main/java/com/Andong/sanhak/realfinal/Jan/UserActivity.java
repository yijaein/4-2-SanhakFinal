package com.Andong.sanhak.realfinal.Jan;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.quickstart.Jan.R;

/**
 * Created by Jan on 2017-10-19.
 * 2017_10_20 이재인 뒤로가기 버튼 추가
 *
 */

public class UserActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_activity);

        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.action_bar_setting);

        /*
        2017_10_20 이재인 뒤로가기 버튼
         */

//        ImageButton backBtn = (ImageButton)findViewById(R.id.action_back);
//        backBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
//                startActivity(intent);
//                finish();
//
//            }
//        });


        /*
        2017_10_19 이재인 다이얼로그를 띄어서 한 번 더 물어봄 -> 어플 종료
         */
        Button button = (Button)findViewById(R.id.logout_btn);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                AlertDialog.Builder builder = new AlertDialog.Builder(UserActivity.this);     // 여기서 this는 Activity의 this

// 여기서 부터는 알림창의 속성 설정
                builder.setTitle("확인을 누르면 어플이 종료됩니다.")        // 제목 설정
                        .setMessage("앱을 종료 하시 겠습니까?")        // 메세지 설정
                        .setCancelable(false)        // 뒤로 버튼 클릭시 취소 가능 설정
                        .setPositiveButton("확인", new DialogInterface.OnClickListener(){
                            // 확인 버튼 클릭시 설정
                            public void onClick(DialogInterface dialog, int whichButton){
                                FirebaseAuth.getInstance().signOut();
                                startActivity(new Intent(UserActivity.this, SignInActivity.class));
                                finish();
                                moveTaskToBack(true);
                                android.os.Process.killProcess(android.os.Process.myPid());
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
    }

}
