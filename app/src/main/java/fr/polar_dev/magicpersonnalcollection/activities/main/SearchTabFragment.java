package fr.polar_dev.magicpersonnalcollection.activities.main;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.leakcanary.RefWatcher;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import fr.polar_dev.magicpersonnalcollection.tools.EndlessScrollListener;
import fr.polar_dev.magicpersonnalcollection.MPCApplication;
import fr.polar_dev.magicpersonnalcollection.R;
import fr.polar_dev.magicpersonnalcollection.activities.search_card.SearchCardAdapter;
import fr.polar_dev.magicpersonnalcollection.database.DaoFactory;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.cards.CardDao;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.decks.DeckDao;
import fr.polar_dev.magicpersonnalcollection.models.Card;
import fr.polar_dev.magicpersonnalcollection.models.Deck;
import fr.polar_dev.magicpersonnalcollection.tools.ParseJSON;

/**
 * Created by Pascal on 21/11/2016.
 *
 */

@EFragment(R.layout.search_tab_layout)
public class SearchTabFragment extends Fragment {

    @ViewById(R.id.search_tab_lv)
    ListView search_listview;

    private SearchCardAdapter adapter;
    private List<Card> datas = new ArrayList<>();

    private static final String BASE_URL = "https://api.deckbrew.com/mtg/cards?page=";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sendRequest(BASE_URL+0);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @AfterViews
    void initialize()
    {

        adapter = new SearchCardAdapter(getActivity(), datas);

        search_listview.setAdapter(adapter);
        search_listview.setOnScrollListener(new EndlessScrollListener() {
            @Override
            public boolean onLoadMore(int pageIndex, int totalItemsCount) {
                // Triggered only when new data needs to be appended to the list
                // Add whatever code is needed to append new items to your AdapterView
                loadNextDataFromApi(pageIndex);
                // or loadNextDataFromApi(totalItemsCount);
                return true; // ONLY if more data is actually being loaded; false otherwise.
            }
        });

        registerForContextMenu(search_listview);
    }

    @ItemClick(R.id.search_tab_lv)
    void onCardClicked(Card clickedCard)
    {
        Intent intent = new Intent(getContext(), CardDetails_.class);
        intent.putExtra("selectedCardId", clickedCard.getId());

        startActivity(intent);
    }

    // Append the next page of data into the adapter
    // This method probably sends out a network request and appends new data items to your adapter.
    public void loadNextDataFromApi(int next_page_index) {
        // Send an API request to retrieve appropriate paginated data
        //  --> Send the request including an offset value (i.e `page`) as a query parameter.
        //  --> Deserialize and construct new model objects from the API response
        //  --> Append the new data objects to the existing set of items inside the array of items
        //  --> Notify the adapter of the new items made with `notifyDataSetChanged()

        String next_page_url = BASE_URL + next_page_index;
        sendRequest(next_page_url);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.setHeaderTitle("Card Options");
        menu.add(0, v.getId(), 0, R.string.searchContextMenu_1);
        menu.add(0, v.getId(), 0, R.string.searchContextMenu_2);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        final AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();

        if(item.getTitle().equals(getResources().getString(R.string.searchContextMenu_1)))
        {
            Toast.makeText(getContext(), "Action 1", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder alertdialogbuilder = new AlertDialog.Builder(getContext());

            alertdialogbuilder.setTitle("Select A Deck ");

            DeckDao deckDao = DaoFactory.getDeckDAO(getContext(), 1);
            final CardDao cardDao = DaoFactory.getCardDAO(getContext(), 1);

            List<Deck> decksList = deckDao.getAllDecks();
            final List<String> decksName = new ArrayList<>();
            for(Deck d : decksList)
            {
                decksName.add(d.getDeckName());
            }

            String[] nameArray = decksName.toArray(new String[decksName.size()]);

            alertdialogbuilder.setItems(nameArray, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String selectedDeck = decksName.get(which);

                    Card selectedCard = datas.get(info.position);
                    List<Card> cards = new ArrayList<>();
                    cards.add(selectedCard);

                    cardDao.insertCardsInDeck(cards, selectedDeck);

                }
            });

            AlertDialog dialog = alertdialogbuilder.create();

            dialog.show();
        }
        else if(item.getTitle().equals(getResources().getString(R.string.searchContextMenu_2)))
        {
            Toast.makeText(getContext(), "Action 2", Toast.LENGTH_SHORT).show();
            //TODO envoyer requête Http au site TCGPLayer.com et ouvrir le browser
        }
        else {return false;}
        return true;
    }

    private void sendRequest(String url)
    {
        StringRequest stringRequest = new StringRequest(url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getActivity(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {
        datas.addAll(ParseJSON.jsonToCardList(response));
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MPCApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
