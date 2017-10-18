package donwit.com.uhf;

import android.util.Log;
import android.widget.ListView;

import com.android.hdhe.uhf.reader.Tools;
import com.android.hdhe.uhf.reader.UhfReader;

import java.util.ArrayList;
import java.util.Map;

import static android.content.ContentValues.TAG;

/**
 * @author Admin
 * @version 扫描线程
 */
public class InventoryThread extends Thread {
    private ArrayList<EPC> listEPC;
    private ArrayList<Map<String, Object>> listMap;
    private byte[] epcbyte;
    private byte[] passbtye = Tools.HexString2Bytes("00000000");
    private byte[] tidbyte;
    private volatile boolean suspendFlag;
    private UhfReader reader;
    private ListView epcList;

    public  InventoryThread(boolean startFlag, UhfReader reader, ListView view){
        this.reader = reader;
        this.suspendFlag = startFlag;
        this.epcList = view;
    }
    @Override
    public void run() {
      while (true){
          if(suspendFlag){
              epcbyte = reader.readFrom6C(1, 2, 3, this.passbtye);
              tidbyte = reader.readFrom6C(2, 0, 6, this.passbtye);
              Log.i(TAG, "run: sdwasdasdasd");

          }
          try {
              Thread.sleep(50);
          } catch (InterruptedException e) {
              e.printStackTrace();
          }
      }
    }

    public void setSuspendFlag(boolean suspendFlag) {
        this.suspendFlag = suspendFlag;
    }
}
