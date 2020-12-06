package com.inti.student.travelmalaysia.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.inti.student.travelmalaysia.R;
import com.inti.student.travelmalaysia.database.DatabaseHelper;
import com.inti.student.travelmalaysia.session.SessionManager;

public class LoginActivity extends AppCompatActivity {

    EditText txtUsername, txtPassword;
    Button btnLogin, btnRegister;
    com.inti.student.travelmalaysia.adapter.AlertDialogManager alert = new com.inti.student.travelmalaysia.adapter.AlertDialogManager();
    SessionManager session;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (Build.VERSION.SDK_INT >= 21) {
            setWindowFlag(this, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
            getWindow().setStatusBarColor(Color.TRANSPARENT);
        }

        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        session = new SessionManager(getApplicationContext());

        txtUsername = findViewById(R.id.email);
        txtPassword = findViewById(R.id.password);

        btnLogin = findViewById(R.id.enter);
        btnRegister = findViewById(R.id.to_register);

        btnLogin.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Get username, password from EditText
                String username = txtUsername.getText().toString();
                String password = txtPassword.getText().toString();

                // Check if username, password is filled
                try {
                    // Check if username, password is filled
                    if (username.trim().length() > 0 && password.trim().length() > 0) {
                        dbHelper.open();

                        if (dbHelper.Login(username, password)) {
                            session.createLoginSession(username);

                            finish();
                            Intent i = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(i);

                        } else {
                            alert.showAlertDialog(LoginActivity.this, "Login failed..", "Incorrect e-mail or password!", false);

                        }
                    } else {
                        alert.showAlertDialog(LoginActivity.this, "Login failed..", "Form cannot be empty!", false);
                    }
                } catch (Exception e) {
                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(LoginActivity.this, com.inti.student.travelmalaysia.activity.RegisterActivity.class);
                startActivity(i);
            }
        });
    }

    public static void setWindowFlag(Activity activity, final int bits, boolean on) {

        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    @Override
    public void onBackPressed() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(LoginActivity.this);
        builder.setMessage("Do you want to exit the application?");
        builder.setCancelable(true);
        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.setPositiveButton(getString(R.string.exit), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

}
