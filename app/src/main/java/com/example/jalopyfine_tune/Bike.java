package com.example.jalopyfine_tune;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Bike extends Fragment {

    Button btnBike_m;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.bike, container, false);
        btnBike_m = rootView.findViewById(R.id.Bike_m);

        btnBike_m.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), BikeMechanic.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
