package in.habel.android.applock.Utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
 
public class MyUtils {
  /**
   * Checks if user has internet connectivity
   *
   * @param context
   * @return
   */
  public static boolean isInternetConnected(Context context) {
    final ConnectivityManager conMgr = (ConnectivityManager) context
            .getSystemService(Context.CONNECTIVITY_SERVICE);
    final NetworkInfo activeNetwork = conMgr.getActiveNetworkInfo();
    if(activeNetwork != null && activeNetwork.isConnected())
      return true;
    else
      return false;
  }
}
