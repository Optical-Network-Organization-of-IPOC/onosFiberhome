/*
 * Copyright 2016-present Open Networking Foundation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.onosproject.roadm;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Range;
import org.onlab.osgi.ServiceDirectory;
import org.onlab.util.Frequency;
import org.onlab.util.Spectrum;
import org.onosproject.net.ChannelSpacing;
import org.onosproject.net.DeviceId;
import org.onosproject.net.device.DeviceService;
import org.onosproject.net.OchSignal;
import org.onosproject.net.PortNumber;
import org.onosproject.net.flow.FlowEntry;
import org.onosproject.net.flow.FlowId;
import org.onosproject.net.flow.FlowRuleService;
import org.onosproject.ui.RequestHandler;
import org.onosproject.ui.UiConnection;
import org.onosproject.ui.UiMessageHandler;

import org.onosproject.ui.table.TableModel;
import org.onosproject.ui.table.TableRequestHandler;
import org.onosproject.ui.table.cell.HexLongFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.*;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;

import static org.onosproject.ui.JsonUtils.node;
import static org.onosproject.ui.JsonUtils.number;
import static org.onosproject.net.Device.Type;

/**
 * Table-View message handler for ROADM flow view.
 */
public class RoadmFlowViewMessageHandler extends UiMessageHandler {

    private static final String ROADM_FLOW_DATA_REQ = "roadmFlowDataRequest";
    private static final String ROADM_FLOW_DATA_RESP = "roadmFlowDataResponse";
    private static final String ROADM_FLOWS = "roadmFlows";

    private static final String ROADM_SET_ATTENUATION_REQ = "roadmSetAttenuationRequest";
    private static final String ROADM_SET_ATTENUATION_RESP = "roadmSetAttenuationResponse";

    private static final String ROADM_DELETE_FLOW_REQ = "roadmDeleteFlowRequest";

    private static final String ROADM_CREATE_FLOW_REQ = "roadmCreateFlowRequest";
    private static final String ROADM_CREATE_FLOW_RESP = "roadmCreateFlowResponse";

    private static final String USER_AGENT = "Mozilla/5.0";
    private static final String ROADM_SHOW_ITEMS_REQ = "roadmShowFlowItemsRequest";
    private static final String ROADM_SHOW_ITEMS_RESP = "roadmShowFlowItemsResponse";

    private static final String ID = "id";
    private static final String FLOW_ID = "flowId";
    private static final String APP_ID = "appId";
    private static final String GROUP_ID = "groupId";
    private static final String TABLE_ID = "tableId";
    private static final String PRIORITY = "priority";
    private static final String PERMANENT = "permanent";
    private static final String TIMEOUT = "timeout";
    private static final String STATE = "state";
    private static final String IN_PORT = "inPort";
    private static final String OUT_PORT = "outPort";
    private static final String CHANNEL_SPACING = "spacing";
    private static final String CHANNEL_MULTIPLIER = "multiplier";
    private static final String CURRENT_POWER = "currentPower";
    private static final String ATTENUATION = "attenuation";
    private static final String HAS_ATTENUATION = "hasAttenuation";
    private static final String CHANNEL_FREQUENCY = "channelFrequency";

    //定义触发数据类型
    private static final String HI_SGP_RESP = "hiResponse";
    private static final String THERECEIVE = "receive message";

    public String theName;
    public String theCpu;
    public String theMemory;
    private static final String HELLOWORLD_REQQQ = "helloworldRequest123";
    private static final String NAME1 = "name1";
    private static final String CPU = "cpu";
    private static final String MEMORY = "memory";


    private static final String[] COLUMN_IDS = {
            ID, FLOW_ID, APP_ID, GROUP_ID, TABLE_ID, PRIORITY, TIMEOUT,
            PERMANENT, STATE, IN_PORT, OUT_PORT, CHANNEL_SPACING, CHANNEL_MULTIPLIER,
            CHANNEL_FREQUENCY, CURRENT_POWER, ATTENUATION, HAS_ATTENUATION
    };

