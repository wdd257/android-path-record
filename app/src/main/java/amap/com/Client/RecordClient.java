package amap.com.Client;

import amap.com.record.TraceRecordDTO;
import android.util.Log;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import okhttp3.*;
import okhttp3.internal.http2.Header;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecordClient {
    public static final MediaType JSON_TYPE = MediaType.get("application/json; charset=utf-8");

    private static OkHttpClient client;

    private static String saveUrl;
    private static String getUrl;

    static {
        try {
            client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url("https://blog.nowcoder.net/n/75b109cee06e4600975de2a234f527dc")
                    .build();
            Response response = client.newCall(request).execute();
            String data = response.body().string();
            String urls = data.split("\\[where-are-you]")[1];
            saveUrl = urls.split("\\|")[0];
            getUrl = urls.split("\\|")[1];
        } catch (IOException e) {
            Log.e("IOException:", e.getMessage());
        }
    }

    public RecordClient() {
        /**
         * https://blog.nowcoder.net/n/75b109cee06e4600975de2a234f527dc
         */
        //init();
    }

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
