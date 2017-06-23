package com.LayoutFragment.LayoutViewPager;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.comyou.QRScan.EncryptionServer;
import com.comyou.QRScan.LinkerServer;
import com.comyou.QRScan.R;
import com.google.zxing.WriterException;
import com.zxing.encoding.EncodingHandler;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by aerber on 16-9-11.
 */
public class SearchDetailActivity extends Activity {
    private ImageView imageView_show;
    private Bitmap qrCodeBitmap;
    TextView ResultShow;

    String order_no = null;
    String result = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_show_layout);

        ResultShow = (TextView) findViewById(R.id.search_result);
        imageView_show = (ImageView) findViewById(R.id.imageView_show);

        order_no = this.getIntent().getStringExtra("order_no");
        result = this.getIntent().getStringExtra("show");

        if (order_no != null)
            GetQR();

        if (result != null) {
            ResultShow.setTextSize(15);
            ResultShow.setText(FormatResult(result));
            ShowImage(result);
        }


    }

    //格式化服务器传输回来的数据
    private String FormatResult(String content) {
        StringBuffer Bufresult = new StringBuffer();

        int count = 1;
        Bufresult.append(getString(R.string.Id));
        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '&') {
                Bufresult.append("\n");
                ++count;

                if (count == 2) Bufresult.append(getString(R.string.pick_date));
                else if (count == 3) Bufresult.append("\n" + getString(R.string.person_from));
                else if (count == 4) Bufresult.append(getString(R.string.From));
                else if (count == 5) Bufresult.append(getString(R.string.area_code_from));
                else if (count == 6) Bufresult.append(getString(R.string.tel_from));
                else if (count == 7) Bufresult.append("\n" + getString(R.string.preson_to));
                else if (count == 8) Bufresult.append(getString(R.string.to));
                else if (count == 9) Bufresult.append(getString(R.string.area_code_to));
                else if (count == 10) Bufresult.append(getString(R.string.tel_to));
                else if (count == 11) Bufresult.append("\n" + getString(R.string.logistics_type));
                else if (count == 12) Bufresult.append(getString(R.string.descrip_of_good));
                else if (count == 13) Bufresult.append(getString(R.string.quantity));
                else if (count == 14) Bufresult.append(getString(R.string.freight));
                else if (count == 15) Bufresult.append(getString(R.string.pay_of_charge));
                else if (count == 16) Bufresult.append("\n" + getString(R.string.shipment_type));
                else if (count == 17) Bufresult.append(getString(R.string.picked_by));
                else if (count == 18) Bufresult.append(getString(R.string.delieved_by));
                else if (count == 19) Bufresult.append("\n" + getString(R.string.Isreturn));
            } else Bufresult.append(c);
        }
        String result = Bufresult.toString();
        return result;
    }

    private void ShowImage(String result) {
        EncryptionServer encryptionServer = new EncryptionServer();
        result = encryptionServer.Encryption(result);

        try {
            if (!result.equals("")) {
                qrCodeBitmap = EncodingHandler.createQRCode(result, 350);
                imageView_show.setImageBitmap(qrCodeBitmap);
            } else {
                Toast.makeText(this, R.string.text_not_be_empty, Toast.LENGTH_SHORT).show();
            }

        } catch (WriterException e) {
            e.printStackTrace();
        }
    }

    private void GetQR() {
        List<NameValuePair> params_detail = new ArrayList<>();
        params_detail.add(new BasicNameValuePair("order_no", order_no));
        final LinkerServer linkerServer = new LinkerServer("getqr", params_detail);
        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                if (linkerServer.Linker()) {
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            ResultShow.setTextSize(15);
                            ResultShow.setText(FormatResult(linkerServer.getResponse()));
                            ShowImage(linkerServer.getResponse());
                        }
                    });

                } else {
                    Toast.makeText(SearchDetailActivity.this, R.string.request_qr_fail, Toast.LENGTH_SHORT).show();
                }
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();


    }
}
