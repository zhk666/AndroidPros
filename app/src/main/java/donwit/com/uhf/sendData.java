package donwit.com.uhf;

import android.content.Context;
import android.os.Handler;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.SocketAddress;
import java.net.URL;
import java.net.URLEncoder;

/**
 * @author Admin
 * @version发送数据到服务器
 */
public class sendData {
    private String URLStr;
    private Handler handler;
    private String IP;
    private String Port;
    private HttpURLConnection conn;
    public static final int SEND_SUCCESS=0x123;
    public static final int SEND_FAIL=0x124;
    public static final int CONN_ERROR=0x125;
    private Context mcontext;

    public sendData(Handler handler, String IP, String port,Context context) {
        this.handler = handler;
        this.IP = IP;
        this.Port = port;
        this.URLStr = "http://"+this.IP+":"+this.Port+"/androidConnectionTest/myservlet";
        this.mcontext = context;
    }

    public void sendDataToServer(final EPC epc){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String flag = sendGetReq(epc);
//                String flag = sendPostReq(epc);
                switch (flag){
                    case "success":
                        handler.sendEmptyMessage(SEND_SUCCESS);
                        break;
                    case "fail":
                        handler.sendEmptyMessage(SEND_FAIL);
                        break;
                    case "error":
                        handler.sendEmptyMessage(CONN_ERROR);
                }
            }
        }).start();
    }

    /**
     * get请求
     * @param
     * @return
     */
    private String sendGetReq(EPC epc){
//        string url = http://localhost:8088/androidConnectionTest/myservlet?
        StringBuffer sb = new StringBuffer(URLStr);
        if(epc != null){
            String jsonStr = Util.ObjectToJsonStr(epc);
            //电信代理服务器地址10.0.0.200，端口80（推荐）或9201
            SocketAddress sa = new InetSocketAddress("10.0.0.200",80);
            Proxy proxy = new Proxy(Proxy.Type.HTTP,sa);
            try{
                sb.append("?epcJson="+URLEncoder.encode(jsonStr,"utf-8"));
                if(Util.checkNetworkType(mcontext)){
                    conn=(HttpURLConnection) new URL(sb.toString()).openConnection(proxy);
                }else {
                    conn=(HttpURLConnection) new URL(sb.toString()).openConnection();
                }
                conn.setConnectTimeout(3000);
                conn.setRequestMethod("GET");//设置请求方式为GET
                if(conn.getResponseCode() == 200){
                    return "success";
                }
            }catch (Exception e){
                e.printStackTrace();
                return "error";
            }
        }
        return "fail";
    }

    /**
     * post请求
     * @param epc
     * @return 处理结果
     */
    private String sendPostReq(EPC epc){
        if(epc != null && epc.getEpc().length()>1){
            String jsonStr = Util.ObjectToJsonStr(epc);
            //电信代理服务器地址10.0.0.200，端口80（推荐）或9201
            SocketAddress sa = new InetSocketAddress("10.0.0.200",80);
            Proxy proxy = new Proxy(Proxy.Type.HTTP,sa);
            try{
                URL url = new URL(URLStr);
                if(Util.checkNetworkType(mcontext)){
                    conn=(HttpURLConnection) url.openConnection(proxy);
                }else {
                    conn=(HttpURLConnection) url.openConnection();
                }
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setUseCaches(false);
                conn.setRequestProperty("Connection", "Keep-Alive");
                conn.setRequestProperty("Charset", "UTF-8");
                conn.setRequestProperty("Content-Type","application/json; charset=UTF-8");
                // 设置接收类型否则返回415错误
                //conn.setRequestProperty("accept","*/*")此处为暴力方法设置接受所有类型，以此来防范返回415;
                conn.setRequestProperty("accept","application/json");
                //往服务器写数据
                if(!"".equals(jsonStr)){
                    byte[] writebytes = jsonStr.getBytes();
                    // 设置文件长度
                    conn.setRequestProperty("Content-Length", String.valueOf(writebytes.length));
                    OutputStream outwritestream = conn.getOutputStream();
                    outwritestream.write(jsonStr.getBytes());
                    outwritestream.flush();
                    outwritestream.close();
                }
                if(conn.getResponseCode() == 200){
                    return "success";
                }
            }catch (Exception e){
                e.printStackTrace();
                return "error";
            }
        }
        return "fail";
    }
}
