package fr.polar_dev.magicpersonnalcollection.activities.search_card;

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
import fr.polar_dev.magicpersonnalcollection.models.Card;

/**
 * Created by Pascal on 16/11/2016.
 *
 */

public class SearchCardAdapter extends BaseAdapter implements Filterable {

    private List<Card>originalData = null;
    private List<Card> filteredData = null;
    private LayoutInflater mInflater;
    private Context context;
    private ItemFilter mFilter = new ItemFilter();

    public SearchCardAdapter(Context context, List<Card> data) {
        this.filteredData = data ;
        this.originalData = data ;
        this.context = context;
        mInflater = LayoutInflater.from(context);
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
        TextView card_name;
        TextView card_type;
        ImageView card_set;
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
            convertView = mInflater.inflate(R.layout.search_lv_card_cell, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.card_name = (TextView) convertView.findViewById(R.id.search_tab_card_name);
            holder.card_type = (TextView) convertView.findViewById(R.id.search_card_type);
            holder.card_set = (ImageView) convertView.findViewById(R.id.search_card_set);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.card_name.setText(filteredData.get(position).getName());
        holder.card_type.setText(filteredData.get(position).showCardTypes());

        String set_string = "icon"+filteredData.get(position).getCardSet().toLowerCase();

        int set_id = context.getResources().getIdentifier(set_string,
                "drawable", context.getApplicationContext().getPackageName());

        if(set_id != 0)
            holder.card_set.setImageResource(set_id);


        return convertView;
    }

    @Override
    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            //remove Uppercase letters for easier comparison
            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<Card> list = originalData;

            int count = list.size();
            final ArrayList<Card> nlist = new ArrayList<>(count);

            Card filterableCard ;

            //Filter the card by their name
            for (int i = 0; i < count; i++) {
                filterableCard = list.get(i);
                if (filterableCard.getName().toLowerCase().contains(filterString)) {
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
            filteredData = (ArrayList<Card>) results.values;
            notifyDataSetChanged();
        }

    }
}
