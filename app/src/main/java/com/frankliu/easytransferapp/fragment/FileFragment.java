package com.frankliu.easytransferapp.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.frankliu.easytransferapp.R;
import com.frankliu.easytransferapp.adapter.FileAdapter;
import com.frankliu.easytransferapp.adapter.OnItemClickListener;
import com.frankliu.easytransferapp.utils.Config;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

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

    private final String[][] MATCH_ARRAY={
            //{后缀名，    文件类型}
            {".3gp",    "video/3gpp"},
            {".apk",    "application/vnd.android.package-archive"},
            {".asf",    "video/x-ms-asf"},
            {".avi",    "video/x-msvideo"},
            {".bin",    "application/octet-stream"},
            {".bmp",      "image/bmp"},
            {".c",        "text/plain"},
            {".class",    "application/octet-stream"},
            {".conf",    "text/plain"},
            {".cpp",    "text/plain"},
            {".doc",    "application/msword"},
            {".docx",    "application/msword"},
            {".xls",    "application/msword"},
            {".xlsx",    "application/msword"},
            {".exe",    "application/octet-stream"},
            {".gif",    "image/gif"},
            {".gtar",    "application/x-gtar"},
            {".gz",        "application/x-gzip"},
            {".h",        "text/plain"},
            {".htm",    "text/html"},
            {".html",    "text/html"},
            {".jar",    "application/java-archive"},
            {".java",    "text/plain"},
            {".jpeg",    "image/jpeg"},
            {".jpg",    "image/jpeg"},
            {".js",        "application/x-javascript"},
            {".log",    "text/plain"},
            {".m3u",    "audio/x-mpegurl"},
            {".m4a",    "audio/mp4a-latm"},
            {".m4b",    "audio/mp4a-latm"},
            {".m4p",    "audio/mp4a-latm"},
            {".m4u",    "video/vnd.mpegurl"},
            {".m4v",    "video/x-m4v"},
            {".mov",    "video/quicktime"},
            {".mp2",    "audio/x-mpeg"},
            {".mp3",    "audio/x-mpeg"},
            {".mp4",    "video/mp4"},
            {".mpc",    "application/vnd.mpohun.certificate"},
            {".mpe",    "video/mpeg"},
            {".mpeg",    "video/mpeg"},
            {".mpg",    "video/mpeg"},
            {".mpg4",    "video/mp4"},
            {".mpga",    "audio/mpeg"},
            {".msg",    "application/vnd.ms-outlook"},
            {".ogg",    "audio/ogg"},
            {".pdf",    "application/pdf"},
            {".png",    "image/png"},
            {".pps",    "application/vnd.ms-powerpoint"},
            {".ppt",    "application/vnd.ms-powerpoint"},
            {".prop",    "text/plain"},
            {".rar",    "application/x-rar-compressed"},
            {".rc",        "text/plain"},
            {".rmvb",    "audio/x-pn-realaudio"},
            {".rtf",    "application/rtf"},
            {".sh",        "text/plain"},
            {".tar",    "application/x-tar"},
            {".tgz",    "application/x-compressed"},
            {".txt",    "text/plain"},
            {".wav",    "audio/x-wav"},
            {".wma",    "audio/x-ms-wma"},
            {".wmv",    "audio/x-ms-wmv"},
            {".wps",    "application/vnd.ms-works"},
            {".xml",    "text/plain"},
            {".z",        "application/x-compress"},
            {".zip",    "application/zip"},
            {"",        "*/*"}
    };

    private OnItemClickListener onItemClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(int position) {
            openFileByPath(getActivity(), adapter.getDatas().get(position).getAbsolutePath());
        }

        @Override
        public void onItemLongClick(int position) {

        }
    };

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
        adapter.setOnItemClickListener(onItemClickListener);
        return rootView;
    }


    private void openFileByPath(Context context, String path) {
        if(context==null||path==null)
            return;
        Intent intent = new Intent();
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        //文件的类型
        String type = "";
        for(int i =0;i < MATCH_ARRAY.length;i++){
            //判断文件的格式
            if(path.contains(MATCH_ARRAY[i][0])){
                type = MATCH_ARRAY[i][1];
                break;
            }
        }
        try {
            File out = new File(path);
            Uri fileURI;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                // 由于7.0以后文件访问权限，可以通过定义xml在androidmanifest中申请，也可以直接跳过权限
                // 通过定义xml在androidmanifest中申请
//                fileURI = FileProvider.getUriForFile(context,
//                        "com.lonelypluto.zyw_test.provider",
//                        out);
                // 直接跳过权限
                StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
                StrictMode.setVmPolicy(builder.build());
                fileURI = Uri.fromFile(out);
            }else{
                fileURI = Uri.fromFile(out);
            }
            //设置intent的data和Type属性
            intent.setDataAndType(fileURI, type);
            //跳转
            if (context.getPackageManager().resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY) != null) {
                context.startActivity(intent);
            } else {
                Toast.makeText(context, "没有找到对应的程序", Toast.LENGTH_SHORT).show();
            }

        } catch (Exception e) { //当系统没有携带文件打开软件，提示
            Toast.makeText(context, "无法打开该格式文件", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }




}
