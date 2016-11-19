package com.woting.ui.mine.myupload.http;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.woting.R;
import com.woting.common.config.GlobalConfig;
import com.woting.ui.mine.myupload.model.FileContentInfo;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.nio.charset.Charset;
import java.util.List;

/**
 * Multipart 上传
 */
public class HttpMultipartPost extends AsyncTask<String, Integer, String> {
    private Context context;
    private List<String> filePathList;

    private ProgressDialog pd;
    private TextView textProgress;
    private ProgressBar progressBar;

    private long totalSize;

    public HttpMultipartPost(Context context, List<String> filePathList) {
        this.context = context;
        this.filePathList = filePathList;
    }

    @Override
    protected void onPreExecute() {
        View progressView = LayoutInflater.from(context).inflate(R.layout.progress_dialog_view, null);
        textProgress = (TextView) progressView.findViewById(R.id.text_progress);
        progressBar = (ProgressBar) progressView.findViewById(R.id.pb_progressbar);
        pd = new ProgressDialog(context);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pd.setCancelable(false);
        pd.show();
        pd.setContentView(progressView);
    }

    @Override
    protected String doInBackground(String... params) {
        String serverResponse = null;
        HttpClient httpClient = new DefaultHttpClient();
        HttpContext httpContext = new BasicHttpContext();
        HttpPost httpPost = new HttpPost(GlobalConfig.uploadWorksFileUrl);

        try {
            CustomMultipartEntity multipartContent = new CustomMultipartEntity(new CustomMultipartEntity.ProgressListener() {
                @Override
                public void transferred(long num) {
                    publishProgress((int) ((num / (float) totalSize) * 100));
                }
            });

            // 把上传内容添加到MultipartEntity
            for (int i = 0; i < filePathList.size(); i++) {
                multipartContent.addPart("ContentFile", new FileBody(new File(filePathList.get(i))));
                multipartContent.addPart("data", new StringBody(filePathList.get(i), Charset.forName(org.apache.http.protocol.HTTP.UTF_8)));
            }
            totalSize = multipartContent.getContentLength();
            System.out.println("totalSize:=========" + totalSize);
            httpPost.setEntity(multipartContent);
            HttpResponse response = httpClient.execute(httpPost, httpContext);
            serverResponse = EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverResponse;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        progressBar.setProgress(progress[0]);
        if(progress[0] < 100) {
            textProgress.setText("文件上传中... " + progress[0] + "%");
        } else {
            textProgress.setText("系统处理中请稍等...");
        }
    }

    @Override
    protected void onPostExecute(String result) {
        System.out.println("result: " + result);
        pd.dismiss();
        if(result == null || result.equals("null") || result.equals("")) {
            uploadFail();
            return ;
        }
        try {
            JSONTokener jsonParser = new JSONTokener(result);
            JSONObject arg1 = (JSONObject) jsonParser.nextValue();
            String ful = arg1.getString("ful");
            List<FileContentInfo> fileContentInfo = new Gson().fromJson(ful, new TypeToken<List<FileContentInfo>>() {}.getType());
            if(fileContentInfo.get(0).getSuccess().equals("TRUE")) {
                String filePath = fileContentInfo.get(0).getStoreFilepath();
//                ((UploadWorksMainActivity)context).finishUpload(filePath);// 上传完成回到原界面
            } else {
                uploadFail();
            }
        } catch (Exception e) {
            e.printStackTrace();
            uploadFail();
        }
    }

    private void uploadFail() {
        ProgressDialog uploadFailDialog = new ProgressDialog(context);
        uploadFailDialog.setCancelable(true);
        uploadFailDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        uploadFailDialog.show();
        uploadFailDialog.setContentView(R.layout.dialog_upload_fail);
    }

    @Override
    protected void onCancelled() {
        System.out.println("cancel");
    }
}
