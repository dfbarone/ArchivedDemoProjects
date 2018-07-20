package com.ad.aggregator.app.model;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MarketItem extends DummyContent.DummyItem implements Comparable<MarketItem>{
    public int changeDirection;
    public String changeValue;
    public String tickerSymbol;
    public String changePercent;
    public String value;
    public String published;

    public MarketItem(String title) {
        super("", title, "", "");

        changeDirection = 0;
        changeValue = "-";
        tickerSymbol = "-";
        changePercent = "-";
        value  = "-";
        published = "";
    }

    public MarketItem(String id, String cardType, JSONObject obj)  {
        super("", "", "", "");

        changeDirection = 0;
        changeValue = "-";
        tickerSymbol = "-";
        changePercent = "-";
        value  = "-";
        published = "";

        try {

            this.id = id;
            this.cardType = cardType;

            this.title = obj.getString("label");
            this.changeDirection = obj.getInt("change_direction");
            this.changeValue = obj.getString("change_value");
            this.tickerSymbol = obj.getString("ticker_symbol");
            this.changePercent = obj.getString("change_percent");
            this.value  = obj.getString("value");
            this.published = obj.getString("last_trade_time");

            this.details = "http://money.cnn.com/quote/quote.html?symb=" + this.tickerSymbol;

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
    public int compareTo(MarketItem o) {
        return this.getDateTime().compareTo(o.getDateTime());
    }

    //@Override
    public int compare(MarketItem o1, MarketItem o2) {
        return o1.getDateTime().compareTo(o2.getDateTime());
    }
}
