package fr.polar_dev.magicpersonnalcollection.activities.main;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import fr.polar_dev.magicpersonnalcollection.R;
import fr.polar_dev.magicpersonnalcollection.tools.DownloadImageTask;

@EActivity(R.layout.activity_card_details)
public class CardDetails extends AppCompatActivity {

    private String cardName;
    private final String BASE_URL = "https://api.deckbrew.com/mtg/cards/";

    @ViewById(R.id.card_details_img)
    ImageView card_img;

    @ViewById(R.id.toolbar)
    Toolbar toolbar;

    @AfterViews
    void initialize()
    {
        setSupportActionBar(toolbar);

        cardName = getIntent().getStringExtra("selectedCardId");

        sendRequest(cardName);
    }

    private void sendRequest(String cardName)
    {
        StringRequest stringRequest = new StringRequest(BASE_URL + cardName,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        showJSON(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(CardDetails.this, error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        RequestQueue requestQueue = Volley.newRequestQueue(CardDetails.this);
        requestQueue.add(stringRequest);
    }

    private void showJSON(String response) {

        String url = "";
        try {
            JSONObject jsonObject = new JSONObject(response);
            JSONArray extensionArray = jsonObject.getJSONArray("editions");
            url = extensionArray.getJSONObject(0).getString("image_url");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if(!url.isEmpty())
            new DownloadImageTask(card_img).execute(url);

    }

}
