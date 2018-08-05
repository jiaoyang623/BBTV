package guru.ioio.alpha;

import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

public class BaseActivity extends AppCompatActivity {
    protected void show(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
