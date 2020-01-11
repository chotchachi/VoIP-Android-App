package com.example.voip_app.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.voip_app.R;
import com.example.voip_app.service.CallService;
import com.example.voip_app.service.ConnectSever;
import com.example.voip_app.view.ui.dashboard.DashboardFragment;
import com.example.voip_app.view.ui.home.HomeFragment;
import com.example.voip_app.view.ui.notifications.NotificationsFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class HomeActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.nav_view)
    BottomNavigationView bottomNavigationView;

    final Fragment fragment1 = new HomeFragment();
    final Fragment fragment2 = new DashboardFragment();
    final Fragment fragment3 = new NotificationsFragment();
    final FragmentManager fm = getSupportFragmentManager();
    Fragment active = fragment1;

    private static final int LISTENER_PORT = 50003;
    private boolean IN_CALL = false;
    private boolean LISTEN = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        initFragment();


        // Connect Server
        new ConnectSever().start();

        startService(new Intent(this, CallService.class));
    }

    private void initFragment() {
        fm.beginTransaction().add(R.id.main_container, fragment3, "3").hide(fragment3).commit();
        fm.beginTransaction().add(R.id.main_container, fragment2, "2").hide(fragment2).commit();
        fm.beginTransaction().add(R.id.main_container,fragment1, "1").commit();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_home:
                fm.beginTransaction().hide(active).show(fragment1).commit();
                active = fragment1;
                return true;

            case R.id.navigation_dashboard:
                fm.beginTransaction().hide(active).show(fragment2).commit();
                active = fragment2;
                return true;

            case R.id.navigation_notifications:
                fm.beginTransaction().hide(active).show(fragment3).commit();
                active = fragment3;
                return true;
        }
        return false;
    }
}
