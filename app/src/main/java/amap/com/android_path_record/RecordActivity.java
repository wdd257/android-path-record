package amap.com.android_path_record;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import amap.com.database.DbAdapter;
import amap.com.record.PathRecord;
import android.widget.Toast;

/**
 * 所有轨迹list展示activity
 */
public class RecordActivity extends Activity implements OnItemClickListener {

    private RecordAdapter mAdapter;
    private ListView mAllRecordListView;
    private DbAdapter mDataBaseHelper;
    private List<PathRecord> mAllRecord = new ArrayList<PathRecord>();
    public static final String RECORD_ID = "record_id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.recordlist);
            mAllRecordListView = (ListView) findViewById(R.id.recordlist);
            mDataBaseHelper = new DbAdapter();
            while (true) {
                searchAllRecordFromDB();
                if (mAllRecord != null && !mAllRecord.isEmpty()) {
                    break;
                }
                Toast.makeText(this, "记录查询中...", Toast.LENGTH_SHORT)
                        .show();
            }
            mAdapter = new RecordAdapter(this, mAllRecord);
            mAllRecordListView.setAdapter(mAdapter);
            mAllRecordListView.setOnItemClickListener(this);
        } catch (Exception e) {
            Toast.makeText(RecordActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
        Toast.makeText(RecordActivity.this, mAllRecord.toString(), Toast.LENGTH_SHORT)
                .show();
    }

    private Runnable searchAllRecord = new Runnable() {
        @Override
        public void run() {
            try {
                mAllRecord = mDataBaseHelper.queryRecordAll();
            } catch (Exception e) {
                Toast.makeText(RecordActivity.this, e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
            }
        }
    };

    private void searchAllRecordFromDB() throws InterruptedException {
        Thread thread = new Thread(searchAllRecord);
        thread.start();
        thread.join();
    }

    public void onBackClick(View view) {
        this.finish();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        try {
            PathRecord recorditem = (PathRecord) parent.getAdapter().getItem(
                    position);
            Intent intent = new Intent(RecordActivity.this,
                    RecordShowActivity.class);
            intent.putExtra(RECORD_ID, recorditem.getmId());
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }
}
