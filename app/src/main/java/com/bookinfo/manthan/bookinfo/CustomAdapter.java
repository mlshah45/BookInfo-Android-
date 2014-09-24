package com.bookinfo.manthan.bookinfo;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Manthan on 9/20/2014.
 * This is a custom adapter for list view
 */
public class CustomAdapter extends ArrayAdapter {
    List allbooks;
    protected Context context;
    int resource_id;
    private ImageThreadLoader imageLoader = new ImageThreadLoader();
    private static LayoutInflater inflater = null;


    public CustomAdapter(Main main, int list_row, List books) {
        super(main, list_row, books);

        this.context = main;
        this.resource_id = list_row;
        allbooks = books;
        inflater = (LayoutInflater) main.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return allbooks.size();
    }

    @Override
    public Object getItem(int position) {
        return allbooks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view;
        TextView TitletextView;
        TextView AuthorTextView;
        final ImageView image;

        view = inflater.inflate(resource_id, parent, false);

        try {
            TitletextView = (TextView) view.findViewById(R.id.TitleTextView);
            AuthorTextView = (TextView) view.findViewById(R.id.AuthorTextView);
            image = (ImageView) view.findViewById(R.id.list_image);
        } catch (ClassCastException e) {
            throw e;
        }

        HashMap item = (HashMap) getItem(position);
        Bitmap cachedImage = null;

        //loading image from url
        try {
            cachedImage = imageLoader.loadImage((String) item.get(Main.KEY_IMAGE), new ImageThreadLoader.ImageLoadedListener() {
                public void imageLoaded(Bitmap imageBitmap) {
                    image.setImageBitmap(imageBitmap);
                    notifyDataSetChanged();
                }
            });
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        TitletextView.setText(item.get(Main.KEY_TITLE).toString());
        AuthorTextView.setText(item.get(Main.KEY_AUTHORS).toString().replaceAll("\"", "").replace("[", "").replace("]", "").replaceAll(",", ", "));

        if (cachedImage != null) {
            image.setImageBitmap(cachedImage);
        }

        return view;
    }
}



