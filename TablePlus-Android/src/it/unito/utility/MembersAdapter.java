package it.unito.utility;


import java.util.List;

import it.unito.gui.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MembersAdapter extends ArrayAdapter<String> {

	public MembersAdapter(Context context, List<String> objects){
		super(context, R.layout.object_row, R.id.title,objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		convertView = super.getView(position, convertView, parent);
		String item = getItem(position);
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		
		if (item.contains("ONLINE"))
			image.setImageResource(R.drawable.user_offline);
		else
			image.setImageResource(R.drawable.user_online);
		return convertView;
		
	}
}
