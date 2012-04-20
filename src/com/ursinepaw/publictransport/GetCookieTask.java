package com.ursinepaw.publictransport;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;
import android.widget.Toast;

public class GetCookieTask extends AsyncTask<String, Integer, String> {
	private MainActivity mActivity;
	
	public GetCookieTask(MainActivity activity) {
		mActivity = activity;
	}	
	
	@Override
	protected String doInBackground(String... args) {
		HttpClient hc = new DefaultHttpClient();
		String uri = String.format("http://beta.ulroad.ru/c.php");
		try {				
			HttpGet request = new HttpGet(uri);	
			HttpResponse response = hc.execute(request);
			if(response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				Header header = response.getFirstHeader("Set-Cookie");
				if (header != null) {
					String cookie = header.getValue();
					int pos = cookie.indexOf(';', 0);
					if (pos != -1)
						cookie = cookie.substring(0, pos);
					return cookie;
				}
			}				
		}
		catch(Exception e){	
		}

		return null;
	}
	
	@Override
	protected void onPostExecute(String result) {
		if (result == null) {
			Toast.makeText(mActivity, R.string.couldnt_get_session_id, Toast.LENGTH_SHORT).show();
			return;
		}
		mActivity.setCookie(result);
    }
}
