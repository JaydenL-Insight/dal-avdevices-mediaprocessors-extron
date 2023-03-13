package com.insightsystems.symphony.dal.Extron;

import com.avispl.symphony.api.dal.control.Controller;
import com.avispl.symphony.api.dal.dto.control.AdvancedControllableProperty;
import com.avispl.symphony.api.dal.dto.control.ControllableProperty;
import com.avispl.symphony.api.dal.dto.monitor.ExtendedStatistics;
import com.avispl.symphony.api.dal.dto.monitor.GenericStatistics;
import com.avispl.symphony.api.dal.dto.monitor.Statistics;
import com.avispl.symphony.api.dal.monitor.Monitorable;
import com.avispl.symphony.dal.communicator.TelnetCommunicator;

import java.util.*;
import java.util.regex.Matcher;

import static com.insightsystems.symphony.dal.Extron.ExtronRecorderConstants.*;

public class ExtronRecorderCommunicator extends TelnetCommunicator implements Monitorable, Controller {
    public ExtronRecorderCommunicator(){
        this.setPasswordPrompt(PASSWORD_PROMPT);
        this.setLoginErrorList(LOGIN_ERROR_LIST);
        this.setLoginSuccessList(LOGIN_SUCCESS_LIST);
        this.setCommandSuccessList(COMMAND_SUCCESS_LIST);
        this.setCommandErrorList(COMMAND_ERROR_LIST);
    }

    private String sendCommand(String command) throws Exception {
        return send(command).trim();
    }


    @Override
    public List<Statistics> getMultipleStatistics() throws Exception {
        GenericStatistics genStats = new GenericStatistics();
        ExtendedStatistics extStats = new ExtendedStatistics();
        Map<String,String> stats = new HashMap<>();
        List<AdvancedControllableProperty> controls = new ArrayList<>();

        addGenericStatistics(genStats);
        addSimpleStatistics(stats);
        addEnumStatistics(stats);
        addPlaybackControl(stats,controls);
        addAlarms(stats);
        addStatelessControls(stats,controls);


        extStats.setStatistics(stats);
        extStats.setControllableProperties(controls);
        return new ArrayList<Statistics>(){{add(genStats);add(extStats);}};
    }

    private void addAlarms(Map<String, String> stats) throws Exception {
        String response = sendCommand(QUERY_ALARMS);
        if (response.equals("None active")){
            stats.put(STAT_ALARMS,"None");
        } else {
            stats.put(STAT_ALARMS, response);
        }
    }

    private void addEnumStatistics(Map<String, String> stats) throws Exception {
        for (EnumStatistic s : ENUM_STATISTICS){
            String response = sendCommand(s.command);
            if (containsError(response)){ //Error Response
                if (this.logger.isDebugEnabled() && !s.command.equals(QUERY_HDCP)){
                    this.logger.debug("Error occurred retrieving statistic - command\""+s.command+"\" Error: " + getErrorString(response));
                }
                continue;
            }

            String value =  s.map.getOrDefault(response,"");
            if (value.equals("")) { //Value not found
                if (this.logger.isErrorEnabled()){
                    this.logger.error("Unable to map statistic \""+s.name+"\" from response \""+response+"\"");
                }
                continue;
            }

            stats.put(s.name,value);

            if (s.command.equals(QUERY_HDCP)){ //If we have succesfully processed this response, no need to continue with remaining stats
                return;
            }
        }
    }

    private void addSimpleStatistics(Map<String, String> stats) throws Exception {
        for (String[] query : SIMPLE_STATISTICS){
            String response = sendCommand(query[COMMAND]);
            if (!containsError(response)){
                stats.put(query[NAME],response);
            }
        }
    }

    private static boolean containsError(String response) {
        response = response.trim();
        for (String error : ERROR_RESPONSES.keySet()){
            if (response.equals(error)){
                return true;
            }
        }
        return false;
    }

    private static String getErrorString(String response) {
        response = response.trim();
        for (String error : ERROR_RESPONSES.keySet()){
            if (response.equals(error)){
                return ERROR_RESPONSES.get(error);
            }
        }
        return "";
    }

