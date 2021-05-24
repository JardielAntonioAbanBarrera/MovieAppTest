package com.example.moviedb.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.moviedb.R;
import com.example.moviedb.gs.MovieGS;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.view.View.inflate;

public class MovieAdapter extends ArrayAdapter {

    private Context context;
    private MovieGS[] GS;
    private String imgPathImg = "https://image.tmdb.org/t/p/original";

    public MovieAdapter(Context context, ArrayList<MovieGS> lessons) {
        super(context, 0, lessons);
        this.context = context;
        this.GS = lessons.toArray(new MovieGS[0]);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        MovieAdapter.ViewHolder holder;

        if (convertView == null) {
            convertView = inflate(getContext(), R.layout.list_movie, null);
            holder = new MovieAdapter.ViewHolder();
            holder.mImage = convertView.findViewById(R.id.imageMovie);
            holder.mTitle = convertView.findViewById(R.id.titleMovie);
            convertView.setTag(holder);
        }
        else {
            holder = (MovieAdapter.ViewHolder) convertView.getTag();
        }

        MovieGS dataGS = GS[position];

        // Sección de cargar de información
        Picasso.with(context).load(imgPathImg + dataGS.getImg()).into(holder.mImage);
        holder.mTitle.setText(dataGS.getTitle());

        return convertView;
    }

    private static class ViewHolder {
        private ImageView mImage;
        private TextView mTitle;
    }
}
