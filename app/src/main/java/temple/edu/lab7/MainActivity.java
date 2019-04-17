package temple.edu.lab7;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import edu.temple.audiobookplayer.AudiobookService;

import static temple.edu.lab7.Book.getBook;

public class MainActivity extends AppCompatActivity implements BookListFragment.GetBookInterface, PlayerFragment.PlayerInterface{

    public static final String DEFAULT_URL = "https://kamorris.com/lab/audlib/booksearch.php";
    String errorMsg;
    String search;
    String searchURL;
    URL url;

    final Object object = new Object();

    Book currentBook;
    ArrayList<String> bookNameArray;
    ArrayList<Book> bookObjList;
    List<Fragment> bdfl = new ArrayList<>();

    BookDetailsFragment bdf;
    PlayerFragment playerFragment;
    FragmentManager fm;
    ViewPager vp;

    boolean singlePane;

    EditText edit;
    Button button;

    Button play;
    Button pause;
    Button stop;
    SeekBar seekBar;

    int currentBookId;

    boolean connected;
    AudiobookService.MediaControlBinder binder;


    Handler showContent = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            JSONObject responseObject = (JSONObject) msg.obj;

            try {
                currentBook = getBook(responseObject);
            } catch (Exception e) {
                e.printStackTrace();
            }

            updateViews();

            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        edit = findViewById(R.id.search);
        button = findViewById(R.id.confirmSearch);
        singlePane = findViewById(R.id.viewPager) != null;
        fm = getSupportFragmentManager();

        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);
        seekBar = findViewById(R.id.seekBar);

        play.setText("play");
        pause.setText("pause");
        stop.setText("stop");

        View.OnClickListener onClickPlay = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(singlePane) {
                    currentBookId = (vp.getCurrentItem() + 1);
                }
                binder.play(currentBookId);
            }
        };

        View.OnClickListener onClickPause = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.pause();
            }
        };

        View.OnClickListener onClickStop = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.stop();
            }
        };

        play.setOnClickListener(onClickPlay);
        pause.setOnClickListener(onClickPause);
        stop.setOnClickListener(onClickStop);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(seekBar.getProgress());
                binder.seekTo(seekBar.getProgress());
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1234);

        searchURL = DEFAULT_URL;
        loadContent.start();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit.getText().toString() != null) {
                    search = edit.getText().toString();
                    searchURL = "https://kamorris.com/lab/audlib/booksearch.php?search=" + search;
                    Log.d("search URL", searchURL);
                } else {
                    searchURL = DEFAULT_URL;
                }
                synchronized (object) {
                    object.notify();
                }
            }
        });

        //playerFragment = new PlayerFragment();
        //fm.beginTransaction().replace(R.id.player_main, playerFragment);
    }

    Thread loadContent = new Thread() {
        @Override
        public void run() {

            while(true) {
                if (isNetworkActive()) {
                    work(searchURL);
                    } else {
                        Toast.makeText(MainActivity.this, "Please connect to a network", Toast.LENGTH_SHORT).show();
                    }
                synchronized (object) {
                    try {
                        object.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };


    public void work(String inputURL) {
        try {
            url = new URL(inputURL);
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(
                            url.openStream()));

            String response = "", tmpResponse;

            tmpResponse = reader.readLine();
            while (tmpResponse != null) {
                response = response + tmpResponse;
                tmpResponse = reader.readLine();
            }

            JSONArray bookList = new JSONArray(response);
            bookNameArray = new ArrayList<String>(bookList.length());
            bookObjList = new ArrayList<Book>(bookList.length());

            for(int i = 0; i < bookList.length(); i++) {
                bookNameArray.add(bookList.getJSONObject(i).getString("title"));
                bookObjList.add(getBook(bookList.getJSONObject(i)));
            }

            bdfl = new ArrayList<Fragment>(bookObjList.size());
            for (int i = 0; i < bookObjList.size(); i++) {
                bdfl.add(BookDetailsFragment.newInstance(bookObjList.get(i)));
            }

            if(singlePane) {
                vp = findViewById(R.id.viewPager);

                MyPageAdapter adapter = new MyPageAdapter(fm,bdfl);
                vp.setAdapter(adapter);
            } else {

                BookListFragment blf = BookListFragment.newInstance(bookNameArray);
                fm.beginTransaction().replace(R.id.fbl, blf).commit();
                BookDetailsFragment bdf = new BookDetailsFragment();
                //fm.beginTransaction().replace(R.id.fbd, bdf).commit();

            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean isNetworkActive() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnected();
    }

    private void updateViews() {
        //display.setText(currentBook.getTitle());
    }

    @Override
    public void bookSelected(int id) {
        if (singlePane) {

        } else{
            BookDetailsFragment newFragment = BookDetailsFragment.newInstance(bookObjList.get(id));
            currentBookId = id;
            fm.beginTransaction()
                    .replace(R.id.fbd, newFragment)
                    .commit();
        }
    }


    //service related

    ServiceConnection myConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            binder = (AudiobookService.MediaControlBinder) service;

            connected = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            connected =false;
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = new Intent(this, AudiobookService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onStop(){
        super.onStop();
        if(connected) {
            unbindService(myConnection);
        }
    }

    private void startMusicService(int id) {
        Intent intentService = new Intent(MainActivity.this, AudiobookService.class);
        intentService.putExtra("id", id);
        startService(intentService);
    }

    @Override
    public void play(int id) {
        if(connected) {
            binder.play(id);
        }
    }

    @Override
    public void pause() {
        if (connected) {
            binder.pause();
        }
    }

    @Override
    public void stop() {
        if(connected) {
            binder.stop();
        }
    }

    @Override
    public void jump(int time) {
        if (connected) {
            binder.seekTo(time);
        }
    }
}
