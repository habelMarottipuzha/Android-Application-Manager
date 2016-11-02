package in.habel.android.applock.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import in.habel.android.applock.AppLockConstants;
import in.habel.android.applock.Data.AppInfo;
import in.habel.android.applock.R;
import in.habel.android.applock.Utils.AppLockLogEvents;
import in.habel.android.applock.Utils.SharedPreference;
 
public class ApplicationListAdapter extends RecyclerView.Adapter<ApplicationListAdapter.ViewHolder> {
  private List<AppInfo> installedApps = new ArrayList();
  private SharedPreference sharedPreference;
  private String requiredAppsType;
  private Context context;

  // Provide a suitable constructor (depends on the kind of dataset)
  public ApplicationListAdapter(List<AppInfo> appInfoList, Context context, String requiredAppsType) {
    installedApps = appInfoList;

    this.context = context;
    this.requiredAppsType = requiredAppsType;
    sharedPreference = new SharedPreference();
    List<AppInfo> lockedFilteredAppList = new ArrayList<>();
    List<AppInfo> unlockedFilteredAppList = new ArrayList<>();
    boolean flag = true;
    if(requiredAppsType.matches(AppLockConstants.LOCKED) || requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
      for(int i = 0; i < installedApps.size(); i++) {
        flag = true;
        if(sharedPreference.getLocked(context) != null) {
          int size = sharedPreference.getLocked(context).size();
          for(int j = 0; j < size; j++) {
            if(installedApps.get(i).getPackageName().matches(sharedPreference.getLocked(context).get(j))) {
              lockedFilteredAppList.add(installedApps.get(i));
              flag = false;
            }
          }
        }
        if(flag) {
          unlockedFilteredAppList.add(installedApps.get(i));
        }
      }
      if(requiredAppsType.matches(AppLockConstants.LOCKED)) {
        installedApps.clear();
        installedApps.addAll(lockedFilteredAppList);
      }
      else if(requiredAppsType.matches(AppLockConstants.UNLOCKED)) {
        installedApps.clear();
        installedApps.addAll(unlockedFilteredAppList);
      }


    }
    Collections.sort(installedApps, new Comparator<AppInfo>() {
      @Override
      public int compare(AppInfo appInfo, AppInfo t1) {
        return appInfo.getName().toLowerCase().compareTo(t1.getName().toLowerCase());
      }
    });
  }

  public void add(int position, String item) {
//        mDataset.add(position, item);
//        notifyItemInserted(position);
  }

  public void remove(AppInfo item) {
//        int position = installedApps.indexOf(item);
//        installedApps.remove(position);
//        notifyItemRemoved(position);
  }

  // Create new views (invoked by the layout manager)
  @Override
  public ApplicationListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                              int viewType) {
    // create a new view
    View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
    // set the view's size, margins, paddings and layout parameters
    return new ViewHolder(v);
  }

  // Replace the contents of a view (invoked by the layout manager)
  @Override
  public void onBindViewHolder(final ViewHolder holder, final int position) {
    // - get element from your dataset at this position
    // - replace the contents of the view with that element
    final AppInfo appInfo = installedApps.get(position);
    holder.applicationName.setText(appInfo.getName());
    holder.icon.setBackgroundDrawable(appInfo.getIcon());

    holder.switchView.setOnCheckedChangeListener(null);
    holder.cardView.setOnClickListener(null);
    if(checkLockedItem(appInfo.getPackageName())) {
      holder.switchView.setChecked(true);
    }
    else {
      holder.switchView.setChecked(false);
    }

    holder.switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
      public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if(isChecked) {
          AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Lock Clicked", "lock_clicked", appInfo.getPackageName());
          sharedPreference.addLocked(context, appInfo.getPackageName());
        }
        else {
          AppLockLogEvents.logEvents(AppLockConstants.MAIN_SCREEN, "Unlock Clicked", "unlock_clicked", appInfo.getPackageName());
          sharedPreference.removeLocked(context, appInfo.getPackageName());
        }
      }
    });

    holder.cardView.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        holder.switchView.performClick();
      }
    });
  }

  // Return the size of your dataset (invoked by the layout manager)
  @Override
  public int getItemCount() {
    return installedApps.size();
  }

  /*Checks whether a particular app exists in SharedPreferences*/
  private boolean checkLockedItem(String checkApp) {
    boolean check = false;
    List<String> locked = sharedPreference.getLocked(context);
    if(locked != null) {
      for(String lock : locked) {
        if(lock.equals(checkApp)) {
          check = true;
          break;
        }
      }
    }
    return check;
  }

  // Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
  class ViewHolder extends RecyclerView.ViewHolder {
    // each data item is just a string in this case

    public ImageView icon;
    TextView applicationName;
    CardView cardView;
    Switch switchView;

    ViewHolder(View v) {
      super(v);
      applicationName = (TextView) v.findViewById(R.id.applicationName);
      cardView = (CardView) v.findViewById(R.id.card_view);
      icon = (ImageView) v.findViewById(R.id.icon);
      switchView = (Switch) v.findViewById(R.id.switchView);
    }
  }

}