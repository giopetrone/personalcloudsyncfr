package it.unito.utility;

import it.unito.gui.R;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyTablesListAdapter extends ArrayAdapter<ViewTable>{

    /** To cache views of item */
    private static class ViewHolder {
        private TextView text1;
        private TextView text2;
         
        ViewHolder() { }
    }

    /** Inflater for list items */
    private final LayoutInflater inflater;

    /**
     * General constructor
     *
     * @param context
     * @param resource
     * @param textViewResourceId
     * @param objects
     */
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
            holder.text2 = (TextView)itemView.findViewById(R.id.subtitle_table);
          
            itemView.setTag(holder);
        } else {
            holder = (ViewHolder)itemView.getTag();
        }

        holder.text1.setText(item.getTableName());
        holder.text2.setText("Docs: "+item.getNumDocuments()+" Members: "+item.getNumMembers());
        return itemView;
    }
}
