package it.unito.utility;

import it.unito.gui.R;
import java.util.List;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

//Adapter for show in TableList the table name and its attribute
public class MyTablesListAdapter extends ArrayAdapter<ViewTable>{

	/** Inflater for list items */
    private final LayoutInflater inflater;
    
    /** To cache views of item */
    private static class ViewHolder {
        private TextView text1;
        private TextView text2;
        private TextView text3;
        ViewHolder() { }
    }

    
    public MyTablesListAdapter(final Context context,
            final int resource,
            final int textViewResourceId,
            final List<ViewTable> objects) {
        super(context, resource, textViewResourceId, objects);
        this.inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(final int position, final View convertView, final ViewGroup parent) {

        View itemView = convertView;
        ViewHolder holder = null;
        final ViewTable item = getItem(position);
        if(null == itemView) {
            itemView = this.inflater.inflate(R.layout.tables_row, parent, false);
            holder = new ViewHolder();
            holder.text1 = (TextView)itemView.findViewById(R.id.title_table);
            holder.text2 = (TextView)itemView.findViewById(R.id.subtitle_docs);
            holder.text3 = (TextView)itemView.findViewById(R.id.subtitle_members);
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder)itemView.getTag();
        }

        holder.text1.setText(item.getTableName());
        holder.text2.setText("N. of docs: "+item.getNumDocuments());
        holder.text3.setText("  N. of members: "+item.getNumMembers());
        return itemView;
    }
}
 