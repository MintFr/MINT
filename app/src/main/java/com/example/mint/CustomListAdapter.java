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
 * This class is a sub-class of BaseAdapter and lets us customise the appearance of the headers and regular items
 */
public class CustomListAdapter extends BaseAdapter {
    private final Context context;
    private final ArrayList<String> items;
    private int nbLastAdd;

    /**
     * Constructor
     * @param context Context
     * @param items ArrayList<String>
     */
    public CustomListAdapter(Context context, ArrayList<String> items) {
        this.context = context;
        this.items = items;
        nbLastAdd = Preferences.getNumberOfLastAddresses("lastAddress",context);
    }

    //returns total number of items in the list

    /**
     * Access total number of items in the list
     * @return int
     */
    @Override
    public int getCount() {
        return items.size();
    }

    //returns list item at the specified position

    /**
     * Access item at a given position in the list
     * @param position : int - position in the list
     * @return Object
     */
    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    /**
     *
     * @param position
     * @return
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     *
     * @param position
     * @return
     */
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
     * Returns true if the item is not a header
     * @param position : index of the item
     * @return boolean, true if regular, false if header
     */
    @Override
    public boolean isEnabled(int position) {
        return position !=1 && position != nbLastAdd + 2;
    }

    /**
     * Returns the view to be displayed for each item of the list
     * @param position : index of the item
     * @param convertView : the view that will be displayed
     * @param parent : the parent of the item
     * @return convertView : the view that will be displayed
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // regular item
        if(getItemViewType(position)==0){
            // inflate regular item layout
            convertView = inflater.inflate(R.layout.list_item,parent,false);
            // get elements from the layout
            TextView text = convertView.findViewById(R.id.text_item);
            ImageView icon = convertView.findViewById(R.id.icon);
            ImageView remove = convertView.findViewById(R.id.remove);
            // set text with the value of the item at the index (position)
            text.setText(items.get(position));

            // if in favorites, remove cross and add star
            if (position>nbLastAdd+1){
                remove.setVisibility(View.GONE);
                icon.setImageResource(R.drawable.ic_star);
            }

            // if it's my position, remove cross and add locate symbol
            if (position==0){
                remove.setVisibility(View.GONE);
                icon.setImageResource(R.drawable.ic_locate);
            }

            // if it's a history item, make cross visible and set onClick callback when you click on cross
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

        // else, we inflate the list_header layout
        else {
            convertView = inflater.inflate(R.layout.list_header,parent,false);
            // the headers are not clickable
            convertView.setClickable(false);
            TextView text = convertView.findViewById(R.id.text_item);
            // fill the text with the value inside the list "items"
            text.setText(items.get(position));
        }
        return convertView;
    }
}
