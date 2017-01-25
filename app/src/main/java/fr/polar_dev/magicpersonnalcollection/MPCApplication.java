package fr.polar_dev.magicpersonnalcollection;

import android.app.Application;
import android.content.Context;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import fr.polar_dev.magicpersonnalcollection.database.DaoFactory;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.cards.CardDao;
import fr.polar_dev.magicpersonnalcollection.database.sqlite.decks.DeckDao;

/**
 * Created by Pascal on 05/01/2017.
 */

public class MPCApplication extends Application {

    public static RefWatcher getRefWatcher(Context context) {
        MPCApplication application = (MPCApplication) context.getApplicationContext();
        return application.refWatcher;
    }

    private RefWatcher refWatcher;
    private RequestQueue requestQueue;

    @Override
    public void onCreate() {
        super.onCreate();
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        refWatcher = LeakCanary.install(this);
        // Normal app init code...
        requestQueue = Volley.newRequestQueue(this);
    }

    public static RequestQueue getRequestQueue(Context context)
    {
        MPCApplication application = (MPCApplication) context.getApplicationContext();
        return application.requestQueue;
    }
}
