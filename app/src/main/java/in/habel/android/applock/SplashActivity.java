package in.habel.android.applock;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.analytics.GoogleAnalytics;

import in.habel.android.applock.services.AlarmReceiver;
import in.habel.android.applock.services.AppCheckServices;

 
public class SplashActivity extends AppCompatActivity {


  private static int SPLASH_TIME_OUT = 1000;
  SharedPreferences sharedPreferences;
  Context context;

  @Override
  protected void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    context = getApplicationContext();
    if(checkdrawPermission())
    /****************************** too much important don't miss it *****************************/
      startService(new Intent(SplashActivity.this, AppCheckServices.class));

    try {
      Intent alarmIntent = new Intent(context, AlarmReceiver.class);
      AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
      PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 999, alarmIntent, 0);
      int interval = (86400 * 1000) / 4;
      if(manager != null) {
        manager.cancel(pendingIntent);
      }
      manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
    } catch(Exception e) {
      e.printStackTrace();
    }

    /***************************************************************************************/

    LinearLayout linearLayout = new LinearLayout(this);
    linearLayout.setBackgroundColor(getResources().getColor(R.color.primary_dark));
    LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
    linearLayout.setGravity(Gravity.CENTER);
    linearLayout.setOrientation(LinearLayout.VERTICAL);
    linearLayout.setLayoutParams(layoutParams);

    TextView textView = new TextView(this);
    textView.setText(getResources().getString(R.string.app_name));
    textView.setTextColor(getResources().getColor(R.color.white));
    textView.setTextSize(32);
    textView.setGravity(Gravity.CENTER);
    linearLayout.addView(textView);

    ImageView imageView = new ImageView(this);
    imageView.setImageDrawable(getResources().getDrawable(R.drawable.bg_splash));
    linearLayout.addView(imageView);

    setContentView(linearLayout);
    sharedPreferences = getSharedPreferences(AppLockConstants.MyPREFERENCES, MODE_PRIVATE);
    final boolean isPasswordSet = sharedPreferences.getBoolean(AppLockConstants.IS_PASSWORD_SET, false);
    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        if(isPasswordSet) {
          Intent i = new Intent(SplashActivity.this, PasswordActivity.class);
          startActivity(i);
        }
        else {
          Intent i = new Intent(SplashActivity.this, PasswordSetActivity.class);
          startActivity(i);
        }
        finish();
      }
    }, SPLASH_TIME_OUT);


  }

  @Override
  protected void onStart() {
    GoogleAnalytics.getInstance(context).reportActivityStart(this);
    super.onStart();
  }


  public boolean checkdrawPermission() {
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      if(!Settings.canDrawOverlays(this)) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 100);
        return false;
      }
    }
    return true;
  }

  @Override
  protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    if(requestCode == 100) {
      if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        if(!Settings.canDrawOverlays(this)) {
          System.exit(0);
        }
        else
          startService(new Intent(SplashActivity.this, AppCheckServices.class));
      }
    }
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }

  @Override
  protected void onStop() {
    GoogleAnalytics.getInstance(context).reportActivityStop(this);
    super.onStop();
  }
}
