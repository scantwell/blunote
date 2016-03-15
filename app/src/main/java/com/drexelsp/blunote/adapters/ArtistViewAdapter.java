package com.drexelsp.blunote.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.drexelsp.blunote.beans.ArtistViewAlbum;
import com.drexelsp.blunote.beans.ArtistViewTrack;
import com.drexelsp.blunote.blunote.R;

import java.util.ArrayList;
import java.util.Set;
import java.util.TreeSet;

/**
 * Created by Brisbin on 3/12/2016.
 */
public class ArtistViewAdapter extends BaseAdapter {
    private static final int TYPE_ALBUM = 0;
    private static final int TYPE_TRACK = 1;
    private static final int TYPE_MAX_COUNT = TYPE_TRACK + 1;

    private ArrayList mData = new ArrayList();
    private LayoutInflater mInflater;
    private Set trackSet = new TreeSet();

    public ArtistViewAdapter(Context context) {
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void addAlbum(ArtistViewAlbum album) {
        mData.add(album);
    }

    public void addTrack(ArtistViewTrack track) {
        mData.add(track);
        trackSet.add(mData.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return trackSet.contains(position) ? TYPE_TRACK : TYPE_ALBUM;
    }

    @Override
    public int getViewTypeCount() {
        return TYPE_MAX_COUNT;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        AlbumViewHolder albumViewHolder;
        TrackViewHolder trackViewHolder;

        int type = getItemViewType(position);
        if (convertView == null) {
            switch (type) {
                case TYPE_ALBUM:
                    albumViewHolder = new AlbumViewHolder();
                    convertView = mInflater.inflate(R.layout.artist_album_row, null);
                    albumViewHolder.albumArt = (ImageView) convertView.findViewById(R.id.album_artwork);
                    albumViewHolder.albumName = (TextView) convertView.findViewById(R.id.album_name);
                    albumViewHolder.numberOfTracks = (TextView) convertView.findViewById(R.id.album_number_tracks);
                    albumViewHolder.albumYear = (TextView) convertView.findViewById(R.id.album_year_released);
                    convertView.setTag(albumViewHolder);
                    break;
                case TYPE_TRACK:
                    trackViewHolder = new TrackViewHolder();
                    convertView = mInflater.inflate(R.layout.artist_track_row, null);
                    trackViewHolder.title = (TextView) convertView.findViewById(R.id.song_name);
                    convertView.setTag(trackViewHolder);
                    break;
            }
        } else {
            if (type == TYPE_ALBUM) {
                albumViewHolder = (AlbumViewHolder)convertView.getTag();
                albumViewHolder.albumArt.setImageBitmap(
                        ((ArtistViewAlbum) mData.get(position)).getAlbumArtwork());
                albumViewHolder.albumName.setText(
                        ((ArtistViewAlbum) mData.get(position)).getAlbumName());
                albumViewHolder.numberOfTracks.setText(
                        ((ArtistViewAlbum) mData.get(position)).getNumberOfTracks());
                albumViewHolder.albumYear.setText(
                        ((ArtistViewAlbum) mData.get(position)).getAlbumYear());
            } else {
                trackViewHolder = (TrackViewHolder)convertView.getTag();
                trackViewHolder.title.setText(
                        ((ArtistViewTrack) mData.get(position)).getTrackName());
            }

        }

        return convertView;
    }

    public static class AlbumViewHolder {
        public ImageView albumArt;
        public TextView albumName;
        public TextView numberOfTracks;
        public TextView albumYear;
    }

    public static class TrackViewHolder {
        public TextView title;
    }
}
