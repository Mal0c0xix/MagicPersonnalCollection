package fr.polar_dev.magicpersonnalcollection.activities.search_card;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import fr.polar_dev.magicpersonnalcollection.R;
import fr.polar_dev.magicpersonnalcollection.models.Card;
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
    private boolean result = true;

    @AfterViews
    void initialize()
    {
        setSupportActionBar(toolbar);
        datas = new ArrayList<>();
        adapter = new SearchCardAdapter(this, datas);
        search_card_lv.setAdapter(adapter);

        search_card_lv.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int pageIndex, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                return loadNextDataFromApi(pageIndex,null);
                // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        sendRequest(BASE_URL);
    }

    @Click(R.id.fab)
    void onFabClicked()
    {
        Intent intent = new Intent();

        intent.putExtra("board", "mainboard");
        if(selectedCard != null)
            intent.putExtra("chosenCard", selectedCard);
        setResult(1, intent);
        finish();
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

        if(id == R.id.action_settings)
        {
            return true;
        }
        else if(id == R.id.action_filter)
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

    private boolean sendRequest(String selectedURL)
    {
        StringRequest stringRequest = new StringRequest(selectedURL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if(!ParseJSON.jsonToCardList(response).isEmpty()) {

                            if (!firstLoad)
                                datas.addAll(ParseJSON.jsonToCardList(response));
                            else {
                                datas = ParseJSON.jsonToCardList(response);
                                firstLoad = false;
                            }
                            adapter.notifyDataSetChanged();
                        }
                        else
                        {
                            result = false;
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(SearchCard.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(SearchCard.this);
        requestQueue.add(stringRequest);
        return result;
    }

    // Append the next page of data into the adapter
    // This method sends out a network request and appends new data items to your adapter.
    public boolean loadNextDataFromApi(int next_page_index, String args) {

        String next_page_url = BASE_URL;
        if(args != null && !args.isEmpty())
            next_page_url += "?" + args;
        next_page_url += "&page=" + next_page_index;

        return sendRequest(next_page_url);

    }

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

                String requestArgs = "";
                String rarity = rarity_spinner.getSelectedItem().toString();
                String set = set_spinner.getSelectedItem().toString();
                String color = mana_spinner.getSelectedItem().toString();
                String type = type_spinner.getSelectedItem().toString();

                if(!rarity.equals("Rarity"))
                    requestArgs += "?rarity=" + rarity;

                if(!set.equals("Set"))
                    requestArgs += "?set=" + set;

                if(!color.equals("Mana Color"))
                    requestArgs += "?color=" + color;

                if(!type.equals("Type"))
                    requestArgs += "?type=" + type;

                sendRequest(BASE_URL+requestArgs);

                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
