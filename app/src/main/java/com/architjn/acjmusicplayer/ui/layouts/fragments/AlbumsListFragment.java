package com.architjn.acjmusicplayer.ui.layouts.fragments;

import android.Manifest;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.architjn.acjmusicplayer.R;
import com.architjn.acjmusicplayer.ui.layouts.activity.MainActivity;
import com.architjn.acjmusicplayer.utils.AlbumListSpacesItemDecoration;
import com.architjn.acjmusicplayer.utils.ListSongs;
import com.architjn.acjmusicplayer.utils.PermissionChecker;
import com.architjn.acjmusicplayer.utils.adapters.AlbumListAdapter;
import com.architjn.acjmusicplayer.utils.items.Album;

import java.util.ArrayList;

/**
 * Created by architjn on 27/11/15.
 */
public class AlbumsListFragment extends Fragment {

    private Context context;
    private View mainView;
    private RecyclerView gv;
    private AlbumListAdapter adapter;
    private PermissionChecker permissionChecker;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_albums,
                container, false);
        mainView = view;
        context = mainView.getContext();
        initViews();
        return view;
    }

    private void initViews() {
        gv = (RecyclerView) mainView.findViewById(R.id.albumsListContainer);
        checkPermissions();
    }

    private void checkPermissions() {
        permissionChecker = new PermissionChecker(context, getActivity(), mainView);
        permissionChecker.check(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                getResources().getString(R.string.storage_permission),
                new PermissionChecker.OnPermissionResponse() {
                    @Override
                    public void onAccepted() {
                        setList();
                    }

                    @Override
                    public void onDecline() {
                        getActivity().finish();
                    }
                });
    }

    private void setList() {
        ArrayList<Album> albumList = ListSongs.getAlbumList(context);
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(mainView.getContext(), 2);
        gridLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        gridLayoutManager.scrollToPosition(0);
        gv.setLayoutManager(gridLayoutManager);
        gv.addItemDecoration(new AlbumListSpacesItemDecoration(2));
        View header = LayoutInflater.from(context).inflate(
                R.layout.album_list_header, gv, false);
        adapter = new AlbumListAdapter(mainView.getContext(), albumList, header);
        gv.setAdapter(adapter);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        permissionChecker.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void onBackPress() {
        ((MainActivity) getActivity()).killActivity();
    }

}
