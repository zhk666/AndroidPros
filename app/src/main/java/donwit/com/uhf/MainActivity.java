package donwit.com.uhf;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(this,"呵呵",Toast.LENGTH_SHORT).show();

        for (int j = 0; j < 10; j++){
            Toast.makeText(this,"呵呵",Toast.LENGTH_SHORT).show();
        }
    }
}
