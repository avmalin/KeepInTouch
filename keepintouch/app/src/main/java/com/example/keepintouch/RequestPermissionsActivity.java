package com.example.keepintouch;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class RequestPermissionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_permissions_acticity);
        Button button = findViewById(R.id.bt_request);
        button.setOnClickListener(view -> requestRunTimePermission());
    }

    private void requestRunTimePermission()
    {
        if(!hasPermissions(MainActivity.PERMISSIONS))
        {
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, MainActivity.PERMISSIONS[0])||
                    ActivityCompat.shouldShowRequestPermissionRationale(this,MainActivity.PERMISSIONS[1]))
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("This message request permission to contacts, call log and external storage to work as excepted.")
                        .setTitle("Permissions Required")
                        .setCancelable(false)
                        .setPositiveButton("Ok",(dialog, which) ->{
                            ActivityCompat.requestPermissions(RequestPermissionsActivity.this, MainActivity.PERMISSIONS,1);
                            dialog.dismiss();
                        })
                        .setNegativeButton("Cancel",(dialog, which) -> dialog.dismiss());
                builder.show();
            }

            else ActivityCompat.requestPermissions(this,MainActivity.PERMISSIONS,1);
        }
    }
    public boolean hasPermissions (String[] permissions)
    {
        if (permissions!= null)
        {
            for (String permission : permissions)
            {
                if (ActivityCompat.checkSelfPermission(this,permission) != PackageManager.PERMISSION_GRANTED)
                {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==1){
            if(grantResults.length>2 && grantResults[0]==PackageManager.PERMISSION_GRANTED
                    &&grantResults[1]==PackageManager.PERMISSION_GRANTED)
                Toast.makeText(this,"Permissions Has Granted Successfully",Toast.LENGTH_LONG).show();

            else if (!ActivityCompat.shouldShowRequestPermissionRationale(this,MainActivity.PERMISSIONS[0])
            && !ActivityCompat.shouldShowRequestPermissionRationale(this,MainActivity.PERMISSIONS[01])) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("The application in unavailable because this Application required permissions that you have denied." +
                                "Please allow Read-Contact, Read-Call-log and Read-External-Storage to continue.")
                        .setTitle("Permission Required")
                        .setCancelable(false)
                        .setNegativeButton("Cancel", ((dialog, which) -> dialog.dismiss()))
                        .setPositiveButton("Setting", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                                Uri uri = Uri.fromParts("package", getPackageName(), null);
                                intent.setData(uri);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        });
                builder.show();

            }
            else ActivityCompat.requestPermissions(this,MainActivity.PERMISSIONS,1);
        }
    }
}