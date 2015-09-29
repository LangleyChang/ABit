package org.bitoo.abit.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;

import org.bitoo.abit.R;
import org.bitoo.abit.ui.custom.BitmapViewPagerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A placeholder fragment containing a simple view.
 */
public class AddMissionFragment extends Fragment implements View.OnClickListener{
    private final static String TAG = "AddMissionFragment";
    private ViewPager viewPager;
    private List<String> xmlPaths;
    private Button saveButton;
    private Button slcButton;
    private EditText titleText;
    private EditText mottoText;
    private String themeImagePath = null;
    private SimpleDraweeView themePreview;

    int currentPage = 0;

    public AddMissionFragment() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_mission, container, false);
        titleText = (EditText) view.findViewById(R.id.et_ms_title);
        mottoText = (EditText) view.findViewById(R.id.et_ms_second_title);
        saveButton = (Button) view.findViewById(R.id.bt_save);
        slcButton = (Button) view.findViewById(R.id.bt_selc);
        themePreview = (SimpleDraweeView) view.findViewById(R.id.dv_theme_prev);
        saveButton.setOnClickListener(this);
        slcButton.setOnClickListener(this);

        viewPager = (ViewPager) view.findViewById(R.id.vp_prev);
        xmlPaths = new ArrayList<>();

        // TODO add these xml paths automatically
        xmlPaths.add("mario.xml");
        xmlPaths.add("pacmonster.xml");
        viewPager.setAdapter(new BitmapViewPagerAdapter(getActivity(), xmlPaths));

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPage = position;
               // Log.d(TAG, String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onClick(View v) {

        if(v.getId() == saveButton.getId()) {
            boolean okayFlag = true;
            String title = titleText.getText().toString();
            String motto = mottoText.getText().toString();

            if(title.length() > 20 || title.length() < 1) {
                Toast.makeText(getActivity(), "Title is invalid", Toast.LENGTH_SHORT).show();
                okayFlag = false;
            }
            else if(motto.length() > 30 || motto.length() < 1) {
                Toast.makeText(getActivity(), "Motto is invalid", Toast.LENGTH_SHORT).show();
                okayFlag = false;
            } else if(themeImagePath == null) {
                Toast.makeText(getActivity(), "Theme Image is not selected", Toast.LENGTH_SHORT).show();
                okayFlag = false;
            }
            if(okayFlag) {
                Intent intent = new Intent();
                intent.putExtra(MainActivity.ACTION_NEW_MISSION_TITLE, title);
                intent.putExtra(MainActivity.ACTION_NEW_MISSION_XML_PATH, xmlPaths.get(viewPager.getCurrentItem()));
                intent.putExtra(MainActivity.ACTION_NEW_MISSION_MOTTO, motto);
                intent.putExtra(MainActivity.ACTION_NEW_MISSION_THEME_IMG_PATH, themeImagePath);

                getActivity().setResult(Activity.RESULT_OK, intent);
                getActivity().finish();
            }
        } else if(v.getId() == slcButton.getId()) {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, AddMissionActivity.REQUEST_SELECT_THEME_IMAGE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == AddMissionActivity.REQUEST_SELECT_THEME_IMAGE && resultCode == Activity.RESULT_OK) {// FIXME why -1?
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getActivity().getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            themeImagePath = picturePath;

            themePreview.setImageURI(Uri.fromFile(new File(picturePath)));
            //themePreviewLayout.setBackground(new BitmapDrawable(getResources(), BitmapFactory.decodeFile(picturePath)));

        }
    }
}
