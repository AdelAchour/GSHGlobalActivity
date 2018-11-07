package com.production.achour_ar.gshglobalactivity;

import java.util.List;

public class URLGenerator {

    public static String generateUrl(String baseUrl, List<KeyValuePair> params) {
        if (params.size() > 0) {
            int cpt = 1 ;
            for (KeyValuePair parameter: params) {
                if (cpt==1){
                    baseUrl += "?" + parameter.getKey() + "=" + parameter.getValue();
                }
                else{
                    baseUrl += "&" + parameter.getKey() + "=" + parameter.getValue();
                }
                cpt++;
            }
        }
        return baseUrl;
    }

}
