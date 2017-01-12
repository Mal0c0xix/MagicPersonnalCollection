package fr.polar_dev.magicpersonnalcollection.activities.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import fr.polar_dev.magicpersonnalcollection.R;
import fr.polar_dev.magicpersonnalcollection.models.Deck;

/**
 * Created by Pascal on 22/11/2016.
 *
 */

public class DeckAdapter extends BaseAdapter implements Filterable{

    private List<Deck> originalData = null;
    private List<Deck> filteredData = null;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public DeckAdapter(Context context, List<Deck> data) {
        this.filteredData = data ;
        this.originalData = data ;
        mInflater = LayoutInflater.from(context);
    }

    public void refreshDatas(List<Deck> newDatas)
    {
        originalData.clear();
        originalData.addAll(newDatas);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public Object getItem(int position) {
        return filteredData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private static class ViewHolder {
        TextView deck_name;
        TextView deck_format;

        ImageView mana_color_red;
        ImageView mana_color_blue;
        ImageView mana_color_green;
        ImageView mana_color_white;
        ImageView mana_color_black;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.deck_lv_card_cell, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.deck_name = (TextView) convertView.findViewById(R.id.deck_lv_name);
            holder.deck_format = (TextView) convertView.findViewById(R.id.deck_lv_format);

            holder.mana_color_white = (ImageView) convertView.findViewById(R.id.mana_color_img_white);
            holder.mana_color_blue = (ImageView) convertView.findViewById(R.id.mana_color_img_blue);
            holder.mana_color_black = (ImageView) convertView.findViewById(R.id.mana_color_img_black);
            holder.mana_color_red = (ImageView) convertView.findViewById(R.id.mana_color_img_red);
            holder.mana_color_green = (ImageView) convertView.findViewById(R.id.mana_color_img_green);

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.deck_name.setText(filteredData.get(position).getDeckName());
        holder.deck_format.setText("MODERN");

        for (String color : filteredData.get(position).getDeckColors())
        {
            switch (color)
            {
                case "white":
                    holder.mana_color_white.setVisibility(View.VISIBLE);
                    break;
                case "blue":
                    holder.mana_color_blue.setVisibility(View.VISIBLE);
                    break;
                case "black":
                    holder.mana_color_black.setVisibility(View.VISIBLE);
                    break;
                case "red":
                    holder.mana_color_red.setVisibility(View.VISIBLE);
                    break;
                case "green":
                    holder.mana_color_green.setVisibility(View.VISIBLE);
                    break;
            }
        }


        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Deck> list = originalData;

            int count = list.size();
            final ArrayList<Deck> nlist = new ArrayList<>(count);

            Deck filterableCard ;

            for (int i = 0; i < count; i++) {
                filterableCard = list.get(i);
                if (filterableCard.getDeckName().toLowerCase().contains(filterString)) {
                    nlist.add(filterableCard);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<Deck>) results.values;
            notifyDataSetChanged();
        }

    }
}
