package temple.edu.lab7;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import static android.provider.ContactsContract.CommonDataKinds.Website.URL;


public class BookDetailsFragment extends Fragment {

    Book book;
    int bookId;

    TextView title;
    TextView author;
    TextView published;
    TextView duration;
    ImageView cover;

    Button download;
    Button delete;

    bookDetailInterface bdi;
    Context parent;

    public static final String BOOK_KEY = "book_name";

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
        download = v.findViewById(R.id.download);
        delete = v.findViewById(R.id.delete);

        try {
            changeBook(book);
        } catch (IOException e) {
            e.printStackTrace();
        }

        download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bdi.downloadBook(book.getId());
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return v;
    }

    public void changeBook(Book book) throws IOException {
        bookId = book.getId();
        title.setText(book.getTitle());
        author.setText("Author" + book.getAuthor());
        published.setText("Published: " + String.valueOf(book.getPublished()));
        duration.setText("Duration: " + String.valueOf(book.getDuration()));

        new DownloadImageFromInternet(cover)
                .execute(book.getCoverURL());
    }

    private class DownloadImageFromInternet extends AsyncTask<String, Void, Bitmap> {
        ImageView imageView;

        public DownloadImageFromInternet(ImageView imageView) {
            this.imageView = imageView;
        }

        protected Bitmap doInBackground(String... urls) {
            String imageURL = urls[0];
            Bitmap bimage = null;
            try {
                InputStream in = new java.net.URL(imageURL).openStream();
                bimage = BitmapFactory.decodeStream(in);

            } catch (Exception e) {
                Log.e("Error Message", e.getMessage());
                e.printStackTrace();
            }
            return bimage;
        }

        protected void onPostExecute(Bitmap result) {
            imageView.setImageBitmap(result);
        }
    }

    public int returnId() {
        return bookId;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.parent = context;
        bdi = (bookDetailInterface)parent;
    }

    public interface bookDetailInterface {
        void downloadBook(int id);
        void deleteBook();
    }

}
