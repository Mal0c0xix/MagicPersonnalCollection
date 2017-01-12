package fr.polar_dev.magicpersonnalcollection.tools;

import android.text.TextUtils;
import android.util.Log;

/**
 * Created by Pascal on 06/01/2017.
 */

@Deprecated
public class LinkHeaderParser {

    public static String parse(String linkheaders)
    {
        String result = "";

        if (!TextUtils.isEmpty(linkheaders)) {
            String[] linkStrings = linkheaders.split(",");
            if (linkStrings.length != 0) {

                for(String link : linkStrings)
                {
                    if(link.contains("next"))
                    {
                        String[] linksparts = link.split(";");
                        String nextPageUrl = linksparts[0];
                        nextPageUrl = nextPageUrl.replaceAll("[<>]","");
                        nextPageUrl = nextPageUrl.trim();
                        Log.d("Headers", nextPageUrl);
                        result = nextPageUrl;
                    }
                }

            }
        }

        return result;
    }
}
