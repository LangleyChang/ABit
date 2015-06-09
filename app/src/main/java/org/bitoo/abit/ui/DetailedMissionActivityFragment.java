package org.bitoo.abit.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.Toast;

import com.gc.materialdesign.views.ButtonFloatSmall;

import org.bitoo.abit.R;
import org.bitoo.abit.mission.image.Mission;
import org.bitoo.abit.mission.image.Tweet;
import org.bitoo.abit.ui.custom.BitMapAdapter;
import org.bitoo.abit.utils.MissionSQLiteHelper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Date;


/**
 * Activities that contain this fragment must implement the
 * {@link DetailedMissionActivityFragment.OnItemSelectedListener} interface
 * to handle interaction events.
 * Use the {@link DetailedMissionActivityFragment#getInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailedMissionActivityFragment extends Fragment {
    private static final String TAG = "ImageFramentDemo";
    private MissionSQLiteHelper sqlHelper;
    private GridView mGridView;
    private ButtonFloatSmall checkButton;
    private OnItemSelectedListener mListener;
    private Mission mission;
    private BitMapAdapter bitmapAdapter;
    Toolbar toolbar;


    private static final String COLOR_KEY = "img_pixel";

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment DetailedMissionActivityFragment.
     */
    public static DetailedMissionActivityFragment getInstance() {
        DetailedMissionActivityFragment fragment = new DetailedMissionActivityFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public DetailedMissionActivityFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        /**
         * Context here is identical to that in {@link MissionSQLiteHelper}
         * Global context required.
         */
        sqlHelper = new MissionSQLiteHelper(getActivity().getApplicationContext());
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image_display, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        toolbar = (Toolbar) getActivity().findViewById(R.id.tb_main);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity)getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        checkButton = (ButtonFloatSmall) getActivity().findViewById(R.id.bt_check);

        try {
            long id = getActivity().getIntent().getLongExtra(MainActivity.MISSION_ID, 0);
            mission = sqlHelper.loadMission(id);
            if(mission == null)
                throw new FileNotFoundException();

            toolbar.setSubtitle(mission.getMotto());

            bitmapAdapter = new BitMapAdapter(getActivity(), mission);
            mGridView = (GridView)getActivity().findViewById(R.id.gv_prog_image);
            mGridView.setNumColumns(mission.getProgressImage().getWidth());
            mGridView.setAdapter(bitmapAdapter);
        } catch (FileNotFoundException e) {
            Toast.makeText(getActivity(), "Load Image source error.", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "Load Image source error.");
            e.printStackTrace();
        }

        if(mission.check()) {
            checkButton.setClickable(true);
            checkButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (mission.check()) {
                        int position = mission.updateProgress(new Date(System.currentTimeMillis()));
                        if(position >= 0) {
                            sqlHelper.updateProgress(mission);
                            try {
                                mission.addTweet(new Tweet(position, "Hello"));
                                bitmapAdapter.notifyDataSetChanged();
                                checkButton.setClickable(false);
                            } catch (IOException e) {
                                Toast.makeText(getActivity(), "Error when add Tweet", Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnItemSelectedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnItemSelectedListener {
        // TODO: Update argument type and name
        public void onItemSelected(int position);
    }

    public void deleteMission() {
        sqlHelper.deleteMission(mission.getId());
    }

    public long getMissionId() {
        return mission.getId();
    }

}