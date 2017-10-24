package donwit.com.uhf;

import android.content.Context;
import android.os.Handler;

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
    private Context context;

    public sendData(Handler handler, String IP, String port,Context context) {
        this.handler = handler;
        this.IP = IP;
        this.Port = port;
        this.URLStr = "http://"+this.IP+":"+this.Port+"/androidConnectionTest/myservlet";
        this.context = context;
    }

    public void sendDataToServer(final EPC epc){
        new Thread(new Runnable() {
            @Override
            public void run() {
                String flag = sendGetReq(epc,"utf-8",context);
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
    private String sendGetReq(EPC epc, String encoding, Context context){
//        string url = http://localhost:8088/androidConnectionTest/myservlet?
        StringBuffer sb = new StringBuffer(URLStr);
        String jsonStr = Util.ObjectToJsonStr(epc);
        //电信代理服务器地址10.0.0.200，端口80（推荐）或9201
        SocketAddress sa = new InetSocketAddress("10.0.0.200",80);
        Proxy proxy = new Proxy(Proxy.Type.HTTP,sa);
        try{
            sb.append("?epcJson="+URLEncoder.encode(jsonStr,encoding));
            if(Util.checkNetworkType(context)){
                conn=(HttpURLConnection) new URL(sb.toString()).openConnection(proxy);
            }else {
                conn=(HttpURLConnection) new URL(sb.toString()).openConnection();
            }
            conn.setConnectTimeout(3000);
            conn.setRequestMethod("GET");//设置请求方式为GET
            if(conn.getResponseCode() == 200){
                return "success";
            }
            return "fail";
        }catch (Exception e){
            e.printStackTrace();
            return "error";
        }
    }

//    private String sendPostReq(Map<String, String> param,String encoding){
//        StringBuffer sb = new StringBuffer(URLStr);
//        try {
//            if (!URLStr.equals("")&!param.isEmpty()) {
//                sb.append("?");
//                for (Map.Entry<String, String>entry:param.entrySet()) {
//                    sb.append(entry.getKey()+"=");
//                    sb.append(URLEncoder.encode(entry.getValue(), encoding));
//                    sb.append("&");
//                }
//                sb.deleteCharAt(sb.length()-1);//删除字符串最后 一个字符“&”
//            }
//            byte[]data=sb.toString().getBytes();
//            conn=(HttpURLConnection) new URL(URLStr).openConnection();
//            conn.setConnectTimeout(5000);
//            conn.setRequestMethod("POST");//设置请求方式为POST
//            conn.setDoOutput(true);//允许对外传输数据
//            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");// 设置窗体数据编码为名称/值对
//            conn.setRequestProperty("Content-Length", data.length+"");
//            OutputStream outputStream=conn.getOutputStream();//打开服务器的输入流
//            outputStream.write(data);//将数据写入到服务器的输出流
//            outputStream.flush();
//            outputStream.close();
//            if(conn.getResponseCode() == 200){
//                return "success";
//            }
//            return "fail";
//        }catch (Exception e){
//            e.printStackTrace();
//            return "error";
//        }
//    }
}
