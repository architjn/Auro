package com.architjn.acjmusicplayer.utils.items;

/**
 * Created by architjn on 28/11/15.
 */
public class Song {

    private long songId, albumId, dateAdded;
    private String name;
    private String artist;
    private String path;
    private String albumName;
    private long duration;
    private boolean fav;

    public Song(long songId, String name, String artist,
                String path, boolean fav, long albumId,
                String albumName, long dateAdded, long duration) {
        this.songId = songId;
        this.name = name;
        this.artist = artist;
        this.path = path;
        this.fav = fav;
        this.dateAdded = dateAdded;
        this.albumId = albumId;
        this.albumName = albumName;
        this.duration = duration;
    }

    public long getAlbumId() {
        return albumId;
    }

    public long getSongId() {
        return songId;
    }

    public String getArtist() {
        return artist;
    }

    public String getName() {
        return name;
    }

    public String getAlbumName() {
        return albumName;
    }

    public String getPath() {
        return path;
    }

    public boolean isFav() {
        return fav;
    }

    public long getDateAdded() {
        return dateAdded;
    }

    public long getDurationLong() {
        return duration;
    }

    public String getDuration() {
        try {
            Long time = duration;
            long seconds = time / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;

            if (seconds < 10) {
                return String.valueOf(minutes) + ":0" + String.valueOf(seconds);
            } else {
                return String.valueOf(minutes) + ":" + String.valueOf(seconds);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return String.valueOf(0);
        }
    }

    public String getFormatedTime(long duration) {
        try {
            Long time = duration;
            long seconds = time / 1000;
            long minutes = seconds / 60;
            seconds = seconds % 60;

            if (seconds < 10) {
                return String.valueOf(minutes) + ":0" + String.valueOf(seconds);

            } else {
                return String.valueOf(minutes) + ":" + String.valueOf(seconds);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return String.valueOf(0);
        }
    }
}
