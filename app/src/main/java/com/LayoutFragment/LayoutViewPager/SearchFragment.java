package com.LayoutFragment.LayoutViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.comyou.QRScan.LinkerServer;
import com.comyou.QRScan.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private Spinner Sele_logis;
    private String sele_logis_str;
    private EditText order_no;
    private EditText order_password;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View search_view = inflater.inflate(R.layout.search_tab, container, false);
        Sele_logis = (Spinner) search_view.findViewById(R.id.home_spin);
        sele_logis_str = Sele_logis.getSelectedItem().toString();
        order_no = (EditText) search_view.findViewById(R.id.home_order_no);
        order_password = (EditText) search_view.findViewById(R.id.home_order_password);
        Button home_btn = (Button) search_view.findViewById(R.id.home_btn);

        //下拉菜单
        Sele_logis.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                sele_logis_str = Sele_logis.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        home_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();

                        LinkerServer linkerServer = new LinkerServer("search",CreatePair());
                        if (linkerServer.Linker()) {
                            Intent intent = new Intent(getActivity(),SearchDetailActivity.class);
                            intent.putExtra("show",linkerServer.getResponse());
                            getActivity().startActivity(intent);

                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    order_no.setText("");
                                    order_password.setText("");
                                }
                            });
                        } else {
                            Toast.makeText(getActivity(), R.string.request_fail, Toast.LENGTH_SHORT).show();
                        }
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
            }
        });
        return search_view;
    }

    private List<NameValuePair> CreatePair(){
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("logistics_type", sele_logis_str));
        params.add(new BasicNameValuePair("order_no", order_no.getText().toString()));
        params.add(new BasicNameValuePair("order_password", order_password.getText().toString()));

        return params;
    }
}