    private RoadmService roadmService;
    private DeviceService deviceService;
    private FlowRuleService flowRuleService;

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public void init(UiConnection connection, ServiceDirectory directory) {
        super.init(connection, directory);
        roadmService = get(RoadmService.class);
        deviceService = get(DeviceService.class);
        flowRuleService = get(FlowRuleService.class);
    }

    @Override
    protected Collection<RequestHandler> createRequestHandlers() {
        return ImmutableSet.of(
                new FlowTableDataRequestHandler(),
                new SetAttenuationRequestHandler(),
                new DeleteConnectionRequestHandler(),
                new CreateConnectionRequestHandler(),
                new CreateShowItemsRequestHandler(),
                new SayHelloWorld()
        );
    }



    private final class SayHelloWorld extends RequestHandler {
        private SayHelloWorld(){super(HELLOWORLD_REQQQ);}

        @Override
        public void process(ObjectNode payload) {
            theName = string(payload,NAME1);
            theCpu = string(payload,CPU);
            theMemory = string(payload,MEMORY);
            log.info("wrtre");
            log.info("TheName is :{}", theName);
            log.info("TheCpu is:{}", theCpu);
            log.info("TheMemory is:{}", theMemory);

            String theResponse = "hi sgp";
            ObjectNode test = objectNode();
            test.put(THERECEIVE, theResponse);
            sendMessage(HI_SGP_RESP, test);
            String s = "";
            try {
                s = sendPost();
                log.info("The result of get is:{}", s);
            }catch(Exception e){
                log.info("The result of get is:{}", s);
                e.printStackTrace();
            }
        }
    }



    // Handler for sample table requests
    private final class FlowTableDataRequestHandler extends TableRequestHandler {

        private FlowTableDataRequestHandler() {
            super(ROADM_FLOW_DATA_REQ, ROADM_FLOW_DATA_RESP, ROADM_FLOWS);
        }

        @Override
        protected String[] getColumnIds() {
            return COLUMN_IDS;
        }

        @Override
        protected String noRowsMessage(ObjectNode payload) {
            return RoadmUtil.NO_ROWS_MESSAGE;
        }

        @Override
        protected TableModel createTableModel() {
            TableModel tm = super.createTableModel();
            tm.setFormatter(FLOW_ID, HexLongFormatter.INSTANCE);
            return tm;
        }

        @Override
        protected void populateTable(TableModel tm, ObjectNode payload) {
            DeviceId deviceId = DeviceId.deviceId(string(payload, RoadmUtil.DEV_ID));
            // Update flows
            Iterable<FlowEntry> flowEntries = flowRuleService.getFlowEntries(deviceId);
            for (FlowEntry flowEntry : flowEntries) {
                populateRow(tm.addRow(), flowEntry, deviceId);
            }
        }

        private void populateRow(TableModel.Row row, FlowEntry entry, DeviceId deviceId) {
            ChannelData cd = ChannelData.fromFlow(entry);
            String spacing = RoadmUtil.NA, multiplier = RoadmUtil.NA, channelFrequency = "";
            OchSignal ochSignal = cd.ochSignal();
            if (ochSignal != null) {
                Frequency spacingFreq = ochSignal.channelSpacing().frequency();
                spacing = RoadmUtil.asGHz(spacingFreq);
                int spacingMult = ochSignal.spacingMultiplier();
                multiplier = String.valueOf(spacingMult);
                channelFrequency = String.format(" (%sGHz)",
                        RoadmUtil.asGHz(Spectrum.CENTER_FREQUENCY.add(spacingFreq.multiply(spacingMult))));
            }

            row.cell(ID, entry.id().value())
                    .cell(FLOW_ID, entry.id().value())
                    .cell(APP_ID, entry.appId())
                    .cell(PRIORITY, entry.priority())
                    .cell(TIMEOUT, entry.timeout())
                    .cell(PERMANENT, entry.isPermanent())
                    .cell(STATE, entry.state().toString())
                    .cell(IN_PORT, cd.inPort().toLong())
                    .cell(OUT_PORT, cd.outPort().toLong())
                    .cell(CHANNEL_SPACING, spacing)
                    .cell(CHANNEL_MULTIPLIER, multiplier)
                    .cell(CHANNEL_FREQUENCY, channelFrequency)
                    .cell(CURRENT_POWER, getCurrentPower(deviceId, cd))
                    .cell(HAS_ATTENUATION, hasAttenuation(deviceId, cd))
                    .cell(ATTENUATION, getAttenuation(deviceId, cd));
        }

