package com.drive.app.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.drive.app.R;
import com.drive.app.model.File;
import com.drive.app.model.Folder;
import java.util.ArrayList;

public class FolderAdapter extends ArrayAdapter<Object> {
    private Context context;
    private int resource;

    public FolderAdapter(Context context, int resource, ArrayList<Object> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        convertView = layoutInflater.inflate(resource, parent, false);

        ImageView imageView = convertView.findViewById(R.id.imageView);
        TextView textView1 = convertView.findViewById(R.id.textView1);
        TextView textView2 = convertView.findViewById(R.id.textView2);

        if(getItem(position).getClass() == Folder.class){
            Folder folder = (Folder) getItem(position);
            imageView.setImageResource(R.drawable.folder);
            textView1.setText(folder.getName());
        }
        if(getItem(position).getClass() == File.class){
            File file = (File) getItem(position);
            imageView.setImageResource(file.getImage());
            textView1.setText(file.getName());
        }
        textView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((ListView) parent).performItemClick(v, position, 0);
            }
        });

        return convertView;
    }
}
