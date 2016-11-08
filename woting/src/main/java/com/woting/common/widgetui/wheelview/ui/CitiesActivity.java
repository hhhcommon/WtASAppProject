package com.woting.common.widgetui.wheelview.ui;

import android.app.Activity;
import android.os.Bundle;

import com.woting.R;
import com.woting.common.widgetui.wheelview.ArrayWheelAdapter;
import com.woting.common.widgetui.wheelview.CityWheelView;
import com.woting.common.widgetui.wheelview.OnWheelChangedListener;
import com.woting.common.widgetui.wheelview.WheelView;

public class CitiesActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog_cities);

        String countries[] = new String[]{"USA", "Canada", "Ukraine", "France"};
        final String cities[][] = new String[][]{
                new String[]{"New York", "Washington", "Chicago", "Atlanta", "Orlando"},
                new String[]{"Ottawa", "Vancouver", "Toronto", "Windsor", "Montreal"},
                new String[]{"Kiev", "Dnipro", "Lviv", "Kharkiv"},
                new String[]{"Paris", "Bordeaux"},
        };

        CityWheelView country = (CityWheelView) findViewById(R.id.country);

        country.setVisibleItems(3);
        country.setCyclic(true);//
        country.setAdapter(new ArrayWheelAdapter<String>(countries));
        country.setCurrentItem(2);

        final CityWheelView city = (CityWheelView) findViewById(R.id.city);
        country.addChangingListener(new OnWheelChangedListener() {
            @Override
            public void onChanged(WheelView wheel, int oldValue, int newValue) {
            }

            @Override
            public void onChangeds(CityWheelView wheel, int oldValue, int newValue) {
                city.setAdapter(new ArrayWheelAdapter<String>(cities[newValue]));
                city.setCurrentItem(cities[newValue].length / 2);
            }

        });
        city.setVisibleItems(5);
    }
}
