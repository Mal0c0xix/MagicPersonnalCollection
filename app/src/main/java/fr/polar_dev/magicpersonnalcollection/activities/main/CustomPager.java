package fr.polar_dev.magicpersonnalcollection.activities.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Created by Pascal on 21/11/2016.
 */

public class CustomPager extends FragmentStatePagerAdapter {

    public CustomPager(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        //Returning the current tabs
        switch (position) {
            case 0:
                SearchTabFragment searchTab = new SearchTabFragment_();
                return searchTab;
            case 1:
                DecksTabFragment decksTab = new DecksTabFragment_();
                return decksTab;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {

        String result = "Search";

        if(position == 1) result = "Decks";

        return result;
    }
}
