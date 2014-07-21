package com.example.EditTextAndShowGrid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;

/* Created by YULIYA on 14.07.2014.
 */
public class GridviewAdapter  extends BaseAdapter
    {
        private ArrayList<String> listFormattedAddress;
        private ArrayList<String> listImagesURL;
        private ArrayList<String> listLatLng;
        private Context context;
        public GridviewAdapter(Context context, ArrayList<String> listFormattedAddress, ArrayList<String> listImagesURL,ArrayList<String> listLatLng) {
            super();
            this.listFormattedAddress = listFormattedAddress;
            this.listImagesURL = listImagesURL;
            this.context = context;
            this.listLatLng = listLatLng;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return listFormattedAddress.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return null;
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return 0;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            View grid;
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            TextView textView;
            TextView textViewLatLng;
            ImageView imageView;
            if (convertView == null) {
                grid = new View(context);
                grid = inflater.inflate(R.layout.grid_row, null);
                textView = (TextView) grid.findViewById(R.id.textView1);
                textViewLatLng = (TextView) grid.findViewById(R.id.textViewLatLng);
                imageView = (ImageView)grid.findViewById(R.id.imageView1);
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.init(ImageLoaderConfiguration.createDefault(context));
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .imageScaleType(ImageScaleType.IN_SAMPLE_INT)
                        .cacheInMemory()
                        .cacheOnDisc()
                        .build();
                imageLoader.displayImage(listImagesURL.get(position), imageView, options);
                textView.setText(listFormattedAddress.get(position));
                textViewLatLng.setText(listLatLng.get(position));
            } else {
                grid = (View) convertView;
            }
            return grid;
        }
    }



