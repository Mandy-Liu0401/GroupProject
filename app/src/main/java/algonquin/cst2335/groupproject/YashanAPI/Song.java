package algonquin.cst2335.groupproject.YashanAPI;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a song entity.
 */
@Entity
public class Song implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private int songId;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "artist")
    private String artist;

    @ColumnInfo(name = "duration")
    private int duration;

    @ColumnInfo(name = "albumName")
    private String albumName;

    @ColumnInfo(name = "albumCoverUrl")
    private String albumCoverUrl;

    public String getTitle() {
        return title;
    }

    public String getArtist() {
        return artist;
    }

    public int getDuration() {
        return duration;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getAlbumCoverUrl() {
        return albumCoverUrl;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void setAlbumName(String albumName) {
        this.albumName = albumName;
    }

    public void setAlbumCoverUrl(String albumCoverUrl) {
        this.albumCoverUrl = albumCoverUrl;
    }

    /**
     * Default constructor.
     */
    public Song() {
        // Default constructor
    }

    /**
     * Parameterized constructor.
     * @param songId The ID of the song.
     * @param title The title of the song.
     * @param artist The artist of the song.
     * @param duration The duration of the song.
     * @param albumName The name of the album.
     * @param albumCoverUrl The URL of the album cover.
     */
    public Song(int songId, String title, String artist, int duration, String albumName, String albumCoverUrl) {
        this.songId = songId;
        this.title = title;
        this.artist = artist;
        this.duration = duration;
        this.albumName = albumName;
        this.albumCoverUrl = albumCoverUrl;
    }

    // Getters and setters

    /**
     * Get the ID of the song.
     * @return The ID of the song.
     */
    public int getSongId() {
        return songId;
    }

    /**
     * Set the ID of the song.
     * @param songId The ID of the song.
     */
    public void setSongId(int songId) {
        this.songId = songId;
    }

    // Parcelable implementation

    protected Song(Parcel in) {
        songId = in.readInt();
        title = in.readString();
        artist = in.readString();
        duration = in.readInt();
        albumName = in.readString();
        albumCoverUrl = in.readString();
    }

    public static final Creator<Song> CREATOR = new Creator<Song>() {
        @Override
        public Song createFromParcel(Parcel in) {
            return new Song(in);
        }

        @Override
        public Song[] newArray(int size) {
            return new Song[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(songId);
        dest.writeString(title);
        dest.writeString(artist);
        dest.writeInt(duration);
        dest.writeString(albumName);
        dest.writeString(albumCoverUrl);
    }
}
