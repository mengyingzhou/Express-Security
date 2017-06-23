package com.comyou.QRScan;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;

import com.bluetooth.BluetoothChatService;
import com.bluetooth.DeviceListActivity;
import com.comyou.qrscan.QRScanListener;
import com.comyou.qrscan.QRScanManager;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;


public final class ScanActivity extends Activity implements QRScanListener {
    QRScanManager qrScanManager;
    RelativeLayout layout_contain;


    //蓝牙组件

    //连接设备提醒
    private TextView mTitle;

    // Debugging
    private static final String TAG = "Bluetooch";
    private static final boolean D = true;

    // Message types sent from the BluetoothChatService Handler
    public static final int MESSAGE_STATE_CHANGE = 1;
    public static final int MESSAGE_READ = 2;
    public static final int MESSAGE_WRITE = 3;
    public static final int MESSAGE_DEVICE_NAME = 4;
    public static final int MESSAGE_TOAST = 5;

    // Key names received from the BluetoothChatService Handler
    public static final String DEVICE_NAME = "device_name";
    public static final String TOAST = "toast";

    // Intent request codes
    private static final int REQUEST_CONNECT_DEVICE = 1;
    private static final int REQUEST_ENABLE_BT = 2;

    // Name of the connected device
    private String mConnectedDeviceName = null;
    // Array adapter for the conversation thread
    private ArrayAdapter<String> mConversationArrayAdapter;
    // String buffer for outgoing messages
    private StringBuffer mOutStringBuffer;
    // Local Bluetooth adapter
    private BluetoothAdapter mBluetoothAdapter = null;
    // Member object for the chat services
    private BluetoothChatService mChatService = null;

    private String readMessage;

