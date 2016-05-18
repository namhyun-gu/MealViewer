package com.earlier.yma.data.model.iab;

/**
 * Created by namhyun on 2016-03-07.
 */
public class SkuDetails {
    private String productId;
    private String type;
    private String price;
    private String price_amount_micros;
    private String price_currency_code;
    private String title;
    private String description;

    public String getProductId() {
        return productId;
    }

    public String getType() {
        return type;
    }

    public String getPrice() {
        return price;
    }

    public String getPrice_amount_micros() {
        return price_amount_micros;
    }

    public String getPrice_currency_code() {
        return price_currency_code;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
