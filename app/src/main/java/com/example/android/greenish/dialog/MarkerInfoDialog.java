package com.example.android.greenish.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.android.greenish.R;
import com.example.android.greenish.util.DistanceUtils;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Date;

public class MarkerInfoDialog extends DialogFragment {

    private static Date age;
    private static Date lastWatering;
    private static String needForWater;
    private static String plantedBy;
    private static boolean isUserIn = false;

    private MarkerInfoDialog() {

    }

    public static MarkerInfoDialog newInstance(Date age, Date lastWatering, String needForWater, String plantedBy,
                                               boolean isUserIn) {
        MarkerInfoDialog dialog = new MarkerInfoDialog();
        MarkerInfoDialog.age = age;
        MarkerInfoDialog.lastWatering = lastWatering;
        MarkerInfoDialog.needForWater = needForWater;
        MarkerInfoDialog.plantedBy = plantedBy;
        MarkerInfoDialog.isUserIn = isUserIn;
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.marker_info_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setDialogProperties();

        ImageView imageView = view.findViewById(R.id.imageViewMarkerInfoDialog);


        TextView ageTxtView = view.findViewById(R.id.ageTextViewMarkerInfoDialog);
        String ageAsString = "Age: " + age.toString();
        ageTxtView.setText(ageAsString);

        TextView lastWateringTextView = view.findViewById(R.id.lastWateringTextViewMarkerInfoDialog);
        String lastWateringAsString = "Last watering: " + lastWatering.toString();
        lastWateringTextView.setText(lastWateringAsString);


        TextView needForWaterTextView = view.findViewById(R.id.needForWaterTextViewMarkerInfoDialog);
        String waterNeed = "Need for water: " + needForWater;
        needForWaterTextView.setText(waterNeed);

        TextView plantedByTextView = view.findViewById(R.id.plantedByTextViewMarkerInfoDialog);
        String planter = "Planted by: " + plantedBy;
        plantedByTextView.setText(planter);

        FloatingActionButton actionButton = view.findViewById(R.id.wateringATreeFloatingActionButton);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // inform - send back - to map activity/fragment ...
                if (isUserIn) {
                    Toast.makeText(getActivity(), "Watering a tree  !!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), "You're out", Toast.LENGTH_SHORT).show();
                    dismiss();
                }
            }
        });

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_from_bottom);
        anim.setDuration(500);
        view.setAnimation(anim);
        super.onViewCreated(view, savedInstanceState);
    }

    private void setDialogProperties() {
        if (getDialog() != null)
        {
            Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // background
            window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, // width
                    WindowManager.LayoutParams.WRAP_CONTENT); // height

            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.BOTTOM; // Align dialog to the BOTTOM of screen;
            window.setAttributes(params);

        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
    }

}
