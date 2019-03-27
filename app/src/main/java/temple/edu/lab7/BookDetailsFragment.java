package temple.edu.lab7;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BookDetailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BookDetailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class BookDetailsFragment extends Fragment {

    TextView text;
    String bookName = "";

    public static final String BOOK_KEY = "book_name";

    public BookDetailsFragment() {
        // Required empty public constructor
    }

    public static BookDetailsFragment newInstance(String bookName) {
        BookDetailsFragment bdf = new BookDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BOOK_KEY, bookName);

        bdf.setArguments(bundle);
        return bdf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookName = getArguments().getString(BOOK_KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_details, container, false);
        text = v.findViewById(R.id.textView);

        change(bookName);

        return v;
    }

    public void changeBook(String bookName) {
        change(bookName);
    }

    private void change (String bookName) {

    }
}
