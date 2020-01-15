package amap.client.database;


import java.text.SimpleDateFormat;
import java.util.*;


import amap.client.Client.QueryParam;
import amap.client.record.TraceRecordDTO;
import amap.client.service.RecordService;
import android.annotation.SuppressLint;

import amap.client.record.PathRecord;
import amap.client.recorduitl.Util;
import com.amap.api.location.AMapLocation;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;

/**
 * 数据库相关操作，用于存取轨迹记录
 */
public class DbAdapter {
    public static final String KEY_ROWID = "id";
    public static final String KEY_DISTANCE = "distance";
    public static final String KEY_DURATION = "duration";
    public static final String KEY_SPEED = "averagespeed";
    public static final String KEY_LINE = "pathline";
    public static final String KEY_STRAT = "stratpoint";
    public static final String KEY_END = "endpoint";
    public static final String KEY_DATE = "date";

    /**
     * 数据库存入一个数据点
     *
     * @param amLocationStr
     * @param date
     * @return
     */
    public static TraceRecordDTO createrecord(String amLocationStr, Date date) {
        TraceRecordDTO recordDTO = new TraceRecordDTO();
        recordDTO.setAmLocationStr(amLocationStr);
        recordDTO.setCreateTime(date);
        return recordDTO;
    }

    /**
     * 查询所有轨迹记录
     *
     * @return
     */
    public List<PathRecord> queryRecordAll() {
        List<PathRecord> allRecord = new ArrayList<PathRecord>();
        List<TraceRecordDTO> recordDTOS = RecordService.getAllTraceRecordHistory();
        if (recordDTOS == null || recordDTOS.isEmpty()) {
            recordDTOS = new ArrayList<>();
        }
        //根据imei拆分记录
        Map<String, List<TraceRecordDTO>> map = new HashMap<>();
        for (TraceRecordDTO dto : recordDTOS) {
            if (map.get(dto.getImei()) == null) {
                map.put(dto.getImei(), new ArrayList<TraceRecordDTO>());
            }
            map.get(dto.getImei()).add(dto);
        }
        if (!map.isEmpty() && !map.values().isEmpty()) {
            for (List<TraceRecordDTO> list : map.values()) {
                PathRecord record = TraceRecordDTO2PathRecord(list);
                allRecord.add(record);
            }
        }

        return allRecord;
    }

    /**
     * 按照id查询
     *
     * @param mRecordItemId
     * @return
     */
    public PathRecord queryRecordById(String mRecordItemId) {
        QueryParam queryParam = new QueryParam();
        queryParam.setImei(mRecordItemId);
        List<TraceRecordDTO> dtoList = RecordService.getTraceRecordHistory(queryParam);
        PathRecord record = new PathRecord();

        if (dtoList != null && !dtoList.isEmpty()) {
            record = TraceRecordDTO2PathRecord(dtoList);
        }
        return record;
    }

    private PathRecord TraceRecordDTO2PathRecord(List<TraceRecordDTO> dtoList) {
        PathRecord record = new PathRecord();
        if (dtoList == null || dtoList.isEmpty()) {
            return record;
        }
        long mStartTime = dtoList.get(0).getCreateTime().getTime();
        long mEndTime = dtoList.get(dtoList.size() - 1).getCreateTime().getTime();
        List<AMapLocation> aMapLocationList = parseLocationList(dtoList);

        String duration = String.valueOf((mEndTime - mStartTime) / 1000f);
        float distance = getDistance(aMapLocationList);
        String average = String.valueOf(distance / (float) (mEndTime - mStartTime));
        AMapLocation firstLocaiton = aMapLocationList.get(0);
        AMapLocation lastLocaiton = aMapLocationList.get(aMapLocationList.size() - 1);

        record.setmId(dtoList.get(0).getImei());
        record.setDistance(String.valueOf(distance));
        record.setDuration(duration);
        record.setAveragespeed(average);
        record.setDate(getcueDate(mStartTime));
        record.setPathline(aMapLocationList);
        record.setStartpoint(firstLocaiton);
        record.setEndpoint(lastLocaiton);

        return record;
    }

    @SuppressLint("SimpleDateFormat")
    private String getcueDate(long time) {
        SimpleDateFormat formatter = new SimpleDateFormat(
                "yyyy-MM-dd  HH:mm:ss ");
        Date curDate = new Date(time);
        String date = formatter.format(curDate);
        return date;
    }

    private String getPathLineString(List<AMapLocation> list) {
        if (list == null || list.size() == 0) {
            return "";
        }
        StringBuffer pathline = new StringBuffer();
        for (int i = 0; i < list.size(); i++) {
            AMapLocation location = list.get(i);
            String locString = amapLocationToString(location);
            pathline.append(locString).append(";");
        }
        String pathLineString = pathline.toString();
        pathLineString = pathLineString.substring(0,
                pathLineString.length() - 1);
        return pathLineString;
    }

    private String amapLocationToString(AMapLocation location) {
        StringBuffer locString = new StringBuffer();
        locString.append(location.getLatitude()).append(",");
        locString.append(location.getLongitude()).append(",");
        locString.append(location.getProvider()).append(",");
        locString.append(location.getTime()).append(",");
        locString.append(location.getSpeed()).append(",");
        locString.append(location.getBearing());
        return locString.toString();
    }

    private float getDistance(List<AMapLocation> list) {
        float distance = 0;
        if (list == null || list.size() == 0) {
            return distance;
        }
        for (int i = 0; i < list.size() - 1; i++) {
            AMapLocation firstpoint = list.get(i);
            AMapLocation secondpoint = list.get(i + 1);
            LatLng firstLatLng = new LatLng(firstpoint.getLatitude(),
                    firstpoint.getLongitude());
            LatLng secondLatLng = new LatLng(secondpoint.getLatitude(),
                    secondpoint.getLongitude());
            double betweenDis = AMapUtils.calculateLineDistance(firstLatLng,
                    secondLatLng);
            distance = (float) (distance + betweenDis);
        }
        return distance;
    }

    private List<AMapLocation> parseLocationList(List<TraceRecordDTO> list) {
        List<AMapLocation> aMapLocationList = new ArrayList<>();
        if (list == null || list.size() == 0) {
            return aMapLocationList;
        }
        for (TraceRecordDTO dto : list) {
            aMapLocationList.add(Util.parseLocation(dto.getAmLocationStr()));
        }
        return aMapLocationList;
    }
}
