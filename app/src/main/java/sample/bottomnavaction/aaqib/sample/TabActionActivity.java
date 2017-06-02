package sample.bottomnavaction.aaqib.sample;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.github.aaqib786.bottomnavigationaction.Item;
import com.github.aaqib786.bottomnavigationaction.NavActionBarLayout;
import com.github.aaqib786.bottomnavigationaction.SelectionCallback;

public class TabActionActivity extends AppCompatActivity {

    public static final String PARAM_NUM_ACTIONS = "PARAM_NUM_ACTIONS";
    public static final String PARAM_NUM_TABS = "PARAM_NUM_TABS";

    private NavActionBarLayout navActionBarLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_action);

        navActionBarLayout = (NavActionBarLayout) findViewById(R.id.navActionLayout);

        int numTabs = 4;
        int numActions = 3;
        if (getIntent() != null) {
            numTabs = getIntent().getIntExtra(PARAM_NUM_TABS, 4);
            numActions = getIntent().getIntExtra(PARAM_NUM_ACTIONS, 3);
        }
        setupScreen(numTabs, numActions);
    }

    private void setupScreen(int numTabs, int numActions) {
        navActionBarLayout.setSelectionCallback(selectionCallback);
        switch (numTabs) {
            case 4:
                navActionBarLayout.addTab("Music", R.drawable.ic_music_note_white_24dp);
            case 3:
                navActionBarLayout.addTab("Note", R.drawable.ic_note_white_24dp);
            case 2:
                navActionBarLayout.addTab("Location", R.drawable.ic_location_on_white_24dp);
                navActionBarLayout.addTab("Video", R.drawable.ic_videocam_white_24dp);
        }
        switch (numActions) {
            case 1:
                navActionBarLayout.addAction("Send",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        R.drawable.ic_send_white_24dp);
                break;
            case 2:
                navActionBarLayout.addAction("Action Base",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        R.drawable.ic_change_history_white_24dp);
                navActionBarLayout.addAction("Delete",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        R.drawable.ic_delete_white_24dp);
                navActionBarLayout.addAction("Undo",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        R.drawable.ic_undo_white_24dp);
                break;
            case 3:
                navActionBarLayout.addAction("Action Base",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        R.drawable.ic_change_history_white_24dp);
                navActionBarLayout.addAction("Delete",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        R.drawable.ic_delete_white_24dp);
                navActionBarLayout.addAction("Send",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        R.drawable.ic_send_white_24dp);
                navActionBarLayout.addAction("Undo",
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
                        R.drawable.ic_undo_white_24dp);
                break;
        }
    }

    SelectionCallback selectionCallback = new SelectionCallback() {
        @Override
        public void onTabSelected(Item item, int containerResID) {
            setFragment(item.name, containerResID);
        }

        @Override
        public void onActionSelected(Item item, int containerResID) {
            Toast.makeText(TabActionActivity.this, "ACTION [" + item.name + "]", Toast.LENGTH_SHORT).show();
        }
    };

    private void setFragment(String itemName, int containerResID) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(containerResID, DummyFragment.newInstance(itemName));
        ft.commit();
    }
}