        private String getCurrentPower(DeviceId deviceId, ChannelData channelData) {
            if (hasAttenuation(deviceId, channelData)) {
                // report channel power if channel exists
                Double currentPower = roadmService.getCurrentChannelPower(deviceId,
                        channelData.outPort(), channelData.ochSignal());
                return RoadmUtil.objectToString(currentPower, RoadmUtil.UNKNOWN);
            }
            // otherwise, report port power
            Type devType = deviceService.getDevice(deviceId).type();
            PortNumber port = devType == Type.FIBER_SWITCH ? channelData.inPort() : channelData.outPort();
            Double currentPower = roadmService.getCurrentPortPower(deviceId, port);
            return RoadmUtil.objectToString(currentPower, RoadmUtil.UNKNOWN);
        }

        private String getAttenuation(DeviceId deviceId, ChannelData channelData) {
            OchSignal signal = channelData.ochSignal();
            if (signal == null) {
                return RoadmUtil.NA;
            }
            Double attenuation = roadmService.getAttenuation(deviceId, channelData.outPort(), signal);
            return RoadmUtil.objectToString(attenuation, RoadmUtil.UNKNOWN);
        }

        private boolean hasAttenuation(DeviceId deviceId, ChannelData channelData) {
            OchSignal signal = channelData.ochSignal();
            if (signal == null) {
                return false;
            }
            return roadmService.attenuationRange(deviceId, channelData.outPort(), signal) != null;
        }
    }

    // Handler for setting attenuation
    private final class SetAttenuationRequestHandler extends RequestHandler {

        // Error messages to display to user
        private static final String ATTENUATION_RANGE_MSG = "Attenuation must be in range %s.";
        private static final String NO_ATTENUATION_MSG = "Cannot set attenuation for this connection";

        private SetAttenuationRequestHandler() {
            super(ROADM_SET_ATTENUATION_REQ);
        }

        @Override
        public void process(ObjectNode payload) {
            DeviceId deviceId = DeviceId.deviceId(string(payload, RoadmUtil.DEV_ID));
            FlowId flowId = FlowId.valueOf(number(payload, FLOW_ID));
            // Get connection information from the flow
            FlowEntry entry = findFlow(deviceId, flowId);
            if (entry == null) {
                log.error("Unable to find flow rule to set attenuation for device {}", deviceId);
                return;
            }
            ChannelData channelData = ChannelData.fromFlow(entry);
            PortNumber port = channelData.outPort();
            OchSignal signal = channelData.ochSignal();
            Range<Double> range = roadmService.attenuationRange(deviceId, port, signal);
            Double attenuation = payload.get(ATTENUATION).asDouble();
            boolean validAttenuation = range != null && range.contains(attenuation);
            if (validAttenuation) {
                roadmService.setAttenuation(deviceId, port, signal, attenuation);
            }
            ObjectNode rootNode = objectNode();
            // Send back flowId so view can identify which callback function to use
            rootNode.put(FLOW_ID, payload.get(FLOW_ID).asText());
            rootNode.put(RoadmUtil.VALID, validAttenuation);
            if (range  == null) {
                rootNode.put(RoadmUtil.MESSAGE, NO_ATTENUATION_MSG);
            } else {
                rootNode.put(RoadmUtil.MESSAGE, String.format(ATTENUATION_RANGE_MSG, range.toString()));
            }
            sendMessage(ROADM_SET_ATTENUATION_RESP, rootNode);
        }

        private FlowEntry findFlow(DeviceId deviceId, FlowId flowId) {
            for (FlowEntry entry : flowRuleService.getFlowEntries(deviceId)) {
                if (entry.id().equals(flowId)) {
                    return entry;
                }
            }
            return null;
        }
    }

    // Handler for deleting a connection
    private final class DeleteConnectionRequestHandler extends RequestHandler {
        private DeleteConnectionRequestHandler() {
            super(ROADM_DELETE_FLOW_REQ);
        }

