package fucklegym.top.entropy;

import android.util.Log;

import com.alibaba.fastjson.*;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class NetworkSupport {
    enum UploadStatus{
        SUCCESS,FAIL,WARNING,NOTLOGIN;
    }

    private static final String URL_LOGIN = "https://cpes.legym.cn/authorization/user/manage/login";
    private static final String URL_UPLOAD_RUNNINGDETAIL = "https://cpes.legym.cn/running/app/uploadRunningDetails";
    private static final String URL_GETSEMESTERID = "https://cpes.legym.cn/education/semester/getCurrent";
    private static final String URL_GETRUNNINGLIMIT = "https://cpes.legym.cn/running/app/getRunningLimit";
    private static final String URL_TODAYACTIVITIES = "https://cpes.legym.cn/education/app/activity/getActivityList";
    private static final String URL_SIGNUP = "https://cpes.legym.cn/education/app/activity/signUp";
    private static final String URL_CANCELSIGNUP = "https://cpes.legym.cn/education/app/activity/cancelSignUp";
    private static final String URL_SIGN = "https://cpes.legym.cn/education/activity/app/attainability/sign";
    private static final double CALORIE_PER_MILEAGE = 58.3;
    public static JSONObject postForReturn(String url, Map<String,String> header,String content) throws IOException {
        URL serverUrl = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) serverUrl.openConnection();
        conn.setRequestMethod("POST");
//        conn.setDoOutput(true);
//        conn.setDoInput(true);
        for(Map.Entry<String,String> entry:header.entrySet()){
            conn.setRequestProperty(entry.getKey(),entry.getValue());
        }
        OutputStream out = conn.getOutputStream();
        out.write(content.getBytes(StandardCharsets.UTF_8));
        conn.connect();
        StringBuilder stringBuffer = new StringBuilder();;
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        BufferedReader buffer = new BufferedReader(reader);
        String tmp;
        while((tmp=buffer.readLine())!=null){
            stringBuffer.append(tmp);
        }
        return JSON.parseObject(stringBuffer.toString());
    }
    public static JSONObject getForReturn(String url, Map<String,String> header) throws IOException {
        URL serverUrl = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) serverUrl.openConnection();
        conn.setRequestMethod("GET");
