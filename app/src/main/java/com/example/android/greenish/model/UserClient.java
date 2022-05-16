package com.example.android.greenish.model;

import android.app.Application;

public class UserClient extends Application {

   private User user;

   public User getUser() {
      return user;
   }

   public void setUser(User user) {
      this.user = user;
   }
}