    private String order_num;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.activity_qrscan);

        mTitle = (TextView) findViewById(R.id.scan_tv_two);

        // Get local Bluetooth adapter
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        //二维码
        qrScanManager = new QRScanManager(this);
        layout_contain = (RelativeLayout) findViewById(R.id.layout_contain);

        qrScanManager.initWithSurfaceView(this, R.id.surfaceview);
        qrScanManager.setBeepResource(R.raw.beep);
        Rect rect = initCrop();
        qrScanManager.setCropRect(rect);

        ScanLineView scanline = (ScanLineView) findViewById(R.id.scanline);

        // 动画效果
        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 1.0f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanline.startAnimation(animation);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                // Launch the DeviceListActivity to see devices and do scan
                Intent serverIntent = new Intent(this, DeviceListActivity.class);
                startActivityForResult(serverIntent, REQUEST_CONNECT_DEVICE);
                return true;
            case R.id.discoverable:
                // Ensure this device is discoverable by others
                ensureDiscoverable();
                return true;
        }
        return false;
    }

    private Rect initCrop() {

        int screenWidth = getWindowManager().getDefaultDisplay().getWidth();

        LayoutParams params = (LayoutParams) layout_contain.getLayoutParams();
        int x = params.leftMargin;
        int y = params.topMargin;
        int width = screenWidth - 2 * x;
        int height = width;

        params.height = height;

        layout_contain.setLayoutParams(params);

        return new Rect(x, y, width + x, height + y);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (D) Log.i(TAG, "++ ON START ++");

        // If BT is not on, request that it be enabled.
        // setupChat() will then be called during onActivityResult
        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableIntent, REQUEST_ENABLE_BT);
            // Otherwise, setup the chat session
        } else {
            if (mChatService == null)
                setupChat();
        }
    }

    private void setupChat() {
        Log.d(TAG, "setupChat()");

        // Initialize the BluetoothChatService to perform bluetooth connections
        mChatService = new BluetoothChatService(this, mHandler);

        // Initialize the buffer for outgoing messages
        mOutStringBuffer = new StringBuffer("");
    }

    @Override
    protected synchronized void onResume() {
        // TODO Auto-generated method stub
        super.onResume();

        qrScanManager.onResume();

        if (D) Log.i(TAG, "———— Bluetooth RESUME +");

        // Performing this ChangeRecheck in onResume() covers the case in which BT was
        // not enabled during onStart(), so we were paused to enable it...
        // onResume() will be called when ACTION_REQUEST_ENABLE activity returns.
        if (mChatService != null) {
            // Only if the state is STATE_NONE, do we know that we haven't started already
            if (mChatService.getState() == BluetoothChatService.STATE_NONE) {
                // Start the Bluetooth chat services
                mChatService.start();
            }
        }

    }

    @Override
    protected void onPause() {
        qrScanManager.onPause();
        super.onPause();
        if (D) Log.i(TAG, "- Bluetooth PAUSE -");
    }

    @Override
    public void onStop() {
        super.onStop();
        if (D) Log.i(TAG, "-- ON STOP --");
    }

    @Override
    protected void onDestroy() {
        qrScanManager.onDestroy();
        super.onDestroy();

        // Stop the Bluetooth chat services
        if (mChatService != null) mChatService.stop();
        if (D) Log.i(TAG, "--- BlueTooth  DESTROY ---");
    }

    private void ensureDiscoverable() {
        if (D) Log.d(TAG, "ensure discoverable");
        if (mBluetoothAdapter.getScanMode() !=
                BluetoothAdapter.SCAN_MODE_CONNECTABLE_DISCOVERABLE) {
            Intent discoverableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
            discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 300);
            startActivity(discoverableIntent);
        }
    }

    /**
     * Bluetooth Sends a message.
     *
     * @param message A string of text to send.
     */
    private boolean sendMessage(String message) {
        // Check that we're actually connected before trying anything
        if (mChatService.getState() != BluetoothChatService.STATE_CONNECTED) {
            Toast.makeText(this, R.string.not_connected, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check that there's actually something to send
        if (message.length() > 0) {
            // Get the message bytes and tell the BluetoothChatService to write
            byte[] send = message.getBytes();
            mChatService.write(send);

            // Reset out string buffer to zero and clear the edit text field
            mOutStringBuffer.setLength(0);

            return true;
        }
        return false;
    }

    // The Handler that gets information back from the BluetoothChatService
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MESSAGE_STATE_CHANGE:
                    if (D) Log.i(TAG, "MESSAGE_STATE_CHANGE: " + msg.arg1);
                    switch (msg.arg1) {
                        case BluetoothChatService.STATE_CONNECTED:
                            mTitle.setText(R.string.title_connected_to);
                            mTitle.append(mConnectedDeviceName);
                            break;
                        case BluetoothChatService.STATE_CONNECTING:
                            mTitle.setText(R.string.title_connecting);
                            break;
                        case BluetoothChatService.STATE_LISTEN:
                        case BluetoothChatService.STATE_NONE:
                            mTitle.setText(R.string.title_not_connected);
                            break;
                    }
                    break;
                case MESSAGE_WRITE:
                    byte[] writeBuf = (byte[]) msg.obj;
                    // construct a string from the buffer
                    String writeMessage = new String(writeBuf);
                    Log.i(TAG, writeMessage);
                    break;
                case MESSAGE_READ:
                    // construct a string from the valid bytes in the buffer
                    byte[] readBuf = (byte[]) msg.obj;
                    readMessage = new String(readBuf, 0, msg.arg1);
                    Log.i(TAG, "read" + readMessage);

                    break;
                case MESSAGE_DEVICE_NAME:
                    // save the connected device's name
                    mConnectedDeviceName = msg.getData().getString(DEVICE_NAME);
                    Toast.makeText(getApplicationContext(), "Connected to "
                            + mConnectedDeviceName, Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_TOAST:
                    Toast.makeText(getApplicationContext(), msg.getData().getString(TOAST),
                            Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (D) Log.d(TAG, "onActivityResult " + resultCode);
        switch (requestCode) {
            case REQUEST_CONNECT_DEVICE:
                // When DeviceListActivity returns with a device to connect
                if (resultCode == Activity.RESULT_OK) {
                    // Get the device MAC address
                    String address = data.getExtras()
                            .getString(DeviceListActivity.EXTRA_DEVICE_ADDRESS);
                    // Get the BLuetoothDevice object
                    BluetoothDevice device = mBluetoothAdapter.getRemoteDevice(address);
                    // Attempt to connect to the device
                    mChatService.connect(device);
                }
                break;
            case REQUEST_ENABLE_BT:
                // When the request to enable Bluetooth returns
                if (resultCode == Activity.RESULT_OK) {
                    // Bluetooth is now enabled, so set up a chat session
                    setupChat();
                } else {
                    // User did not enable Bluetooth or an error occured
                    Log.d(TAG, "BT not enabled");
                    Toast.makeText(this, R.string.bt_not_enabled_leaving, Toast.LENGTH_SHORT).show();
                    finish();
                }
        }
    }

    @Override
    public void onScanResult(String content) {
        Log.i(TAG, content);
        StringBuffer unicode = new StringBuffer();
        String result;
        int num = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '&') unicode.append("&");
            else {
                int c_code = (int) c;
                --c_code;
                unicode.append(c_code + "|");
            }
        }
        result = unicode.toString();
        Log.i(TAG, result);
        unicode.delete(0, result.length());

//         全部解密
//        for (int i = 0; i < result.length(); ++i){
//            char c = result.charAt(i);
//
//            if (c == '|'){
//                char temp = (char) num;
//                unicode.append(temp);
//                num = 0;
//            }
//            else if (c == '&') unicode.append("&");
//            else num = c - '0' + num * 10;
//        }
//        result = unicode.toString();

        //部分解密
        String id = null;
        String time = null;
        String logistics_type = null;
        for (int i = 0, count = 1; i < result.length(); ++i) {
            char c = result.charAt(i);

            if (c == '|') {
                if (count == 1 || count == 2 || count == 11) {
                    char temp = (char) num;
                    unicode.append(temp);
                }
                num = 0;
            } else if (c == '&') {
                if (count == 1)
                    id = unicode.toString();
                else if (count == 2)
                    time = unicode.toString();
                else if (count == 11)
                    logistics_type = unicode.toString();
                ++count;
                unicode.delete(0, unicode.length());
            } else num = c - '0' + num * 10;
        }

        final String finalId = id;
        order_num = id;
        final String finalTime = time;
        final String finalLogistics_type = logistics_type;

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("logistics_type", finalLogistics_type));
                params.add(new BasicNameValuePair("order_no", finalId));
                params.add(new BasicNameValuePair("order_password", finalTime));
                LinkerServer linkerServer = new LinkerServer("scan", params);

                boolean check = linkerServer.Linker();
                if (check) {
                    if (sendMessage("1")) {
                        Toast.makeText(ScanActivity.this, linkerServer.getResponse(), Toast.LENGTH_SHORT).show();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                showCenterPopupWindow(layout_contain);
                            }
                        });
                    }

                } else Toast.makeText(ScanActivity.this, "Failed!", Toast.LENGTH_SHORT).show();
                handler.sendEmptyMessage(0);
                Looper.loop();

            }
        }).start();

    }

    /**
     * 中间弹出PopupWindow
     * <p>
     * 设置PopupWindow以外部分有一中变暗的效果
     *
     * @param view parent view
     */
    public void showCenterPopupWindow(View view) {
        View contentView = LayoutInflater.from(this).inflate(R.layout.scan_check_layout, null);
        final PopupWindow mPopupWindow = new PopupWindow(contentView, 600, LinearLayout.LayoutParams.WRAP_CONTENT);
        TextView tvConfirm = (TextView) contentView.findViewById(R.id.tv_checkGet);

        tvConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<NameValuePair> params_detail = new ArrayList<>();
                params_detail.add(new BasicNameValuePair("order_no", order_num));
                final LinkerServer linkerServer = new LinkerServer("changere", params_detail);

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Looper.prepare();
                        if (linkerServer.Linker()) {
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    if (sendMessage("0")) {
                                        mPopupWindow.dismiss();
                                        Toast.makeText(ScanActivity.this, R.string.check_toast, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(ScanActivity.this, R.string.request_fail, Toast.LENGTH_SHORT).show();
                        }
                        handler.sendEmptyMessage(0);
                        Looper.loop();
                    }
                }).start();
            }
        });


        mPopupWindow.setFocusable(false);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(0x00000000));
        // 设置PopupWindow以外部分的背景颜色  有一种变暗的效果
        final WindowManager.LayoutParams wlBackground = getWindow().getAttributes();
        wlBackground.alpha = 0.0f;      // 0.0 完全不透明,1.0完全透明
        getWindow().setAttributes(wlBackground);
        // 当PopupWindow消失时,恢复其为原来的颜色
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                wlBackground.alpha = 1.0f;
                getWindow().setAttributes(wlBackground);
            }
        });
        //设置PopupWindow进入和退出动画
        mPopupWindow.setAnimationStyle(R.style.anim_popup_centerbar);
        // 设置PopupWindow显示在中间
        mPopupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);

    }

}
