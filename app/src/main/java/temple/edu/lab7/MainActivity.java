package temple.edu.lab7;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity implements BookListFragment.GetBookInterface {

    BookDetailsFragment bdf;
    FragmentManager fm;

    boolean singlePane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        singlePane = findViewById(R.id.container_2) == null;
        bdf = new BookDetailsFragment();
        fm = getSupportFragmentManager();

        fm.beginTransaction().replace(R.id.container_1, new MasterFragment()).commit();

        if(!singlePane) {
            fm.beginTransaction().replace(R.id.container_2, df).commit();
        }
    }

    @Override
    public void bookSelected(String bookName) {

    }
}
