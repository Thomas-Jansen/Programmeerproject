// Custom adapter to show PlantNodes in LinkingActivity.

package thomas.jansen.plantbase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AdapterLinkNode extends ArrayAdapter{

    Context context;
    ArrayList<String> arrayListNodeName;

    public AdapterLinkNode(@NonNull Context context, int resource, @NonNull ArrayList<String> objects) {
        super(context, resource, objects);
        arrayListNodeName = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_plant_node, parent, false);
        }
        String nodeName = arrayListNodeName.get(position);
        TextView nameView = convertView.findViewById(R.id.textViewNodeName);
        nameView.setText(nodeName);

        return convertView;
    }
}
