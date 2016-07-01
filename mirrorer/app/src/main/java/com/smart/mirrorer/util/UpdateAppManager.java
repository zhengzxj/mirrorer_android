package com.smart.mirrorer.util;

/**
 * Created by zhengfei on 16/5/27.
 */
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.smart.mirrorer.R;
import com.smart.mirrorer.base.BaseApplication;

public class UpdateAppManager {
    // 文件分隔符
    private static final String FILE_SEPARATOR = "/";
    // 外存sdcard存放路径
    private static final String FILE_PATH = Environment.getExternalStorageDirectory() + FILE_SEPARATOR +"autoupdate" + FILE_SEPARATOR;
    // 下载应用存放全路径
    private static final String FILE_NAME = FILE_PATH + "miyuzheupdate.apk";
    // 更新应用版本标记
    private static final int UPDARE_TOKEN = 0x29;
    // 准备安装新版本应用标记
    private static final int INSTALL_TOKEN = 0x31;

    private Context context;
    private String title = "检测到有新版本发布，建议您更新！";
    private String desc = "更新内容! \\n 1.添加历史纪录入口 \\n 2.优化视频质量 \\n 3.缩短呼叫等待延迟时长";
    // 以华为天天聊hotalk.apk为例
    private String spec;
    // 下载应用的对话框
    private Dialog dialog;
    // 下载应用的进度条
    private ProgressBar progressBar;
    // 进度条的当前刻度值
    private int curProgress;
    // 用户是否取消下载
    private boolean isCancel;

    AlertDialog myDialog;

    public UpdateAppManager(Context context) {
        this.context = context;
    }

    private final Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDARE_TOKEN:
//                    Log.i("lzm","下载中"+curProgress);
                    progressBar.setProgress(curProgress);
                    break;

                case INSTALL_TOKEN:
                    installApp();
                    break;
            }
        }
    };

    /**
     * 检测应用更新信息
     */
    public void checkUpdateInfo(String url,String title,String desc) {
        spec = url;
        this.title = title;
        this.desc = desc;
        Log.i("lzm","checkUpdataInfo");
        showNoticeDialog();
    }

    /**
     * 显示提示更新对话框
     */
    private void showNoticeDialog() {
//        new AlertDialog.Builder(context)
//                .setTitle("软件版本更新")
//                .setMessage(message)
//                .setPositiveButton("下载", new OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                        showDownloadDialog();
//                    }
//                }).setNegativeButton("以后再说", new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        }).create().show();

        myDialog = new AlertDialog.Builder(context).create();
        myDialog.show();
        myDialog.getWindow().setContentView(R.layout.dialog_apk_update_notice);
        TextView tv_title = (TextView)myDialog.getWindow().findViewById(R.id.tv_update_title);
        tv_title.setText(title);
        TextView tv_desc = (TextView)myDialog.getWindow().findViewById(R.id.tv_update_desc);
        tv_desc.setText(desc);
        myDialog.getWindow().findViewById(R.id.button_apk_download_yes).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
                showDownloadDialog();
            }
        });
        myDialog.getWindow().findViewById(R.id.button_apk_download_no).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDialog.dismiss();
            }
        });
    }



    /**
     * 显示下载进度对话框
     */
    private void showDownloadDialog() {
//        View view = LayoutInflater.from(context).inflate(R.layout.apk_update_progressbar, null);
//        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("版本更新");
//        builder.setNegativeButton("取消", new OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//                isCancel = true;
//            }
//        });
        dialog = builder.create();
        dialog.show();
        dialog.getWindow().setContentView(R.layout.dialog_apk_update_progressbar);
        progressBar = (ProgressBar)dialog.getWindow().findViewById(R.id.pb_apk_update);
        TextView tv_cancle = (TextView)dialog.getWindow().findViewById(R.id.tv_update_cancle);
        tv_cancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                isCancel = true;
            }
        });
        downloadApp();
    }

    /**
     * 下载新版本应用
     */
    private void downloadApp() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("lzm","开始下载");
                URL url = null;
                InputStream in = null;
                FileOutputStream out = null;
                HttpURLConnection conn = null;
                try {
                    url = new URL(spec);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.connect();
                    long fileLength = conn.getContentLength();
                    in = conn.getInputStream();
                    File filePath = new File(FILE_PATH);
                    if(!filePath.exists()) {
                        filePath.mkdir();
                    }
                    out = new FileOutputStream(new File(FILE_NAME));
                    byte[] buffer = new byte[1024];
                    int len = 0;
                    long readedLength = 0l;
                    while((len = in.read(buffer)) != -1) {
                        // 用户点击“取消”按钮，下载中断
                        if(isCancel) {
                            Log.i("lzm","下载取消");
                            break;
                        }
                        out.write(buffer, 0, len);
                        readedLength += len;
                        curProgress = (int) (((float) readedLength / fileLength) * 100);
                        handler.sendEmptyMessage(UPDARE_TOKEN);
                        if(readedLength >= fileLength) {
                            dialog.dismiss();
                            // 下载完毕，通知安装
                            handler.sendEmptyMessage(INSTALL_TOKEN);
                            break;
                        }
                    }
                    out.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if(out != null) {
                        try {
                            out.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(in != null) {
                        try {
                            in.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if(conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }

    /**
     * 安装新版本应用
     */
    private void installApp() {
        File appFile = new File(FILE_NAME);
        if(!appFile.exists()) {
            return;
        }
        // 跳转到新版本应用安装页面
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse("file://" + appFile.toString()), "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    //获取版本号
    public int getVersionCode()
    {
        try {
            return BaseApplication.getInstance().getPackageManager().getPackageInfo("com.smart.mirrorer", 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e("lzm","获取本地版本号失败:"+e.getMessage());
        }
        return -1;
    }
}