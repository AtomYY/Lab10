package temple.edu.lab7;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;


public class BookDetailsFragment extends Fragment {

    Book book;

    TextView tile;
    TextView author;
    TextView published;
    ImageView cover;

    public static final String BOOK_KEY = "book_name";

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance(Book book) {


        BookDetailsFragment bdf = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(BOOK_KEY, book);

        bdf.setArguments(bundle);
        return bdf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            book = getArguments().getParcelable(BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_details, container, false);
        tile = v.findViewById(R.id.title);
        author = v.findViewById(R.id.author);
        published = v.findViewById(R.id.published);
        cover = v.findViewById(R.id.cover);

        return v;
    }

    public void changeBook(Book book) throws IOException {
        tile.setText(book.getTitle());
        author.setText(book.getAuthor());
        published.setText(book.getPublished());

        URL imgURL = new URL(book.getCoverURL());
        Bitmap image = BitmapFactory.decodeStream(imgURL.openConnection().getInputStream());
        cover.setImageBitmap(image);
    }
}
