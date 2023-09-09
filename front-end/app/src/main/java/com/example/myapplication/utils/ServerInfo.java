package com.example.myapplication.utils;

public class ServerInfo {

//    public static String ServerLoginURI = "http://172.20.10.2:8080/login";
    private static String BASEURL="http://10.63.137.237:8081";
    public static String ServerLoginURI = BASEURL+"/login";
    public static String ServerLocationURI = BASEURL + "/locationinfo";
    public static String ServerWSURI = "ws://10.63.137.237:8080/ws";

    public static String ServerRegisterURI = BASEURL + "/register";

    public static String ServerMatchURI = BASEURL + "/match";


}
