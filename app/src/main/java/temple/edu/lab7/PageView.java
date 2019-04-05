package temple.edu.lab7;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;


public class PageView extends Fragment {
    MyPageAdapter pageAdapter;
    public PageView() {
        // Required empty public constructor
    }

    private ArrayList<Fragment> fragments;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_page_view, container, false);
        pageAdapter = new MyPageAdapter(getFragmentManager(), fragments);
        ViewPager vp = v.findViewById(R.id.viewPager);
        vp.setAdapter(pageAdapter);
        return v;
    }

}
