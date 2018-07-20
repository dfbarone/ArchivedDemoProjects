package com.ad.aggregator.app.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class StreamItem extends DummyContent.DummyItem implements Comparable<StreamItem>{
    public String image;
    public String source;
    public String published;

    public StreamItem(JSONObject obj)  {
        super("", "", "", "");

        source = "";
        published = "";
        try {

            this.id = obj.getString("id");
            this.cardType = obj.getString("card_type");

            if (cardType != null && cardType.compareTo("article") == 0) {

                JSONObject baselink = obj.getJSONObject("baselink");
                this.title = baselink.getString("headline");
                JSONArray images = baselink.getJSONArray("image");
                if (images.length() > 0) {
                    JSONObject o = (JSONObject)images.get(0);
                    this.image = o.getString("url");
                }
                this.details = baselink.getString("content_url");
                this.source = baselink.getString("source");
                this.published = baselink.getString("published");
            }
            else if (cardType != null && cardType.compareTo("tweet") == 0) {

                JSONObject baselink = obj.getJSONObject("baselink");
                this.title = baselink.getString("headline");
                this.details = baselink.getString("content_url");
                this.source = "Twitter";
                this.published = baselink.getString("published");
            }
            else if (cardType != null && cardType.compareTo("dfp_ad") == 0) {

                this.title = "Advertisement";
                this.image = "http://blog.clairepeetz.com/wp-content/uploads/2016/08/cnnmoney.jpg?resolution=1024,1";
                this.source = "Advertisement";
                this.published = "";
            }

        } catch (Exception e) {

        }
    }

    public Date getDateTime() {
        Date dt = new Date(0);
        try {
            String[] s = this.published.split("T");
            String day = s[0];
            String hms = s[1];

            String[] s2 = hms.split("\\.");
            hms = s2[0];

            String zone = s2[1].split("Z")[0];

            SimpleDateFormat date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
            dt = date.parse(day + " " + hms + " ");
        } catch (Exception e) {

        }
        return dt;
    }

    @Override
    public int compareTo(StreamItem o) {
        return this.getDateTime().compareTo(o.getDateTime());
    }

    //@Override
    public int compare(StreamItem o1, StreamItem o2) {
        return o1.getDateTime().compareTo(o2.getDateTime());
    }
}
