package com.example.tareaprog;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class PhotoAdapter extends ArrayAdapter<Photograph> {
    public PhotoAdapter(Context context, List<Photograph> photos) {
        super(context, 0, photos);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.photo_item, parent, false);
        }

        ImageView photoImageView = convertView.findViewById(R.id.photoImageView);
        TextView descriptionTextView = convertView.findViewById(R.id.descriptionTextView);

        Photograph photo = getItem(position);

        if (photo != null) {
            photoImageView.setImageBitmap(photo.getImage());
            descriptionTextView.setText(photo.getDescription());
        }

        return convertView;
    }
}