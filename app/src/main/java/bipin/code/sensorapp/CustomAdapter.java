package bipin.code.sensorapp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

public class CustomAdapter extends ArrayAdapter {
    private Activity context;

    //to store the animal images

    //to store the list of countries
    private List<String> nameArray;
    HomeScreen homeScreen;
    //to store the list of countries
    private String[] infoArray;

    public CustomAdapter(@NonNull Activity context, List<String> name,HomeScreen checkInterface) {
        super(context, R.layout.sensorlist, name);
        this.context = context;
        this.nameArray = name;
        this.homeScreen = checkInterface;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.sensorlist, null, true);
        TextView name = rowView.findViewById(R.id.name);
        CheckBox checkBox = rowView.findViewById(R.id.check);
        name.setText(nameArray.get(position));
        checkBox.setTag(""+position);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (buttonView.getTag().equals("0")) {
                    if (isChecked) {
                        homeScreen.checkChange("0",true);
                    } else {
                        homeScreen.checkChange("0",false);
                    }
                }
                if (buttonView.getTag().equals("1")) {
                    if (isChecked) {
                        homeScreen.checkChange("1",true);
                    } else {
                        homeScreen.checkChange("1",false);
                    }
                }if (buttonView.getTag().equals("2")) {
                    if (isChecked) {
                        homeScreen.checkChange("2",true);
                    } else {
                        homeScreen.checkChange("2",false);
                    }
                }if (buttonView.getTag().equals("3")) {
                    if (isChecked) {
                        homeScreen.checkChange("3",true);
                    } else {
                        homeScreen.checkChange("3",false);
                    }
                }if (buttonView.getTag().equals("4")) {
                    if (isChecked) {
                        homeScreen.checkChange("4",true);
                    } else {
                        homeScreen.checkChange("4",false);
                    }
                }if (buttonView.getTag().equals("5")) {
                    if (isChecked) {
                        homeScreen.checkChange("5",true);
                    } else {
                        homeScreen.checkChange("5",false);
                    }
                }
            }
        });
        return rowView;
    }
}
