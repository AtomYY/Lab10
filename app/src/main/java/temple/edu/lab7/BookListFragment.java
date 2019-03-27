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



public class BookListFragment extends Fragment {

    ListView listView;

    Context parent;

    public BookListFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_book_list, container, false);
        listView = v.findViewById(R.id.listView);

        listView.setAdapter(new ArrayAdapter<>(parent, android.R.layout.simple_list_item_1,
                parent.getResources().getStringArray(R.array.books)));

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
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface GetBookInterface {
        void bookSelected(String bookName);
    }
}
