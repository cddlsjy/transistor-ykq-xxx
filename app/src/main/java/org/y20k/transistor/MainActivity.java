/**
 * MainActivity.java
 * Implements the app's main activity
 * The main activity sets up the main view
 *
 * This file is part of
 * TRANSISTOR - Radio App for Android
 *
 * Copyright (c) 2015-20 - Y20K.org
 * Licensed under the MIT-License
 * http://opensource.org/licenses/MIT
 */

package org.y20k.transistor;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.PreferenceManager;

import org.y20k.transistor.collection.CollectionViewModel;
import org.y20k.transistor.constant.JsonConstants;
import org.y20k.transistor.core.Station;
import org.y20k.transistor.helpers.DialogError;
import org.y20k.transistor.helpers.ImageHelper;
import org.y20k.transistor.helpers.JsonHelper;
import org.y20k.transistor.helpers.LogHelper;
import org.y20k.transistor.helpers.PermissionHelper;
import org.y20k.transistor.helpers.ShortcutHelper;
import org.y20k.transistor.helpers.StationListHelper;
import org.y20k.transistor.helpers.StorageHelper;
import org.y20k.transistor.helpers.TransistorKeys;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * MainActivity class
 */
public final class MainActivity extends AppCompatActivity implements TransistorKeys {

    /* Define log tag */
    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    /* Main class variables */
    private CollectionViewModel mCollectionViewModel;
    private ArrayList<Station> mStationList;
    private Station mTempStation;
    private boolean mAutoLoaded = false; // 类成员变量，防止重复添加


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // initialize list of stations
        mStationList = new ArrayList<Station>();

        // initialize temp station (used by image change requests)
        mTempStation = null;

        // check if system/app has external storage access
        checkExternalStorageState();

        // initialize view model containing live data
        mCollectionViewModel = ViewModelProviders.of(this).get(CollectionViewModel.class);

        // set layout
        setContentView(R.layout.activity_main);

        // observe changes in LiveData
        mCollectionViewModel.getStationList().observe(this, createStationListObserver());

