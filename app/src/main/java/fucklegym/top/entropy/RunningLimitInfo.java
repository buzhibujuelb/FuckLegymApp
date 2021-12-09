package fucklegym.top.entropy;

class RunningLimitInfo{
    private double totMileage,dailyMileage;
    private String limitationsGoalsSexInfoId;
    RunningLimitInfo(String limitationsGoalsSexInfoId,double totMileage,double dailyMileage){
        this.limitationsGoalsSexInfoId = limitationsGoalsSexInfoId;
        this.totMileage = totMileage;
        this.dailyMileage = dailyMileage;
    }

    public void setTotMileage(double totMileage) {
        this.totMileage = totMileage;
    }

    public void setdailyMileage(double dailyMileage) {
        this.dailyMileage = dailyMileage;
    }

    public void setLimitationsGoalsSexInfoId(String limitationsGoalsSexInfoId) {
        this.limitationsGoalsSexInfoId = limitationsGoalsSexInfoId;
    }

    public double getTotMileage() {
        return totMileage;
    }
    public double getdailyMileage() {
        return dailyMileage;
    }
    public String getLimitationsGoalsSexInfoId() {
        return limitationsGoalsSexInfoId;
    }
}