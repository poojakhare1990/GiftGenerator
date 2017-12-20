package com.example.giftgenerator;

import java.util.HashMap;
import java.util.Map;

/*
 * This class shows how to make a simple authenticated call to the
 * Amazon Product Advertising API.
 *
 * See the README.html that came with this sample for instructions on
 * configuring and running the sample.
 */
public class AmazonURIGenerator {

    /*
     * Your Access Key ID, as taken from the Your Account page.
     */
    private static final String ACCESS_KEY_ID = "AKIAIUJT3RAEPSSP2ZWQ";

    /*
     * Your Secret Key corresponding to the above ID, as taken from the
     * Your Account page.
     */
    private static final String SECRET_KEY = "eZvkH+9RYhNxQ/06zOzwoKr6JRl4NMAgS4jknMM8";

    /*
     * Use the end-point according to the region you are interested in.
     */
    private static final String ENDPOINT = "webservices.amazon.com";

    public static String generateURI(int i,String orderby) {

        /*
         * Set up the signed requests helper.
         */
        String search ="", keyword="", price="";
        SignedRequestsHelper helper;

        switch(i){
            case 1: search="Baby";
                    keyword="Baby Gifts";
                    break;
            case 2: search="Baby";
                keyword="Toddler Toys";
                break;
            case 3: search="FashionBoys";
                keyword="boy toys";
                orderby="popularity-rank";
                break;
            case 4: search="Toys";
                keyword="boy games";
                orderby="pmrank";
                break;
            case 5: search="VideoGames";
                keyword="kids";
                orderby="pmrank";
                break;
            case 6: search="Books";
                keyword="young adult";
                orderby="relevancerank";
                break;
            case 7: search="Movies";
                keyword="Thriller";
                orderby="relevancerank";
                break;
            case 8: search="FashionMen";
                keyword="shirts";
                orderby="popularity-rank";
                break;
            case 9: search="Electronics";
                keyword="music";
                orderby="pmrank";
                break;
            case 10: search="Wine";
                keyword="fine";
               orderby="relevancerank";
                break;
            case 11: search="Toys";
                keyword="Dolls";
                orderby="pmrank";
                break;
            case 12: search="FashionGirls";
                keyword="kids dress";
                orderby="popularity-rank";
                break;
            case 13: search="Books";
                keyword="mystery";
                orderby="relevancerank";
                break;
            case 14: search="Beauty";
                keyword="makeup";
                orderby="pmrank";
                break;
            case 15: search="FashionWomen";
                keyword="blouse";
                orderby="popularity-rank";
                break;
            case 16: search="FashionWomen";
                orderby="popularity-rank";
                keyword="handbag";
                break;
            case 17: search="FashionWomen";
                keyword="jewelery";
                orderby="popularity-rank";
                break;
            case 18: search="FashionWomen";
                keyword="watch";
                orderby="popularity-rank";
                break;
            case 19: search="Books";
                keyword="classics";
                break;
                   }


        try {
            helper = SignedRequestsHelper.getInstance(ENDPOINT, ACCESS_KEY_ID, SECRET_KEY);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }

        String requestUrl = null;

        Map<String, String> params = new HashMap<String, String>();

        params.put("Service", "AWSECommerceService");
        params.put("Operation", "ItemSearch");
        params.put("AWSAccessKeyId", "AKIAIUJT3RAEPSSP2ZWQ");
        params.put("AssociateTag", "giftgenerat09-20");
        params.put("SearchIndex",search);
        params.put("Keywords", keyword);
        params.put("Sort", orderby);
        params.put("ResponseGroup", "Images,ItemAttributes,Offers");

        requestUrl = helper.sign(params);
        return requestUrl;
    }
}