        @Override
        public void process(ObjectNode payload) {
            DeviceId deviceId = DeviceId.deviceId(string(payload, RoadmUtil.DEV_ID));
            FlowId flowId = FlowId.valueOf(payload.get(ID).asLong());
            roadmService.removeConnection(deviceId, flowId);
        }
    }

    // Handler for creating a creating a connection from form data
    private final class CreateConnectionRequestHandler extends RequestHandler {

        // Keys to load from JSON
        private static final String FORM_DATA = "formData";
        private static final String CHANNEL_SPACING_INDEX = "index";

        // Keys for validation results
        private static final String CONNECTION = "connection";
        private static final String CHANNEL_AVAILABLE = "channelAvailable";

        // Error messages to display to user
        private static final String IN_PORT_ERR_MSG = "Invalid input port.";
        private static final String OUT_PORT_ERR_MSG = "Invalid output port.";
        private static final String CONNECTION_ERR_MSG = "Invalid connection from input port to output port.";
        private static final String CHANNEL_SPACING_ERR_MSG = "Channel spacing not supported.";
        private static final String CHANNEL_ERR_MSG = "Channel index must be in range %s.";
        private static final String CHANNEL_AVAILABLE_ERR_MSG = "Channel is already being used.";
        private static final String ATTENUATION_ERR_MSG = "Attenuation must be in range %s.";

        private CreateConnectionRequestHandler() {
            super(ROADM_CREATE_FLOW_REQ);
        }

        @Override
        public void process(ObjectNode payload) {
            DeviceId did = DeviceId.deviceId(string(payload, RoadmUtil.DEV_ID));
            ObjectNode flowNode = node(payload, FORM_DATA);
            int priority = (int) number(flowNode, PRIORITY);
            boolean permanent = bool(flowNode, PERMANENT);
            int timeout = (int) number(flowNode, TIMEOUT);
            PortNumber inPort = PortNumber.portNumber(number(flowNode, IN_PORT));
            PortNumber outPort = PortNumber.portNumber(number(flowNode, OUT_PORT));
            ObjectNode chNode = node(flowNode, CHANNEL_SPACING);
            ChannelSpacing spacing = channelSpacing((int) number(chNode, CHANNEL_SPACING_INDEX));
            int multiplier = (int) number(flowNode, CHANNEL_MULTIPLIER);
            OchSignal och = OchSignal.newDwdmSlot(spacing, multiplier);
            double att = number(flowNode, ATTENUATION);

            boolean showItems = deviceService.getDevice(did).type() != Type.FIBER_SWITCH;
            boolean validInPort = roadmService.validInputPort(did, inPort);
            boolean validOutPort = roadmService.validOutputPort(did, outPort);
            boolean validConnect = roadmService.validConnection(did, inPort, outPort);
            boolean validSpacing = true;
            boolean validChannel = roadmService.validChannel(did, inPort, och);
            boolean channelAvailable = roadmService.channelAvailable(did, och);
            boolean validAttenuation = roadmService.attenuationInRange(did, outPort, att);

            if (validConnect) {
                if (validChannel && channelAvailable) {
                    if (validAttenuation) {
                        roadmService.createConnection(did, priority, permanent, timeout, inPort, outPort, och, att);
                    } else {
                        roadmService.createConnection(did, priority, permanent, timeout, inPort, outPort, och);
                    }
                }
            }

            String channelMessage = "Invalid channel";
            String attenuationMessage = "Invalid attenuation";
            if (showItems) {
                // Construct error for channel
                if (!validChannel) {
                    Set<OchSignal> lambdas = roadmService.queryLambdas(did, outPort);
                    if (lambdas != null) {
                        Range<Integer> range = channelRange(lambdas);
                        if (range.contains(och.spacingMultiplier())) {
                            // Channel spacing error
                            validSpacing = false;
                        } else {
                            channelMessage = String.format(CHANNEL_ERR_MSG, range.toString());
                        }
                    }
                }

                // Construct error for attenuation
                if (!validAttenuation) {
                    Range<Double> range = roadmService.attenuationRange(did, outPort, och);
                    if (range != null) {
                        attenuationMessage = String.format(ATTENUATION_ERR_MSG, range.toString());
                    }
                }
            }

            // Build response
            ObjectNode node = objectNode();
            node.set(IN_PORT, validationObject(validInPort, IN_PORT_ERR_MSG));
            node.set(OUT_PORT, validationObject(validOutPort, OUT_PORT_ERR_MSG));
            node.set(CONNECTION, validationObject(validConnect, CONNECTION_ERR_MSG));
            node.set(CHANNEL_SPACING, validationObject(validChannel || validSpacing, CHANNEL_SPACING_ERR_MSG));
            node.set(CHANNEL_MULTIPLIER, validationObject(validChannel || !validSpacing, channelMessage));
            node.set(CHANNEL_AVAILABLE, validationObject(!validChannel || channelAvailable, CHANNEL_AVAILABLE_ERR_MSG));
            node.set(ATTENUATION, validationObject(validAttenuation, attenuationMessage));
            sendMessage(ROADM_CREATE_FLOW_RESP, node);
        }