    private static void addStatelessControls(Map<String, String> stats, List<AdvancedControllableProperty> controls) {
        AdvancedControllableProperty.Button ejectButton = new AdvancedControllableProperty.Button();
        ejectButton.setLabelPressed(EJECT_PRESS_LABEL);
        ejectButton.setLabel(EJECT_LABEL);
        ejectButton.setGracePeriod(7000L);
        controls.add(new AdvancedControllableProperty(CTRL_EJECT_USB,new Date(),ejectButton,"0"));
        stats.put(CTRL_EJECT_USB,"0");

        AdvancedControllableProperty.Button rebootButton = new AdvancedControllableProperty.Button();
        rebootButton.setLabelPressed(REBOOT_PRESS_LABEL);
        rebootButton.setLabel(REBOOT_LABEL);
        rebootButton.setGracePeriod(30000L);
        controls.add(new AdvancedControllableProperty(CTRL_REBOOT,new Date(),rebootButton,"0"));
        stats.put(CTRL_REBOOT,"0");

        AdvancedControllableProperty.Button netRestartButton = new AdvancedControllableProperty.Button();
        rebootButton.setLabelPressed(RESTART_NET_PRESS_LABEL);
        rebootButton.setLabel(RESTART_NET_LABEL);
        rebootButton.setGracePeriod(10000L);
        controls.add(new AdvancedControllableProperty(CTRL_RESTART_NET,new Date(),netRestartButton,"0"));
        stats.put(CTRL_RESTART_NET,"0");

        AdvancedControllableProperty.Button clearAlarmsButton = new AdvancedControllableProperty.Button();
        rebootButton.setLabelPressed(ALARMCLR_PRESS_LABEL);
        rebootButton.setLabel(ALARMCLR_LABEL);
        rebootButton.setGracePeriod(1000L);
        controls.add(new AdvancedControllableProperty(CTRL_CLEAR_ALARMS,new Date(),clearAlarmsButton,"0"));
        stats.put(CTRL_CLEAR_ALARMS,"0");
    }

    private void addGenericStatistics(GenericStatistics stats) throws Exception {
        //#Bytes used out of #KBytes
        final String memoryResponse = sendCommand(QUERY_SYSTEM_MEM);

        Matcher memUsed = MEMORY_USED_PAT.matcher(memoryResponse);
        Matcher memTotal = MEMORY_TOTAL_PAT.matcher(memoryResponse);

        if (!memUsed.find() || !memTotal.find()){ 
            if (this.logger.isDebugEnabled()){
                this.logger.debug("Memory usage not found in response: " + memoryResponse);
            }
            return;
        }

        try {
            //Parse and convert to GB
            stats.setMemoryInUse((float) (Double.parseDouble(memUsed.group(1)) / (1e+9))); //from Bytes to GB
            stats.setMemoryTotal((float) (Double.parseDouble(memTotal.group(1)) / (1e+6))); //from Kb to GB
        } catch (Exception e){
            if (this.logger.isDebugEnabled()){
                this.logger.debug("An error occurred parsing memory usage",e);
            }
        }

        final String systemUsageResponse = sendCommand(QUERY_SYSTEM_USAGE);
        try {
            stats.setCpuPercentage(Float.parseFloat(systemUsageResponse));
        } catch (Exception e){
            if (this.logger.isDebugEnabled()){
                this.logger.debug("An error occurred parsing system usage",e);
            }
        }
    }

    private void addPlaybackControl(Map<String,String> stats,List<AdvancedControllableProperty> controls) throws Exception {
        String response = sendCommand(QUERY_REC_STATUS);
        if (containsError(response)){
            if (this.logger.isDebugEnabled()){
                this.logger.debug("Error occurred retrieving statistic - command\""+QUERY_REC_STATUS+"\" Error: " + getErrorString(response));
            }
            return;
        }

        AdvancedControllableProperty.Preset playback = new AdvancedControllableProperty.Preset();
        playback.setLabels(PLAYBACK_STATES);
        playback.setOptions(PLAYBACK_STATES);
        controls.add(new AdvancedControllableProperty(CTRL_PLAYBACK,new Date(),playback,response));
        stats.put(CTRL_PLAYBACK,response);
    }

    @Override
    public void controlProperty(ControllableProperty cp) throws Exception {
        String name = cp.getProperty();
        String value = String.valueOf(cp.getValue());
        String response;
        switch (name){
            case CTRL_EJECT_USB: response = sendCommand(CMD_EJECT_USB); break;
            case CTRL_REBOOT: response = sendCommand(CMD_REBOOT); break;
            case CTRL_RESTART_NET: response = sendCommand(CMD_RESTART_NET); break;
            case CTRL_CLEAR_ALARMS: response = sendCommand(CMD_CLEAR_ALARMS); break;
            case CTRL_PLAYBACK: 
                switch (value){
                    case "0":response = sendCommand(CMD_START);break;
                    case "1" :response = sendCommand(CMD_STOP );break;
                    case "2":response = sendCommand(CMD_PAUSE);break;
                    default:
                        throw new UnsupportedOperationException("Unknown Playback State: "+ value);
                }
                break;
            default:
                throw new UnsupportedOperationException("Control Property "+ name + " is unknown or invalid.");
        }
        if (this.logger.isDebugEnabled() && containsError(response)){
            this.logger.debug("An error has occurred running command for device \"" + cp.getDeviceId() + "\": " + response);
        }
    }

    @Override
    public void controlProperties(List<ControllableProperty> list) throws Exception {
        for (ControllableProperty cp : list)
            controlProperty(cp);
    }
}