package com.parse.starter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecyclerLocAdapter extends RecyclerView.Adapter<RecyclerLocAdapter.LocationItemHolder> {

    Context context;
    List<LocModel> locList;

    // create constructor to take the context and list from the activity
    public RecyclerLocAdapter(List<LocModel> locList , Context context)
    {
        this.locList = locList;
        this.context = context;
    }
    @Override
    public LocationItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        // inflate the view ( row item )
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_row_item , parent , false);
        LocationItemHolder holder = new LocationItemHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(LocationItemHolder holder, int position) {

        // action to do when the views declared in the view holder are attached to the recycler view
        LocModel locModel = locList.get(position);
        holder.main.setText(locModel.main);
        holder.desc.setText(locModel.desc);
        holder.lat.setText("latitude: "+locModel.lat);
        holder.lon.setText("longitude: "+locModel.lon);

        if(locModel.main.equals("Rain")){
            holder.imageView.setImageResource(R.drawable.rain);
        }
        else if(locModel.main.equals("Clear")) {
            holder.imageView.setImageResource(R.drawable.sunrise);

        }
        else {
            holder.imageView.setImageResource(R.drawable.cloudy);
        }


    }



    @Override
    public int getItemCount() {
        return locList.size();
    }

    class LocationItemHolder extends RecyclerView.ViewHolder{


        // declare view item
        TextView main;
        TextView desc;
        TextView lat;
        TextView lon;
        ImageView imageView;

        public LocationItemHolder(View itemView) {
            super(itemView);

            // init view item
            main = itemView.findViewById(R.id.main_txt_item);
            desc = itemView.findViewById(R.id.desc_txt_item);
            lat = itemView.findViewById(R.id.lat_item);
            lon = itemView.findViewById(R.id.lon_item);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
