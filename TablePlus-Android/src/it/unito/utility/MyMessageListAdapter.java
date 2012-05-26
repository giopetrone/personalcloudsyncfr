package it.unito.utility;

import it.unito.gui.R;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

//Adapter for show in BlackBoard with the message its type and the author which writed that message
public class MyMessageListAdapter extends ArrayAdapter<ViewMessage>{

	/** Inflater for list items */
    private final LayoutInflater inflater;
    
    /** To cache views of item */
    private static class ViewHolder {
        private TextView text1;
        private TextView text2;
        private TextView text3;
        ViewHolder() { }
    }


    public MyMessageListAdapter(final Context context,
            final int resource,
            final int textViewResourceId,
            final List<ViewMessage> objects) {
        super(context, resource, textViewResourceId, objects);

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {
        View itemView = convertView;
        ViewHolder holder = null;
        ViewMessage item = getItem(position);
        if(null == itemView) {
            itemView = this.inflater.inflate(R.layout.blackboard_row, parent, false);
            holder = new ViewHolder();
            holder.text1 = (TextView)itemView.findViewById(R.id.title_table);
            holder.text2 = (TextView)itemView.findViewById(R.id.subtitle_docs);
            holder.text3 = (TextView)itemView.findViewById(R.id.subtitle_members);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder)itemView.getTag();
        }
	    holder.text1.setText(item.getContent());
        holder.text2.setText("Type: "+item.getType());
        holder.text3.setText(item.getAuthor());
        return itemView;
    }
}
