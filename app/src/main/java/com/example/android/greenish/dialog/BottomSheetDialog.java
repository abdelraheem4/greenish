package com.example.android.greenish.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.android.greenish.BottomSheetListener;
import com.example.android.greenish.R;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

public class BottomSheetDialog extends DialogFragment {

    private static final String TAG = "log_trace";
    private BottomSheetListener mListener;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    private BottomSheetDialog() {

    }

    public static BottomSheetDialog newInstance(BottomSheetListener bottomSheetListener) {
        BottomSheetDialog dialog = new BottomSheetDialog();
        dialog.mListener = bottomSheetListener;
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: BottomSheetDialog");
        return inflater.inflate(R.layout.bottom_sheet_layout, container,
                false); // Pass "false" is the standard in fragments => because the fragment manager
        // is responsible for attach the View to the container;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onViewCreated: BottomSheetDialog");
        setDialogProperties();
        LinearLayout takePhoto_ll = view.findViewById(R.id.takePhotoLayout);
        takePhoto_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TAKE_A_PHOTO = 0;
                finish(0);
            }
        });

        LinearLayout chooseFromGallery_ll = view.findViewById(R.id.chooseFromGalleryLayout);
        chooseFromGallery_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // CHOOSE_FROM_GALLERY = 1;
                finish(1);
            }
        });

        Button cancel_btn = view.findViewById(R.id.cancelButton);
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                // CANCEL
                finish(2);
            }
        });

        super.onViewCreated(view, savedInstanceState);
    }


    private void setDialogProperties() {
        if (getDialog() != null)
        {
            Window window = getDialog().getWindow();
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // background


            WindowManager.LayoutParams params = window.getAttributes();
            params.gravity = Gravity.CENTER; // Set dialog in screen' center;
            window.setAttributes(params);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setCancelable(false);
        return super.onCreateDialog(savedInstanceState);
    }

    private void finish(int userAction)
    {
        if (mListener != null)
        {
            mListener.onItemClicked(userAction);
        }
        dismiss();
    }

}