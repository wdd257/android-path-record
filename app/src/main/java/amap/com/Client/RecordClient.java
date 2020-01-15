package amap.com.Client;

import amap.com.record.TraceRecordDTO;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import okhttp3.*;

import java.io.IOException;
import java.util.List;

public class RecordClient {
    public static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client = new OkHttpClient();

    private static String saveUrl = "http://3.115.241.36:8088/saveRecord";
    private static String getUrl = "http://3.115.241.36:8088/getRecord";
    private static String getInterval = "http://3.115.241.36:8088/getInterval";

    private static long defaultInterval = 120000;


    static {
        try {
            client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://blog.nowcoder.net/n/75b109cee06e4600975de2a234f527dc")
                    .build();
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            String host = data.split("\\[where-are-you]")[1];
            saveUrl = "http://" + host + ":8088/saveRecord";
            getUrl = "http://" + host + ":8088/getRecord";
            getInterval = "http://" + host + ":8088/getInterval";
        } catch (IOException e) {
            Log.e("IOException:", e.getMessage());
        }
    }

    public static long getInterval() {
        try {
            Request request = new Request.Builder()
                    .url(getInterval)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return defaultInterval;
            }
            long interval = Long.parseLong(response.body().string());
            return interval <= 0 ? defaultInterval : interval;
        } catch (IOException e) {
            Log.e("IOException:", e.getMessage());
            return defaultInterval;
        }
    }

//    public static void main(String[] args) {
//       System.out.println(getInterval());
//    }

    public static int doSaveRecord(List<TraceRecordDTO> traceRecordDTOList) {
        try {
            RequestBody body = RequestBody.create(JSON.toJSONString(traceRecordDTOList), JSON_TYPE);
            Request request = new Request.Builder()
                    .url(saveUrl)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            if (!response.isSuccessful()) {
                return -1;
            }
        } catch (IOException e) {
            Log.e("IOException:", e.getMessage());
            return -1;
        }
        return 1;
    }

    public static String doGetRecord(QueryParam queryParam) {
        String res = "";
        try {
            RequestBody body = RequestBody.create(JSON.toJSONString(queryParam), JSON_TYPE);
            Request request = new Request.Builder()
                    .url(getUrl)
                    .post(body)
                    .build();
            Response response = client.newCall(request).execute();
            res = response.body().string();
        } catch (IOException e) {
            Log.e("IOException:", e.getMessage());
        }
        return res;
    }

}
