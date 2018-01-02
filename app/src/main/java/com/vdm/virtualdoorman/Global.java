package com.vdm.virtualdoorman;

import android.app.Application;


public class Global extends Application{
	  private static String loginID;

	    public void setLoginDetail(String id) {
	       this.loginID = id;
	    }
	    public String getLoginDetail() {
	        return loginID;
	    }
}
