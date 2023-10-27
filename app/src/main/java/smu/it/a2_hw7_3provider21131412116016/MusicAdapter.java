package smu.it.a2_hw7_3provider21131412116016;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public class MusicAdapter extends BaseAdapter {
    // 객체 선언
    List<MusicData> list;
    LayoutInflater inflater;
    Activity activity;

    public MusicAdapter() {
    }

    public MusicAdapter(Activity activity, List<MusicData> list) {
        this.list = list;
        this.activity = activity;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        // LayoutInflater 객체 사용할 준비
    }

    // Adapter에 사용되는 데이터의 개수를 리턴 (필수 구현)
    @Override
    public int getCount() {
        return list.size();
    }

    // 지정한 위치(position)에 있는 데이터를 리턴 (필수 구현)
    @Override
    public Object getItem(int position) {
        return position;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템의 ID를 리턴 (필수 구현)
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // "listview_item" Layout 을 inflate 하여 convertView 참조 획득.
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.listview_item, parent, false);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            convertView.setLayoutParams(layoutParams);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        // 음악 재생 목록에서 앨범 아트를 아이콘으로 띄워주는 기능
        //ImageView imageView = (ImageView) convertView.findViewById(R.id.album);
        //Bitmap albumImage = getAlbumImage(activity, Integer.parseInt((list.get(position)).getAlbumId()), 170);
        //imageView.setImageBitmap(albumImage);
        // 앨범 아트가 없는 노래도 있어서 주석 처리 함.

        // 음악 재생 목록에서 제목을 나타내는 기능
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(list.get(position).getTitle());

        // 음악 재생 목록에서 아티스트명을 나타내는 기능
        TextView artist = (TextView) convertView.findViewById(R.id.artist);
        artist.setText(list.get(position).getArtist());

        return convertView;
    }

    private static final BitmapFactory.Options options = new BitmapFactory.Options();

    private static Bitmap getAlbumImage(Context context, int album_id, int MAX_IMAGE_SIZE) {
        // 실제로 ImageView에는 drawble을 표시하는 데 사용되는 1 픽셀 프레임이 존재함.
        // 나중에 확장할 필요가 없도록 지금 고려.
        ContentResolver res = context.getContentResolver();
        Uri uri = Uri.parse("content://media/external/audio/albumart/" + album_id);
        if (uri != null) {
            ParcelFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = res.openFileDescriptor(uri, "r");

                // 가장 가까운 제곱 인자 계산
                // sBitmapOptionsCache.inSampleSize 로  값을 전달
                // 결과적으로 디코딩 속도가 빨라지고 품질이 향상.

                //크기를 얻어오기 위한 옵션 ,
                //inJustDecodeBounds값이 true로 설정되면 decoder가 bitmap object에 대해 메모리를 할당하지 않고, 따라서 bitmap을 반환하지도 않는다.
                // 다만 options fields는 값이 채워지기 때문에 Load 하려는 이미지의 크기를 포함한 정보들을 얻어올 수 있다.
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);
                int scale = 0;
                if (options.outHeight > MAX_IMAGE_SIZE || options.outWidth > MAX_IMAGE_SIZE) {
                    scale = (int) Math.pow(2, (int) Math.round(Math.log(MAX_IMAGE_SIZE / (double) Math.max(options.outHeight, options.outWidth)) / Math.log(0.5)));
                }
                options.inJustDecodeBounds = false;
                options.inSampleSize = scale;

                Bitmap bitmap = BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);

                if (bitmap != null) {
                    // 원하는 사이즈로 재조정
                    if (options.outWidth != MAX_IMAGE_SIZE || options.outHeight != MAX_IMAGE_SIZE) {
                        Bitmap tmp = Bitmap.createScaledBitmap(bitmap, MAX_IMAGE_SIZE, MAX_IMAGE_SIZE, true);
                        bitmap.recycle();
                        bitmap = tmp;
                    }
                }
                return bitmap;
            } catch (FileNotFoundException e) {
            } finally {
                try {
                    if (fileDescriptor != null)
                        fileDescriptor.close();
                } catch (IOException e) {
                }
            }
        }
        return null;
    }
}
