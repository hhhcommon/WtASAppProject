package Test;

import org.json.JSONArray;
import org.json.JSONObject;

import com.android.volley.Response;
import com.android.volley.Response.Listener;

public class TestJson {
	public static void main(String[] args){
		Listener<Object> listener = new Listener<Object>() {

			@Override
			public void onResponse(Object arg0) {
				if (arg0 instanceof JSONObject) {
					
				} else if (arg0 instanceof JSONArray) {
					
				}
				// TODO Auto-generated method stub
				
			}
			
		};
	}
}