        // put collection list in main container
        MainActivityFragment listFragment = new MainActivityFragment();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_container, listFragment, MAIN_ACTIVITY_FRAGMENT_TAG)
                .commit();

        // 启动时自动加载JSON电台并播放
        autoLoadAndPlayJsonStation();
    }

    /**
     * 自动加载JSON电台：优先本地，后内置默认
     */
    private void autoLoadAndPlayJsonStation() {
        if (mAutoLoaded) return;
        mAutoLoaded = true;

        List<Station> stationList;
        // 1. 尝试读取本地 /sdcard/station/station.json
        stationList = JsonHelper.parseLocalJson(JsonConstants.LOCAL_JSON_PATH);
        if (stationList == null || stationList.isEmpty()) {
            LogHelper.d(LOG_TAG, "本地 JSON 文件不存在或解析失败，使用内置电台");
            stationList = JsonHelper.parseJsonString(JsonConstants.DEFAULT_JSON_CONTENT);
        }

        if (stationList != null && !stationList.isEmpty()) {
            for (Station station : stationList) {
                // 检查是否已存在（根据 URL 判断）
                if (StationListHelper.findStationId(mStationList, station.getStreamUri()) == -1) {
                    // 获取 Collection 文件夹
                    File folder = StorageHelper.getCollectionDirectory(this);
                    // 写入 m3u 文件（Station 内部会生成文件名）
                    station.writePlaylistFile(folder);
                    // 添加到列表并更新 LiveData
                    ArrayList<Station> newStationList = StationListHelper.copyStationList(mStationList);
                    newStationList.add(station);
                    mCollectionViewModel.getStationList().setValue(newStationList);
                    LogHelper.d(LOG_TAG, "添加电台：" + station.getStationName());
                }
            }
            // 自动播放第一个电台
            Station first = stationList.get(0);
            int position = StationListHelper.findStationId(mStationList, first.getStreamUri());
            if (position != -1) {
                MainActivityFragment fragment = (MainActivityFragment) getSupportFragmentManager()
                        .findFragmentByTag(MAIN_ACTIVITY_FRAGMENT_TAG);
                if (fragment != null) {
                    fragment.startPlayback(mStationList.get(position));
                    LogHelper.d(LOG_TAG, "自动播放：" + first.getStationName());
                }
            }
        } else {
            LogHelper.e(LOG_TAG, "本地和内建 JSON 均解析失败，无电台可加载");
        }
    }


    @Override
    protected void onResume() {
        super.onResume();

        // check state of External Storage
        checkExternalStorageState();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem autoPlayItem = menu.findItem(R.id.menu_auto_play);
        // 读取保存的自动播放状态并设置菜单项的选中状态
        boolean autoPlayEnabled = PreferenceManager.getDefaultSharedPreferences(this)
                .getBoolean(PREF_AUTO_PLAY, false);
        autoPlayItem.setChecked(autoPlayEnabled);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_auto_play) {
            // 切换自动播放状态
            boolean newState = !item.isChecked();
            item.setChecked(newState);
            PreferenceManager.getDefaultSharedPreferences(this)
                    .edit()
                    .putBoolean(PREF_AUTO_PLAY, newState)
                    .apply();
            Toast.makeText(this, newState ? R.string.auto_play_on : R.string.auto_play_off, Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        // try to minimize player - super.onBackPressed otherwise
        Fragment listFragment = getSupportFragmentManager().findFragmentByTag(MAIN_ACTIVITY_FRAGMENT_TAG);
        if (listFragment != null && !((MainActivityFragment)listFragment).minimizePlayer()) {
            super.onBackPressed();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_REQUEST_IMAGE_PICKER_READ_EXTERNAL_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    selectFromImagePicker();
                } else {
                    // permission denied
                    Toast.makeText(this, getString(R.string.toastalert_permission_denied) + " READ_EXTERNAL_STORAGE", Toast.LENGTH_LONG).show();
                }
                break;
            }
            case PERMISSION_REQUEST_STATION_FETCHER_READ_EXTERNAL_STORAGE: {
                // let list fragment handle the request
                Fragment listFragment = getSupportFragmentManager().findFragmentByTag(MAIN_ACTIVITY_FRAGMENT_TAG);
                if (listFragment != null) {
                    listFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                }
                break;
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // make sure that MainActivityFragment's onActivityResult() gets called
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_CANCELED) {
            return;
        }

        // check if a station image change request was received
        if (requestCode == REQUEST_LOAD_IMAGE) {
            handleStationImageChange(data);
        }

    }


    /* Puts new station in list and updates live data  */
    public int handleStationAdd(Bundle stationDownloadBundle) {

        // get collection folder
        File folder = StorageHelper.getCollectionDirectory(this);

        // get station, station image and station URL from download bundle
        Station station = null;
        Bitmap stationBitmap = null;
        if (stationDownloadBundle.containsKey(KEY_DOWNLOAD_STATION)) {
            station = stationDownloadBundle.getParcelable(KEY_DOWNLOAD_STATION);
        }
        if (stationDownloadBundle.containsKey(KEY_DOWNLOAD_STATION_IMAGE)) {
            stationBitmap = stationDownloadBundle.getParcelable(KEY_DOWNLOAD_STATION_IMAGE);
        }

        // check is station is valid and unique
        if (station != null && StationListHelper.findStationId(mStationList, station.getStreamUri()) == -1) {
            // write playlist file and station image - if available
            station.writePlaylistFile(folder);
            if (stationBitmap != null) {
                station.writeImageFile(stationBitmap);
            }
            // create copy of main list of stations
            ArrayList<Station> newStationList = StationListHelper.copyStationList(mStationList);
            // add station to new list of stations
            newStationList.add(station);
            // update live data list of stations
            mCollectionViewModel.getStationList().setValue(newStationList);
            // return new index
            return StationListHelper.findStationId(newStationList, station.getStreamUri());
        } else {
            // notify user and log failure to add
            String errorTitle = getResources().getString(R.string.dialog_error_title_fetch_write);
            String errorMessage = getResources().getString(R.string.dialog_error_message_fetch_write);
            String errorDetails = getResources().getString(R.string.dialog_error_details_write);
            DialogError.show(this, errorTitle, errorMessage, errorDetails);
            LogHelper.e(LOG_TAG, "Unable to add station to collection: Duplicate name and/or stream URL.");

            return -1;
        }

    }


    /* Puts renamed station in list and updates live data */
    public int handleStationRename(Station station, String newStationName) {

        // name of station is new
        if (station != null && newStationName.length() > 0 && !station.getStationName().equals(newStationName)) {
            // get collection folder
            File folder = StorageHelper.getCollectionDirectory(this);

            // create copies of station and main list of stations
            ArrayList<Station> newStationList = StationListHelper.copyStationList(mStationList);
            Station newStation = new Station(station);

            // get position of station in list
            int stationID = StationListHelper.findStationId(newStationList, station.getStreamUri());

            // set new name
            newStation.setStationName(newStationName);

            // delete old playlist file
            File stationPlaylistFile = station.getStationPlaylistFile();
            stationPlaylistFile.delete();
            // set new playlist file - and write file
            newStation.setStationPlaylistFile(folder);
            newStation.writePlaylistFile(folder);

            // rename existing image file
            File stationImageFile = station.getStationImageFile();
            newStation.setStationImageFile(folder);
            stationImageFile.renameTo(newStation.getStationImageFile());

            // update list
            newStationList.set(stationID, newStation);

            // update liva data station from PlayerService - used in MainActivityFragment
            mCollectionViewModel.getPlayerServiceStation().setValue(newStation);

            // update live data list of stations - used in CollectionAdapter
            mCollectionViewModel.getStationList().setValue(newStationList);

            // return id of changed station
            return StationListHelper.findStationId(newStationList, newStation.getStreamUri());

        } else {
            // name of station is null or not new - notify user
            Toast.makeText(this, getString(R.string.toastalert_rename_unsuccessful), Toast.LENGTH_LONG).show();
            return -1;
        }

    }


    /* Removes given station from list and updates live data */
    public int handleStationDelete(Station station) {

        // keep track of delete success
        boolean success = false;

        // get position of station
        int stationId = StationListHelper.findStationId(mStationList, station.getStreamUri());

        // delete png image file
        File stationImageFile = station.getStationImageFile();
        if (stationImageFile != null && stationImageFile.exists() && stationImageFile.delete()) {
            success = true;
        }

        // delete m3u playlist file
        File stationPlaylistFile = station.getStationPlaylistFile();
        if (stationPlaylistFile != null && stationPlaylistFile.exists() && stationPlaylistFile.delete()) {
            success = true;
        }

        // remove station and notify user
        if (success) {

            // create copy of main list of stations
            ArrayList<Station> newStationList = StationListHelper.copyStationList(mStationList);
            // remove station from new station list
            newStationList.remove(stationId);
            // determine ID of next station
            if (newStationList.size() >= stationId && stationId > 0) {
                stationId--;
            } else {
                stationId = 0;
            }

            // show next station in list & show next station in player & minimize player
            Fragment mainActivityFragment = getSupportFragmentManager().findFragmentByTag(MAIN_ACTIVITY_FRAGMENT_TAG);
            if (mainActivityFragment!= null && mainActivityFragment.isAdded() && newStationList.size() > 0) {
                ((MainActivityFragment)mainActivityFragment).updatePlayerAfterDelete(newStationList.get(stationId));
//            } else if (newStationList.size() == 0) {
//                ((MainActivityFragment)mainActivityFragment).updatePlayerAfterDelete(null);
            }

            // update live data list of stations - used in CollectionAdapter
            mCollectionViewModel.getStationList().setValue(newStationList);

            // notify user
            Toast.makeText(this, getString(R.string.toastalert_delete_successful), Toast.LENGTH_LONG).show();
        }

        // delete station shortcut
        ShortcutHelper.removeShortcut(this, station);

        // return ID of station next to the deleted station station
        return stationId;
    }


    /* Check permissions and start image picker */
    public void pickImage(Station station) {
        mTempStation = station;
        View rootView = findViewById(android.R.id.content);
        if (PermissionHelper.requestReadExternalStorage(this, rootView, PERMISSION_REQUEST_IMAGE_PICKER_READ_EXTERNAL_STORAGE)) {
            selectFromImagePicker();
        }
    }


    /* Start image picker */
    private void selectFromImagePicker() {
        // get system picker for images
        Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        List<ResolveInfo> imagePickerApps = getPackageManager().queryIntentActivities(pickImageIntent, 0);
        if (!imagePickerApps.isEmpty()) {
            startActivityForResult(pickImageIntent, REQUEST_LOAD_IMAGE);
        } else {
            Toast.makeText(this, getString(R.string.toastalert_no_image_picker), Toast.LENGTH_LONG).show();
        }

    }


    /* Saves and sets new station image and updates station list and live data */
    private boolean handleStationImageChange(Intent data) {

        // get collection folder
        File folder = StorageHelper.getCollectionDirectory(this);

        // retrieve selected image Uri from image picker
        Bitmap newImage = null;
        if (null != data) {
            ImageHelper imageHelper = new ImageHelper(data.getData(), this);
            newImage = imageHelper.getInputImage();
        }

        if (newImage != null && mTempStation != null) {
            // write image to storage
            try (FileOutputStream out = new FileOutputStream(mTempStation.getStationImageFile())) {
                newImage.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                LogHelper.e(LOG_TAG, "Unable to save: " + newImage.toString());
                return false;
            }

            // create copy of main list of stations
            ArrayList<Station> newStationList = StationListHelper.copyStationList(mStationList);

            // create a copy of mTempStation
            Station newStation = new Station(mTempStation);

            // set new station image file object
            newStation.setStationImageFile(folder);

            // update list
            int stationID = StationListHelper.findStationId(mStationList, mTempStation.getStreamUri());
            newStationList.set(stationID, newStation);

            // update liva data station from PlayerService
            mCollectionViewModel.getPlayerServiceStation().setValue(newStation);

            // update live data
            mCollectionViewModel.getStationList().setValue(newStationList);

            // reset mTempStation
            mTempStation = null;

            return true;

        } else {
            LogHelper.e(LOG_TAG, "Unable to get image from media picker.");
            return false;
        }
    }


    /* Creates an observer for collection of stations stored as LiveData */
    private Observer<ArrayList<Station>> createStationListObserver() {
        return new Observer<ArrayList<Station>>() {
            @Override
            public void onChanged(@Nullable ArrayList<Station> newStationList) {
                // update station list
                mStationList = newStationList;
            }
        };
    }


    /* Checks state of External Storage */
    private void checkExternalStorageState() {

        String state = Environment.getExternalStorageState();
        if (!state.equals(Environment.MEDIA_MOUNTED) || StorageHelper.getCollectionDirectory(this) == null) {
            Toast.makeText(this, getString(R.string.toastalert_no_external_storage), Toast.LENGTH_LONG).show();
            LogHelper.e(LOG_TAG, "Error: Unable to mount External Storage. Current state: " + state);

            // move MainActivity to back
            moveTaskToBack(true);

            // shutting down app
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

}