        // Returns the ChannelSpacing based on the selection made
        private ChannelSpacing channelSpacing(int selectionIndex) {
            switch (selectionIndex) {
                case 0: return ChannelSpacing.CHL_100GHZ;
                case 1: return ChannelSpacing.CHL_50GHZ;
                case 2: return ChannelSpacing.CHL_25GHZ;
                case 3: return ChannelSpacing.CHL_12P5GHZ;
                // 6.25GHz cannot be used with ChannelSpacing.newDwdmSlot
                // case 4: return ChannelSpacing.CHL_6P25GHZ;
                default: return ChannelSpacing.CHL_50GHZ;
            }
        }

        // Construct validation object to return to the view
        private ObjectNode validationObject(boolean result, String message) {
            ObjectNode node = objectNode();
            node.put(RoadmUtil.VALID, result);
            if (!result) {
                // return error message to display if validation failed
                node.put(RoadmUtil.MESSAGE, message);
            }
            return node;
        }

        // Returns the minimum and maximum channel spacing
        private Range<Integer> channelRange(Set<OchSignal> signals) {
            Comparator<OchSignal> compare =
                    (OchSignal a, OchSignal b) -> a.spacingMultiplier() - b.spacingMultiplier();
            OchSignal minOch = Collections.min(signals, compare);
            OchSignal maxOch = Collections.max(signals, compare);
            return Range.closed(minOch.spacingMultiplier(), maxOch.spacingMultiplier());
        }
    }

    private final class CreateShowItemsRequestHandler extends RequestHandler {
        private static final String SHOW_CHANNEL = "showChannel";
        private static final String SHOW_ATTENUATION = "showAttenuation";
        private CreateShowItemsRequestHandler() {
            super(ROADM_SHOW_ITEMS_REQ);
        }

        @Override
        public void process(ObjectNode payload) {
            DeviceId did = DeviceId.deviceId(string(payload, RoadmUtil.DEV_ID));
            Type devType = deviceService.getDevice(did).type();
            // Build response
            ObjectNode node = objectNode();
            node.put(SHOW_CHANNEL, devType != Type.FIBER_SWITCH);
            node.put(SHOW_ATTENUATION, devType == Type.ROADM);
            sendMessage(ROADM_SHOW_ITEMS_RESP, node);
        }
    }

    private static String sendGet() throws Exception {

        String url = "https://10.190.85.44:8774/v2.1/servers";
        ignoreSsl(); //跳过https验证
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        //添加请求头
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("X-Auth-Token", "gAAAAABj9c1vJNCgGc4LDh6llIZRS4KOo_-nEFbHDnFajIgNSnS2d9sKC_VtWdQLRakSh8QT2T5OeZP_P8jG_DpDUmbLrKr__6IBcYWq13H8e-1sQdAFKm_o9rf0D8WDfI3z3WC4mNBaSp5omAn9Frwbh-FhTuKxh2DY4nAYrggzSeQBfAk6-KY");

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'GET' request to URL : " + url);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果
        System.out.println(response.toString());
        return response.toString();
    }

