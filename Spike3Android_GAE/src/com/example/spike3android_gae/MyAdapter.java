package com.example.spike3android_gae;

import java.util.List;

import com.example.spike3android_gae.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MyAdapter extends ArrayAdapter<ViewDoc>{
	  public MyAdapter(Context context, List<ViewDoc> objects){
          super(context, R.layout.myadapter, R.id.title,objects);
  }
 
 @Override
 public View getView(int position, View convertView, ViewGroup parent)
 {convertView = super.getView(position, convertView, parent);
 ViewDoc item = getItem(position);
 ImageView image = (ImageView) convertView.findViewById(R.id.image);

 if (item.getLink().contains("https://docs") || item.getLink().contains("https://drive"))
         image.setImageResource(R.drawable.doc48);
 else
         image.setImageResource(R.drawable.world48);
 return convertView;
 }
}
