package it.unito.utility;

import java.util.List;

import it.unito.gui.R;
import it.unito.model.Message;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class MyMessageListAdapter extends ArrayAdapter<Message>{

	/** Inflater for list items */
    private final LayoutInflater inflater;
    
    /** To cache views of item */
    private static class ViewHolder {
        private TextView text1;
        private TextView text2;
        private TextView text3;
        ViewHolder() { }
    }

    

    /**
     * General constructor
     *
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param objects
     */
    public MyMessageListAdapter(final Context context,
            final int resource,
            final int textViewResourceId,
            final List<Message> objects) {
        super(context, resource, textViewResourceId, objects);

        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View itemView = convertView;
        ViewHolder holder = null;
        final Message item = getItem(position);

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
        holder.text3.setText("Author: "+item.getAuthor());
        return itemView;
    }
}
