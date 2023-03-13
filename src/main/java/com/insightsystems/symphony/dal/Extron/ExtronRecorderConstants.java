package com.insightsystems.symphony.dal.Extron;

import java.util.*;
import java.util.regex.Pattern;

public class ExtronRecorderConstants {
    protected static final String ESC = String.valueOf((char)0x1B);
    protected static final String CR  = String.valueOf((char)0x0D);
    protected static final String LF  = String.valueOf((char)0x0A);
    protected static final String CRLF = CR+LF;
    protected static final int NAME = 0;
    protected static final int COMMAND = 1;

    protected static final String PASSWORD_PROMPT = "Password:";
    protected static final List<String> LOGIN_ERROR_LIST = Arrays.asList("Incorrect Password","Login Unsuccessful");
    protected static final List<String> LOGIN_SUCCESS_LIST = Collections.singletonList("Login Administrator"+CRLF);
    protected static final List<String> COMMAND_SUCCESS_LIST = Collections.singletonList(CRLF);
    protected static final List<String> COMMAND_ERROR_LIST = Collections.singletonList("ERROR");


    protected static final String STAT_DEV_PRE = "Device#";
    protected static final String STAT_SERIAL = STAT_DEV_PRE + "serialNumber";
    protected static final String STAT_FIRMWARE = STAT_DEV_PRE + "firmwareVersion";
    protected static final String STAT_MAC_ADDRESS = STAT_DEV_PRE + "macAddress";
    protected static final String STAT_NAME = STAT_DEV_PRE + "name";
    protected static final String STAT_PART_NUM = STAT_DEV_PRE + "partNumber";
    protected static final String STAT_MODEL = STAT_DEV_PRE + "model";
    protected static final String STAT_DATE_TIME = STAT_DEV_PRE + "dateTime";
    protected static final String STAT_CONNECTED_USERS = STAT_DEV_PRE + "connectedUsers";

    protected static final String STAT_REC_PRE = "Record#";
    protected static final String STAT_REC_STATUS = STAT_REC_PRE + "status";
    protected static final String STAT_REC_DST = STAT_REC_PRE + "destination";
    protected static final String STAT_TRANSFER_CONFIG = STAT_REC_PRE + "transferConfig";

    protected static final String STAT_HDCP = "HdcpState";
    protected static final String STAT_HDCP1 = "HdcpStates#Input 1";
    protected static final String STAT_HDCP2 = "HdcpStates#Input 2";
    protected static final String STAT_HDCP3 = "HdcpStates#Input 3";
    protected static final String STAT_HDCP4 = "HdcpStates#Input 4";
    protected static final String STAT_HDCP5 = "HdcpStates#Input 5";
    protected static final String STAT_ALARMS = "Alarms";

    protected static final String CTRL_PRE = "Controls#";
    protected static final String CTRL_PLAYBACK = CTRL_PRE+"playbackControl";
    protected static final String CTRL_EJECT_USB = CTRL_PRE+"ejectUsb";
    protected static final String CTRL_REBOOT = CTRL_PRE+"reboot";
    protected static final String CTRL_RESTART_NET = CTRL_PRE+"restartNetwork";
    protected static final String CTRL_CLEAR_ALARMS = CTRL_PRE+"clearAlarms";

    protected static final Pattern MEMORY_USED_PAT = Pattern.compile("(\\d+)\\s*Bytes");
    protected static final Pattern MEMORY_TOTAL_PAT = Pattern.compile("(\\d+)\\s*KBytes");

    protected static final String QUERY_PART_NUM = "n";
    protected static final String QUERY_FIRMWARE = "q";
    protected static final String QUERY_NAME = ESC + "CN" + CR;
    protected static final String QUERY_DATE_TIME = ESC + "CT" + CR;
    protected static final String QUERY_REC_STATUS = ESC + "YRCDR" + CR;
    protected static final String QUERY_REC_DST = ESC + "DRCDR" + CR;
    protected static final String QUERY_HDCP = ESC + "IHDCP" + CR;
    protected static final String QUERY_HDCP1 = ESC + "I1HDCP" + CR;
    protected static final String QUERY_HDCP2 = ESC + "I2HDCP" + CR;
    protected static final String QUERY_HDCP4 = ESC + "I4HDCP" + CR;


    protected static final String QUERY_MODEL = "1I";
    protected static final String QUERY_SYSTEM_MEM = "3I";
    protected static final String QUERY_CONNECTED_USERS = "10I";
    protected static final String QUERY_SYSTEM_USAGE = "11I";
    protected static final String QUERY_NET_LINK = "13I";
    protected static final String QUERY_TRANSFER_CONFIG = "38I";
    protected static final String QUERY_ALARMS = "39I";

