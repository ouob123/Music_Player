package smu.it.a2_hw7_3provider21131412116016;

import java.io.Serializable;

public class MusicData implements Serializable {
    // 클래스의 객체를 직렬화
    private String id;
    private String albumId;
    private String title;
    private String artist;

    public MusicData() {
    }

    public MusicData(String id, String albumId, String title, String artist) {
        this.id = id;
        this.albumId = albumId;
        this.title = title;
        this.artist = artist;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAlbumId() {
        return albumId;
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    // 자바 객체를 문자열로 표기
    @Override
    public String toString() {
        return "MusicData{" +
                "id='" + id + '\'' +
                ", albumId='" + albumId + '\'' +
                ", title='" + title + '\'' +
                ", artist='" + artist + '\'' +
                '}';
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }
}