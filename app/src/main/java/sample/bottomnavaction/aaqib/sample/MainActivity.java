package sample.bottomnavaction.aaqib.sample;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void on4Tabs1ActionClicked(View view) {
        invokeTabActionActivity(4, 1);
    }

    public void on4Tabs3ActionClicked(View view) {
        invokeTabActionActivity(4, 3);
    }

    public void on3ActionClicked(View view) {
        invokeTabActionActivity(0, 3);
    }

    public void on1ActionClicked(View view) {
        invokeTabActionActivity(0, 1);
    }

    public void on4TabsClicked(View view) {
        invokeTabActionActivity(4, 0);
    }

    private void invokeTabActionActivity(int numTabs, int numActions) {
        Intent intent = new Intent(this, TabActionActivity.class);
        intent.putExtra(TabActionActivity.PARAM_NUM_TABS, numTabs);
        intent.putExtra(TabActionActivity.PARAM_NUM_ACTIONS, numActions);
        startActivity(intent);
    }
}
