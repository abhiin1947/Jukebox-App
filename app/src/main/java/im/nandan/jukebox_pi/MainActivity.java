package im.nandan.jukebox_pi;

import android.app.ActionBar;
import android.app.Activity;
import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;


public class MainActivity extends Activity {

    final String IP_ADDRESS = "192.168.0.100";
    final int port = 8087;

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
            /*//Title TextView
            TextView tv = designTV();
            tv.setText(cursor.getString(1));
            tv.setOnClickListener(designClick(cursor.getString(3)));
            ml.addView(tv);

            //Artist TextView
            tv = designTV2();
            tv.setText(cursor.getString(4));
            tv.setOnClickListener(designClick(cursor.getString(3)));*/

            final String songname = cursor.getString(1);
            final String artistname = cursor.getString(4);
            final String datauri = cursor.getString(3);

            LinearLayout ll = designLL(songname, artistname, datauri);

            ml.addView(ll);
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

    private View.OnClickListener designClick(final String data)
    {
        View.OnClickListener ocl = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startTheMusic(data);
            }
        };
        return ocl;
    }

    private LinearLayout designLL(String tv1text, String tv2text, String datauri)
    {
        LinearLayout ll = new LinearLayout(getApplicationContext());
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        ll.setPadding(10,2,0,2);

        TextView tv = designTV();
        tv.setText(tv1text);
        ll.addView(tv);

        //Artist TextView
        tv = designTV2();
        tv.setText(tv2text);
        ll.addView(tv);

        ll.setOnClickListener(designClick(datauri));

        return ll;
    }

    public void startTheMusic(String filepath)
    {
        NetworkFileTransfer nft = new NetworkFileTransfer();
        nft.execute(filepath);
    }

    private class NetworkFileTransfer extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String filepath = strings[0];

            try {
                Socket s = new Socket(IP_ADDRESS, port);
                if(s.isConnected())
                {
                    FileInputStream fis = new FileInputStream(new File(filepath));
                    OutputStream os = s.getOutputStream();

                    byte[] headers = new byte[38000];
                    byte[] data = new byte[512];

                    fis.read(headers);
                    os.write(headers);

                    while(fis.read(data) != -1)
                    {
                        os.write(data);
                    }
                    s.close();
                }
                else
                {
                    Log.e("Not Connected", "Connection the pi has failed");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
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
