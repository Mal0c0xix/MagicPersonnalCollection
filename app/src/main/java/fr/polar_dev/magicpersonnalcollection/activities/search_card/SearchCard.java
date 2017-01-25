package fr.polar_dev.magicpersonnalcollection.activities.search_card;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import fr.polar_dev.magicpersonnalcollection.MPCApplication;
import fr.polar_dev.magicpersonnalcollection.R;
import fr.polar_dev.magicpersonnalcollection.models.Card;
import fr.polar_dev.magicpersonnalcollection.tools.CardApiUrlConstructor;
import fr.polar_dev.magicpersonnalcollection.tools.EndlessScrollListener;
import fr.polar_dev.magicpersonnalcollection.tools.ParseJSON;

@EActivity(R.layout.activity_search_card)
public class SearchCard extends AppCompatActivity {

    private static final String BASE_URL = "https://api.deckbrew.com/mtg/cards";

    @ViewById(R.id.search_card_lv)
    ListView search_card_lv;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    private List<Card> datas;
    private SearchCardAdapter adapter;
    private Card selectedCard;
    private boolean firstLoad = true;
    private boolean filtering = false;
    private boolean result = true;
    private CardApiUrlConstructor urlConstructor;

    @AfterViews
    void initialize()
    {
        setSupportActionBar(toolbar);
        datas = new ArrayList<>();
        adapter = new SearchCardAdapter(this, datas);
        search_card_lv.setAdapter(adapter);

        search_card_lv.setOnScrollListener(new EndlessScrollListener(5, 0) {
            @Override
            public boolean onLoadMore(int pageIndex, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                // ONLY return true if more data is actually being loaded; false otherwise.
                return loadNextDataFromApi(pageIndex,null);
            }
        });

        sendRequest(BASE_URL);
    }

    @Click(R.id.fab)
    void onFabClicked()
    {
        if(selectedCard != null) {
            Intent intent = new Intent();
            intent.putExtra("board", "mainboard");
            intent.putExtra("chosenCard", selectedCard);
            setResult(1, intent);
            finish();
        }
        else
        {
            Toast.makeText(this, "No Card Selected ! Please select one before clicking this button !", Toast.LENGTH_LONG)
            .show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_search_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == R.id.action_filter)
        {
            showSpinnerDialog();
        }

        return true;
    }

    @ItemClick(R.id.search_card_lv)
    void onListItemClicked(Card choosenCard)
    {
        selectedCard = choosenCard;
    }

    /**
     * Send an http request using the Volley library
     * @param selectedURL the url targeted by the request
     * @return a boolean which is true if data is loaded, false otherwise.
     */
    private boolean sendRequest(String selectedURL)
    {
        StringRequest stringRequest = new StringRequest(selectedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        result = parseResponse(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchCard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        MPCApplication.getRequestQueue(this).add(stringRequest);
        Log.d("Requestresult", String.valueOf(result));
        return result;
    }

    /**
     * This method sends out a network request and appends new data items to the adapter
     * @param next_page_index an int representing the next page index
     * @param args a string representing the arguments to add to the request
     * @return a boolean which is true if data is loaded, false otherwise
     */
    public boolean loadNextDataFromApi(int next_page_index, String args) {

        String next_page_url = BASE_URL;
        if(args != null && !args.isEmpty())
            next_page_url += "?" + args + "&page=" + next_page_index;
        else
            next_page_url += "?page=" + next_page_index;

        return sendRequest(next_page_url);

    }

    /**
     * Show the dialog containing the spinners for filtering the list.
     */
    private void showSpinnerDialog()
    {
        final Dialog dialog = new Dialog(this);
        dialog.setTitle("Filter Choice");
        dialog.setCanceledOnTouchOutside(true);
        dialog.setContentView(R.layout.dialog_filter_layout);

        final Spinner rarity_spinner = (Spinner) dialog.findViewById(R.id.search_card_rarity_spinner);
        final Spinner set_spinner = (Spinner) dialog.findViewById(R.id.search_card_set_spinner);
        final Spinner mana_spinner = (Spinner) dialog.findViewById(R.id.search_card_color_spinner);
        final Spinner type_spinner = (Spinner) dialog.findViewById(R.id.search_card_type_spinner);

        Button btnDone = (Button) dialog.findViewById(R.id.dialog_btn_filter);

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                filtering = true;

                String rarity = rarity_spinner.getSelectedItem().toString();
                String set = set_spinner.getSelectedItem().toString();
                String color = mana_spinner.getSelectedItem().toString();
                String type = type_spinner.getSelectedItem().toString();

                List<String> urlArgs = new ArrayList<>();

                if(!rarity.equals("Rarity"))
                    urlArgs.add("rarity=" + rarity);

                if(!set.equals("Set"))
                    urlArgs.add("set=" + set);

                if(!color.equals("Mana Color"))
                    urlArgs.add("color=" + color);

                if(!type.equals("Type"))
                    urlArgs.add("type=" + type);

                urlConstructor = new CardApiUrlConstructor(BASE_URL, urlArgs);

                sendRequest(urlConstructor.build());

                dialog.dismiss();
                filtering = false;
            }
        });

        dialog.show();
    }

    /**
     * Parse the http response string and return a boolean to inform the user if data has been loaded or not
     * @param response the http response in String format
     * @return a boolean which is true if data is loaded, false otherwise.
     */
    private boolean parseResponse(String response)
    {
        boolean dataIsLoaded;

        if(!ParseJSON.jsonToCardList(response).isEmpty()) {

            if(firstLoad && !filtering)
            {
                firstLoad = false;
                datas = ParseJSON.jsonToCardList(response);
            }
            else if(!firstLoad && filtering)
            {
                datas.addAll(ParseJSON.jsonToCardList(response));
            }
            adapter.notifyDataSetChanged();
            Log.d("SearchCard", datas.toString());
            dataIsLoaded = true;
        }
        else
        {
            dataIsLoaded = false;
        }

        return dataIsLoaded;
    }
}
