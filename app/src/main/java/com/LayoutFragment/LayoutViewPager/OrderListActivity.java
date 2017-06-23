package com.LayoutFragment.LayoutViewPager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.comyou.QRScan.LinkerServer;
import com.comyou.QRScan.R;
import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

/**
 * Created by aerber on 16-9-11.
 */
public class OrderListActivity extends AppCompatActivity {
    private MaterialListView mListView;
    Stack<String> list_id;
    Stack<String> list_person_to;
    Stack<String> list_phone;
    Stack<String> list_flag;
    Stack<String> list_address;

    List<Card> cards;

    String response;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_list);
        mListView = (MaterialListView) findViewById(R.id.search_material_listview);

        fillArray();
    }

    private void fillArray() {
        cards = new ArrayList<>();

        list_id = new Stack<>();
        list_person_to = new Stack<>();
        list_phone = new Stack<>();
        list_flag = new Stack<>();
        list_address = new Stack<>();

        new Thread(new Runnable() {
            @Override
            public void run() {
                Looper.prepare();
                LinkerServer linkerServer = new LinkerServer("getlist");
                if (linkerServer.Linker()) {
                    response = linkerServer.getResponse();
                    String[] str_record = response.split("\\|");
                    for (int i = 0; i < str_record.length; ++i) {
                        String[] record = str_record[i].split(";");
                        list_id.push(record[0]);
                        list_person_to.push(record[1]);
                        list_phone.push(record[2]);
                        list_flag.push(record[3]);
                        list_address.push(record[4]);
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            int length = list_id.size();
                            for (int i = 0; i < length; i++) {
                                cards.add(CreateNewCard(list_id.pop(), list_person_to.pop(),
                                        list_address.pop(), list_flag.pop(), list_phone.pop()));
                            }
                            mListView.getAdapter().addAll(cards);
                        }
                    });
                } else {
                    Toast.makeText(OrderListActivity.this, R.string.request_fail, Toast.LENGTH_SHORT).show();
                    cards.add(CreateNewCard("", "", "", "", ""));
                    mListView.getAdapter().addAll(cards);
                }
                handler.sendEmptyMessage(0);
                Looper.loop();
            }
        }).start();
    }

    private Card CreateNewCard(final String order_num, final String person, final String address,
                               final String flag, final String phone) {
        String state = "未收取";
        if (Objects.equals(flag, "0"))
            state = "未达到";
        else if (Objects.equals(flag, "2"))
            state = "已收取";

        final CardProvider provider = new Card.Builder(this)
                .setTag(flag)
                .withProvider(new CardProvider())
                .setLayout(R.layout.material_basic_buttons_card)
                .setTitle("订单号：" + order_num)
                .setSubtitle("收件人：" + person)
                .setSubtitleColor(+R.color.black)
                .setDescription("收件地址：" + address + "\n联系方式：" + phone)
                .addAction(R.id.left_text_button, new TextViewAction(this)
                        .setText("物流详情…")
                        .setTextColor(+R.color.black)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        startActivity(new Intent(OrderListActivity.this, TraceShow.class));
                                    }
                                }).start();
                            }
                        }))
                .addAction(R.id.right_text_button, new TextViewAction(this)
                        .setText(state + "…")
                        .setTextResourceColor(R.color.colorTheme)
                        .setListener(new OnActionClickListener() {
                            @Override
                            public void onActionClicked(View view, Card card) {
                                new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Looper.prepare();
                                        if (!Objects.equals(flag, "1"))
                                            Toast.makeText(OrderListActivity.this, "快件属于无法收件状态！", Toast.LENGTH_SHORT).show();
                                        else {
                                            Intent intent = new Intent(OrderListActivity.this, SearchDetailActivity.class);
                                            intent.putExtra("order_no", order_num);
                                            OrderListActivity.this.startActivity(intent);
                                        }
                                        handler.sendEmptyMessage(0);
                                        Looper.loop();
                                    }
                                }).start();
                            }
                        }));

        return provider.endConfig().build();
    }
}

