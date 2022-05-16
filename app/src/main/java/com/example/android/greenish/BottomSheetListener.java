package com.example.android.greenish;

public interface BottomSheetListener {

 int TAKE_A_PHOTO = 0;
 int CHOOSE_FROM_GALLERY = 1;
 int CANCEL = 2;

 void onItemClicked(int Action);

}