//        conn.setDoOutput();
        for(Map.Entry<String,String> entry:header.entrySet()){
            conn.setRequestProperty(entry.getKey(),entry.getValue());
        }
        StringBuilder stringBuffer = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        BufferedReader buffer = new BufferedReader(reader);
        String tmp;
        while((tmp=buffer.readLine())!=null){
            stringBuffer.append(tmp);
        }
        return JSON.parseObject(stringBuffer.toString());
    }
    public static JSONObject putForReturn(String url, Map<String,String> header,String content) throws IOException {
        URL serverUrl = new URL(url);
        HttpsURLConnection conn = (HttpsURLConnection) serverUrl.openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setDoInput(true);
        for(Map.Entry<String,String> entry:header.entrySet()){
            conn.setRequestProperty(entry.getKey(),entry.getValue());
        }
        OutputStream out = conn.getOutputStream();
        out.write(content.getBytes(StandardCharsets.UTF_8));
        StringBuilder stringBuffer = new StringBuilder();
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        BufferedReader buffer = new BufferedReader(reader);
        String tmp;
        while((tmp=buffer.readLine())!=null){
            stringBuffer.append(tmp);
        }
        return JSON.parseObject(stringBuffer.toString());
    }
    public static Pair<String,String> getAccessTokenId(String name,String pwd) throws IOException {
        JSONObject content = new JSONObject();
        content.put("userName",name);
        content.put("password",pwd);
        content.put("entrance","1");
        HashMap<String,String> header = new HashMap<String,String>();
        header.put("Content-type","application/json");
        JSONObject data = postForReturn(URL_LOGIN,header,content.toString()).getJSONObject("data");
        Pair<String,String> ret = new Pair<String,String>(data.getString("accessToken"),data.getString("id"));
        if(ret.getKey()==null)return null;
        else return ret;
    }
    public static String getSemesterId(String accessToken) throws IOException {
        HashMap<String,String> header = new HashMap<String ,String >();
        header.put("Content-type","application/json");
        header.put("Authorization","Bearer "+accessToken);
        return getForReturn(URL_GETSEMESTERID,header).getJSONObject("data").getString("id");
    }
    public static RunningLimitInfo getRunningLimiteInfo(String accessToken,String semesterId) throws IOException {
        HashMap<String,String> header = new HashMap<>();
        header.put("Content-type", "application/json");
        header.put("Authorization","Bearer "+accessToken);
        JSONObject content = new JSONObject();
        content.put("semesterId",semesterId);
        JSONObject info = postForReturn(URL_GETRUNNINGLIMIT,header,content.toString());
        double tot = 0.0,dai = 0.0;
        tot = info.getJSONObject("data").getDouble("totalDayMileage");
        dai = info.getJSONObject("data").getDouble("dailyMileage");
        return new RunningLimitInfo(info.getJSONObject("data").getString("limitationsGoalsSexInfoId").toString(), tot, dai);
    }
    public static UploadStatus uploadRunningDetail(String accessToken, String limitationsGoalsSexInfoId,String semesterId, double totMileage, double validMileage, Date startTime,Date endTime) throws IOException {
        //这函数啥也不管，只管上传，检查数据是否安全不是它的事
        HashMap<String,String> header = new HashMap<>();
        header.put("Content-type","application/json");
        header.put("Authorization","Bearer "+accessToken);
        Random random = new Random(System.currentTimeMillis());
        JSONObject content = new JSONObject();
        double pace = 0.5+random.nextInt(6)/10.0;
        content.put("paceRange",pace);
        content.put("totalMileage",totMileage);
        content.put("limitationsGoalsSexInfoId",limitationsGoalsSexInfoId);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        content.put("endTime",formatter.format(endTime));
        content.put("startTime",formatter.format(startTime));
        content.put("effectiveMileage",validMileage);
        content.put("semesterId",semesterId);
        content.put("scoringType",1);
        content.put("signPoint",new JSONArray());
        content.put("totalPart",1);
        content.put("calorie",(int)(totMileage*CALORIE_PER_MILEAGE));
        ArrayList<HashMap<String,String>> runPoints = new ArrayList<>();
        ArrayList<Pair<Double,Double>> genPoints = PathGenerator.genRegularRoutine(250);
        for(Pair<Double,Double> point :genPoints){
            HashMap<String,String> tmp = new HashMap<>();
            tmp.put("latitude",point.getKey().toString());
            tmp.put("longitude",point.getValue().toString());
            runPoints.add(tmp);
        }
        content.put("routineLine",runPoints);
        content.put("type","自由跑");
        content.put("paceNumber",(int)(totMileage*1000/pace/2));
        content.put("effectivePart",1);
        content.put("gpsMileage",totMileage);
        content.put("uneffectiveReason","");
        content.put("avePace",((int)((endTime.getTime()-startTime.getTime())/1000/totMileage))*1000);
        //System.out.println(content.toString());return UploadStatus.NOTLOGIN;
        JSONObject res = postForReturn(URL_UPLOAD_RUNNINGDETAIL,header,content.toString());
        //System.out.println(res.toString());
        if(res.getBoolean("data"))return UploadStatus.SUCCESS;
        else return UploadStatus.FAIL;
    }
    public static Map<String,String> getTodayActivities(String accessToken) throws IOException {
        HashMap<String,String> header = new HashMap<>();
        header.put("Content-type","application/json");
        header.put("Authorization","Bearer "+accessToken);
        JSONObject content = new JSONObject();
        content.put("name","");
        content.put("campus","");
        content.put("page",1);
        content.put("size",10);
        content.put("state","");
        content.put("topicId","");
        content.put("week","");
        JSONObject ret = postForReturn(URL_TODAYACTIVITIES,header,content.toString());
        JSONArray items = ret.getJSONObject("data").getJSONArray("items");
        HashMap<String,String> acts = new HashMap<>();
        for(int i = 0;i<items.size();i++){
            acts.put(items.getJSONObject(i).getString("name"),items.getJSONObject(i).getString("id"));
        }
        return acts;
    }
    public static NetworkSupport.UploadStatus signup(String accessToken,String activityId) throws IOException {
        HashMap<String,String> header = new HashMap<>();
        header.put("Content-type","application/json");
        header.put("Authorization","Bearer "+accessToken);
        JSONObject content = new JSONObject();
        content.put("activityId",activityId);
        JSONObject ret = postForReturn(URL_SIGNUP,header,content.toString());
        if(ret.getJSONObject("data").getBoolean("success"))return UploadStatus.SUCCESS;
        else return UploadStatus.FAIL;
    }
    public static NetworkSupport.UploadStatus cancelSignup(String accessToken,String activityId) throws IOException {
        HashMap<String,String> header = new HashMap<>();
        header.put("Content-type","application/json");
        header.put("Authorization","Bearer "+accessToken);
        JSONObject content = new JSONObject();
        content.put("activityId",activityId);
        JSONObject ret = postForReturn(URL_CANCELSIGNUP,header,content.toString());
        if(ret.getBoolean("data"))return UploadStatus.SUCCESS;
        else return UploadStatus.FAIL;
    }
    public static NetworkSupport.UploadStatus sign(String accessToken,String userId,String activityId) throws IOException {
        HashMap<String,String> header = new HashMap<>();
        header.put("Content-type","application/json");
        header.put("Authorization","Bearer "+accessToken);
        JSONObject content = new JSONObject();
        content.put("pageType","activity");
        content.put("times","1");
        content.put("activityType",0);
        content.put("attainabilityType",2);
        content.put("activityId",activityId);
        content.put("userId",userId);
        JSONObject ret = putForReturn(URL_SIGN,header,content.toString());
        if(ret.getString("message").equals("成功"))return UploadStatus.SUCCESS;
        else return  UploadStatus.FAIL;
    }

}
