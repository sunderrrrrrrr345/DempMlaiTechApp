package com.dempmlaitechapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.theartofdev.edmodo.cropper.CropImage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends AppCompatActivity implements  EasyPermissions.PermissionCallbacks {
    private String TAG = HomeActivity.class.getSimpleName();
    private String PERM_RATIONALE = "This app needs access to your camera.";
    private static final int RC_SETTINGS = 130;
    private static final int RC_PERM_CAMERA_STORAGE = 131;
    private static final int CAMERA_TAKE_REQUEST = 201;
    private String[] wantedPerms = {Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private Activity activity=HomeActivity.this;
    private File file;
    private ImageView imageButton;
    private Button btn_upload;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        imageButton=findViewById(R.id.image_view);
        btn_upload=findViewById(R.id.btn_upload);
        findViewById(R.id.btn_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean result = Utility.checkPermission(HomeActivity.this);
                    if (result)
                        cameraIntent();
                
            }
        });
        if (!EasyPermissions.hasPermissions(activity, wantedPerms)) {
            EasyPermissions.requestPermissions(activity, PERM_RATIONALE,
                    RC_PERM_CAMERA_STORAGE, wantedPerms);
        }

        btn_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                Camera mcamera = Camera.open(Camera.CameraInfo.CAMERA_FACING_BACK);
                android.hardware.Camera.Parameters  parameters=mcamera.getParameters();
                android.hardware.Camera.Size size = parameters.getPictureSize();
                int height = size.height;
                int width = size.width;
                int mp=(width*height)/1000000;
                Log.i("Height"+"Height",""+height);
                Log.i("Height"+"Height",""+width);
                Log.i("Height"+"Height",""+mp);
                if(mp<30){
                    Toast.makeText(activity, "Camera is less than 30MP.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(activity, "File has been saved in Gallery.Please check it.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }



    private void cameraIntent() {
        take();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List perms) {
        Log.d(TAG, "onPermissionsGranted:" + requestCode + ":" + perms.size());
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List perms) {
        Log.d(TAG, "onPermissionsDenied:" + requestCode + ":" + perms.size());
        if (EasyPermissions.somePermissionPermanentlyDenied(activity, perms)) {
            new AppSettingsDialog.Builder(activity)
                    .setTitle("Permissions Required")
                    .setPositiveButton("Settings")
                    .setNegativeButton("Cancel")
                    .setRequestCode(RC_SETTINGS)
                    .build()
                    .show();
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    public void take() {
        if (checkCameraExists()) {
            if (EasyPermissions.hasPermissions(activity, wantedPerms)) {
                launchCamera();
            } else {
                EasyPermissions.requestPermissions(activity, PERM_RATIONALE,
                        RC_PERM_CAMERA_STORAGE, wantedPerms);
            }
        } else {
            Toast.makeText(activity, "Camera not available.", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean checkCameraExists() {
        return activity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA);
    }
    private void launchCamera() {
        Intent intent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);

        file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS),
                String.valueOf(System.currentTimeMillis()) + ".jpg");
        Uri uri = FileProvider.getUriForFile(activity,
                activity.getApplicationContext().getPackageName() + ".provider", file);

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(intent, CAMERA_TAKE_REQUEST);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CAMERA_TAKE_REQUEST:
                CropImage.activity(android.net.Uri.parse(file.toURI().toString()))
                        .setInitialCropWindowPaddingRatio(0)
                        .start(activity);
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    try {
                        final Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), result.getUri());
                        imageButton.setImageBitmap(bitmap);
                        MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "yourTitle" , "yourDescription");
                        btn_upload.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.d(TAG, "onActivityResult: " + error.getMessage());
                }
                break;
        }
    }



}
