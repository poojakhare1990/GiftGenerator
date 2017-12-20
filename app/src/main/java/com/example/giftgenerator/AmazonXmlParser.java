/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

package com.example.giftgenerator;

import android.util.Log;
import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * This class parses XML feeds from stackoverflow.com.
 * Given an InputStream representation of a feed, it returns a List of entries,
 * where each list element represents a single entry (post) in the XML feed.
 */
public class AmazonXmlParser {
    private static final String ns = null;

    // We don't use namespaces

    public List<Entry> parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            Log.v("MyTag","create the parser...");

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);

            parser.nextTag();
            Log.v("MyTag",parser.getName());

            Log.v("MyTag","start readfeed...");
            return readFeed(parser);
        } finally {
            in.close();
        }
    }

    private List<Entry> readFeed(XmlPullParser parser) throws XmlPullParserException, IOException {
        List<Entry> entries = new ArrayList<Entry>();

        //parser.require(XmlPullParser.START_TAG, ns, "ItemSearchResponse");
        parser.require(XmlPullParser.START_TAG, ns, "ItemSearchResponse");

        //Log.v("CHK ", "1");

        while (parser.next() != XmlPullParser.END_TAG) {

            //Log.v("Tag ", parser.getName());
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();

            //Log.v("Tag found ", name);

            // Starts by looking for the entry tag
            if (name.equals("Items")) {
                //parser.require(XmlPullParser.START_TAG,ns,"Item");
                while(parser.next()!=XmlPullParser.END_TAG){
                    String str = parser.getName();
                    if(parser.getName().equals("Item")) //Bring parser to the Item tag
                        entries.add(readEntry(parser)); //read Item URL from the DetailPageURL Tag
                    else
                        skip(parser);
                }
            } else {
                skip(parser);
            }
        }
        return entries;
    }

    // This class represents a single entry (post) in the XML feed.
    // It includes the data members "title," "link," and "imgURL."
    public static class Entry {
        public final String title;
        public final String link;
        public final String imgURL;
        public final String price;
        public final String id;
        public final String description;

        private Entry(String title, String imgURL, String link, String price, String id, String description) {
            this.title = title;
            this.imgURL = imgURL;
            this.link = link;
            this.price=price;
            this.id = id;
            this.description=description;
        }
    }

    // Parses the contents of an entry. If it encounters a title, imgURL, or link tag, hands them
    // off
    // to their respective methods for processing. Otherwise, skips the tag.
    private Entry readEntry(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "Item");
        String title = "";
        String imgURL = null;
        String link = null;
        String price = null;
        String id = null;
        String description =null;


        while (parser.next() != XmlPullParser.END_TAG) {

            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();


            if (name.equals("ItemAttributes")) {
                parser.require(XmlPullParser.START_TAG, ns, "ItemAttributes");

               while (parser.next() != XmlPullParser.END_TAG) {

                    if (parser.getEventType() != XmlPullParser.START_TAG) {
                        continue;
                    }
                   if(parser.getName().equals("Title")) //Bring parser to the Item tag
                       description = readText(parser);
                   else if(parser.getName().equals("ListPrice"))
                            price = readPrice(parser);
                   else skip(parser);
               }
            } else if (name.equals("LargeImage")) {
                imgURL = readImage(parser);
            } else if (name.equals("DetailPageURL")) {
                link = readLink(parser);
            } else if (name.equals("ASIN")) {
                id = readID(parser);
            }else {
                skip(parser);
            }
        }
        String [] words = description.split("\\s");
        int cnt=0;
        for(String s:words){
            if(cnt>5)
                break;
            else
                title=title+" "+s;
            cnt++;
        }

        return new Entry(title, imgURL, link, price,id,description);
    }

    // Processes title tags in the feed.
    private String readPrice(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ListPrice");
        String price = null;
        while(parser.next()!=XmlPullParser.END_TAG){
            String str = parser.getName();
            if(parser.getName().equals("FormattedPrice")) //Bring parser to the Item tag
                price = readText(parser);
            else
                skip(parser);
        }

        parser.require(XmlPullParser.END_TAG, ns, "ListPrice");
        return price;
    }

    // Processes link tags in the feed.
    private String readID(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "ASIN");
        String id = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "ASIN");
        return id;
    }

    // Processes link tags in the feed.
    private String readLink(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "DetailPageURL");
        String URL = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "DetailPageURL");
        return URL;
    }

    // Processes imgURL tags in the feed.
    private String readImage(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "LargeImage");
        String imgURL=null;
        while(parser.next()!=XmlPullParser.END_TAG){
            String str = parser.getName();
            if(parser.getName().equals("URL")) //Bring parser to the Item tag
              imgURL = readText(parser);
            else
                skip(parser);
        }
        parser.require(XmlPullParser.END_TAG, ns, "LargeImage");
        return imgURL;
    }

    // For the tags title and imgURL, extracts their text values.
    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";

        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }

        return result;
    }

    // Skips tags the parser isn't interested in. Uses depth to handle nested tags. i.e.,
    // if the next tag after a START_TAG isn't a matching END_TAG, it keeps going until it
    // finds the matching END_TAG (as indicated by the value of "depth" being 0).
    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
            case XmlPullParser.END_TAG:
                    depth--;
                    break;
            case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }
}
