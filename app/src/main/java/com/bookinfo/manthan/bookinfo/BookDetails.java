package com.bookinfo.manthan.bookinfo;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.util.HashMap;

/**
 * Created by Manthan on 9/20/2014.
 * This class provides the details of the book selected by the user from the list view provided
 */
public class BookDetails extends Activity {
    final String Title = "title";
    final String Sub_Title = "subTitle";
    final String Authors = "authors";
    final String Publisher = "publisher";
    final String Published_Date = "publishedDate";
    final String Description = "description";
    final String Book_Image = "bookImage";
    final String PageCount = "pageCount";
    final String Category = "category";
    final String AVG_Rating = "avg_rating";
    final String Ratings_Count = "ratingCount";
    final String WebLink = "weblink";
    final String Amount = "amount";
    final String BuyLink = "buylink";
    final String CurrencyCode = "currencyCode";
    final String Saleability = "saleability";
    final String ISBN13 = "ISBN13";
    final String ISBN10 = "ISBN10";
    final String buylink2 = "&sitesec=buy&source=gbs_buy_r";
    TextView titleTV, subTitleTV, publisherTV, publishedDateTV, authorsTV, descriptionTV, pageCountTV, categoryTV, ratingTV, amountTV, isbn13TV, isbn10Tv;
    ImageView bookImageIV;
    private ImageThreadLoader imageLoader = new ImageThreadLoader();
    HashMap hm;
    boolean subtitle, sale, author;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bookdetails);

        titleTV = (TextView) findViewById(R.id.TitleDTextView);
        subTitleTV = (TextView) findViewById(R.id.SubTitleDTextView);
        publisherTV = (TextView) findViewById(R.id.PublisherDTextView);
        publishedDateTV = (TextView) findViewById(R.id.PublishedDateDTextView);
        authorsTV = (TextView) findViewById(R.id.AuthorTextView);
        descriptionTV = (TextView) findViewById(R.id.DescriptionDTextView);
        pageCountTV = (TextView) findViewById(R.id.PageCountDTextView);
        categoryTV = (TextView) findViewById(R.id.CategoryDTextView);
        ratingTV = (TextView) findViewById(R.id.RatingDTextView);
        amountTV = (TextView) findViewById(R.id.AmountDTextView);
        isbn13TV = (TextView) findViewById(R.id.ISBN13DTextView);
        isbn10Tv = (TextView) findViewById(R.id.ISBN10DTextView);
        bookImageIV = (ImageView) findViewById(R.id.imageView);
        Button webButton = (Button) findViewById(R.id.WebButton);
        Button buyButton = (Button) findViewById(R.id.BuyButton);

        subtitle = true;
        sale = true;
        author = true;
        hm = new HashMap();

        Intent receive = getIntent();
        Bundle bundle = receive.getExtras();
        String jsonString = bundle.getString("jsonString");
        int position = bundle.getInt("selectedBook");

        parseJson(jsonString, position);
    }

    private void parseJson(String jsonString, int position) {
        JSONObject jsonobj = null;

        try {
            jsonobj = new JSONObject(jsonString);
            JSONObject query = jsonobj.getJSONObject("query");
            JSONObject results = query.getJSONObject("results");
            JSONObject json = results.getJSONObject("json");
            JSONArray items = json.getJSONArray("items");


            JSONObject book = items.getJSONObject(position);
            JSONObject volumeInfo = book.getJSONObject("volumeInfo");
            hm.put(Title, volumeInfo.getString("title"));
            Log.d("title", volumeInfo.getString("title"));

            try {
                hm.put(Sub_Title, volumeInfo.getString("subtitle"));
                Log.d("subtitle", volumeInfo.getString("subtitle"));
            } catch (JSONException e) {
                subtitle = false;
                e.printStackTrace();
            }

            try {
                hm.put(Authors, volumeInfo.getString("authors"));
                Log.d("title", volumeInfo.getString("authors"));
            } catch (JSONException e) {
                author = false;
            }

            try {
                hm.put(Publisher, volumeInfo.getString("publisher"));
                Log.d("publisher", volumeInfo.getString("publisher"));
            } catch (JSONException e) {
                hm.put(Publisher, "not available");
            }

            try {
                hm.put(Published_Date, volumeInfo.getString("publishedDate"));
                Log.d("publishedDate", volumeInfo.getString("publishedDate"));
            } catch (JSONException e) {
                hm.put(Published_Date, "not available");
            }

            try {
                hm.put(Description, volumeInfo.getString("description"));
                Log.d("description", volumeInfo.getString("description"));
            } catch (JSONException e) {
                hm.put(Description, "description: not available");
            }

            try {
                hm.put(PageCount, volumeInfo.getString("pageCount"));
                Log.d("pageCount", volumeInfo.getString("pageCount"));
            } catch (JSONException e) {
                hm.put(PageCount, "not available");
            }

            try {
                hm.put(Category, volumeInfo.getString("categories"));
                Log.d("categories", volumeInfo.getString("categories"));
            } catch (JSONException e) {
                hm.put(Category, "not available");
            }

            try {
                hm.put(AVG_Rating, volumeInfo.getString("averageRating"));
                Log.d("averageRating", volumeInfo.getString("averageRating"));
                hm.put(Ratings_Count, volumeInfo.getString("ratingsCount"));
                Log.d("ratingsCount", volumeInfo.getString("ratingsCount"));
            } catch (JSONException e) {
                hm.put(AVG_Rating, "not available");
                hm.put(Ratings_Count, " ");
                e.printStackTrace();
            }

            hm.put(WebLink, volumeInfo.getString("infoLink"));
            Log.d("infoLink", volumeInfo.getString("infoLink"));
            JSONObject imageLinks = volumeInfo.getJSONObject("imageLinks");
            Log.d("imageLinks", volumeInfo.getString("imageLinks"));
            hm.put(Book_Image, imageLinks.getString("thumbnail"));

            try {
                JSONArray industryIdentifier = volumeInfo.getJSONArray("industryIdentifiers");

                try {
                    JSONObject ISBN = industryIdentifier.getJSONObject(0);

                    hm.put(ISBN13, ISBN.getString("identifier"));
                    Log.d("identifier", ISBN.getString("identifier"));
                } catch (NullPointerException e) {
                    hm.put(ISBN13, "not available");
                } catch (JSONException e) {
                    hm.put(ISBN13, "not available");
                }


                try {
                    JSONObject ISBN = industryIdentifier.getJSONObject(1);
                    hm.put(ISBN10, ISBN.getString("identifier"));
                    Log.d("identifier", ISBN.getString("identifier"));

                } catch (NullPointerException e) {
                    hm.put(ISBN10, "not available");
                } catch (JSONException e) {
                    hm.put(ISBN10, "not available");
                    e.printStackTrace();
                }
            } catch (JSONException e) {
                hm.put(ISBN13, "not available");
                hm.put(ISBN10, "not available");
                e.printStackTrace();
            }


            JSONObject saleInfo = book.getJSONObject("saleInfo");
            hm.put(Saleability, saleInfo.getString("saleability"));
            if (!saleInfo.getString("saleability").contains("NOT")) {
                hm.put(BuyLink, saleInfo.getString("buyLink"));
                JSONObject retailPrice = saleInfo.getJSONObject("retailPrice");
                hm.put(Amount, retailPrice.getString("amount"));
                hm.put(CurrencyCode, retailPrice.getString("currencyCode"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        setDisplay();
    }

    private void setDisplay() {

        Bitmap cachedImage = null;

        //loading image from url
        try {
            cachedImage = imageLoader.loadImage((String) hm.get(Book_Image), new ImageThreadLoader.ImageLoadedListener() {
                public void imageLoaded(Bitmap imageBitmap) {
                    bookImageIV.setImageBitmap(imageBitmap);
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        try {
            titleTV.setText(hm.get(Title).toString());

            if (subtitle)
                subTitleTV.setText(hm.get(Sub_Title).toString());
            else
                subTitleTV.setVisibility(View.INVISIBLE);

            if (author)
                authorsTV.setText("by-" + hm.get(Authors).toString().replaceAll("\"", "").replace("[", "").replace("]", "").replaceAll(",", ", "));
            else
                authorsTV.setVisibility(View.INVISIBLE);

            publishedDateTV.setText("Published Date: " + hm.get(Published_Date).toString());
            publisherTV.setText("Publisher: " + hm.get(Publisher).toString());
            isbn10Tv.setText("ISBN10: " + hm.get(ISBN10).toString());
            isbn13TV.setText("ISBN13: " + hm.get(ISBN13).toString());
            ratingTV.setText("Rating: " + hm.get(AVG_Rating).toString() + " (" + hm.get(Ratings_Count).toString() + ")");

            if (!hm.get(Saleability).toString().contains("NOT")) {
                amountTV.setText("Price: " + hm.get(Amount).toString() + " " + hm.get(CurrencyCode).toString());
            } else {
                amountTV.setVisibility(View.INVISIBLE);
            }

            pageCountTV.setText("Pages: " + hm.get(PageCount).toString());
            descriptionTV.setText(hm.get(Description).toString());
            categoryTV.setText("Category: " + hm.get(Category).toString());

            if (cachedImage != null) {
                bookImageIV.setImageBitmap(cachedImage);
            }

        } catch (NullPointerException e) {
            e.printStackTrace();
        }

    }

    public void webButtonOnClick(View view) {
        String url = hm.get(WebLink).toString();

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(url));
        startActivity(i);
    }

    public void buyButtonOnClick(View view) {
        if (sale) {

            try {
                String urlfull[] = hm.get(BuyLink).toString().split("&");
                String url = urlfull[0] + buylink2;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (NullPointerException e) {
                Toast toast = Toast.makeText(getApplicationContext(), "Sorry! Buy link of this book is not available", Toast.LENGTH_SHORT);
                toast.show();
            }

        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "Sorry! This Book is not for sale", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
}
