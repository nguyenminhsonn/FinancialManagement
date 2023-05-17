package com.fptu.android.financialmanagement.Category;

import androidx.annotation.NonNull;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Toast;

import com.fptu.android.financialmanagement.Login.Account;
import com.fptu.android.financialmanagement.MainActivity;
import com.fptu.android.financialmanagement.R;
import com.google.android.gms.tasks.OnCompleteListener;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddActivity extends AppCompatActivity {
    EditText category_name, category_level;
    Spinner categoryParentId_input;
    Button addButton;

    String userID = Account.USER.getUid() ;
    String category_name_String ;
    String category_ParentId_String;
    String category_level_String;
    private DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private ProgressDialog loader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        bindingView();
        bindingAction();
    }

    private void bindingView() {
        category_name = findViewById(R.id.categoryName_input);
        categoryParentId_input = findViewById(R.id.parentId_input);
        category_level = findViewById(R.id.level_input);
        addButton = findViewById(R.id.Createbutton);

    }

    mydbhelper FinancialManagement = new mydbhelper(AddActivity.this);
    Cursor cursor ;
    private void bindingAction(){

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                category_name_String =category_name.getText().toString();
                category_ParentId_String =categoryParentId_input.getSelectedItem().toString();
                category_level_String =category_level.getText().toString();

                // notifi
                SendNotification();
                SendCustomNotification();

                cursor = FinancialManagement.readCateLatest();

                String id = "";
                if (cursor.moveToNext()) {
                    id = String.valueOf(cursor.getInt(0)+1);
                }else {
                    id = "1";

                }


                Log.i("aaaaaaa", "onClick: ");
                writeNewCategory(userID,id, category_name_String,category_ParentId_String,category_level_String);
            }
        });
    }
    private void SendNotification() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher_round);

        Notification notification = new NotificationCompat.Builder(this, MainActivity.CHANNEL_ID)
                .setContentTitle("Add Success" + category_name_String)
                .setContentText("Chuc Mung Ban Da Add "+ category_name_String+" Thanh Cong")
                .setSmallIcon(R.drawable.download)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager!=null) {
            notificationManager.notify(getNotificationId(), notification);
        }
    }

    private void SendCustomNotification() {
        RemoteViews notificationLayout = new  RemoteViews(getPackageName(), R.layout.layout_custom_notification);
        notificationLayout.setTextViewText(R.id.tv_title_custom_notification,"Add Category " + category_name_String + " Succeed");
        notificationLayout.setTextViewText(R.id.tv_message_custom_notification,"Chuc Mung Ban Da Add "+ category_name_String+" Thanh Cong");
        notificationLayout.setTextViewText(R.id.tv_time_custom_notification,"Message custom notification");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String strDate = sdf.format(new Date());
        notificationLayout.setTextViewText(R.id.tv_time_custom_notification, strDate);

        Notification notification = new NotificationCompat.Builder(this,MainActivity.CHANNEL_ID_2)
                .setSmallIcon(R.drawable.download)
                .setCustomContentView(notificationLayout)
                .build();
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        notificationManager.notify(getNotificationId(), notification);
    }

    private int getNotificationId() {
        return (int) new Date().getTime();
    }

    public void writeNewCategory(String userID, String CategoryID, String CategoryName,String ParentID,String Level) {
        Log.i("aaaaaaa", "writeNewCategory: ");
        Category user = new Category(userID, CategoryID,CategoryName,ParentID,Level);

        mDatabase.child("Categories").child(userID).child(CategoryID).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(AddActivity.this, "add success", Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(AddActivity.this, "fail", Toast.LENGTH_SHORT).show();
                }
                loader.dismiss();
            }
        });
    }

}