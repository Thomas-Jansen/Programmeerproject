/*
    Thomas Jansen 11008938
    Programmeerproject - PlantBase

    Custom adapter to show MyPlants in LinkingActivity.
*/

package thomas.jansen.plantbase.Adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import thomas.jansen.plantbase.Classes.MyPlant;
import thomas.jansen.plantbase.R;

public class AdapterLinkMyPlant extends ArrayAdapter<MyPlant> {

    private ArrayList<MyPlant> arrayListMyPlant;
    Context context;

    public AdapterLinkMyPlant(@NonNull Context context, int resource, @NonNull ArrayList<MyPlant> objects) {
        super(context, resource, objects);
        arrayListMyPlant = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_link_myplant, parent, false);
        }

        MyPlant myplant = arrayListMyPlant.get(position);
        TextView nameView = convertView.findViewById(R.id.textViewLinkPlantName);
        TextView linkedToView = convertView.findViewById(R.id.textViewLinkedTo);


        nameView.setText(myplant.getName());
        linkedToView.setText("Linked to: " + myplant.getArduinoName());

        return convertView;
    }
}
