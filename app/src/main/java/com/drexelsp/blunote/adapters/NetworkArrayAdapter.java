package com.drexelsp.blunote.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.drexelsp.blunote.beans.ConnectionListItem;
import com.drexelsp.blunote.blunote.R;

import java.util.ArrayList;

/**
 * Custom Array Adapter to view the available networks on startup.
 */
public class NetworkArrayAdapter extends ArrayAdapter<ConnectionListItem> {
    private final Context context;
    private final ArrayList<ConnectionListItem> connectionsList;

    public NetworkArrayAdapter(Context context, ArrayList<ConnectionListItem> connectionsList) {
        super(context, R.layout.network_row, connectionsList);

        this.context = context;
        this.connectionsList = connectionsList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.network_row, parent, false);

        TextView networkNameView = (TextView) rowView.findViewById(R.id.network_name);
        TextView totalConnections = (TextView) rowView.findViewById(R.id.connections);
        TextView totalSongs = (TextView) rowView.findViewById(R.id.total_songs);

        networkNameView.setText(connectionsList.get(position).getConnectionName());
        totalConnections.setText("Connections: " + connectionsList.get(position).getTotalConnections());
        totalSongs.setText("Total Songs: " + connectionsList.get(position).getTotalSongs());

        return rowView;
    }


}
