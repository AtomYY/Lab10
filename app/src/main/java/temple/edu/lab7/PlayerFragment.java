package temple.edu.lab7;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;


public class PlayerFragment extends Fragment {

    Context parent;
    PlayerInterface pi;

    Button play;
    Button pause;
    Button stop;
    SeekBar seekBar;

    int bookId;

    public PlayerFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static PlayerFragment newInstance() {
        PlayerFragment fragment = new PlayerFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player, container, false);
        play = v.findViewById(R.id.play);
        pause = v.findViewById(R.id.pause);
        stop = v.findViewById(R.id.stop);
        seekBar = v.findViewById(R.id.seekBar);

        View.OnClickListener onClickPlay = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlayerInterface)parent).play(bookId);
            }
        };

        View.OnClickListener onClickPause = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlayerInterface)parent).pause();
            }
        };

        View.OnClickListener onClickStop = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((PlayerInterface)parent).stop();
            }
        };

        play.setOnClickListener(onClickPlay);
        pause.setOnClickListener(onClickPause);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                seekBar.setProgress(seekBar.getProgress());
                ((PlayerInterface)parent).jump(seekBar.getProgress());
            }
        });
        return v;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.parent = context;
        pi = (PlayerInterface) context;

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public interface PlayerInterface {
        void play(int id);
        void pause();
        void stop();
        void jump(int time);
    }

    public void setBook(int id) {
        bookId = id;
    }

}
