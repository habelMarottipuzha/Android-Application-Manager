package in.habel.android.applock;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
 
public class LoadingActivity extends AppCompatActivity {
  private static int TIME_OUT = 1000;
  Context context;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_loading);
    context = getApplicationContext();

    new Handler().postDelayed(new Runnable() {
      @Override
      public void run() {
        Intent i = new Intent(LoadingActivity.this, MainActivity.class);
        startActivity(i);
        finish();
      }
    }, TIME_OUT);

  }

}
