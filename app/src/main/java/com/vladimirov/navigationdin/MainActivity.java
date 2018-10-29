package com.vladimirov.navigationdin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String BASE_URL = "https://www.dropbox.com/s/fk3d5kg6cptkpr6/menu.json?dl=1";

    private static final String STATE_SELECTED_POSITION = "NAVIGATION_DRAWER_POSITION";

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private Fragment fragment;

    private boolean savedInst = false;
    private int mCurrentSelectedPosition;
    private int totalItems;

    private ArrayList<String> Fun;
    private ArrayList<String> Par;

    private NavigationView navigationView;
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        menu = navigationView.getMenu();

        if(!isOnline()) {
            Toast.makeText(getApplicationContext(), R.string.internetError, Toast.LENGTH_SHORT).show();
        } else {
            if (savedInstanceState != null) {
                mCurrentSelectedPosition = savedInstanceState.getInt(STATE_SELECTED_POSITION);
                new ParseTask().execute();
            } else {
                new ParseTask().execute();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class ParseTask extends AsyncTask<Void, Void, String> {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String resultJson = null;

        @Override
        protected String doInBackground(Void... voids) {

            try {
                URL url = new URL(BASE_URL);

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();

                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                resultJson = buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                urlConnection.disconnect();
            }

            return resultJson;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            JSONObject dataJsonObj = null;

            try {

                dataJsonObj = new JSONObject(s);
                JSONArray items = dataJsonObj.getJSONArray("menu");
                totalItems = items.length();

                Fun = new ArrayList<String>();
                Par = new ArrayList<String>();

                for (int i = 0; i < totalItems; i++){
                    JSONObject item = items.getJSONObject(i);
                    String nameItem = item.getString("name");
                    String function = item.getString("function");
                    String param = item.getString("param");
                    switch (function) {
                        case "text":
                            menu.add(i, i, Menu.FIRST, nameItem)
                                    .setIcon(R.drawable.format_text);
                            Fun.add(function);
                            Par.add(param);
                            break;
                        case "image":
                            menu.add(i, i, Menu.FIRST, nameItem)
                                    .setIcon(R.drawable.ic_menu_gallery);
                            Fun.add(function);
                            Par.add(param);
                            break;
                        case "url":
                            menu.add(i, i, Menu.FIRST, nameItem)
                                    .setIcon(R.drawable.web);
                            Fun.add(function);
                            Par.add(param);
                            break;
                        default:
                            menu.add(i, i, Menu.FIRST, nameItem);
                            Fun.add(function);
                            Par.add(param);
                            break;
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            if(savedInst) {
                navigationView.getMenu().getItem(0).setChecked(true);
                replaceFragment(Fun.get(0), Par.get(0));
            } else {
                openState();
            }

        }
    }

    private void openState() {
        for(int i = 0; i < totalItems; i++){
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        navigationView.getMenu().getItem(mCurrentSelectedPosition).setChecked(true);
        replaceFragment(Fun.get(mCurrentSelectedPosition), Par.get(mCurrentSelectedPosition));
    }

    private void replaceFragment(String function, String param) {
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if(param != null) {
            switch (function) {
                case "text":
                    fragment = new TextFragment(param);
                    transaction
                            .replace(R.id.container_fragment, fragment)
                            .commit();
                    break;
                case "image":
                    fragment = new ImageFragment(param);
                    transaction
                            .replace(R.id.container_fragment, fragment)
                            .commit();
                    break;
                case "url":
                    fragment = new UrlFragment(param);
                    transaction
                            .replace(R.id.container_fragment, fragment)
                            .commit();
                    break;
                default:
                    Toast.makeText(this, "Error reading file", Toast.LENGTH_SHORT).show();
                    break;
            }
        } else {
            Toast.makeText(this, "Invalid value", Toast.LENGTH_SHORT).show();
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (Fun.get(mCurrentSelectedPosition).equals("url") && ((UrlFragment)fragment).webCanGoBack()) {
                ((UrlFragment)fragment).myOnKeyDown();
            } else {
                finish();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        mCurrentSelectedPosition = id;
        openState();

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(STATE_SELECTED_POSITION, mCurrentSelectedPosition);
        savedInst = true;
    }

}
