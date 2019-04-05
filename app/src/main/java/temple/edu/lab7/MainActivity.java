package temple.edu.lab7;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import static temple.edu.lab7.Book.getBook;

public class MainActivity extends AppCompatActivity implements BookListFragment.GetBookInterface {

    String errorMsg;

    Book book;
    Book currentBook;
    ArrayList<String> bookNameArray;
    ArrayList<Book> bookObjList;

    BookDetailsFragment bdf;
    FragmentManager fm;

    boolean singlePane;

    Handler showContent = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {

            JSONObject responseObject = (JSONObject) msg.obj;

            try {
                Log.d("Current response: ", responseObject.toString());
                currentBook = getBook(responseObject);
                Log.d("Current Title", responseObject.getString("title"));
                Log.d("Current Title", currentBook.getTitle());

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

        singlePane = findViewById(R.id.viewPager) != null;
        fm = getSupportFragmentManager();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(new String[]{Manifest.permission.INTERNET, Manifest.permission.ACCESS_NETWORK_STATE}, 1234);


        Thread loadContent = new Thread() {
            @Override
            public void run() {

                if (isNetworkActive()) {

                    URL url;

                    try {
                        url = new URL("https://kamorris.com/lab/audlib/booksearch.php");
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
                        JSONObject books = bookList.getJSONObject(1);
                        bookNameArray = new ArrayList<String>(bookList.length());
                        bookObjList = new ArrayList<Book>(bookList.length());

                        for(int i = 0; i < bookList.length(); i++) {
                            bookNameArray.add(bookList.getJSONObject(i).getString("title"));
                            bookObjList.add(getBook(bookList.getJSONObject(i)));
                        }

                        if(singlePane) {
                            ViewPager vp = findViewById(R.id.viewPager);
                            //for (int i = 0; i < bookArray.length; i++) {
                            //BookDetailsFragment.newInstance(books[i]);
                            //}
                            //BookListFragment blf = new BookListFragment();
                            //fm.beginTransaction().replace(R.id.booklist, blf).commit();

                        } else {

                            BookListFragment blf = BookListFragment.newInstance(bookNameArray);
                            fm.beginTransaction().replace(R.id.fbl, blf).commit();
                            BookDetailsFragment bdf = new BookDetailsFragment();
                            //fm.beginTransaction().replace(R.id.fbd, bdf).commit();

                        }

                        Message msg = Message.obtain();

                        msg.obj = books;

                        showContent.sendMessage(msg);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                } else {
                    Toast.makeText(MainActivity.this, "Please connect to a network", Toast.LENGTH_SHORT).show();

                }
            }
        };
        loadContent.start();

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
