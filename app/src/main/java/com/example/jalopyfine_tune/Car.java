package com.example.jalopyfine_tune;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

public class Car extends Fragment {

    Button btnCar_mechanic;
    Button btnCar_electrical;
    Button btnCar_tyre;
    Button btnCar_petrol;
    Button btnCar_Wash;

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_car, container, false);

        btnCar_mechanic = (Button) rootView.findViewById(R.id.Car_m);
        btnCar_electrical = (Button) rootView.findViewById(R.id.Car_e);
        btnCar_tyre = (Button) rootView.findViewById(R.id.Car_t);
        btnCar_petrol = (Button) rootView.findViewById(R.id.Car_p);
        btnCar_Wash = (Button) rootView.findViewById(R.id.Car_w);

        btnCar_mechanic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CarMechanic.class);
                startActivity(intent);
            }
        });

        btnCar_electrical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CarElectrical.class);
                startActivity(intent);
            }
        });

        btnCar_tyre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CarTyreFlat.class);
                startActivity(intent);
            }
        });

        btnCar_petrol.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CarPetrol.class);
                startActivity(intent);
            }
        });

        btnCar_Wash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CarWash.class);
                startActivity(intent);
            }
        });

        return rootView;
    }
}
