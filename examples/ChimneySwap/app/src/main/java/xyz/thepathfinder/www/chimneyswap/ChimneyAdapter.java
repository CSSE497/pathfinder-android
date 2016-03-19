package xyz.thepathfinder.www.chimneyswap;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ChimneyAdapter extends ArrayAdapter<Chimney> {

    private final String TAG = "ChimneyAdapter";

    private List<Chimney> chimneys;
    private Context context;

    public ChimneyAdapter(Context context, List<Chimney> chimneys) {
        super(context, -1, chimneys);
        this.context = context;
        this.chimneys = chimneys;
    }

    public void addChimney(Chimney chimney) {
        this.chimneys.add(chimney);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View chimneyView = inflater.inflate(R.layout.chimney_view, parent, false);

        TextView textView = (TextView) chimneyView.findViewById(R.id.chimney_name);
        ImageView imageView = (ImageView) chimneyView.findViewById(R.id.chimney_icon);

        Chimney chimney = this.chimneys.get(position);
        textView.setText(chimney.getName());
        imageView.setImageBitmap(chimney.getImage());

        return chimneyView;
    }
}
