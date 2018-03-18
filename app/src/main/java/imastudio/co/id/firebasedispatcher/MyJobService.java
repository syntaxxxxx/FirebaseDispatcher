package imastudio.co.id.firebasedispatcher;

import android.app.NotificationManager;
import android.content.Context;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONObject;

import java.text.DecimalFormat;

import cz.msebera.android.httpclient.Header;

//TODO 2 Implements Method
public class MyJobService extends JobService {

    //TODO 3 Deklarasi
    public static final String TAG = MyJobService.class.getSimpleName();
    final String APP_ID = "610f6dfb068b217f3396b6241ed45534";
    public static String EXTRAS_CITY = "extras_city";

    @Override
    public boolean onStartJob(JobParameters job) {
        //TODO 4 Create Method
        getCurrentWeather(job);
        // run dgn proses thread berbeda beri true seperti asynctask
        return true;
    }

    @Override
    public boolean onStopJob(JobParameters job) {
        return true;
    }

    //TODO 5 Request API Use AsyncTask
    private void getCurrentWeather(final JobParameters job) {
        String city = job.getExtras().getString(EXTRAS_CITY);
        AsyncHttpClient client = new AsyncHttpClient();
        String url = "http://api.openweathermap.org/data/2.5/weather?q="+city+"&appid="+APP_ID;
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                String result = new String(responseBody);
                Log.d(TAG, result);

                try {

                    JSONObject responseObject = new JSONObject(result);
                    String currentWeather = responseObject.getJSONArray("weather").getJSONObject(0).getString("main");
                    String description = responseObject.getJSONArray("weather").getJSONObject(0).getString("description");

                    double tempInKelvin = responseObject.getJSONObject("main").getDouble("temp");
                    double tempInCelcius = tempInKelvin - 273;

                    String temprature = new DecimalFormat("##.##").format(tempInCelcius);
                    String title = "Current Weather";
                    String message = currentWeather + ", " + description + "with" + temprature + "celcius";

                    int notifId = 100;

                    showNotification(getApplicationContext(), title, message, notifId);

                    // ketika proses selesai
                    jobFinished(job, false);
                } catch (Exception e) {
                    // set ulang ketika  error
                    jobFinished(job, true);
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                // ketika proses gagal
                jobFinished(job, true);
            }
        });
    }

    //TODO 6 Displaying Notification
    private void showNotification(Context context, String title, String message, int notifId) {
        NotificationManager notificationManagerCompat = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(title)
                .setSmallIcon(R.drawable.ic_replay_30_black_24dp)
                .setContentText(message)
                .setColor(ContextCompat.getColor(context, android.R.color.black))
                .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                .setSound(alarmSound);

        if (notificationManagerCompat != null) {
            notificationManagerCompat.notify(notifId, builder.build());
        }
    }
}