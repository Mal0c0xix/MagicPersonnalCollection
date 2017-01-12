package fr.polar_dev.magicpersonnalcollection.activities.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.squareup.leakcanary.RefWatcher;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import fr.polar_dev.magicpersonnalcollection.MPCApplication;
import fr.polar_dev.magicpersonnalcollection.R;
import fr.polar_dev.magicpersonnalcollection.activities.deck_creation.DeckCreationActivity;
import fr.polar_dev.magicpersonnalcollection.activities.deck_creation.DeckCreationActivity_;
import fr.polar_dev.magicpersonnalcollection.database.DaoFactory;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.decks.DeckDao;
import fr.polar_dev.magicpersonnalcollection.models.Deck;

/**
 * Created by Pascal on 21/11/2016.
 */

@EFragment(R.layout.decks_tab_layout)
public class DecksTabFragment extends Fragment {

    private DeckAdapter adapter;
    private DeckDao deckDao;

    @ViewById(R.id.decks_tab_lv)
    ListView decks_lv;

    @AfterViews
    void initialize()
    {
        //Initialize the Data Access Object (DAO)
        deckDao = DaoFactory.getDeckDAO(getContext(), 1);

        //Initialize the ListView adapter
        adapter = new DeckAdapter(getContext(), deckDao.getAllDecks());

        //Link the adapter to the listview
        decks_lv.setAdapter(adapter);
    }

    @Click(R.id.decks_tab_add_btn)
    void onCreateBtnClicked()
    {
        Intent intent = new Intent(getContext(), DeckCreationActivity_.class);
        startActivity(intent);
    }

    @ItemClick(R.id.decks_tab_lv)
    void onDeckClicked(Deck clickedDeck)
    {
        Intent intent = new Intent(getContext(), DeckCreationActivity_.class);
        intent.putExtra("modifyDeckName", clickedDeck.getDeckName());

        startActivity(intent);
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.refreshDatas(deckDao.getAllDecks());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MPCApplication.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }
}
