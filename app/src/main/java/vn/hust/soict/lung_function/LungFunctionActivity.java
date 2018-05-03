package vn.hust.soict.lung_function;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

import vn.hust.soict.lung_function.data.AppData;
import vn.hust.soict.lung_function.model.LungFunction;
import vn.hust.soict.lung_function.utils.FontUtils;
import vn.hust.soict.lung_function.utils.Prompt;

public class LungFunctionActivity extends BaseActivity {

    private LungFunction mLungFunction;

    private String mFormat = "%.2f";

    private TextView mPEF;
    private TextView mFEV1;
    private TextView mFVC;
    private TextView mFEV1divFVC;

    private TextView mFlowLabel;
    private TextView mVolumeLabel;
    private LineChart mChart;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lung_function);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        initContext();
        initView();
        updateUI();
    }

    private void updateUI() {
        mLungFunction = AppData.getInstance().getLungFunction();
        if (mLungFunction == null) {
            Prompt.show(mContext, R.string.msg_no_found_lung_function_exception);
            finish();
        }

        updateChart();

        mPEF.setText(String.format(mFormat, mLungFunction.getPEF()));
        mFEV1.setText(String.format(mFormat, mLungFunction.getFEV1()));
        mFVC.setText(String.format(mFormat, mLungFunction.getFVC()));
        mFEV1divFVC.setText(String.format(mFormat, (mLungFunction.getFEV1()) / mLungFunction.getFVC()));
    }

    @Override
    protected void initView() {
        mPEF = (TextView) findViewById(R.id.tvPEFValue);
        mFEV1 = (TextView) findViewById(R.id.tvFEV1Value);
        mFVC = (TextView) findViewById(R.id.tvFVCValue);
        mFEV1divFVC = (TextView) findViewById(R.id.tvFEV1divFVCValue);

        mFlowLabel = (TextView) findViewById(R.id.tvFlowLable);
        mVolumeLabel = (TextView) findViewById(R.id.tvVolumeLable);
        mChart = (LineChart) findViewById(R.id.chartFlowVolume);

        FontUtils.setFont(mFlowLabel);
        FontUtils.setFont(mVolumeLabel);

        FontUtils.setFont(mPEF);
        FontUtils.setFont(mFEV1);
        FontUtils.setFont(mFVC);
        FontUtils.setFont(mFEV1divFVC);
        FontUtils.setFont(findViewById(R.id.tvPEF));
        FontUtils.setFont(findViewById(R.id.tvFEV1));
        FontUtils.setFont(findViewById(R.id.tvFVC));
        FontUtils.setFont(findViewById(R.id.tvFEV1divFVC));
    }

    private void updateChart() {
        mChart.getDescription().setEnabled(false);
        mChart.setTouchEnabled(false);

        ArrayList<Entry> values = new ArrayList<>();

        List<Float> xData = mLungFunction.getVolume();
        List<Float> yData = mLungFunction.getFlow();
        int length = xData.size();
        if (length > yData.size()) length = yData.size();

        for (int i = 0; i < length; i++) {
            values.add(new Entry(xData.get(i), yData.get(i)));
        }

        LineDataSet set1 = new LineDataSet(values, "Flow_Volume");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(getResources().getColor(R.color.chart_label_line));
        set1.setValueTextColor(Color.GRAY);
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(Color.MAGENTA);
        set1.setHighLightColor(Color.MAGENTA);
        set1.setDrawCircleHole(false);

        // create a data object with the datasets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.YELLOW);
        data.setValueTextSize(9f);

        // set data
        mChart.setData(data);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(10f);
        xAxis.setDrawAxisLine(true);
        xAxis.setDrawGridLines(false);
        xAxis.setTextColor(getResources().getColor(R.color.chart_label_text));
        xAxis.setCenterAxisLabels(false);
        xAxis.setTypeface(FontUtils.getTypeface());
        xAxis.setGranularity(0.5f); // one hour
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format(mFormat, value);
            }
        });

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setTextColor(getResources().getColor(R.color.chart_label_text));
        leftAxis.setDrawGridLines(false);
        leftAxis.setTypeface(FontUtils.getTypeface());
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(mLungFunction.getPEF() + 0.5f);
        leftAxis.setYOffset(-7f);
        leftAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return String.format(mFormat, value);
            }
        });

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);


        mChart.invalidate();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        } else
            return super.onOptionsItemSelected(item);
    }
}
