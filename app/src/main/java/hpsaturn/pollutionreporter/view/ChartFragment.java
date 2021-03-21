package hpsaturn.pollutionreporter.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.MultiSelectListPreference;
import androidx.preference.PreferenceManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.hpsaturn.tools.DeviceUtil;
import com.hpsaturn.tools.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import hpsaturn.pollutionreporter.Config;
import hpsaturn.pollutionreporter.MainActivity;
import hpsaturn.pollutionreporter.R;
import hpsaturn.pollutionreporter.common.Storage;
import hpsaturn.pollutionreporter.models.SensorData;
import hpsaturn.pollutionreporter.models.SensorTrack;
import hpsaturn.pollutionreporter.models.SensorTrackInfo;

/**
 * Created by Antonio Vanegas @hpsaturn on 6/30/18.
 */
public class ChartFragment extends Fragment {

    public static String TAG = ChartFragment.class.getSimpleName();

    @BindView(R.id.lc_measures)
    LineChart chart;

    @BindView(R.id.tv_chart_name)
    TextView chart_name;

    @BindView(R.id.tv_chart_date)
    TextView chart_date;

    @BindView(R.id.tv_chart_desc)
    TextView chart_desc;

    @BindView(R.id.tv_chart_loc)
    TextView chart_loc;

    @BindView(R.id.rl_separator)
    RelativeLayout rl_separator;

    private long referenceTimestamp;
    private boolean loadingData = true;

    private static final String KEY_RECORD_ID = "key_record_id";
    private String recordId;
    private SensorTrack track;

    private List<ChartVar> variables = new ArrayList<>();

    private List<ILineDataSet> dataSets = new ArrayList<>();

    private Map<String,String> map = new HashMap<>();

    public static ChartFragment newInstance() {
        return new ChartFragment();
    }

    public static ChartFragment newInstance(String recordId) {
        ChartFragment fragment = new ChartFragment();
        Bundle args = new Bundle();
        args.putString(KEY_RECORD_ID,recordId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_chart, container, false);
        ButterKnife.bind(this, view);

        Description description = new Description();
        description.setTextColor(getResources().getColor(R.color.black));
        description.setText(getString(R.string.app_name));
        description.setTextSize(16f);
        description.setTextAlign(Paint.Align.RIGHT);

        chart.setDescription(description);
        chart.setNoDataText(getString(R.string.msg_chart_loading));

        // Display the axis on the left (contains the labels 1*, 2* and so on)
        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTH_SIDED);
        xAxis.setEnabled(true);
        // Time reference for XAxis
        calculateReferenceTime();
        // Current variables supported
        String[] labels = getResources().getStringArray(R.array.pref_vars_entries);
        String[] types = getResources().getStringArray(R.array.pref_vars_values);
        // Hash map of types and labels (it will not change)
        for (int i = 0; i < labels.length ; i++){
            map.put(types[i],labels[i]);
        }

        loadSelectedVariables();

        Bundle args = getArguments();
        if(args!=null){
            recordId = args.getString(KEY_RECORD_ID) ;
            Logger.i(TAG,"[CHART] recordId: "+recordId);
        }

        return view;
    }

    public void loadSelectedVariables(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getMain());
        Set<String> values = preferences.getStringSet(getString(R.string.key_setting_vars), null);

        variables.clear();

        Logger.i(TAG, "[CHART] selected values:");

        for (String type : values) {
            ChartVar var = new ChartVar(getContext(), type, map.get(type));
            variables.add(var);
            Logger.i(TAG, "[CHART]"+type);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Logger.i(TAG,"[CHART] starting load data thread..");
        requireActivity().runOnUiThread(this::loadData);
    }

    private void calculateReferenceTime(){
        ArrayList<SensorData> data = Storage.getSensorData(getActivity());
        if (data.isEmpty()) {
            referenceTimestamp = System.currentTimeMillis() / 1000;
        } else {
            referenceTimestamp = data.get(0).timestamp;
        }
        HourAxisValueFormatter xAxisFormatter = new HourAxisValueFormatter(referenceTimestamp);
        XAxis xAxis = chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
    }


    public void addData(SensorData data) {
        if (!loadingData) {
            Long currentTime = System.currentTimeMillis() / 1000;
            long time = currentTime - referenceTimestamp;
            addValue(time,data);
            refreshDataSets();
        }
    }

    private void addData(ArrayList<SensorData> data){
        if(data==null)return;
        else if (!data.isEmpty()) {
            Iterator<SensorData> it = data.iterator();
            while (it.hasNext()) {
                SensorData d = it.next();
                long time = d.timestamp - referenceTimestamp;
                addValue(time,d);
            }

            refreshDataSets();
        }
        loadingData = false;
    }

    private void addValue(long time, SensorData data) {
        Iterator<ChartVar> it = variables.iterator();
        while (it.hasNext()){
            it.next().addValue(time,data);
        }
    }

    private void refreshDataSets() {
        dataSets.clear();

        Iterator<ChartVar> it = variables.iterator();

        while (it.hasNext()){
            ChartVar var = it.next();
            var.refresh();
            dataSets.add(var.dataSet);
        }

        LineData linedata = new LineData(dataSets);

        chart.setData(linedata);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    private void loadData() {
        Logger.i(TAG,"[CHART] loading data..");
        loadingData = true;
        ArrayList<SensorData> data = new ArrayList<>();
        if(recordId==null) {
            Logger.i(TAG,"[CHART] loading current data in storage..");
            data = Storage.getSensorData(getActivity());
        }
        else {
            track = Storage.getTrack(getActivity(), recordId);
            if(track!=null) {
                Logger.i(TAG,"[CHART] loading track data from storage..");
                data = track.data;
                setTrackDescription(track);
                getMain().enableShareButton();
            }
            else{
                Logger.i(TAG,"[CHART] loading track from firebase..");
                DatabaseReference trackRef = getMain().getDatabase().child(Config.FB_TRACKS_DATA).child(recordId);
                trackRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        SensorTrack track = dataSnapshot.getValue(SensorTrack.class);
                        if(track!=null){
                            Logger.i(TAG,"[CHART] loading track on chart..");
                            addData(track.data);
                            setTrackDescription(track);
                        }
                        else{
                            Logger.e(TAG,"[CHART] onDataChange getValue is null");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Logger.e(TAG,"[CHART] onCancelled, databaseError: "+databaseError.getDetails());
                    }
                });
            }
        }
        addData(data);
    }

    private void setTrackDescription(SensorTrack track){
        chart_name.setText(track.getName());
        chart_date.setText(track.getDate());
        chart_desc.setText(""+track.size+" points");
        rl_separator.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        getMain().disableShareButton();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void clearData() {
        Logger.w(TAG, "[CHART] clear recorded data and chart..");
        Iterator<ChartVar> it = variables.iterator();
        while (it.hasNext()){
            it.next().clear();
        }
        chart.clear();
        Storage.setSensorData(getActivity(), new ArrayList<>());
        calculateReferenceTime();
    }

    public void shareAction(){
        if(recordId!=null && track!=null) {
            Logger.i(TAG,"publis track..");
            track.deviceId = DeviceUtil.getDeviceId(getActivity());
            getMain().getDatabase().child(Config.FB_TRACKS_DATA).child(track.name).setValue(track);
            getMain().getDatabase().child(Config.FB_TRACKS_INFO).child(track.name).setValue(new SensorTrackInfo(track));
            getMain().popBackLastFragment();
        }
    }

    private MainActivity getMain(){
        return (MainActivity)getActivity();
    }

}
