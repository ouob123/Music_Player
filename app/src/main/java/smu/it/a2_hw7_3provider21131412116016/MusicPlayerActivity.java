package smu.it.a2_hw7_3provider21131412116016;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class MusicPlayerActivity extends AppCompatActivity implements View.OnClickListener {
    // 객체 선언
    ImageButton btn;
    private ArrayList<MusicData> list;
    private MediaPlayer mediaPlayer;
    private TextView title, tv_time, total_time;
    private ImageView album,previous,play,pause,next;
    private SeekBar seekBar;
    boolean isPlaying = true;
    private ContentResolver res;
    private ProgressUpdate progressUpdate;
    private int position;

    // 현재 재생시간을 원하는 형식으로 나타내기 위한 SimpleDateFormat 클래스 사용
    private SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);
        // 음악 정보 관련 객체 생성
        Intent intent = getIntent();
        mediaPlayer = new MediaPlayer();
        title = findViewById(R.id.title);
        tv_time = findViewById(R.id.textViewTime);
        total_time = findViewById(R.id.totalTime);
        album = findViewById(R.id.album);

        // intent를 통해 데이터를 받아옴
        position = intent.getIntExtra("position",0);

        // intent를 통해 데이터를 받아옴
        list = (ArrayList<MusicData>) intent.getSerializableExtra("playlist");
        // 데이터 접근을 위한 ContentResolver 얻어오기
        res = getContentResolver();


        // 음악 재생 컨트롤 관련 객체 생성
        previous = findViewById(R.id.pre);
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekbar);

        // 각 버튼에 클릭 이벤트 주기
        previous.setOnClickListener(this);
        play.setOnClickListener(this);
        pause.setOnClickListener(this);
        next.setOnClickListener(this);

        playMusic(list.get(position)); // 음악재생 함수 호출
        progressUpdate = new ProgressUpdate();
        progressUpdate.start();

        btn = findViewById(R.id.musicListButton);
        // 음악 목록 화면으로 돌아가는 이벤트 작성
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MusicPlayerActivity.this, MainActivity.class);
                startActivity(intent);
                mediaPlayer.stop(); // 버튼 누르면 음악도 중지
            }
        });

        // seekBar의 상태 변화시에 자동으로 호출되는 call back 리스너
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {  // 드래그 중 발생
                mediaPlayer.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { // 드래그를 멈추면 발생
                // seekBar로 음악 재생구간 설정
                // seekBar를 누르면 눌린 시간으로 mp3 시간을 이동
                mediaPlayer.seekTo(seekBar.getProgress()); // seekBar의 상태에 따른 재생 위치 지정
                // seekBar로 조정한 음악 재생 구간이 0보다 크거나 같고, 재생버튼이 보이지 않는 상태면 음악재생
                if(seekBar.getProgress()>=0 && play.getVisibility()==View.GONE){
                    mediaPlayer.start();
                    // 해당 곡의 현재 재생시간을 텍스트 뷰에 출력
                    new Thread() {
                        public void run() {
                            while (mediaPlayer.isPlaying()) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        tv_time.setText(timeFormat.format(mediaPlayer.getCurrentPosition()));
                                    }
                                }); SystemClock.sleep(200); // 0.2초동안 쉬게 함(멈춰있음)
                            }
                        }
                    }.start();
                }
            }
        });

        // 해당 곡의 재생이 끝났을 시에 수행하는 리스너
        // 다음 곡을 재생
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if(position+1<list.size()) {
                    position++; // 위치값 1 증가 (다음 곡)
                    playMusic(list.get(position)); // 다음 곡 재생
                }
            }
        });
        checkDangerousPermissions();
    }

    // 음악 재생 함수 정의
    public void playMusic(MusicData musicDto) {

        try {
            seekBar.setProgress(0); // seekBar의 진행상태 0으로 초기화
            title.setText(musicDto.getArtist()+" - "+musicDto.getTitle()); // 현재 곡의 제목 불러옴
            // 음악 파일의 경로(Uri)
            Uri musicURI = Uri.withAppendedPath(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, ""+musicDto.getId());
            mediaPlayer.reset();
            mediaPlayer.setDataSource(this, musicURI); // 파일경로로 음원 지정
            mediaPlayer.prepare(); // 로딩(준비)하는 함수: prepare()
            mediaPlayer.start();
            // 곡을 클릭하면 곡의 전체 시간을 seekBar에 Max 값으로 넣음
            // getDuration(): 재생 시간
            seekBar.setMax(mediaPlayer.getDuration());
            // 해당 곡의 전체 시간을 텍스트 뷰에 출력
            total_time.setText(timeFormat.format(mediaPlayer.getDuration()));
            // 해당 곡의 현재 재생시간을 텍스트 뷰에 출력
            new Thread() {
                public void run() {
                    while (mediaPlayer.isPlaying()) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                tv_time.setText(timeFormat.format(mediaPlayer.getCurrentPosition()));
                            }
                        }); SystemClock.sleep(200); // 0.2초동안 쉬게 함(멈춰있음)
                    }
                }
            }.start();

            if(mediaPlayer.isPlaying()){
                // 음악 재생중이라면 play 버튼 숨기고, pause 버튼 보이게
                play.setVisibility(View.GONE);
                pause.setVisibility(View.VISIBLE);
            }else{
                // 음악 재생중이 아니라면 play 버튼 보이게 하고, pause 버튼 숨김
                play.setVisibility(View.VISIBLE);
                pause.setVisibility(View.GONE);
            }

            // 음악 재생시 중앙에 앨범 커버 사진이 뜨도록 하는 기능
            // API 버전에 따른 오류로 제작한 이미지로 통일성 있게대체.
            //Bitmap bitmap = BitmapFactory.decodeFile(getCoverArtPath(Long.parseLong(musicDto.getAlbumId()),getApplication()));
            //album.setImageBitmap(bitmap);
        }
        catch (Exception e) {
            Log.e("SimplePlayer", e.getMessage());
        }
    }

    //앨범이 저장되어 있는 경로를 리턴
    private static String getCoverArtPath(long albumId, Context context) {

        Cursor albumCursor = context.getContentResolver().query(
                MediaStore.Audio.Albums.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Albums.ALBUM_ART},
                MediaStore.Audio.Albums._ID + " = ?",
                new String[]{Long.toString(albumId)},
                null
        );
        boolean queryResult = albumCursor.moveToFirst();
        String result = null;
        if (queryResult) {
            result = albumCursor.getString(0);
        }
        albumCursor.close();
        return result;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.play: // play(재생) 버튼 클릭 시
                pause.setVisibility(View.VISIBLE);
                play.setVisibility(View.GONE);
                // 현재 재생 위치를 가져와서 (다시) 재생할 위치 지정
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition());
                mediaPlayer.start();
                // 해당 곡의 현재 재생시간을 텍스트 뷰에 출력
                new Thread() {
                    public void run() {
                        while (mediaPlayer.isPlaying()) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    tv_time.setText(timeFormat.format(mediaPlayer.getCurrentPosition()));
                                }
                            }); SystemClock.sleep(200); // 0.2초동안 쉬게 함(멈춰있음)
                        }
                    }
                }.start();
                break;

            case R.id.pause: // pause(일시중지) 버튼 클릭 시
                pause.setVisibility(View.GONE);
                play.setVisibility(View.VISIBLE);
                mediaPlayer.pause();
                break;

            case R.id.pre: // previous(이전) 버튼 클릭 시
                if(position-1>=0 ){
                    position--; // 위치 값 하나 감소(-1)
                    playMusic(list.get(position));
                    seekBar.setProgress(0); // seekBar의 진행상태 0으로 초기화
                }
                break;

            case R.id.next:
                if(position+1<list.size()){
                    position++; // 위치 값 하나 증가(+1)
                    playMusic(list.get(position));
                    seekBar.setProgress(0); // seekBar의 진행상태 0으로 초기화
                }

                break;
        }
    }

    // 현재 작업 진행 상태 화면에 갱신
    class ProgressUpdate extends Thread{
        @Override
        public void run() {
            while(isPlaying){
                try {
                    Thread.sleep(500);
                    if(mediaPlayer!=null){
                        seekBar.setProgress(mediaPlayer.getCurrentPosition());
                    }
                } catch (Exception e) {
                    Log.e("ProgressUpdate",e.getMessage());
                }
            }
        }
    }

    // onDestroy() 메소드 재정의하여 MediaPlayer 해제
    @Override
    protected void onDestroy() {
        super.onDestroy();
        isPlaying = false;
        if(mediaPlayer!=null){
            // MediaPlayer 해제 후 무효화
            mediaPlayer.release(); // 올바르게 해제되었는지 확인
            mediaPlayer = null;
        }
    }

    // (오픈소스) 보안기능을 넣어줌. Permission 체크될 위험성이 있는 것들을 승인을 받는 기능.
    private void checkDangerousPermissions() {
        String[] permissions = {
                Manifest.permission.READ_EXTERNAL_STORAGE // 오픈소스이므로 다른 것은 건드리지 않고, 해당 줄만 변경.
                // 시스템에 영향을 줄 수 있는 것이 permission에 걸리기 때문에 작동하지 않는 부분을 위와 같이 작성해주면 됨.
                // 나머지 코드는 자동으로 체크해주므로 건드릴 필요없음.
        };

        int permissionCheck = PackageManager.PERMISSION_GRANTED;
        for (int i = 0; i < permissions.length; i++) {
            permissionCheck = ContextCompat.checkSelfPermission(this, permissions[i]);
            if (permissionCheck == PackageManager.PERMISSION_DENIED) {
                break;
            }
        }
        if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "권한 있음", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this, "권한 없음", Toast.LENGTH_LONG).show();

            if (ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[0])) {
                Toast.makeText(this, "권한 설명 필요함.", Toast.LENGTH_LONG).show();
            } else {
                ActivityCompat.requestPermissions(this, permissions, 1);
            }
        }
    }

    // (오픈소스) Toast로 권한을 승인할 것인지 띄워줌.
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            for (int i = 0; i < permissions.length; i++) {
                if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, permissions[i] + " 권한이 승인됨.", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(this, permissions[i] + " 권한이 승인되지 않음.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}