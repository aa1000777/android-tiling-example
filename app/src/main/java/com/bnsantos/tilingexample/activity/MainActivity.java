package com.bnsantos.tilingexample.activity;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.bnsantos.tilingexample.App;
import com.bnsantos.tilingexample.fragment.PagePickerDialog;
import com.bnsantos.tilingexample.R;

import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends ActionBarActivity implements PagePickerListener{
    private static final String TAG = MainActivity.class.getSimpleName();
    private ListView mFiles;
    private ArrayAdapter<String> mAdapter;

    private String mFileId;
    private int mPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
        initAdapter();
    }

    private void initView(){
        mFiles = (ListView) findViewById(R.id.filesListView);
        mFiles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mFileId = mAdapter.getItem(position);
                PagePickerDialog pagePickerDialog = PagePickerDialog.newInstance(mFileId);
                pagePickerDialog.setListener(MainActivity.this);
                pagePickerDialog.show(getSupportFragmentManager(), "PAGE_PICKER");
            }
        });
    }

    private void initAdapter(){
        App.getService().retrieveFiles()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<String>>() {
                    @Override
                    public void call(List<String> files) {
                        mAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, files);
                        mFiles.setAdapter(mAdapter);
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, "Error retrieving files", throwable);
                        Toast.makeText(MainActivity.this, R.string.error_files, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void startTileActivity(){
        TileActivity.start(this, mFileId, mPage);
        finish();
    }

    @Override
    public void setPage(int page) {
        mPage = page;
        startTileActivity();
    }
}
