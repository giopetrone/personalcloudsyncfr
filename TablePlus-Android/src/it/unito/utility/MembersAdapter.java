package it.unito.utility;


import java.util.List;

import it.unito.gui.R;
import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

public class MembersAdapter extends ArrayAdapter<ViewMembers> {

	public MembersAdapter(Context context, List<ViewMembers> objects){
		super(context, R.layout.object_row, R.id.title,objects);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		convertView = super.getView(position, convertView, parent);
		ViewMembers item = getItem(position);
		ImageView image = (ImageView) convertView.findViewById(R.id.image);
		
		if (item.getStatus().contains("online"))
			image.setImageResource(R.drawable.online);
		else
			image.setImageResource(R.drawable.offline);
		return convertView;
		
	}
}
