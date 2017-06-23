package com.LayoutFragment.LayoutViewPager;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.comyou.QRScan.R;
import com.comyou.QRScan.ScanActivity;

public class ScanFragment extends Fragment {
    private BluetoothAdapter mBluetoothAdapter = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View scan_view = inflater.inflate(R.layout.scan_tab, container, false);
        ImageButton scran_but = (ImageButton) scan_view.findViewById(R.id.scran_button);
        scran_but.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                // Get local Bluetooth adapter
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
                // If the adapter is null, then Bluetooth is not supported
                if (mBluetoothAdapter == null) {
                    Toast.makeText(getActivity(), "未连接蓝牙！请确认连接蓝牙！", Toast.LENGTH_LONG).show();
                    return;
                }else {
                    //在这里使用getActivity
                    Intent intent = new Intent(getActivity(), ScanActivity.class);
                    startActivity(intent);
                }
            }
        });
        return scan_view;
    }
}
