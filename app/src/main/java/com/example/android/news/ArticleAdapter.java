package com.example.android.news;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by bplewis5 on 7/3/16.
 */
public class ArticleAdapter extends ArrayAdapter<Article> {

    private String LOG_TAG = "ArticleAdapter";

    static class ViewHolder {
        public TextView webTitleTextView;
        public TextView sectionNameTextView;
        public ImageView imageView;
    }

    public ArticleAdapter(Context context, ArrayList<Article> objects) {
        super(context, 0, objects);
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.list_item_article,
                    parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.webTitleTextView = (TextView) listItemView.findViewById(R.id.title_textview);
            viewHolder.sectionNameTextView = (TextView) listItemView.findViewById(R.id.section_name_textview);
            viewHolder.imageView = (ImageView) listItemView.findViewById(R.id.list_item_imageview);
            listItemView.setTag(viewHolder);
        }
        ViewHolder holder = (ViewHolder) listItemView.getTag();
        Article article = getItem(position);

        holder.webTitleTextView.setText(article.getTitle());
        holder.sectionNameTextView.setText(article.getSectionName());
        holder.imageView.setImageBitmap(article.getBitmap());



        return listItemView;
    }
}

