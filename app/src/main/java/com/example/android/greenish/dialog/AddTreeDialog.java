package com.example.android.greenish.dialog;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.android.greenish.MarkerInfo;
import com.example.android.greenish.OnPlantTreeListener;
import com.example.android.greenish.R;
import com.example.android.greenish.util.ZoomLevel;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class AddTreeDialog extends DialogFragment {

    private static final String TAG = "log_trace";

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_ADDRESS_INFO = "ADDRESS_INFO";

    // TODO: Rename and change types of parameters
    private ArrayList<String> addressInfo;
    private OnPlantTreeListener onPlantTreeListener;

    // vars
    private boolean ok = false;

    public AddTreeDialog() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param addressInfo Parameter 1.
     * @return A new instance of fragment AddTreeDialogFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddTreeDialog newInstance(OnPlantTreeListener listener, ArrayList<String> addressInfo)
    {
        AddTreeDialog dialog = new AddTreeDialog();
        Bundle args = new Bundle();
        args.putStringArrayList(ARG_ADDRESS_INFO, addressInfo);
        dialog.setArguments(args);
        dialog.onPlantTreeListener = listener;
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            addressInfo = getArguments().getStringArrayList(ARG_ADDRESS_INFO);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.add_tree_dialog, container, false);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return super.onCreateDialog(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        setDialogProperties();
        EditText firstInput = view.findViewById(R.id.firstInput);
        EditText secondInput = view.findViewById(R.id.secondInput);

        TextView tvLat = view.findViewById(R.id.addressInfoTextView);
        tvLat.setText(getAddressInfo());

        Button actionButton = view.findViewById(R.id.addTreeButtonDialog);
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ok = true;
                dismiss();
            }
        });

        Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_from_bottom);
        anim.setDuration(500);
        view.setAnimation(anim);
        Log.d(TAG, "onViewCreated: - end_AddTreeDialog");
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


    private double latitude;
    private double longitude;
    String countryName;
    String adminArea;
    String subAdminArea;
    String thoroughFare;
    String subThoroughFare;

    private String getAddressInfo()
    {
        latitude = Double.parseDouble(addressInfo.get(0));
        longitude = Double.parseDouble(addressInfo.get(1));
        countryName = addressInfo.get(2);
        adminArea = addressInfo.get(3);
        subAdminArea = addressInfo.get(4);
        thoroughFare = addressInfo.get(5);
        subThoroughFare = addressInfo.get(6);

        return "Lat: " + latitude + "\n" +
                "Lng: " + longitude + "\n" +
                "Country name: " + countryName + "\n" +
                "Admin area: " + adminArea + "\n" +
                "Sub admin area: " + subAdminArea + "\n" +
                "Thorough fare: " + thoroughFare + "\n" +
                "Sub thorough fare: " + subThoroughFare + "\n";
    }

    @Override
    public void onDetach() {
        if (onPlantTreeListener != null)
        {
            onPlantTreeListener.onPlantTree(ok,
                    new MarkerOptions().position(new LatLng(latitude, longitude))
                            .title(countryName)
                            .snippet("Admin area: " + adminArea + "\n" +
                                    "Sub admin area: " + subAdminArea + "\n" +
                                    "Thorough fare: " + thoroughFare + "\n" +
                                    "Sub thorough fare: " + subThoroughFare + "\n")

            );
        }
        super.onDetach();
    }

}