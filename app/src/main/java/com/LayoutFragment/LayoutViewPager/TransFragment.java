package com.LayoutFragment.LayoutViewPager;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Toast;

import com.comyou.QRScan.EncryptionServer;
import com.comyou.QRScan.LinkerServer;
import com.comyou.QRScan.R;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class TransFragment extends Fragment {
    private Bitmap qrCodeBitmap;

    private EditText person_from;
    private EditText from;
    private EditText area_code_from;
    private EditText tel_from;
    private EditText person_to;
    private EditText to;
    private EditText area_code_to;
    private EditText tel_to;
    private EditText description_of_goods;
    private EditText quantity;
    private EditText freight;
    private EditText picked_by;
    private EditText delieved_by;

    private Spinner logistics_type;
    private Spinner payment_of_change;
    private Spinner shipment_type;
    private String logistics_type_str;
    private String payment_of_change_str;
    private String shipment_type_str;

    private ScrollView trans_scrollview;

    private String time;
    private String Id;
    private String response = null;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View trans_view = inflater.inflate(R.layout.trans_tab, container, false);
        Button trans_post = (Button) trans_view.findViewById(R.id.trans_btn);

        person_from = (EditText) trans_view.findViewById(R.id.person_from);
        from = (EditText) trans_view.findViewById(R.id.From);
        area_code_from = (EditText) trans_view.findViewById(R.id.Area_Code_from);
        tel_from = (EditText) trans_view.findViewById(R.id.Tel_from);
        person_to = (EditText) trans_view.findViewById(R.id.Preson_to);
        to = (EditText) trans_view.findViewById(R.id.To);
        area_code_to = (EditText) trans_view.findViewById(R.id.Area_Code_to);
        tel_to = (EditText) trans_view.findViewById(R.id.Tel_to);
        description_of_goods = (EditText) trans_view.findViewById(R.id.Description_Of_Goods);
        quantity = (EditText) trans_view.findViewById(R.id.Quantity);
        freight = (EditText) trans_view.findViewById(R.id.Freight);
        picked_by = (EditText) trans_view.findViewById(R.id.Pickup_by);
        delieved_by = (EditText) trans_view.findViewById(R.id.Delieved_by);

        logistics_type = (Spinner) trans_view.findViewById(R.id.Logistics_type);
        logistics_type_str = (String) logistics_type.getSelectedItem();
        payment_of_change = (Spinner) trans_view.findViewById(R.id.Payment_of_Charge);
        payment_of_change_str = (String) payment_of_change.getSelectedItem();
        shipment_type = (Spinner) trans_view.findViewById(R.id.Shipment_Type);
        shipment_type_str = (String) shipment_type.getSelectedItem();

        trans_scrollview = (ScrollView) trans_view.findViewById(R.id.trans_scro);

        logistics_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                logistics_type_str = logistics_type.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        payment_of_change.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                payment_of_change_str = payment_of_change.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        shipment_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shipment_type_str = shipment_type.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });


        trans_post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LinkerServer linkerServer = new LinkerServer("trans",CreatePair());
                        UIAndMakeQR(linkerServer.Linker());
                        handler.sendEmptyMessage(0);
                    }
                }).start();
            }
        });
        return trans_view;
    }

    private void UIAndMakeQR(boolean isLinkSucceed){
        if (isLinkSucceed) {
            String contentString = Id + "&"
                    + time + "&"
                    + person_from.getText().toString() + "&"
                    + from.getText().toString() + "&"
                    + area_code_from.getText().toString() + "&"
                    + tel_from.getText().toString() + "&"
                    + person_to.getText().toString() + "&"
                    + to.getText().toString() + "&"
                    + area_code_to.getText().toString() + "&"
                    + tel_to.getText().toString() + "&"
                    + logistics_type_str + "&"
                    + description_of_goods.getText().toString() + "&"
                    + quantity.getText().toString() + "&"
                    + freight.getText().toString() + "&"
                    + payment_of_change_str + "&"
                    + shipment_type_str + "&"
                    + picked_by.getText().toString() + "&"
                    + delieved_by.getText().toString() + "&";

            EncryptionServer encryptionServer = new EncryptionServer();
            contentString = encryptionServer.Encryption(contentString);
            try {
                if (!contentString.equals("")) {
                    qrCodeBitmap = EncodingHandler.createQRCode(contentString, 350);
                    saveQrCodePicture();
                } else {
                    Toast.makeText(getActivity(), R.string.text_not_be_empty, Toast.LENGTH_SHORT).show();
                }

            } catch (WriterException e) {
                e.printStackTrace();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    person_from.setText("");
                    from.setText("");
                    area_code_from.setText("");
                    tel_from.setText("");
                    person_to.setText("");
                    to.setText("");
                    area_code_to.setText("");
                    tel_to.setText("");
                    description_of_goods.setText("");
                    quantity.setText("");
                    freight.setText("");
                    picked_by.setText("");
                    delieved_by.setText("");

                    trans_scrollview.fullScroll(ScrollView.FOCUS_UP);
                }
            });
        } else
            Toast.makeText(getActivity(), response, Toast.LENGTH_SHORT).show();
    }

    private List<NameValuePair> CreatePair(){
        Date date = new Date();
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = format.format(date);

        //建立传递的参数的list
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("person_from", person_from.getText().toString()));
        params.add(new BasicNameValuePair("from", from.getText().toString()));
        params.add(new BasicNameValuePair("area_code_from", area_code_from.getText().toString()));
        params.add(new BasicNameValuePair("tel_from", tel_from.getText().toString()));
        params.add(new BasicNameValuePair("person_to", person_to.getText().toString()));
        params.add(new BasicNameValuePair("to", to.getText().toString()));
        params.add(new BasicNameValuePair("area_code_to", area_code_to.getText().toString()));
        params.add(new BasicNameValuePair("tel_to", tel_to.getText().toString()));
        params.add(new BasicNameValuePair("logistics_type", logistics_type_str));
        params.add(new BasicNameValuePair("description_of_goods", description_of_goods.getText().toString()));
        params.add(new BasicNameValuePair("quantity", quantity.getText().toString()));
        params.add(new BasicNameValuePair("freight", freight.getText().toString()));
        params.add(new BasicNameValuePair("payment_of_change", payment_of_change_str));
        params.add(new BasicNameValuePair("shipment_type", shipment_type_str));
        params.add(new BasicNameValuePair("picked_by", picked_by.getText().toString()));
        params.add(new BasicNameValuePair("delieved_by", picked_by.getText().toString()));
        params.add(new BasicNameValuePair("pick_date", time));

        return params;
    }

    private void DirExit(String path){
        File file = new File(path);
        if (!file.exists())
            file.mkdir();
    }

    private void saveQrCodePicture() {
        /**
         * 保存生成的二維碼圖片
         */
        String path = Environment.getExternalStorageDirectory() + "/AQR";
        DirExit(path);
        final File qrImage = new File(path, time + ".jpeg");
        if (qrImage.exists()) {
            qrImage.delete();
        }
        try {
            qrImage.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(qrImage);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (qrCodeBitmap == null) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.image_not_exist, Toast.LENGTH_SHORT).show();
                }
            });
            return;
        }
        qrCodeBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
            fOut.close();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getActivity(), R.string.image_sucess, Toast.LENGTH_SHORT).show();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
