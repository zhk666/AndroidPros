package donwit.com.uhf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;
import java.util.ArrayList;
import java.util.List;
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
    private String IMEI ;
    private boolean startFlag = false;
    private ArrayList<EPC> listEPC;
    private SharedPreferences sp;
    private String Ip;
    private String Port;
    private String Power;
    private int Sum;
    private TextView now_power;
    private PowerManager.WakeLock wakeLock;
    private TextView sum_view;

    @Override
    protected void onStart() {
        super.onStart();
        acquireWakeLock();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseWakeLock();
    }

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

    /**
     * 初始化各个组件
     */
    private void initView() {
        sp = this.getSharedPreferences("config",this.MODE_PRIVATE);
        Util.initSoundPool(this);
        IMEI = Util.getIMEI(this);
        start_btn = (Button) findViewById(R.id.start_btn);
        start_btn.setOnClickListener(this);
        setting_btn = (Button) findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(this);
        clean_btn = (Button) findViewById(R.id.clean_btn);
        clean_btn.setOnClickListener(this);
        now_power = (TextView) findViewById(R.id.now_power);
        listEPC = new ArrayList<>();
        Ip = sp.getString("IP","127.0.0.1");
        Port = sp.getString("Port","8080");
        Power = sp.getString("Power","26");
        Sum = sp.getInt("Sum",0);
        now_power.setText(Power+"dBm");
        reader.setOutputPower(Integer.parseInt(Power));
        if(isNet){
            clean_btn.setText(R.string.clean_sum);
        }else {
            clean_btn.setText(R.string.isOpenNet);
            setButtonClickable(start_btn,false);
            setButtonClickable(start_btn,false);
            setButtonClickable(setting_btn,false);
        }
        sum_view = (TextView) findViewById(R.id.txt_num);
        sum_view.setText(String.valueOf(Sum));
    }

    /**
     * 服务器相应之后的回调函数
     */
    Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case sendData.SEND_SUCCESS:
                    Sum+=1;
                    Util.play(1,0);
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

    /**
     * 按钮点击事件
     * @param v 事件对象
     */
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
                    SharedPreferences.Editor edit = sp.edit();
                    edit.putInt("Sum",0);
                    edit.commit();
                    this.finish();
                    System.exit(0);
                }else {
                    isNet = Util.isNetworkConnected(this);
                    if(isNet){
                        clean_btn.setText(R.string.clean_sum);
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

    /**
     * 盘存线程
     */
    private class InventoryThread extends Thread{
//        private volatile byte[] epcbyte;
//        private byte[] passbtye = Tools.HexString2Bytes("00000000");
//        private volatile byte[] tidbyte;
        private List<byte[]> epcList;
        @Override
        public void run() {
            while (true){
                if(startFlag){
//                    epcbyte = reader.readFrom6C(1, 2, 3, this.passbtye);
//                    tidbyte = reader.readFrom6C(2, 0, 6, this.passbtye);
//                    if(epcbyte != null && epcbyte.length > 1 && tidbyte != null && tidbyte.length > 1){
//                        String tidStr = Tools.Bytes2HexString(tidbyte, tidbyte.length);
//                        String epcStr = Tools.Bytes2HexString(epcbyte, epcbyte.length);
//                        addToList(listEPC,epcStr);
//                    }
                    epcList = reader.inventoryRealTime();
                    if(epcList != null && !epcList.isEmpty()){
                        //播放提示音
                        for(byte[] epc:epcList){
                            String epcStr = Tools.Bytes2HexString(epc, epc.length);
                            addToList(listEPC, epcStr);
                        }
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

    private void addToList(final ArrayList<EPC> list, final String epcStr) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //第一次读入数据
                if(list.isEmpty()){
                    EPC epc = new EPC();
                    epc.setEpc(epcStr);
                    epc.setIMEI(IMEI);
                    epc.setDate(getDate());
                    list.add(epc);
                    UpLoadData(epc);
                }else{
                    for(int i = 0; i < list.size(); i++){
                        EPC mEPC = list.get(i);
                        //list中有此EPC
                        if(epcStr.equals(mEPC.getEpc())){
                            list.set(i, mEPC);
                            break;
                        }else if(i == (list.size() - 1)){
                            //list中没有此epc
                            EPC epc = new EPC();
                            epc.setEpc(epcStr);
                            epc.setIMEI(IMEI);
                            epc.setDate(getDate());
                            list.add(epc);
                            UpLoadData(epc);
                        }
                    }
                }
                //将数据添加到ListView
//                listMap = new ArrayList<>();
//                for(EPC epcdata : list){
//                    Map<String, Object> map = new HashMap<>();
//                    map.put("EPC", epcdata.getEpc());
//                    map.put("Date", epcdata.getDate());
//                    listMap.add(map);
//                }
//                listViewData.setAdapter(new SimpleAdapter(MainActivity.this,
//                        listMap, R.layout.listview_item,
//                        new String[]{"EPC", "Date"},
//                        new int[]{R.id.textView_epc, R.id.textView_date}));
                sum_view.setText(String.valueOf(Sum));
                SharedPreferences.Editor edit = sp.edit();
                edit.putInt("Sum",Sum);
                edit.commit();
            }
        });
    }

    //上传数据
    private void UpLoadData(EPC epc) {
        sendData sd = new sendData(handler,Ip,Port,this);
        sd.sendDataToServer(epc);
    }

    /**
     * 获得电源管理对象，让手持机息屏了之后线程不被杀死
     */
    private void acquireWakeLock() {
        if (null == wakeLock) {
            PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
            wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK
                    | PowerManager.ON_AFTER_RELEASE, getClass()
                    .getCanonicalName());
            if (null != wakeLock) {
                wakeLock.acquire();
            }
        }
    }

    /**
     * 电源管理对象资源
     */
    private void releaseWakeLock() {
        if (null != wakeLock && wakeLock.isHeld()) {
            wakeLock.release();
            wakeLock = null;
        }
    }
}
