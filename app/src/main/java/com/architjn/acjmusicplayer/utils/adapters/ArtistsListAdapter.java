package com.architjn.acjmusicplayer.utils.adapters;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.activity.ArtistActivity;
import com.architjn.acjmusicplayer.ui.layouts.activity.MainActivity;
import com.architjn.acjmusicplayer.ui.layouts.fragments.ArtistListFragment;
import com.architjn.acjmusicplayer.utils.handlers.ArtistImgHandler;
import com.architjn.acjmusicplayer.utils.decorations.ArtistSubListSpacesItemDecoration;
import com.architjn.acjmusicplayer.utils.ImageConverter;
import com.architjn.acjmusicplayer.utils.Utils;
import com.architjn.acjmusicplayer.utils.items.Artist;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by architjn on 28/11/15.
 */
public class ArtistsListAdapter extends RecyclerView.Adapter<ArtistsListAdapter.SimpleItemViewHolder> {

    private ArrayList<Artist> items;
    private ArtistListFragment fragment;
    private Context context;
    private int expandedPosition = -1;
    private SimpleItemViewHolder expandedHolder;
    private int expandSize = 255;
    private ArtistSubListAdapter extendedAdapter;

    public ArtistsListAdapter(Context context, ArrayList<Artist> items, ArtistListFragment fragment) {
        this.context = context;
        this.items = items;
        this.fragment = fragment;
    }

