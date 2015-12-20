package com.architjn.acjmusicplayer.utils.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.service.PlayerService;
import com.architjn.acjmusicplayer.task.AlbumItemLoad;
import com.architjn.acjmusicplayer.ui.layouts.activity.AlbumActivity;
import com.architjn.acjmusicplayer.ui.layouts.activity.ArtistActivity;
import com.architjn.acjmusicplayer.utils.ArtistImgHandler;
import com.architjn.acjmusicplayer.utils.ImageConverter;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.items.Album;
import com.architjn.acjmusicplayer.utils.items.Artist;
import com.architjn.acjmusicplayer.utils.items.Search;
import com.architjn.acjmusicplayer.utils.items.Song;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class SearchListAdapter extends RecyclerView.Adapter<SearchListAdapter.SimpleItemViewHolder> {
    public static final int ITEM_VIEW_TYPE_HEADER_ARTISTS = 0;
    public static final int ITEM_VIEW_TYPE_HEADER_ALBUMS = 1;
    public static final int ITEM_VIEW_TYPE_HEADER_SONGS = 2;
    public static final int ITEM_VIEW_TYPE_LIST_ARTIST = 3;
    public static final int ITEM_VIEW_TYPE_LIST_ALBUM = 4;
    public static final int ITEM_VIEW_TYPE_LIST_SONG = 5;
    private static final String TAG = "SearchListAdapter-TAG";
    private ArrayList<Song> songs;
    private ArrayList<Album> albums;
    private ArrayList<Artist> artists;
    private int headerArtistPos, headerAlbumPos, headerSongPos,
            totalSize;
    private Context context;

    public SearchListAdapter(Context context, ArrayList<Song> songs,
                             ArrayList<Album> albums,
                             ArrayList<Artist> artists) {
        this.context = context;
        init(songs, albums, artists);
    }

    private void init(ArrayList<Song> songs, ArrayList<Album> albums, ArrayList<Artist> artists) {
        this.songs = songs;
        this.albums = albums;
        this.artists = artists;
        headerArtistPos = 0;
        headerAlbumPos = this.artists.size() + 1;
        headerSongPos = this.artists.size() + this.albums.size() + 2;
        this.totalSize = songs.size() + albums.size() + artists.size() + 3;
    }

    public int whatView(int position) {
        if (position == headerArtistPos) {
            return ITEM_VIEW_TYPE_HEADER_ARTISTS;
        } else if (position == headerAlbumPos) {
            return ITEM_VIEW_TYPE_HEADER_ALBUMS;
        } else if (position == headerSongPos) {
            return ITEM_VIEW_TYPE_HEADER_SONGS;
        } else if (position > 0 && position < headerAlbumPos) {
            return ITEM_VIEW_TYPE_LIST_ARTIST;
        } else if (position > headerAlbumPos && position < headerSongPos) {
            return ITEM_VIEW_TYPE_LIST_ALBUM;
        } else
            return ITEM_VIEW_TYPE_LIST_SONG;
    }

    public void updateList(Search searchRes) {
        init(searchRes.getSongs(), searchRes.getAlbums(), searchRes.getArtists());
        notifyDataSetChanged();
    }

    private int getPosition(int position) {
        if (position > headerArtistPos && position < headerAlbumPos) {
            return position - 1;
        } else if (position > headerAlbumPos && position < headerSongPos) {
            return position - artists.size() - 2;
        } else
            return position - artists.size() - albums.size() - 3;
    }

    @Override
    public SearchListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView;
        switch (viewType) {
            case ITEM_VIEW_TYPE_HEADER_ALBUMS:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.search_header, parent, false);
                return new SimpleItemViewHolder(itemView);
            case ITEM_VIEW_TYPE_HEADER_SONGS:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.search_header, parent, false);
                return new SimpleItemViewHolder(itemView);
            case ITEM_VIEW_TYPE_HEADER_ARTISTS:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.search_header, parent, false);
                return new SimpleItemViewHolder(itemView);
            case ITEM_VIEW_TYPE_LIST_SONG:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.songs_list_item, parent, false);
                return new SimpleItemViewHolder(itemView);
            case ITEM_VIEW_TYPE_LIST_ARTIST:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.artist_list_item, parent, false);
                return new SimpleItemViewHolder(itemView);
            case ITEM_VIEW_TYPE_LIST_ALBUM:
                itemView = LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.album_grid_item, parent, false);
                return new SimpleItemViewHolder(itemView);

        }
        return null;
    }

    @Override
    public void onBindViewHolder(final SearchListAdapter.SimpleItemViewHolder holder, final int position) {
        if (whatView(position) == ITEM_VIEW_TYPE_HEADER_ARTISTS) {
            setHeaderBg(holder);
            if (artists.size() == 0) {
                holder.headerHolder.setVisibility(View.GONE);
                return;
            } else {
                holder.headerHolder.setVisibility(View.VISIBLE);
            }
            holder.headerText.setText(R.string.artists);
            return;
        } else if (whatView(position) == ITEM_VIEW_TYPE_HEADER_ALBUMS) {
            setHeaderBg(holder);
            if (albums.size() == 0) {
                holder.headerHolder.setVisibility(View.GONE);
                return;
            } else {
                holder.headerHolder.setVisibility(View.VISIBLE);
            }
            holder.headerText.setText(R.string.albums);
            return;
        } else if (whatView(position) == ITEM_VIEW_TYPE_HEADER_SONGS) {
            setHeaderBg(holder);
            if (songs.size() == 0) {
                holder.headerHolder.setVisibility(View.GONE);
                return;
            } else {
                holder.headerHolder.setVisibility(View.VISIBLE);
            }
            holder.headerText.setText(R.string.songs);
            return;
        } else if (whatView(position) == ITEM_VIEW_TYPE_LIST_ARTIST) {
            if (artists.size() == 0)
                return;
            holder.artistArt.setImageDrawable(context.getResources()
                    .getDrawable(R.drawable.default_artist_art, null));
            getArtistImg(holder, getPosition(position));
            holder.expandView.setVisibility(View.GONE);
            holder.artistName.setText(artists.get(getPosition(position)).getArtistName());
            holder.artistSongCount.setText(artists.get(getPosition(position)).getNumberOfSongs() + " "
                    + context.getResources().getString(R.string.songs) + " . " +
                    artists.get(getPosition(position)).getNumberOfAlbums() + " " +
                    context.getResources().getString(R.string.albums));
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, ArtistActivity.class);
                    i.putExtra("name", artists.get(getPosition(position)).getArtistName());
                    i.putExtra("id", artists.get(getPosition(position)).getArtistId());
                    context.startActivity(i);
                }
            });
        } else if (whatView(position) == ITEM_VIEW_TYPE_LIST_ALBUM) {
            if (albums.size() == 0)
                return;
            holder.albumArt.setImageDrawable(new ColorDrawable(0xffffffff));
            holder.albumName.setText(albums.get(getPosition(position)).getAlbumTitle());
            holder.albumArtist.setText(albums.get(getPosition(position)).getAlbumArtist());
            setArt(holder, getPosition(position));
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(context, AlbumActivity.class);
                    i.putExtra("albumName", albums.get(getPosition(position)).getAlbumTitle());
                    i.putExtra("albumId", albums.get(getPosition(position)).getAlbumId());
                    context.startActivity(i);
                }
            });
        } else {
            if (songs.size() == 0)
                return;
            holder.songArt.setImageDrawable(new ColorDrawable(0xffffffff));
            setSongArt(getPosition(position), holder);
            holder.songName.setText(songs.get(getPosition(position)).getName());
            holder.songArtist.setText(songs.get(getPosition(position)).getArtist());
            holder.mainView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent();
                    i.setAction(PlayerService.ACTION_PLAY_ALL_SONGS);
                    i.putExtra("songId", songs.get(getPosition(position)).getSongId());
                    i.putExtra("pos", getPosition(position));
                    context.sendBroadcast(i);
                }
            });
        }
        holder.mainView.setBackgroundColor(0xffffffff);
        holder.mainView.setElevation(dpToPx(2));
    }

    private void setHeaderBg(SimpleItemViewHolder holder) {
        holder.mainView.setBackgroundColor(context.getResources().getColor(R.color.appBackground));
        holder.mainView.setElevation(dpToPx(0));
    }

    private void setSongArt(int position, SimpleItemViewHolder holder) {
        String path = ListSongs.getAlbumArt(context,
                songs.get(position).getAlbumId());
        if (path != null)
            Picasso.with(context).load(new File(path)).resize(dpToPx(50),
                    dpToPx(50)).centerCrop().into(holder.songArt);
        else
            Picasso.with(context).load(R.drawable.default_art).into(holder.songArt);
    }


    public void getArtistImg(final SimpleItemViewHolder holder, int position) {
        ArtistImgHandler imgHandler = new ArtistImgHandler(context) {
            @Override
            public void onDownloadComplete(final String url) {
                if (url != null)
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(context).load(new File(url)).into(holder.artistArt);
                            setImageToView(url, holder);
                        }
                    });
            }
        };
        String path = imgHandler.getArtistImgFromDB("name");
        if (path != null && !path.matches("")) {
            setImageToView(path, holder);
        } else {
            String urlIfAny = imgHandler.getArtistArtWork(artists.get(position).getArtistName(), position);
            if (urlIfAny != null)
                setImageToView(urlIfAny, holder);
        }
    }

    public void setImageToView(String url, final SimpleItemViewHolder holder) {
        Picasso.with(context).load(new File(url)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
                holder.artistArt.setImageBitmap(circularBitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void setArt(SimpleItemViewHolder holder, int position) {
        //For album art
        if (albums.get(position).getAlbumArtPath() != null) {
            new AlbumItemLoad(context, albums.get(position).getAlbumArtPath(), holder).execute();
            setAlbumArt(position, holder);
        } else {
            int colorPrimary = context.getResources()
                    .getColor(R.color.colorPrimary);
            holder.albumArt.setImageDrawable(new ColorDrawable(colorPrimary));
            holder.bgView.setBackgroundColor(colorPrimary);
        }
    }

    private void setAlbumArt(int position, SimpleItemViewHolder holder) {
        String art = albums.get(position).getAlbumArtPath();
        if (art != null)
            Picasso.with(context).load(new File(art)).resize(dpToPx(180),
                    dpToPx(180)).centerCrop().into(holder.albumArt);
        else Picasso.with(context).load(R.drawable.default_art).into(holder.albumArt);
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    @Override
    public int getItemCount() {
        return totalSize;
    }

    @Override
    public int getItemViewType(int position) {
        return whatView(position);
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public View mainView;

        //For Header View
        public View headerHolder;
        public TextView headerText;

        //For Song View
        public TextView songName, songArtist;
        public ImageView songArt, songMenu;

        //For Artist View
        public TextView artistSongCount, artistName;
        public ImageView artistArt, artistMenu;
        public View expandView;

        //For Album View
        public TextView albumName, albumArtist;
        public ImageView albumArt;
        public View bgView;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;

            headerHolder = itemView.findViewById(R.id.search_header_holder);
            headerText = (TextView) itemView.findViewById(R.id.search_header_text);

            songName = (TextView) itemView.findViewById(R.id.song_item_name);
            songArtist = (TextView) itemView.findViewById(R.id.song_item_artist);
            songArt = (ImageView) itemView.findViewById(R.id.song_item_img);
            songMenu = (ImageView) itemView.findViewById(R.id.song_item_menu);

            artistName = (TextView) itemView.findViewById(R.id.artist_item_name);
            artistSongCount = (TextView) itemView.findViewById(R.id.artist_item_song_count);
            artistArt = (ImageView) itemView.findViewById(R.id.artist_item_img);
            artistMenu = (ImageView) itemView.findViewById(R.id.artist_item_menu);
            expandView = itemView.findViewById(R.id.expanded_area);

            albumName = (TextView) itemView.findViewById(R.id.album_list_name);
            albumArtist = (TextView) itemView.findViewById(R.id.album_list_artist);
            albumArt = (ImageView) itemView.findViewById(R.id.album_list_img);
            bgView = itemView.findViewById(R.id.album_list_bottom);
        }
    }

}
