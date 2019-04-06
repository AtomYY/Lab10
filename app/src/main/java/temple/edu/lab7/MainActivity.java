package temple.edu.lab7;

import android.Manifest;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import static temple.edu.lab7.Book.getBook;

public class MainActivity extends AppCompatActivity implements BookListFragment.GetBookInterface {

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
    FragmentManager fm;

    boolean singlePane;

    EditText edit;
    Button button;

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
                ViewPager vp = findViewById(R.id.viewPager);

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
            //bdf.changeBook(bookName);
            BookDetailsFragment newFragment = BookDetailsFragment.newInstance(bookObjList.get(id));
            fm.beginTransaction()
                    .replace(R.id.fbd, newFragment)
                    .commit();
        }
    }
}
