package donwit.com.uhf;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.android.hdhe.uhf.reader.UhfReader;

/**
 * @author Admin
 * @version 设置页面
 **/
public class SettingActivity extends AppCompatActivity implements OnClickListener{

    private Button setting;
    private EditText IP;
    private EditText Port;
    private SharedPreferences sp;
    private Spinner spinner;
    private UhfReader reader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        reader = UhfReader.getInstance();
        if(reader != null){
            initView();
        }
    }

    private void initView() {
        sp = this.getSharedPreferences("config",this.MODE_PRIVATE);
        setting = (Button) findViewById(R.id.setting);
        setting.setOnClickListener(this);
        IP = (EditText) findViewById(R.id.IP_Edit);
        IP.setText(sp.getString("IP","127.0.0.1"));
        Port = (EditText) findViewById(R.id.Port_Edit);
        Port.setText(sp.getString("Port","8080"));
        spinner = (Spinner) findViewById(R.id.sp_power);
        String spSelect = sp.getString("Power","26");
        setSelectValue(spSelect);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.setting:
                String PortStr = Port.getText().toString().trim();
                String IPStr = IP.getText().toString().trim();
                String PowerStr = spinner.getSelectedItem().toString();
                if((!PortStr.isEmpty()) && (!IPStr.isEmpty()) && (!PowerStr.isEmpty())){
                    if(Util.isIpAddress(IPStr)){
                        if(reader.setOutputPower(Integer.parseInt(sp.getString("Power","26")))){
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("IP",IPStr);
                            edit.putString("Port",PortStr);
                            edit.putString("Power",PowerStr);
                            edit.commit();
                            Toast toast = Toast.makeText(this,R.string.setting_success,Toast.LENGTH_SHORT);
                            Util.showMyToast(toast,1000);
                            Intent intent = new Intent(this,MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }else {
                            Toast toast = Toast.makeText(this,R.string.setting_fail,Toast.LENGTH_SHORT);
                            Util.showMyToast(toast,1000);
                        }
                    }else {
                        Toast toast = Toast.makeText(this,R.string.ip_error,Toast.LENGTH_SHORT);
                        Util.showMyToast(toast,1000);
                    }
                }else{
                    Toast toast = Toast.makeText(this,R.string.input_error,Toast.LENGTH_SHORT);
                    Util.showMyToast(toast,1000);
                }
                break;
            default:
                Toast toast = Toast.makeText(this,R.string.click_error,Toast.LENGTH_SHORT);
                Util.showMyToast(toast,1000);
                break;
        }
    }

    private void setSelectValue(String str){
        SpinnerAdapter sa = spinner.getAdapter();
        int item = sa.getCount();
        for (int i = 0; i < item ; i++){
            if(str.equals(sa.getItem(i).toString())){
                spinner.setSelection(i);
                break;
            }
        }
    }

}
