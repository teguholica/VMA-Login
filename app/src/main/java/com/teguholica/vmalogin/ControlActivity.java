package com.teguholica.vmalogin;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.teguholica.vmalogin.utils.ASCII;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.HashMap;
import java.util.Map;

public class ControlActivity extends AppCompatActivity {

    private static final int FLAG_LOGIN = 1;
    private static final int FLAG_LOGOUT = 2;

    private ProgressBar vLoading;
    private Button btnControl;
    private TextView txtIpAddress;
    private View vStatusLayout;
    private User user;

    private String capId;
    private String capChallenge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);

        user = new User(this);

        vLoading = (ProgressBar) findViewById(R.id.loading);
        btnControl = (Button) findViewById(R.id.btnControl);
        txtIpAddress = (TextView) findViewById(R.id.ipaddress);
        vStatusLayout = findViewById(R.id.statusLayout);

        btnControl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int flag = (int) v.getTag();
                switch (flag) {
                    case FLAG_LOGIN:
                        login();
                        break;
                    case FLAG_LOGOUT:
                        logout();
                        break;
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        latestState();
    }

    private void latestState() {
        loadingState();
        String url = "http://vma.net/status";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Document doc = Jsoup.parse(response);
                Element btnElement = doc.select("input[type=submit]").first();
                txtIpAddress.setText("");
                String status = btnElement.attr("value");
                if (status.equals("log off")) {
                    Element ipAddress = doc.select("table.tabula tr").first().select("td").get(1);
                    txtIpAddress.setText(ipAddress.text());
                    logoutState();
                } else {
                    loginState();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        App.getInstance().addToRequestQueue(strReq, "");
    }

    private void loadingState() {
        vStatusLayout.setVisibility(View.GONE);
        vLoading.setVisibility(View.VISIBLE);
    }

    private void loginState() {
        vStatusLayout.setVisibility(View.VISIBLE);
        vLoading.setVisibility(View.GONE);
        btnControl.setTag(FLAG_LOGIN);
        btnControl.setText("LOGIN");
    }

    private void logoutState() {
        vStatusLayout.setVisibility(View.VISIBLE);
        vLoading.setVisibility(View.GONE);
        btnControl.setTag(FLAG_LOGOUT);
        btnControl.setText("LOGOUT");
    }

    private void login() {
        loadingState();
        String url = "http://vma.net/login";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                int index1Length = "document.sendin.password.value = hexMD5('\\".length();
                int index1 = response.indexOf("document.sendin.password.value = hexMD5('\\") + index1Length;
                int index2 = response.indexOf("');", index1);
                String capStr = response.substring(index1, index2).replace("' + document.login.password.value + '", "").replace("\\", "-");
                String[] caps = TextUtils.split(capStr, "-");
                capId = ASCII.convert(caps[0]);
                StringBuilder capChallengeBuilder = new StringBuilder();
                for (int i=1; i<caps.length; i++) {
                    capChallengeBuilder.append(ASCII.convert(caps[i]));
                }
                capChallenge = capChallengeBuilder.toString();

                String url = "http://vma.net/login";
                StringRequest strReq = new StringRequest(Request.Method.POST,
                        url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        latestState();
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }) {

                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", user.getUsername());
                        params.put("password", user.getEncyrptPassword(capId, capChallenge));

                        return params;
                    }
                };

                App.getInstance().addToRequestQueue(strReq, "");
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        App.getInstance().addToRequestQueue(strReq, "");
    }

    private void logout() {
        loadingState();
        String url = "http://vma.net/logout";
        StringRequest strReq = new StringRequest(Request.Method.GET,
                url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                latestState();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        App.getInstance().addToRequestQueue(strReq, "");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_control, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.reset) {
            logout();
            user.clear();
            Intent mainActivity = new Intent(this, MainActivity.class);
            startActivity(mainActivity);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
