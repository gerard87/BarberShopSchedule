package com.udl.bss.barbershopschedule;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.transition.Fade;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.ads.MobileAds;
import com.google.gson.Gson;
import com.udl.bss.barbershopschedule.domain.Barber;
import com.udl.bss.barbershopschedule.domain.Client;
import com.udl.bss.barbershopschedule.fragments.BarberDetailFragment;
import com.udl.bss.barbershopschedule.fragments.BarberHomeFragment;
import com.udl.bss.barbershopschedule.fragments.BarberListFragment;
import com.udl.bss.barbershopschedule.fragments.BarberPromotionsFragment;
import com.udl.bss.barbershopschedule.fragments.BarberScheduleFragment;
import com.udl.bss.barbershopschedule.fragments.BarberServicesFragment;
import com.udl.bss.barbershopschedule.fragments.HomeFragment;

public class HomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BarberListFragment.OnFragmentInteractionListener,
        BarberDetailFragment.OnFragmentInteractionListener,
        HomeFragment.OnFragmentInteractionListener,
        BarberHomeFragment.OnFragmentInteractionListener,
        BarberScheduleFragment.OnFragmentInteractionListener,
        BarberServicesFragment.OnFragmentInteractionListener,
        BarberPromotionsFragment.OnFragmentInteractionListener {

    private FloatingActionMenu floatingActionMenu;
    private boolean doubleBack = false;

    private Client client;
    private Barber barber;
    private SharedPreferences mPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MobileAds.initialize(this, getString(R.string.ad_mob_app_key));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Fade fade = new Fade();
            fade.excludeTarget(android.R.id.statusBarBackground, true);
            fade.excludeTarget(android.R.id.navigationBarBackground, true);

            getWindow().setEnterTransition(fade);
            getWindow().setExitTransition(fade);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        ViewStub stub = findViewById(R.id.stub);
        Fragment fragment;

        mPrefs = getSharedPreferences("USER", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = mPrefs.getString("user", "");
        String mode = mPrefs.getString("mode", "");

        if (mode.equals("Barber")) {

            barber = gson.fromJson(json, Barber.class);


            stub.setLayoutResource(R.layout.barber_fab);
            navigationView.inflateMenu(R.menu.activity_barber_home_drawer);

            fragment = BarberHomeFragment.newInstance(barber);
            stub.inflate();

            FloatingActionButton fab_new_service = findViewById(R.id.fab_barber_new_service);
            floatingActionMenu = findViewById(R.id.fab_menu);
            fab_new_service.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                    Intent intent = new Intent(getApplicationContext(), BarberNewServiceActivity.class);
                    startActivity(intent);
                }
            });

            FloatingActionButton fab_new_promo = findViewById(R.id.fab_barber_new_promo);
            fab_new_promo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    floatingActionMenu.close(true);
                    Intent intent = new Intent(getApplicationContext(), BarberNewPromotionActivity.class);
                    startActivity(intent);
                }
            });

            startFragment(fragment);

        } else if (mode.equals("User")) {

            client = gson.fromJson(json, Client.class);

            stub.setLayoutResource(R.layout.user_fab);
            navigationView.inflateMenu(R.menu.activity_home_drawer);

            fragment = HomeFragment.newInstance(client);
            stub.inflate();
            floatingActionMenu = findViewById(R.id.fab_menu);
            floatingActionMenu.setVisibility(View.GONE);
            startFragment(fragment);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (floatingActionMenu != null && floatingActionMenu.isOpened()) {
                floatingActionMenu.close(true);
            } else {
                if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                    if (backAction()) super.onBackPressed();
                }
                else {
                    super.onBackPressed();
                }
            }
        }
    }

    public boolean backAction(){
        if(doubleBack) return true;
        this.doubleBack = true;
        Toast.makeText(this, getString(R.string.double_back), Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBack = false;
            }
        }, 2000);
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.home) {
            HomeFragment hf = HomeFragment.newInstance(client);
            startFragment(hf);
        } else if (id == R.id.barber_home) {
            BarberHomeFragment bhf = BarberHomeFragment.newInstance(barber);
            startFragment(bhf);
        } else if (id == R.id.show_barbers) {
            BarberListFragment blf = BarberListFragment.newInstance();
            startFragment(blf);
        } else if (id == R.id.show_schedule) {
            BarberScheduleFragment bsf = BarberScheduleFragment.newInstance();
            startFragment(bsf);
        } else if (id == R.id.show_services) {
            BarberServicesFragment bsf = BarberServicesFragment.newInstance();
            startFragment(bsf);
        } else if (id == R.id.show_promotions) {
            BarberPromotionsFragment bpf = BarberPromotionsFragment.newInstance();
            startFragment(bpf);
        } else if (id == R.id.profile) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.log_out) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

            mPrefs.edit().remove("user").remove("mode").apply();

            startActivity(intent);
            finish();
        } else if (id == R.id.exit) {
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void startFragment(Fragment fragment) {
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_home, fragment)
                    .commit();
        }
    }

    private void startFragmentBackStack(Fragment fragment) {
        if (fragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.content_home, fragment)
                    .addToBackStack(null)
                    .commit();
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
