package donwit.com.uhf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.widget.Toast.makeText;
import static donwit.com.uhf.Util.getDate;
import static donwit.com.uhf.Util.isNetworkConnected;
import static donwit.com.uhf.Util.setButtonClickable;
import static donwit.com.uhf.Util.showMyToast;


public class MainActivity extends AppCompatActivity implements OnClickListener{
    private UhfReader reader;
    private Button start_btn;
    private Button setting_btn;
    private Button clean_btn;
    private boolean isNet;
    private String IMEI;
    private boolean startFlag = false;
    private ListView listViewData;
    private ArrayList<EPC> listEPC;
    private ArrayList<Map<String, Object>> listMap;
    private SharedPreferences sp;
    private String Ip;
    private String Port;
    private String Power;
    private TextView now_power;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reader = UhfReader.getInstance();
        if(reader == null){
            setButtonClickable(start_btn,false);
            setButtonClickable(setting_btn,false);
            setButtonClickable(clean_btn,false);
            makeText(this,"获取实例失败！",Toast.LENGTH_SHORT).show();
        }else {
            isNet = isNetworkConnected(this);
            initView();
            Thread thread = new InventoryThread();
            thread.start();
        }
    }

    private void initView() {
        sp = this.getSharedPreferences("config",this.MODE_PRIVATE);
        IMEI = Util.getIMEI(this);
        Util.initSoundPool(this);
        start_btn = (Button) findViewById(R.id.start_btn);
        start_btn.setOnClickListener(this);
        setting_btn = (Button) findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(this);
        clean_btn = (Button) findViewById(R.id.clean_btn);
        clean_btn.setOnClickListener(this);
        now_power = (TextView) findViewById(R.id.now_power);
        listViewData = (ListView) findViewById(R.id.epc_list);
        listEPC = new ArrayList<>();
        Ip = sp.getString("IP","127.0.0.1");
        Port = sp.getString("Port","8080");
        Power = sp.getString("Power","26");
        now_power.setText(Power+"dBm");
        reader.setOutputPower(Integer.parseInt(Power));
        if(isNet){
            clean_btn.setText(R.string.clean_list);
        }else {
            clean_btn.setText(R.string.isOpenNet);
            setButtonClickable(start_btn,false);
            setButtonClickable(setting_btn,false);
        }
    }

    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case sendData.SEND_SUCCESS:
                    Util.play(1,0);
                    Toast toast_success = Toast.makeText(MainActivity.this,
                            R.string.upload_success,Toast.LENGTH_SHORT);
                    showMyToast(toast_success,1000);
                    break;
                case sendData.SEND_FAIL:
                    Toast toast_fail = Toast.makeText(MainActivity.this,
                            R.string.upload_fail,Toast.LENGTH_SHORT);
                    showMyToast(toast_fail,1000);
                    break;
                case sendData.CONN_ERROR:
                    Toast toast_error = Toast.makeText(MainActivity.this,
                            R.string.conn_error,Toast.LENGTH_SHORT);
                    showMyToast(toast_error,1000);
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:
                if(!startFlag){
                    startFlag = true;
                    setButtonClickable(setting_btn,false);
                    start_btn.setText(R.string.end);
                }else {
                    startFlag = false;
                    setButtonClickable(setting_btn,true);
                    start_btn.setText(R.string.start);
                }
                break;
            case R.id.clean_btn:
                if(isNet){
                    if(listViewData.getCount()>0){
                        cleanListView();
                    }else {
                        Toast toast = makeText(this,R.string.no_data,Toast.LENGTH_LONG);
                        showMyToast(toast,1000);
                    }
                }else {
                    isNet = Util.isNetworkConnected(this);
                    if(isNet){
                        clean_btn.setText(R.string.clean_list);
                        setButtonClickable(start_btn,true);
                        setButtonClickable(setting_btn,true);
                    }else {
                        Toast toast = makeText(this,R.string.openNet,Toast.LENGTH_LONG);
                        showMyToast(toast,1000);
                    }
                }
                break;
            case R.id.setting_btn:
                Intent intent = new Intent(this,SettingActivity.class);
                startActivity(intent);
            default:
                break;
        }
    }

    private class InventoryThread extends Thread{
        private volatile byte[] epcbyte;
        private byte[] passbtye = Tools.HexString2Bytes("00000000");
        private volatile byte[] tidbyte;
        @Override
        public void run() {
            while (true){
                if(startFlag){
                    epcbyte = reader.readFrom6C(1, 2, 3, this.passbtye);
                    tidbyte = reader.readFrom6C(2, 0, 6, this.passbtye);
                    if(epcbyte != null && epcbyte.length > 1 && tidbyte != null && tidbyte.length > 1){
                        String tidStr = Tools.Bytes2HexString(tidbyte, tidbyte.length);
                        String epcStr = Tools.Bytes2HexString(epcbyte, epcbyte.length);
                        addToList(listEPC,tidStr,epcStr);
                    }
                }
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void addToList(final ArrayList<EPC> list, final String tidStr, final String epcStr) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //第一次读入数据
                if(list.isEmpty()){
                    EPC epc = new EPC();
                    epc.setEpc(epcStr);
                    epc.setTid(tidStr);
                    epc.setIMEI(IMEI);
                    epc.setScanDate(getDate());
                    list.add(epc);
                    UpLoadData(epc);
                }else{
                    for(int i = 0; i < list.size(); i++){
                        EPC mEPC = list.get(i);
                        //list中有此EPC
                        if(epcStr.equals(mEPC.getEpc()) || tidStr.equals(mEPC.getTid())){
                            list.set(i, mEPC);
                            break;
                        }else if(i == (list.size() - 1)){
                            //list中没有此epc
                            EPC epc = new EPC();
                            epc.setEpc(epcStr);
                            epc.setTid(tidStr);
                            epc.setIMEI(IMEI);
                            epc.setScanDate(getDate());
                            UpLoadData(epc);
                            list.add(epc);
                        }
                    }
                }
                //将数据添加到ListView
                listMap = new ArrayList<>();
                for(EPC epcdata : list){
                    Map<String, Object> map = new HashMap<>();
                    map.put("TID", epcdata.getTid());
                    map.put("EPC", epcdata.getEpc());
                    map.put("Date", epcdata.getScanDate());
                    listMap.add(map);
                }
                listViewData.setAdapter(new SimpleAdapter(MainActivity.this,
                        listMap, R.layout.listview_item,
                        new String[]{"TID", "EPC", "Date"},
                        new int[]{R.id.textView_id, R.id.textView_epc, R.id.textView_date}));
            }
        });
    }

    private void UpLoadData(EPC epc) {
        sendData sd = new sendData(handler,Ip,Port);
        sd.sendDataToServer(epc);
    }

    private void cleanListView() {
        listEPC.removeAll(listEPC);
        listViewData.setAdapter(null);
    }
}
