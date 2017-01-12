package fr.polar_dev.magicpersonnalcollection.activities.deck_creation;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ItemClick;
import org.androidannotations.annotations.ViewById;

import java.util.ArrayList;
import java.util.List;

import fr.polar_dev.magicpersonnalcollection.R;
import fr.polar_dev.magicpersonnalcollection.activities.main.CardDetails;
import fr.polar_dev.magicpersonnalcollection.activities.search_card.SearchCardAdapter;
import fr.polar_dev.magicpersonnalcollection.activities.search_card.SearchCard_;
import fr.polar_dev.magicpersonnalcollection.database.DaoFactory;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.cards.CardDao;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.decks.DeckDao;
import fr.polar_dev.magicpersonnalcollection.models.Card;
import fr.polar_dev.magicpersonnalcollection.models.Deck;

@EActivity(R.layout.activity_deck_creation)
public class DeckCreationActivity extends AppCompatActivity {

    @ViewById(R.id.mainboard_lv)
    ListView mainboard_lv;

    @ViewById(R.id.sideboard_lv)
    ListView sideboard_lv;

    private List<Card> mainboard_cards = new ArrayList<>(),
            sideboard_cards = new ArrayList<>();

    @ViewById(R.id.deck_name_edittext)
    TextInputEditText editText;

    private DeckDao deckDao;
    private CardDao cardDao;
    private Deck modifyDeck;
    private SearchCardAdapter mainboard_lv_adapter, sideboard_lv_adapter;

    private static final int CHOOSE_CARD_REQUEST = 1;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @AfterViews
    void initialize()
    {
        setSupportActionBar(toolbar);

        deckDao = DaoFactory.getDeckDAO(this, DaoFactory.SQLite);
        cardDao = DaoFactory.getCardDAO(this, 1);

        if(getIntent().hasExtra("modifyDeckName"))
        {
            String deckName = getIntent().getStringExtra("modifyDeckName");
            modifyDeck = deckDao.getDeckFromName(deckName);
            editText.setText(modifyDeck.getDeckName());
            mainboard_cards = cardDao.getCardByDeck(deckDao.getDeckIdFromName(deckName));
        }

        //Initialize Adapters
        mainboard_lv_adapter = new SearchCardAdapter(this, mainboard_cards);
        sideboard_lv_adapter = new SearchCardAdapter(this, sideboard_cards);

        mainboard_lv.setAdapter(mainboard_lv_adapter);
        sideboard_lv.setAdapter(sideboard_lv_adapter);


    }

    @Click(R.id.deck_finish_creation_btn)
    void onCreateDeckClicked()
    {
        Deck currentDeck;

        if(modifyDeck == null) {

            currentDeck = new Deck(editText.getText().toString());
            currentDeck.setDeckColors(generateDeckColors(mainboard_cards));
        }
        else
        {
            currentDeck = modifyDeck;
        }

        deckDao.insertDeck(currentDeck);
        cardDao.insertCardsInDeck(mainboard_cards, currentDeck.getDeckName());
        finish();
    }

    @ItemClick(R.id.sideboard_lv)
    void onSideBoardItemClicked(Card sideClickedCard)
    {
        Intent intent = new Intent(DeckCreationActivity.this, CardDetails.class);
        intent.putExtra("selectedCardId", sideClickedCard.getId());

        startActivity(intent);
    }

    @ItemClick(R.id.mainboard_lv)
    void onMainBoardItemClicked(Card mainClickedCard)
    {
        Intent intent = new Intent(DeckCreationActivity.this, CardDetails.class);
        intent.putExtra("selectedCardId", mainClickedCard.getId());

        startActivity(intent);
    }

    private List<String> generateDeckColors(List<Card> list) {
        List<String> results = new ArrayList<>();

        for(Card c : list)
        {
            for(String color : c.getManaCost().getColors())
            {
                if(!results.contains(color))
                    results.add(color);
            }
        }

        return results;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_deck_creation, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        else if(id == R.id.action_add_card)
        {
            Intent intent = new Intent(this, SearchCard_.class);
            startActivityForResult(intent, CHOOSE_CARD_REQUEST);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CHOOSE_CARD_REQUEST)
        {
            String board = data.getStringExtra("board");

            Card chosenCard = (Card)data.getSerializableExtra("chosenCard");

            if(board.equals("mainboard")) {
                mainboard_cards.add(chosenCard);
                mainboard_lv_adapter.notifyDataSetChanged();
            }
            else if(board.equals("sideboard"))
            {
                sideboard_cards.add(chosenCard);
                sideboard_lv_adapter.notifyDataSetChanged();
            }
        }

    }

}
