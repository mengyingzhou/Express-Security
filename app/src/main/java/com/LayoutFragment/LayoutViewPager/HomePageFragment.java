package com.LayoutFragment.LayoutViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.comyou.QRScan.LinkerServer;
import com.comyou.QRScan.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomePageFragment extends Fragment {
    private EditText username;
    private EditText password;
    private Button login;
    private TextView logout;
    private LinearLayout login_face;
    private LinearLayout login_succeed;
//    private TextView tv_welcome;
    private TextView tv_order;
    private Context context;


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState){
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View person_view = inflater.inflate(R.layout.homepage_tab, container, false);
        username = (EditText) person_view.findViewById(R.id.person_username);
        password = (EditText) person_view.findViewById(R.id.person_password);
        login = (Button) person_view.findViewById(R.id.person_login);
        login_face = (LinearLayout) person_view.findViewById(R.id.person_layout);
        login_succeed = (LinearLayout) person_view.findViewById(R.id.login_succeed_layout);
        logout = (TextView) person_view.findViewById(R.id.tv_logout);
//        tv_welcome = (TextView) person_view.findViewById(R.id.login_welcome);
        tv_order = (TextView) person_view.findViewById(R.id.tv_order);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        LinkerServer linkerServer = new LinkerServer("login",CreatePair());
                        if (linkerServer.Linker()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    login_face.setVisibility(View.GONE);
                                    login_succeed.setVisibility(View.VISIBLE);
//                                    tv_welcome.setText(getString(R.string.login_welcome) + "\n" + username.getText().toString());

                                    Toast.makeText(getActivity(), getString(R.string.login_welcome) + username.getText().toString(),Toast.LENGTH_SHORT).show();
                                }
                            });
                        } else {
                            if (linkerServer.getResponse().equals("0"))
                                Toast.makeText(getActivity(), R.string.login_fail, Toast.LENGTH_SHORT).show();
                            else if (linkerServer.getResponse().equals("2"))
                                Toast.makeText(getActivity(), R.string.password_error, Toast.LENGTH_SHORT).show();
                        }
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });

        tv_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                            Intent intent = new Intent(getActivity(),OrderListActivity.class);
                            getActivity().startActivity(intent);
                    }
                }).start();
            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        final LinkerServer linkerServer = new LinkerServer("logout",CreatePair());
                        if (linkerServer.Linker()){
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    login_face.setVisibility(View.VISIBLE);
                                    login_succeed.setVisibility(View.GONE);
                                    username.setText("");
                                    password.setText("");

                                    Toast.makeText(getActivity(),getString(R.string.workingtime) + linkerServer.getResponse(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        }else
                            Toast.makeText(getActivity(),R.string.logout_fail,Toast.LENGTH_SHORT).show();
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });
        return person_view;
    }

    private List<NameValuePair> CreatePair(){
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String time = format.format(date);

        //建立传递的参数的list
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("username", username.getText().toString()));
        params.add(new BasicNameValuePair("password", password.getText().toString()));
        params.add(new BasicNameValuePair("time", time));

        return params;
    }
}
