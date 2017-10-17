package donwit.com.uhf;

import android.content.Context;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Button;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author Admin
 * @version 工具类
 */
public class Util {
    public static SoundPool sp ;
    public static Map<Integer, Integer> suondMap;
    public static Context context;

    //初始化声音池
    public static void initSoundPool(Context context){
        Util.context = context;
        sp = new SoundPool(1, AudioManager.STREAM_MUSIC, 1);
        suondMap = new HashMap<Integer, Integer>();
        suondMap.put(1, sp.load(context, R.raw.msg, 1));
    }

    //播放声音池声音
    public static  void play(int sound, int number){
        AudioManager am = (AudioManager)Util.context.getSystemService(Util.context.AUDIO_SERVICE);
        //返回当前AudioManager对象的音量值
        float audioCurrentVolume = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        sp.play(
                suondMap.get(sound), //播放的音乐Id
                audioCurrentVolume, //左声道音量
                audioCurrentVolume, //右声道音量
                1, //优先级，0为最低
                number, //循环次数，0无不循环，-1无永远循环
                1);//回放速度，值在0.5-2.0之间，1为正常速度
    }

    //设置Toast时间
    public static void showMyToast(final Toast toast, final int cnt) {
        final Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                toast.show();
            }
        }, 0, 3500);
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                toast.cancel();
                timer.cancel();
            }
        }, cnt );
    }

    //判断是否有网络
    public static   boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected())
            {
                // 当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED)
                {
                    // 当前所连接的网络可用
                    return true;
                }
            }
        }
        return false;
    }

    //设置按钮是否可用
    public static void setButtonClickable(Button button, boolean flag){
        button.setClickable(flag);
        if(flag){
            button.setTextColor(Color.BLACK);
        }else{
            button.setTextColor(Color.GRAY);
        }
    }
}