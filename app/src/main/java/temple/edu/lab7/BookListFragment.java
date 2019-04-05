package temple.edu.lab7;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;


public class BookListFragment extends Fragment {

    ListView listView;
    ArrayList<String> bookList;
    GetBookInterface gbi;
    Context parent;

    public static final String BOOK_KEY = "book_list";

    public BookListFragment() {
        // Required empty public constructor
    }

    public static BookListFragment newInstance(ArrayList<java.lang.String> bookList) {
        BookListFragment blf = new BookListFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(BOOK_KEY, bookList);

        return blf;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            bookList = getArguments().getParcelable(BOOK_KEY);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);
        listView = v.findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<String>(parent, R.layout.support_simple_spinner_dropdown_item, bookList));

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parentView, View view, int position, long id) {
                String bookName = (String) parentView.getItemAtPosition(position);
                ((GetBookInterface) parent).bookSelected(bookName);
            }
        });

        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.parent = context;
        gbi = (GetBookInterface)context;
    }


    public interface GetBookInterface {
        void bookSelected(String bookName);
    }
}
