package it.unito.utility;

import java.util.List;

import it.unito.gui.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

//Adapter for show in ExistantTable at the left of Object name different images
public class MyObjectListAdapter extends ArrayAdapter<ViewDoc> {

	public MyObjectListAdapter(Context context, List<ViewDoc> objects){
		super(context, R.layout.object_row, R.id.title,objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{	
		convertView = super.getView(position, convertView, parent);
		ViewDoc item = getItem(position);
		ImageView image = (ImageView) convertView.findViewById(R.id.image);

		if (item.getLink().contains("https:") || item.getLink().contains("http:"))
			image.setImageResource(R.drawable.world48);
		else
			image.setImageResource(R.drawable.doc48);
		return convertView;
		
	}
}
