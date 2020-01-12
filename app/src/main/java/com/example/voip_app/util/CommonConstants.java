package com.example.voip_app.util;

public class CommonConstants {
    public static final String SERVER_IP = "192.168.1.3";
    public static final int SERVER_PORT = 8888;
    public static final String URL_BASE = "http://"+ SERVER_IP +":8081/voip_app/";
    public static final String URL_LOGIN = "login.php";
    public static final String URL_REGISTER = "register.php";
    public static final String URL_LOAD_CONTACT = "contact.php";

    public final static String EXTRA_CONTACT = "CONTACT";
    public final static String EXTRA_DATA_SOCKET = "DATA_SOCKET";
    public final static String EXTRA_IP = "IP";
    public final static String EXTRA_DISPLAYNAME = "DISPLAYNAME";

    public static final int AUDIO_PORT = 50000;
}
