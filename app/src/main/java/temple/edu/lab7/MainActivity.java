package temple.edu.lab7;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Environment;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import edu.temple.audiobookplayer.AudiobookService;

import static temple.edu.lab7.Book.getBook;

public class MainActivity extends AppCompatActivity implements BookListFragment.GetBookInterface, PlayerFragment.PlayerInterface, BookDetailsFragment.bookDetailInterface{

    public static final String DEFAULT_URL = "https://kamorris.com/lab/audlib/booksearch.php";
    String errorMsg;
    String search;
    String searchURL;
    URL url;

    final Object object = new Object();
    final Object object2 = new Object();
    final Object object3 = new Object();

    Book currentBook;
    ArrayList<String> bookNameArray;
    ArrayList<Book> bookObjList;
    List<Fragment> bdfl = new ArrayList<>();
    MyPageAdapter adapter;

    BookDetailsFragment bdf;
    BookListFragment blf;
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
    int currentpos = 0;

    boolean connected;
    AudiobookService.MediaControlBinder binder;

    int downloadBookId;
    //data storage
    SharedPreferences preferences;

    String internalFilename = "myfile";
    File file;
    boolean downloaded = false;
    String savedURL = "https://kamorris.com/lab/audlib/booksearch.php";
    File savedURLFIle;

    int playingBookId;
    int playingPosition;
    int currentPlayingBook;

    Context main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        main = getApplicationContext();

        file = new File(getFilesDir(), internalFilename);
        preferences = getPreferences(MODE_PRIVATE);

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
                binder.pause();
                if(singlePane) {
                    currentBookId = bookObjList.get(vp.getCurrentItem()).getId();
                    Log.d("current item position", String.valueOf(currentBookId));
                }
                currentPlayingBook = currentBookId;

                downloaded = false;
                downloaded = preferences.getBoolean(String.valueOf(currentBookId),false);
                Log.d("downloaded?", String.valueOf(currentBookId) + String.valueOf(downloaded));


                playingPosition = 0;
                playingPosition = preferences.getInt(getRecordBookId(currentBookId), 0);
                seekBar.setProgress(playingPosition);
                if(downloaded) {
                    String fileName = main.getFilesDir() + "/BookDownload/" + String.valueOf(currentBookId) + ".mp3";
                    Log.d("Ready to play",fileName);
                    File audioFIle = new File(fileName);
                    binder.play(audioFIle, playingPosition);
                } else {
                    if (connected) {
                        binder.play(currentBookId, playingPosition);
                    }
                }

