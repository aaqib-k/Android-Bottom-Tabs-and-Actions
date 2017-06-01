package sample.bottomnavaction.aaqib.sample;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class DummyFragment extends Fragment {

    private static final String PARAM_TAB_NAME = "PARAM_TAB_NAME";

    private String mTabName;

    public DummyFragment() {
        // Required empty public constructor
    }

    public static DummyFragment newInstance(String tabName) {
        DummyFragment fragment = new DummyFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_TAB_NAME, tabName);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mTabName = getArguments().getString(PARAM_TAB_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dummy, container, false);
        ((TextView) view.findViewById(R.id.tv_intro)).setText("TAB [" + mTabName + "]");
        return view;
    }

}
