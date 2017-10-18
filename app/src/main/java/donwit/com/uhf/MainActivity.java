package donwit.com.uhf;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import com.android.hdhe.uhf.reader.UhfReader;
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
    private InventoryThread thread;
    private ListView listViewData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reader = UhfReader.getInstance();
        if(reader == null){
            setButtonClickable(start_btn,false);
            setButtonClickable(setting_btn,false);
            setButtonClickable(clean_btn,false);
            Toast.makeText(this,"获取实例失败！",Toast.LENGTH_SHORT).show();
        }else {
            isNet = isNetworkConnected(this);
            initView();
            thread = new InventoryThread(startFlag,reader,listViewData);
            thread.start();
        }
    }

    private void initView() {
        Util.initSoundPool(this);
        IMEI = Util.getIMEI(this);
        start_btn = (Button) findViewById(R.id.start_btn);
        start_btn.setOnClickListener(this);
        setting_btn = (Button) findViewById(R.id.setting_btn);
        setting_btn.setOnClickListener(this);
        clean_btn = (Button) findViewById(R.id.clean_btn);
        clean_btn.setOnClickListener(this);
        listViewData = (ListView) findViewById(R.id.epc_list);
        if(isNet){
            clean_btn.setText(R.string.clean_list);
        }else {
            clean_btn.setText(R.string.isOpenNet);
            setButtonClickable(start_btn,false);
            setButtonClickable(setting_btn,false);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_btn:
                if(!startFlag){
                    startFlag = true;
                    thread.setSuspendFlag(startFlag);
                    start_btn.setText(R.string.end);
                }else {
                    startFlag = false;
                    thread.setSuspendFlag(startFlag);
                    start_btn.setText(R.string.start);
                }
                break;
            case R.id.clean_btn:
                if(isNet){
                    cleanListView();
                }else {
                    isNet = Util.isNetworkConnected(this);
                    if(isNet){
                        clean_btn.setText(R.string.clean_list);
                        setButtonClickable(start_btn,true);
                        setButtonClickable(setting_btn,true);
                    }else {
                        Toast toast = Toast.makeText(this,"请打开网络",Toast.LENGTH_LONG);
                        showMyToast(toast,1000);
                    }
                }
                break;
            default:
                break;
        }
    }

    private void cleanListView() {
        Log.i("MyError", "清除成功");
    }
}
