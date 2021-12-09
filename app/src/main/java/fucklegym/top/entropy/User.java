package fucklegym.top.entropy;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class User {
    private String username;
    private String password;
    private String accessToken;
    private String id;
    private String limitationsGoalsSexInfoId;
    private String semesterId;
    private double daliyMileage;
    private double totalDailyMileage;
    boolean hasLogin = false;
    private HashMap<String,String> activities;
    public User(String username, String password){
        this.username = username;
        this.password = password;
    }
    public void login() throws IOException {
        if(hasLogin)return;
        Pair<String,String> loginfo = NetworkSupport.getAccessTokenId(username,password);
        this.accessToken = loginfo.getKey();this.id = loginfo.getValue();
        if(accessToken==null||id==null) return;
        this.semesterId = NetworkSupport.getSemesterId(accessToken);
        if(semesterId==null)return;

        RunningLimitInfo info = NetworkSupport.getRunningLimiteInfo(accessToken,semesterId);
        if(info.getLimitationsGoalsSexInfoId()==null)return;
        this.limitationsGoalsSexInfoId = info.getLimitationsGoalsSexInfoId();
        this.totalDailyMileage = info.getTotMileage();
        this.daliyMileage = info.getdailyMileage();
        this.hasLogin = true;
    }
    public NetworkSupport.UploadStatus uploadRunningDetail(Date startTime, Date endTime, double totalMileage, double validMileage) throws IOException {
        if(!this.hasLogin)login();
        return NetworkSupport.uploadRunningDetail(accessToken,limitationsGoalsSexInfoId,semesterId,totalMileage,validMileage,startTime,endTime);

    }
    public Map<String, String> getTodayActivities() throws IOException {
        if(!hasLogin)login();
        if(this.activities!=null)return activities;
        this.activities = (HashMap<String, String>) NetworkSupport.getTodayActivities(accessToken);
        return this.activities;
    }
    public NetworkSupport.UploadStatus sign(String name) throws IOException {
        if(!hasLogin)login();
        if(this.activities == null)getTodayActivities();
        if(!activities.containsKey(name))return NetworkSupport.UploadStatus.FAIL;
        NetworkSupport.sign(accessToken,id,activities.get(name));
        return NetworkSupport.UploadStatus.SUCCESS;
    }
    public void setDaliyMileage(double daliyMileage) {
        this.daliyMileage = daliyMileage;
    }

    public void setTotalDailyMileage(double totalDailyMileage) {
        this.totalDailyMileage = totalDailyMileage;
    }

    public double getDaliyMileage() {
        return daliyMileage;
    }

    public double getTotalDailyMileage() {
        return totalDailyMileage;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setLimitationsGoalsSexInfoId(String limitationsGoalsSexInfoId) {
        this.limitationsGoalsSexInfoId = limitationsGoalsSexInfoId;
    }

    public void setSemesterId(String semesterId) {
        this.semesterId = semesterId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getId() {
        return id;
    }

    public String getLimitationsGoalsSexInfoId() {
        return limitationsGoalsSexInfoId;
    }

    public String getSemesterId() {
        return semesterId;
    }
}
