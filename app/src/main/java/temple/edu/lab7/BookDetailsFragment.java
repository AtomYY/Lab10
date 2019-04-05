package temple.edu.lab7;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;


public class BookDetailsFragment extends Fragment {

    Book book;
    String bookTitle;

    TextView title;
    TextView author;
    TextView published;
    TextView duration;
    ImageView cover;

    public static final String BOOK_KEY = "book_name";

    /*public static final String BOOK_ID = "book_id";
    public static final String BOOK_TITLE = "book_title";
    public static final String BOOK_AUTHOR = "book_author";
    public static final String BOOK_DURATION = "book_duration";
    public static final String BOOK_PUBLISHED = "book_published";
    public static final String BOOK_COVER = "book_cover";*/

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance(Book book) {

        BookDetailsFragment bdf = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BOOK_KEY, book);

        bdf.setArguments(bundle);
        Log.d("book retrieved: ", book.getTitle());
        return bdf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getParcelable(BOOK_KEY);
            //book = new Book(getArguments().getInt(BOOK_ID), getArguments().getString(BOOK_TITLE),getArguments().getString(BOOK_AUTHOR),getArguments().getInt(BOOK_DURATION),getArguments().getInt(BOOK_PUBLISHED),getArguments().getString(BOOK_COVER));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_details, container, false);
        title = v.findViewById(R.id.title);
        author = v.findViewById(R.id.author);
        published = v.findViewById(R.id.published);
        duration = v.findViewById(R.id.duration);
        cover = v.findViewById(R.id.cover);

        try {
            changeBook(book);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return v;
    }

    public void changeBook(Book book) throws IOException {
        title.setText(book.getTitle());
        author.setText("Author" + book.getAuthor());
        published.setText("Published: " + String.valueOf(book.getPublished()));
        duration.setText("Duration" + String.valueOf(book.getDuration()));

        URL imgURL = new URL(book.getCoverURL());
        //Bitmap image = BitmapFactory.decodeStream((InputStream) imgURL.openConnection().getContent());
        //cover.setImageBitmap(image);
    }
}
