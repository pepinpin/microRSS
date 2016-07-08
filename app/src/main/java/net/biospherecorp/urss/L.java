package net.biospherecorp.urss;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Pe on 05/06/2016.
 */


public abstract class L extends Context{

	private static boolean IS_ACTIVATED = true;     // change this to active / deactivate this tool

	static void m(String message){
		if (IS_ACTIVATED) {
			Log.i(">> Log message : ", message);
		}
	}

	// easy way to turn a L.t() into a L.m()
	static void m(Context context, String message){

		// do nothing with context
		m(message);

	}

	static void t(Context context, String message){

		if (IS_ACTIVATED) {
			Toast.makeText(context, message, Toast.LENGTH_LONG).show();
		}
	}
}
