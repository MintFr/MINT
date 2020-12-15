package com.example.helloworld;

import android.content.Context;
import android.media.Image;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> items;
    private int nbLastAdd;

    //public constructor
    public CustomListAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        this.items = items;
        nbLastAdd = Preferences.getNumberOfLastAddresses("lastAddress",context);
    }

    @Override
    public int getCount() {
        return items.size(); //returns total of items in the list
    }

    @Override
    public Object getItem(int position) {
        return items.get(position); //returns list item at the specified position
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position){
        if(position==0 || position == nbLastAdd+1) {
            return 1; // header item
        }
        else {
            return 0;// regular items
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(getItemViewType(position)==0){ // regular item
            convertView = inflater.inflate(R.layout.list_item,parent,false); // cast to regular item
            TextView text = convertView.findViewById(R.id.text_item);
            ImageView icon = convertView.findViewById(R.id.icon);
            ImageView remove = convertView.findViewById(R.id.remove);
            text.setText(items.get(position));
            if (position>nbLastAdd+1){
                remove.setVisibility(View.GONE);
                icon.setImageResource(R.drawable.ic_star);
            }
            else{
                final View finalConvertView = convertView;
                final int finalPosition = position;
                remove.setVisibility(View.VISIBLE);
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalConvertView.setVisibility(View.GONE);
                        items.remove(finalPosition);
                        notifyDataSetChanged();
                        Preferences.removeLastAddress("lastAddress",finalPosition,context);
                        nbLastAdd--;
                    }
                });
                icon.setImageResource(R.drawable.ic_history);
            }
        }
        else {
            convertView = inflater.inflate(R.layout.list_header,parent,false);
            TextView text = convertView.findViewById(R.id.text_item);
            text.setText(items.get(position));
        }
        return convertView;
    }
}
