package smu.it.a2_hw7_3provider21131412116016;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

public class LoadingActivity extends AppCompatActivity {
    ImageView imageView;
    AnimationDrawable animationDrawable; // 애니메이션 효과를 줄 수 있는 기능
    Handler handler = new Handler(); // 핸들러 생성

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);
        //로딩화면 시작, 함수 호출
        LoadingStart();
    }

    // frame animation 함수 정의
    private void startAnimation() {
        // imageView를 여기서 생성한 이유: 어차피 글로벌 변수, 여기서 사용할 예정
        imageView = findViewById(R.id.loading);
        animationDrawable = (AnimationDrawable)imageView.getBackground();
        animationDrawable.setVisible(true, true);
        animationDrawable.start();
    }

    // 로딩화면 함수 정의
    private void LoadingStart(){
        startAnimation(); // 함수 호출, 애니메이션 실행
        // 3초 뒤에 Runnable 객체 안의 run이 실행
        handler.postDelayed(new Runnable(){
            public void run(){
                Intent intent=new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
                finish();
            }
        },3000); // 로딩화면 지속시간 3초(run이 실행되기까지 지연되는 시간)
    }
}