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

class myPlantsListAdapter extends ArrayAdapter<MyPlant> {

    ArrayList<MyPlant> arrayListMyPlant;
    Context context;

    public myPlantsListAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MyPlant> objects) {
        super(context, resource, objects);
        arrayListMyPlant = objects;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_my_plant, parent, false);
        }

        MyPlant myplant = arrayListMyPlant.get(position);
        TextView nameView = convertView.findViewById(R.id.textViewMPName);
        TextView statusView = convertView.findViewById(R.id.textViewMPStatus);
        ImageView imageView = convertView.findViewById(R.id.imageViewMPFoto);
        ImageView statusImageView = convertView.findViewById(R.id.imageViewMPConnected);

        nameView.setText(myplant.getName());
        statusView.setText(myplant.getStatus());
        if (!myplant.isConnected()) {
            statusImageView.setVisibility(View.GONE);
        }

        return convertView;
    }
}