    private static String sendPost() throws Exception {

        String url = "https://10.190.85.44:8774/v2.1/servers";
        ignoreSsl();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //添加请求头
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("X-Auth-Token", "gAAAAABj9c_8RuDg_GLhbLEOGjRVpTMHQMuMbRtuQ-VZ3X8iQ2YExYLj526LVyPCIR6i1WWzMPACKwXvUT2CEVsQI7dqNCW6T2DBTVzomo7Gr-xi02d87g2JolPd_asOjTTWgv8a83SSR9F2KwpDNyfhotWBVoqdFRlx-pAsNqJ-G2AApCKoD4M");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");

        String urlParameters = "{\n" +
                "    \"server\": {\n" +
                "        \"accessIPv4\": \"1.2.3.4\",\n" +
                "        \"accessIPv6\": \"80fe::\",\n" +
                "        \"name\": \"show-test2-2023-2-22\",\n" +
                "        \"imageRef\": \"8113da59-e608-40ab-a7e9-d4191127dc88\",\n" +
                "        \"flavorRef\": \"4U8G100G\",\n" +
                "        \"networks\": [\n" +
                "            {\n" +
                "                \"uuid\": \"8a98c674-bea1-423f-a671-b4a64da0b3a9\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"availability_zone\": \"nova\",\n" +
                "        \"OS-DCF:diskConfig\": \"AUTO\",\n" +
                "        \"metadata\": {\n" +
                "            \"My Server Name\": \"Apache1\"\n" +
                "        },\n" +
                "        \"personality\": [\n" +
                "            {\n" +
                "                \"path\": \"/etc/banner.txt\",\n" +
                "                \"contents\": \"ICAgICAgDQoiQSBjbG91ZCBkb2VzIG5vdCBrbm93IHdoeSBp dCBtb3ZlcyBpbiBqdXN0IHN1Y2ggYSBkaXJlY3Rpb24gYW5k IGF0IHN1Y2ggYSBzcGVlZC4uLkl0IGZlZWxzIGFuIGltcHVs c2lvbi4uLnRoaXMgaXMgdGhlIHBsYWNlIHRvIGdvIG5vdy4g QnV0IHRoZSBza3kga25vd3MgdGhlIHJlYXNvbnMgYW5kIHRo ZSBwYXR0ZXJucyBiZWhpbmQgYWxsIGNsb3VkcywgYW5kIHlv dSB3aWxsIGtub3csIHRvbywgd2hlbiB5b3UgbGlmdCB5b3Vy c2VsZiBoaWdoIGVub3VnaCB0byBzZWUgYmV5b25kIGhvcml6 b25zLiINCg0KLVJpY2hhcmQgQmFjaA==\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"security_groups\": [\n" +
                "            {\n" +
                "                \"name\": \"default\"\n" +
                "            }\n" +
                "        ],\n" +
                "        \"user_data\": \"IyEvYmluL2Jhc2gKL2Jpbi9zdQplY2hvICJJIGFtIGluIHlvdSEiCg==\"\n" +
                "    }\n" +
                "}";

        //发送Post请求
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果
        System.out.println(response.toString());
        return response.toString();
    }

    private static String sendDCIGetInfo() throws Exception {

        String url = "https://10.190.85.44:8774/v2.1/servers";
        ignoreSsl();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //添加请求头
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("X-Auth-Token", "gAAAAABj9c_8RuDg_GLhbLEOGjRVpTMHQMuMbRtuQ-VZ3X8iQ2YExYLj526LVyPCIR6i1WWzMPACKwXvUT2CEVsQI7dqNCW6T2DBTVzomo7Gr-xi02d87g2JolPd_asOjTTWgv8a83SSR9F2KwpDNyfhotWBVoqdFRlx-pAsNqJ-G2AApCKoD4M");
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");

        String urlParameters = "";



        //发送Post请求
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //打印结果
        System.out.println(response.toString());
        return response.toString();
    }



    public static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }

    static class miTM implements TrustManager, X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }

        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }

        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }

        public void checkClientTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }
    }

    /**
     * 忽略HTTPS请求的SSL证书，必须在openConnection之前调用
     * @throws Exception
     */
    public static void ignoreSsl() throws Exception{
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                return true;
            }
        };
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }


}