    protected static final String QUERY_INTERNAL_STORAGE = "55I";
    protected static final String QUERY_FUSB_STORAGE = "56I";
    protected static final String QUERY_RUSB_STORAGE = "57I";

    protected static final String QUERY_MAC_ADDRESS = "98I";
    protected static final String QUERY_SERIAL = "99I";

    protected static final String CMD_STOP  = ESC + "Y0RCDR"+ CR;
    protected static final String CMD_START = ESC + "Y1RCDR"+ CR;
    protected static final String CMD_PAUSE = ESC + "Y2RCDR"+ CR;

    protected static final String CMD_EJECT_USB = ESC + "0USBE" + CR;
    protected static final String CMD_REBOOT = ESC + "1BOOT" + CR;
    protected static final String CMD_RESTART_NET = ESC + "2BOOT" + CR;
    protected static final String CMD_CLEAR_ALARMS = ESC + "CALRM" + CR;

    protected static final String STOP = "Stop";
    protected static final String START = "Start";
    protected static final String PAUSE = "Pause";
    protected static final String[] PLAYBACK_STATES = {STOP,START,PAUSE};
    protected static final String EJECT_LABEL        = "Eject All USBs";
    protected static final String EJECT_PRESS_LABEL  = "Ejecting...";
    protected static final String REBOOT_LABEL       = "Reboot Device";
    protected static final String REBOOT_PRESS_LABEL = "Rebooting...";
    protected static final String RESTART_NET_LABEL = "Restart";
    protected static final String RESTART_NET_PRESS_LABEL = "Restarting..";
    protected static final String ALARMCLR_PRESS_LABEL = "Clearing..";
    protected static final String ALARMCLR_LABEL = "Clear Alarms";

    protected static final String[][] SIMPLE_STATISTICS = new String[][]{
       //{Name,Command};
        {STAT_PART_NUM,QUERY_PART_NUM},
        {STAT_FIRMWARE,QUERY_FIRMWARE},
        {STAT_SERIAL,QUERY_SERIAL},
        {STAT_MAC_ADDRESS,QUERY_MAC_ADDRESS},
        {STAT_NAME,QUERY_NAME},
        {STAT_DATE_TIME,QUERY_DATE_TIME},
        {STAT_MODEL,QUERY_MODEL},
        {STAT_TRANSFER_CONFIG,QUERY_TRANSFER_CONFIG},
        {STAT_CONNECTED_USERS,QUERY_CONNECTED_USERS}
    };

    protected static final Map<String,String> ERROR_RESPONSES = new HashMap<String,String>(){{
        put("E10","Unrecognized command");
        put("E18","System timed out");
        put("E12","Invalid port number");
        put("E22","Busy");
        put("E13","Invalid parameter (number is out of range)");
        put("E24","Privilege violation");
        put("E14","Not valid for this configuration");
        put("E26","Maximum connections exceeded");
        put("E17","Invalid command for signal type");
        put("E28","Bad file name or file not found");
    }};

    protected static final Map<String,String> STORAGE_DESTINATIONS = new HashMap<String,String>(){{
        put("0","Auto");
        put("1","Internal");
        put("2","USB Front");
        put("3","USB Rear");
        put("11","Internal + Auto");
        put("12","Internal + USB Front");
        put("13","Internal + USB Rear");
        put("14","Internal + USB RCP");
    }};

    protected static final Map<String,String> HDCP_STATES = new HashMap<String,String>(){{
        put("0", "No Source Detected");
        put("1", "Source with HDCP");
        put("2", "Source without HDCP");
    }};

    protected static final Map<String,String> REC_STATES = new HashMap<String,String>(){{
        put("0", "Stopped");
        put("1", "Recording");
        put("2", "Paused");
    }};



    protected static final EnumStatistic[] ENUM_STATISTICS = new EnumStatistic[]{
        new EnumStatistic(STAT_REC_DST,QUERY_REC_DST,STORAGE_DESTINATIONS),
        new EnumStatistic(STAT_REC_STATUS,QUERY_REC_STATUS,REC_STATES),
        new EnumStatistic(STAT_HDCP,QUERY_HDCP,HDCP_STATES),
        new EnumStatistic(STAT_HDCP1,QUERY_HDCP1,HDCP_STATES),
        new EnumStatistic(STAT_HDCP2,QUERY_HDCP2,HDCP_STATES),
        new EnumStatistic(STAT_HDCP4,QUERY_HDCP4,HDCP_STATES)
    };

}
class EnumStatistic{
    protected String name;
    protected String command;
    protected Map<String,String> map;
    public EnumStatistic(String name,String command,Map<String,String> map){
        this.name = name;
        this.command = command;
        this.map = map;
    }
}
