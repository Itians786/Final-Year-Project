package com.example.jalopyfine_tune;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Bike extends Fragment {

    Button btnBike_mechanic;
    Button btnBike_electrical;
    Button btnBike_tyre;
    Button btnBike_petrol;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_bike, container, false);

        btnBike_mechanic = (Button) rootView.findViewById(R.id.Bike_m);
        btnBike_electrical = (Button) rootView.findViewById(R.id.Bike_e);
        btnBike_tyre = (Button) rootView.findViewById(R.id.Bike_t);
        btnBike_petrol = (Button) rootView.findViewById(R.id.Bike_p);

        btnBike_mechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BikeMechanic.class);
                startActivity(intent);
            }
        });

        btnBike_electrical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BikeElectrical.class);
                startActivity(intent);
            }
        });

        btnBike_tyre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BikeTyreFlat.class);
                startActivity(intent);
            }
        });

        btnBike_petrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BikePetrol.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