    @Override
    public ArtistsListAdapter.SimpleItemViewHolder onCreateViewHolder(ViewGroup parent,
                                                                      int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.artist_list_item, parent, false);
        return new SimpleItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ArtistsListAdapter.SimpleItemViewHolder holder,
                                 final int position) {
        Utils utils = new Utils(context);
        holder.img.setImageBitmap(utils.getBitmapOfVector(R.drawable.default_artist_art,
                utils.dpToPx(50), utils.dpToPx(50)));
        holder.name.setText(items.get(position).getArtistName());
        getArtistImg(holder, position);
        holder.songCount.setText(context.getResources().getString(R.string.songs)
                + " " + items.get(position).getNumberOfSongs() + " . "
                + context.getResources().getString(R.string.albums) + " "
                + items.get(position).getNumberOfAlbums());
        if (position == expandedPosition) {
            expandWithoutAnimation(holder);
        } else {
            collapseWithoutAnimation(holder);
        }
        setOnClicks(holder, position);
    }

    private void setOnClicks(final SimpleItemViewHolder holder, final int position) {
        holder.mainView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Check for an expanded view, collapse if you find one
                if (expandedPosition != position) {
                    if (expandedPosition >= 0) {
                        collapse();
                    }
                    expand(holder, position);
                } else {
                    Intent i = new Intent(context, ArtistActivity.class);
                    i.putExtra("name", items.get(position).getArtistName());
                    i.putExtra("id", items.get(position).getArtistId());
                    context.startActivity(i);
                }
            }
        });
        holder.songsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, ArtistActivity.class);
                i.putExtra("name", items.get(position).getArtistName());
                i.putExtra("id", items.get(position).getArtistId());
                context.startActivity(i);
            }
        });
        holder.shuffleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        holder.menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    public void setImageToView(String url, final SimpleItemViewHolder holder) {
        Picasso.with(context).load(new File(url)).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                Bitmap circularBitmap = ImageConverter.getRoundedCornerBitmap(bitmap, 100);
                holder.img.setImageBitmap(circularBitmap);
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {

            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {

            }
        });
    }

    private void expand(SimpleItemViewHolder holder, int position) {
        expandedPosition = position;
        expandedHolder = holder;
        animateElevation(0, 12, holder.mainView);
        holder.mainView.setBackgroundColor(0xffffffff);
        if (expandedPosition == -1)
            animateHeight(0, dpToPx(expandSize), holder.expandedArea, false);
        else
            animateHeight(0, dpToPx(expandSize), holder.expandedArea, true);
        setSubList(position);
    }

    private void collapse() {
        animateElevation(12, 0, expandedHolder.mainView);
        expandedHolder.mainView.setBackgroundColor(0xfffafafa);
        animateHeight(dpToPx(expandSize), 0, expandedHolder.expandedArea, false);
        expandedPosition = -1;
        expandedHolder = null;
    }

    private void collapseWithoutAnimation(SimpleItemViewHolder holder) {
        holder.mainView.setElevation(0);
        holder.expandedArea.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(0)));
        holder.mainView.setBackgroundColor(0xfffafafa);
    }

    private void expandWithoutAnimation(SimpleItemViewHolder holder) {
        holder.mainView.setElevation(12);
        holder.expandedArea.setLayoutParams(new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, dpToPx(expandSize)));
        holder.mainView.setBackgroundColor(0xffffffff);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private void animateElevation(int from, int to, final View view) {
        Integer elevationFrom = from;
        Integer elevationTo = to;
        ValueAnimator colorAnimation =
                ValueAnimator.ofObject(
                        new ArgbEvaluator(), elevationFrom, elevationTo);
        colorAnimation.addUpdateListener(
                new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        view.setElevation(
                                (Integer) animator.getAnimatedValue());
                    }

                });
        colorAnimation.start();
    }

    private void animateHeight(int from, int to, final View view, boolean delay) {
        // Declare a ValueAnimator object
        int duration = 500;
        ValueAnimator valueAnimator;
        valueAnimator = ValueAnimator.ofInt(from, to); // These values in this method can be changed to expand however much you like
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new DecelerateInterpolator());
        if (delay)
            valueAnimator.setStartDelay(duration);
        valueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                view.getLayoutParams().height = value.intValue();
                view.requestLayout();
            }
        });


        valueAnimator.start();
    }

    public void recyclerScrolled() {
//        if (expandedPosition != -1 && expandedHolder != null)
//            collapse();
    }

    public void setSubList(int position) {
        if (expandedHolder.rv.getAdapter() == null) {
            expandedHolder.rv.setLayoutManager(new LinearLayoutManager(context,
                    LinearLayoutManager.HORIZONTAL, false));
            expandedHolder.rv.addItemDecoration(
                    new ArtistSubListSpacesItemDecoration(context, dpToPx(5)));
        }
        extendedAdapter = new ArtistSubListAdapter(context, items.get(position).getArtistId());
        expandedHolder.rv.setAdapter(extendedAdapter);
    }

    public void getArtistImg(final SimpleItemViewHolder holder, int position) {
        ArtistImgHandler imgHandler = new ArtistImgHandler(context) {
            @Override
            public void onDownloadComplete(final String url) {
                if (url != null)
                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(context).load(new File(url)).into(holder.img);
                            setImageToView(url, holder);
                        }
                    });
            }
        };
        String path = imgHandler.getArtistImgFromDB("name");
        if (path != null && !path.matches("")) {
            setImageToView(path, holder);
        } else {
            String urlIfAny = imgHandler.getArtistArtWork(items.get(position).getArtistName(), position);
            if (urlIfAny != null)
                setImageToView(urlIfAny, holder);
        }
    }

    public void onBackPressed() {
        if (expandedPosition != -1)
            collapse();
        else
            ((MainActivity) fragment.getActivity()).killActivity();
    }

    public class SimpleItemViewHolder extends RecyclerView.ViewHolder {

        public TextView name, songCount, songsButton, shuffleButton;
        public View mainView, menu;
        public LinearLayout expandedArea;
        public ImageView img;
        public RecyclerView rv;

        public SimpleItemViewHolder(View itemView) {
            super(itemView);
            mainView = itemView;
            img = (ImageView) itemView.findViewById(R.id.artist_item_img);
            menu = itemView.findViewById(R.id.artist_item_menu);
            name = (TextView) itemView.findViewById(R.id.artist_item_name);
            songsButton = (TextView) itemView.findViewById(R.id.artist_song_button);
            shuffleButton = (TextView) itemView.findViewById(R.id.artist_shuffle_button);
            expandedArea = (LinearLayout) itemView.findViewById(R.id.expanded_area);
            songCount = (TextView) itemView.findViewById(R.id.artist_item_song_count);
            rv = (RecyclerView) itemView.findViewById(R.id.artist_sub_rv);
        }
    }

}