                if(singlePane) {
                    playingBookId = vp.getCurrentItem();
                } else
                {
                    playingBookId = blf.getBookSelected();
                }
                Log.d("playingBookId", String.valueOf(playingBookId));

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("playingBookId", playingBookId);
                editor.apply();

            }
        };

        View.OnClickListener onClickPause = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.pause();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("playingPosition", playingPosition);
                editor.apply();

                editor.putInt(getRecordBookId(currentBookId), playingPosition);
                editor.apply();
                Log.d("playingposition", String.valueOf(currentBookId) + String.valueOf(playingPosition));
            }
        };

        View.OnClickListener onClickStop = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binder.stop();
                seekBar.setProgress(0);
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
                currentpos = seekBar.getProgress();
                binder.seekTo(seekBar.getProgress());

                playingPosition = seekBar.getProgress();
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("playingPosition", playingPosition);

                editor.putInt(getRecordBookId(playingBookId), playingPosition);
                editor.apply();


            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1234);

        savedURLFIle = new File(getFilesDir(), "lastURL");
        if (savedURLFIle.exists()) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(savedURLFIle));
                StringBuilder text = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    text.append(line);
                }
                br.close();
                savedURL = text.toString();
                Log.d("savedURL", savedURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        searchURL = savedURL;
        loadContent.start();
        donwloadBook.start();

        if(singlePane) {

            synchronized (object3) {

                try {
                    object3.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                vp = findViewById(R.id.viewPager);

                adapter = new MyPageAdapter(fm, bdfl);
                vp.setAdapter(adapter);

                playingBookId = preferences.getInt("playingBookId",playingBookId);
                vp.setCurrentItem(playingBookId);
                Log.d("playingbookId", String.valueOf(playingBookId));
            }
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edit.getText().toString() != null) {
                    search = edit.getText().toString();
                    searchURL = "https://kamorris.com/lab/audlib/booksearch.php?search=" + search;
                    savedURLFIle.delete();
                    savedURLFIle = new File(getFilesDir(), "lastURL");
                    try {
                        savedURLFIle.createNewFile();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    try {
                        FileOutputStream outputStream = new FileOutputStream(savedURLFIle);
                        outputStream.write(searchURL.getBytes());
                        Log.d("search URL should be", searchURL);
                        outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    if (savedURLFIle.exists()) {
                        try {
                            BufferedReader br = new BufferedReader(new FileReader(savedURLFIle));
                            StringBuilder text = new StringBuilder();
                            String line;
                            while ((line = br.readLine()) != null) {
                                text.append(line);
                            }
                            br.close();
                            savedURL = text.toString();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }

                    Log.d("search URL", savedURL);
                } else {
                    searchURL = DEFAULT_URL;
                }

                synchronized (object) {
                    object.notify();
                }
                if(singlePane) {
                    adapter.notifyDataSetChanged();

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

            synchronized (object3) {
                object3.notify();
            }

            if(singlePane) {

            } else {

                blf = BookListFragment.newInstance(bookNameArray);
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
            currentBookId = newFragment.getId();
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
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("playingPosition", playingPosition);
        editor.apply();
    }

    private void startMusicService(int id) {
        Intent intentService = new Intent(MainActivity.this, AudiobookService.class);
        intentService.putExtra("id", id);
        startService(intentService);
    }

    @Override
    public void play(int id) {
        downloaded = preferences.getBoolean(String.valueOf(id),false);
        if(true) {
            String fileName = main.getFilesDir() + "/BookDownload/" + String.valueOf(id) + ".mp3";
            Log.d("saved", "test");
            File audioFIle = new File(fileName);
            binder.play(audioFIle);
        } else {
            if (connected) {
                binder.play(id);
            }
        }
        playingBookId = id;
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
        playingPosition = 0;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt("playingPosition", playingPosition);
        editor.apply();

        editor.putInt(getRecordBookId(playingBookId), playingPosition);
        editor.apply();
    }

    @Override
    public void jump(int time) {
        if (connected) {
            binder.seekTo(time);
        }
    }

    Thread donwloadBook = new Thread() {
        @Override
        public void run() {

            while(true) {

                synchronized (object2) {
                    try {
                        object2.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (isNetworkActive()) {
                    String urlStr = "https://kamorris.com/lab/audlib/download.php?id=" + String.valueOf(downloadBookId);
                    String fileName = String.valueOf(downloadBookId) + ".mp3";
                    HttpDownloader httpDownloader = new HttpDownloader();
                    httpDownloader.downlaodFile(main, urlStr,fileName);
                } else {
                    Toast.makeText(MainActivity.this, "Please connect to a network", Toast.LENGTH_SHORT).show();
                }
            }
        }
    };

    @Override
    public void downloadBook(int id) {
        downloadBookId = id;
        synchronized (object2) {
            object2.notify();
        }
        downloaded = preferences.getBoolean(String.valueOf(id),false);
        downloaded = true;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(String.valueOf(id), downloaded);
        editor.apply();
        Log.d("check downloaded", String.valueOf(String.valueOf(id) + preferences.getBoolean(String.valueOf(id),false)));

    }

    @Override
    public void deleteBook(int id) {
        String fileName = main.getFilesDir() + "/BookDownload/" + String.valueOf(id) + ".mp3";
        File deletedFile = new File(fileName);
        deletedFile.delete();
        Log.d("Still exists?", fileName + String.valueOf(deletedFile.exists()));
        downloaded = preferences.getBoolean(String.valueOf(id),false);
        downloaded = false;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(String.valueOf(id), downloaded);
        editor.apply();
    }

    private String getRecordBookId(int id) {
        return "BookId" + String.valueOf(id);
    }

}
