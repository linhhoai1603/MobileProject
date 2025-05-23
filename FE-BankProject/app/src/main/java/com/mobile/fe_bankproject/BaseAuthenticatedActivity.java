package com.mobile.fe_bankproject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public abstract class BaseAuthenticatedActivity extends AppCompatActivity {

    private Handler logoutHandler;
    private Runnable logoutRunnable;
    private static final long LOGOUT_DELAY = 180000; // 3 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        logoutHandler = new Handler(Looper.getMainLooper());
        logoutRunnable = new Runnable() {
            @Override
            public void run() {
                checkSession();
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Start timer when activity is resumed or comes to foreground
        startLogoutTimer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Cancel timer when activity goes to background
        cancelLogoutTimer();
    }
    
    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        // Reset timer on user interaction
        resetLogoutTimer();
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Ensure timer is cancelled when activity is destroyed
        cancelLogoutTimer();
    }

    private void startLogoutTimer() {
        logoutHandler.removeCallbacks(logoutRunnable);
        logoutHandler.postDelayed(logoutRunnable, LOGOUT_DELAY);
        Log.d("BaseActivity", "Logout timer started for " + LOGOUT_DELAY + " ms");
    }

    private void resetLogoutTimer() {
        Log.d("BaseActivity", "Logout timer reset.");
        startLogoutTimer();
    }

    private void cancelLogoutTimer() {
        Log.d("BaseActivity", "Logout timer cancelled.");
        logoutHandler.removeCallbacks(logoutRunnable);
    }

    private void checkSession() {
        Log.d("BaseActivity", "Checking session...");
        // Check if the user info still exists in SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        String accountName = sharedPreferences.getString("accountName", null);

        if (accountName == null) {
            // Session already cleared (e.g., by explicit logout from another screen)
            // Do nothing, as user is already unauthenticated
             Log.d("BaseActivity", "Session already expired.");
        } else {
            // Session expired due to inactivity, perform logout actions
            Log.d("BaseActivity", "Session expired due to inactivity. Performing logout...");
            performLogout();
        }
    }

    private void performLogout() {
        // Clear saved user info (AccountResponse) from SharedPreferences
        SharedPreferences sharedPreferences = getSharedPreferences("UserInfo", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear(); // Clear all data related to the user
        editor.apply();

        Toast.makeText(this, "Phiên sử dụng đã hết", Toast.LENGTH_LONG).show();

        // Show session expired layout
        setContentView(R.layout.layout_session_expired);

        Button btnLoginAgain = findViewById(R.id.btnLoginAgain);
        if (btnLoginAgain != null) {
             btnLoginAgain.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Navigate back to LoginActivity
                    Intent intent = new Intent(BaseAuthenticatedActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish(); // Close the current activity
                }
            });
        }
    }
} 