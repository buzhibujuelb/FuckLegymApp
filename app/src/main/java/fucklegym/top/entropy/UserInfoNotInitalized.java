package fucklegym.top.entropy;

public class UserInfoNotInitalized extends Exception{
    public String toString(){
        return "User information not initialized, please initialize it first";
    }
}
