package thomas.jansen.plantbase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

class plantsListAdapter extends ArrayAdapter<Plant> {

    ArrayList<Plant> arrayListPlant;
    Context context;

    public plantsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Plant> objects) {
        super(context, resource, objects);
        arrayListPlant = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_plant_info, parent, false);
        }


        Plant plant = arrayListPlant.get(position);

        String imageName= plant.getImageID();
        int imageId = context.getResources().getIdentifier(imageName , "drawable", context.getPackageName());

        TextView nameView = convertView.findViewById(R.id.textViewItemPlantName);
        ImageView imageView = convertView.findViewById(R.id.imageViewItemFoto);

        nameView.setText(plant.getName());
        imageView.setImageResource(imageId);

        return convertView;
    }
}
