package com.frankliu.easytransferapp.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frankliu.easytransferapp.R;
import com.frankliu.easytransferapp.adapter.FileAdapter;
import com.frankliu.easytransferapp.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class FileFragment extends Fragment {

    private final String TAG = FileFragment.class.getSimpleName();

    @BindView(R.id.rv_file)
    RecyclerView rvFile;

    private FileAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_file, container, false);
        ButterKnife.bind(this, rootView);
        adapter = new FileAdapter(null);
        rvFile.setLayoutManager(new LinearLayoutManager(getActivity()));
        rvFile.setItemAnimator(new DefaultItemAnimator());
        rvFile.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        rvFile.setAdapter(adapter);
        Observable.create(new ObservableOnSubscribe<ArrayList<File>>() {
            @Override
            public void subscribe(ObservableEmitter<ArrayList<File>> emitter) throws Exception {
                File dir = new File(Config.fileSaveDir);
                File[] files = dir.listFiles();
                ArrayList<File> res = new ArrayList<>(Arrays.asList(files));
                emitter.onNext(res);
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<ArrayList<File>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(ArrayList<File> files) {
                        adapter.updateData(files);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        return rootView;
    }
}
