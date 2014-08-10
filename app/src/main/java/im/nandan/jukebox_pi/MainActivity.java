package im.nandan.jukebox_pi;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.TextView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        createTVS();

    }

    private void createTVS()
    {
        LinearLayout ml = (LinearLayout)findViewById(R.id.mainlayout);

        //Some audio may be explicitly marked as not being music
        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";

        String[] projection = {
                MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DISPLAY_NAME,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST
        };

        Cursor cursor = this.managedQuery(
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                projection,
                selection,
                null,
                null);

        while(cursor.moveToNext()) {
            //Title TextView
            TextView tv = designTV();
            tv.setText(cursor.getString(1));
            ml.addView(tv);

            //Artist TextView
            tv = designTV2();
            tv.setText(cursor.getString(4));
            ml.addView(tv);
        }
    }

    private TextView designTV()
    {
        TextView tv = new TextView(getApplicationContext());
        tv.setTextSize(18);
        tv.setTextColor(Color.BLACK);
        tv.setLines(1);
        return tv;
    }

    private TextView designTV2()
    {
        TextView tv = new TextView(getApplicationContext());
        tv.setTextSize(13);
        tv.setPadding(20,0,0,0);
        tv.setTextColor(Color.GRAY);
        tv.setLines(1);
        return tv;
    }

    private LinearLayout designLL()
    {
        LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setOrientation(LinearLayout.HORIZONTAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));


        return ll;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
