package org.bitoo.abit.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;

import org.bitoo.abit.R;
import org.bitoo.abit.mission.Mission;
import org.bitoo.abit.mission.MissionSQLiteHelper;
import org.bitoo.abit.ui.custom.HidingScrollListener;
import org.bitoo.abit.ui.custom.MissionListAdapter;

import java.io.FileNotFoundException;
import java.util.List;

public class HomeFragment extends Fragment implements View.OnClickListener {
    private final static String TAG = "HomeFragment";

    private MissionSQLiteHelper sqLiteHelper;
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Mission> missions;
    private FloatingActionButton addButton;

    public HomeFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.v(TAG, "create view");
        View view = inflater.inflate(R.layout.fragment_main, container, false);
        addButton = (FloatingActionButton) view.findViewById(R.id.bt_add);
        addButton.setOnClickListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.rv_mission_list);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        sqLiteHelper = new MissionSQLiteHelper(getActivity().getApplication());
        missions = sqLiteHelper.loadMissions(false);
        adapter = new MissionListAdapter(getActivity(), missions);
        recyclerView.setAdapter(adapter);
        recyclerView.addOnScrollListener(new HidingScrollListener(getActivity()) {
            @Override
            public void onMoved(int d) {
                ((MainActivity) getActivity()).moveToolbar(d);
            }

            @Override
            public void onShow() {
                ((MainActivity) getActivity()).showToolbar();
            }

            @Override
            public void onHide() {
                ((MainActivity) getActivity()).hideToolbar();
            }
        });
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.v(TAG, "activity created");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.v(TAG, "create");
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(getActivity(), AddMissionActivity.class);
        startActivityForResult(intent, MainActivity.REQUEST_IS_NEW);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case MainActivity.REQUEST_IS_DELETE :
                if(resultCode == Activity.RESULT_OK) {
                    long id = data.getLongExtra(MainActivity.ACTION_ID_DELETED, -1);
                    if(id != -1) {
                        for(Mission mission : missions) {
                            if (mission.getId() == id) {
                                missions.remove(mission);
                                break;
                            }
                        }
                    }
                }
                break;
            case MainActivity.REQUEST_IS_NEW :
                if(resultCode == Activity.RESULT_OK) {
                    try {
                        Mission newMission = new Mission(getActivity(),
                                data.getStringExtra(MainActivity.ACTION_NEW_MISSION_TITLE),
                                System.currentTimeMillis(),
                                data.getStringExtra(MainActivity.ACTION_NEW_MISSION_XML_PATH),
                                data.getStringExtra(MainActivity.ACTION_NEW_MISSION_MOTTO),
                                data.getStringExtra(MainActivity.ACTION_NEW_MISSION_THEME_IMG_PATH));
                        newMission.setId(sqLiteHelper.addMission(newMission));
                        missions.add(newMission);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                    //missions.add(newMission);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
        addButton.setTranslationY(250);
        addButton.animate()
                .translationY(0)
                .setDuration(400)
                .setInterpolator(new DecelerateInterpolator(1.f))
                .setStartDelay(400);
        Log.v(TAG, "resume");

    }
}
