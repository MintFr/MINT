package com.example.mint;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * TODO Explain aim and use of class here
 */
public class CustomListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<String> items;
    private int nbLastAdd;

    /**
     * Constructor
     * @param context
     * @param items
     */
    public CustomListAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        this.items = items;
        nbLastAdd = Preferences.getNumberOfLastAddresses("lastAddress",context);
    }

    //returns total number of items in the list
    @Override
    public int getCount() {
        return items.size();
    }

    //returns list item at the specified position
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position){
        if(position ==1 || position == nbLastAdd+2) {
            return 1; // header item
        }
        else {
            return 0;// regular items
        }
    }

    /**
     * TODO comment
     * @param position
     * @return
     */
    @Override
    public boolean isEnabled(int position) {
        return position !=1 && position != nbLastAdd + 2;
    }

    /**
     * TODO comment
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(getItemViewType(position)==0){ // regular item
            // cast to regular item
            convertView = inflater.inflate(R.layout.list_item,parent,false);
            TextView text = convertView.findViewById(R.id.text_item);
            ImageView icon = convertView.findViewById(R.id.icon);
            ImageView remove = convertView.findViewById(R.id.remove);
            text.setText(items.get(position));

            // if in favorites, replace cross with star
            if (position>nbLastAdd+1){
                remove.setVisibility(View.GONE);
                icon.setImageResource(R.drawable.ic_star);
            }
            if (position==0){
                remove.setVisibility(View.GONE);
                icon.setImageResource(R.drawable.ic_locate);
            }

            else{
                final View finalConvertView = convertView;
                final int finalPosition = position;
                remove.setVisibility(View.VISIBLE);

                // when you click on the cross, removes one of the last addresses from history
                remove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finalConvertView.setVisibility(View.GONE);
                        items.remove(finalPosition);
                        notifyDataSetChanged();
                        Preferences.removeLastAddress("lastAddress",finalPosition-1,context);
                        nbLastAdd--;
                    }
                });
                // sets icon for history elements
                icon.setImageResource(R.drawable.ic_history);
            }
        }
        else {
            //the rest is titles, so non-clickable
            convertView = inflater.inflate(R.layout.list_header,parent,false);
            convertView.setClickable(false);
            TextView text = convertView.findViewById(R.id.text_item);
            // fill the text with the value inside the list "items"
            text.setText(items.get(position));
        }
        return convertView;
    }
}
