package amap.client.service;

import amap.client.Client.QueryParam;
import amap.client.Client.RecordClient;
import amap.client.record.TraceRecordDTO;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RecordService {
    static TypeReference<List<TraceRecordDTO>> recordListType = new TypeReference<List<TraceRecordDTO>>() {
    };

    public static int retryTimes = 3; //保存记录重试次数
    public static int batchGetSize = 50;    //批量请求条数
    public static int batchGetSizeAll = -1;    //批量请求条数
    public static int batchSaveSize = 1;    //批量提交条数

    public static QueryParam queryParam;
    public static String resData;


    private static CopyOnWriteArrayList<TraceRecordDTO> traceData = new CopyOnWriteArrayList<>();

    public static List<TraceRecordDTO> getAllTraceRecordHistory() {
        QueryParam param = new QueryParam();
        param.setBathSize(-1);
        String records = RecordClient.doGetRecord(param);
        List<TraceRecordDTO> recordDTOS = JSON.parseObject(records, recordListType);
        return recordDTOS;
    }

    public static List<TraceRecordDTO> getTraceRecordHistory(QueryParam param) {
        List<TraceRecordDTO> recordDTOS = null;
        try {
            queryParam = param;
            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    resData = RecordClient.doGetRecord(queryParam);
                }
            });
            thread.start();
            thread.join();

            recordDTOS = JSON.parseObject(resData, recordListType);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return recordDTOS;
    }

    public static TraceRecordDTO getTraceRecordHistoryById(int id) {
        QueryParam param = new QueryParam();
        param.setId(id);
        String records = RecordClient.doGetRecord(param);
        List<TraceRecordDTO> recordDTOS = JSON.parseObject(records, recordListType);
        if (recordDTOS == null) {
            return null;
        }
        return recordDTOS.get(0);
    }

    public static void saveTraceRecordHistory(List<TraceRecordDTO> records) {
        int retry = retryTimes;
        while (retry > 0) {
            try {
                int res = RecordClient.doSaveRecord(records);
                if (res > 0) {
                    records.clear();
                    break;
                }
                retry--;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 网络操作相关的子线程
     */
    static Runnable saveTraceTask = new Runnable() {
        @Override
        public void run() {
            saveTraceRecordHistory(traceData);
        }
    };

    public static void addTraceRecord(TraceRecordDTO recordDTO) {
        if (batchSaveSize <= 0) {
            saveTraceRecordHistory(Arrays.asList(recordDTO));
        } else {
            traceData.add(recordDTO);
            if (traceData.size() >= batchSaveSize) {
                new Thread(saveTraceTask).start();
            }
        }
    }
}
