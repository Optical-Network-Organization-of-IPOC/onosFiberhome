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
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.IOException;
import org.json.JSONObject;
import org.json.JSONArray;

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

    private static final String VM_Delete_Response = "vmDeleteResponse";
    private static final String DCI_Delete_Response = "dciDeleteResponse";

    private static final String THERECEIVE = "receive message";


    private static final String HI_SGP_RESP_DCI = "yiResponse";

    public String theName;
    public String theCpu;
    public String theMemory;
    private static final String HELLOWORLD_REQQQ = "helloworldRequest123";
    private static final String NAME1 = "name1";
    private static final String CPU = "cpu";
    private static final String MEMORY = "memory";

    public String theFirst;
    public String theSecond;
    public String theThird;
    public String theFourth;
    public String theFifth;
    //public String thePost;
    public String theFitos;
    public String thedciPost;

    public String theDCI_1;
    public String theDCI_2;
    public String theDCI_3;
    public String theDCI_4;
    public String theDCI_5;
    public String theDCI_6;

    private static final String THE_DCI_GET = "DCI get message";
    private static final String THE_DCI_POST = "DCI post message";
    private static final String HELLO_DCI_REQ = "dciRequest";
    private static final String THE_DCI_SRC_NEID = "srcNeId";

    private static final String NUM_GET_MESSAGE="num_id";
    private static final String THE_DCI_SRC_CLIENTEP = "srcClientEp";
    private static final String THE_DCI_SRC_PHYSICALCHANNEL = "srcPhysicalChannel";
    private static final String THE_DCI_DST_NEID = "dstNeId";
    private static final String THE_DCI_DST_CLIENTEP = "dstClientEp";
    private static final String THE_DCI_PHYSICALCHANNEL = "dstPhysicalChannel";
    private static final String VM_REQ = "vmCreateRequest";
    private static final String THE_NAME = "name";
    private static final String THE_IMAGE_REF = "imageRef";
    private static final String THE_UUID = "uuid";
    private static final String THE_FLAVOR_REF = "flavorRef";

    //delete clould vm
    private static final String VM_DEL_REQ = "vmDeleteRequest";
    private static final String DCI_DEL_REQ = "dciDeleteRequest";

    private static final String UU_ID = "uuid";
    private static final String VM_ID = "vm_id";

    private String BACK_VM_ID;
    private String DCI_ID;

    private static final String HI_SGP_RESP_DCN = "dcnResponse";

    private static final String HELLO_DCN_REQ = "dcnRequest";
    private static final String THE_DCN_STATUS = "get";
    public String theDCN;
    private static final String DEVICE = "device";
    private static final String PORT = "port";
    private static final String CONTROL = "control";

    private String token="gAAAAABkYdRmkI7nRJYkjzigNmqGMcLyD98orRrRpf5y-0dXY_Ee6J6STXIno-foLa0-NIRTWMD65yAWOBLl4vV8uAIWn-0YnssRBGRCOwpTfBFT3Gqr99mJPWAImhasHH8ADr9Qt9__0prT4y8NNWMc72SPt4XdvBZXpWjJQFaSx_D-StIiKcs";
    private String token_xiaoyun="gAAAAABkYdQ-VTjl_Okf8S8y4lzy9Tm3IkTrQxj2AYtiXt34HdY1Dy1iZCm1ZdeEpNyhPvQU9y7CMMYbOSmhy7JfXz3m-KoB_1a3qvyVTqaOTQHV8Xqc5XcuyWnC0BaSRx7sbcH5-YB981md93LU23vNeKsm4GONxJjBO48Uamo0m0MIgApMPqg";
    private String token_huake="gAAAAABkYdQBP-2zUW3FJYp9d59ZzUHxGdwveQEHNNqdIbKk4UDr7_dO4l4gh-DtJQXeooMdAhGdGP3ZzmN30lVaIsqiR5n7FqQOsA0YgEiApWqlFz9hkLRZX54efe-jBCi4k4sBt1p7ve3fEQDx_eiXx_pGqkUaU-nZLClO4jQWj4pJQKb-BtI";
    //xiaoyunjianlixuniji
    private static final String THERECEIVE_SC = "receive message";
    private static final String HI_SGP_RESP_SC = "hiResponseF2";
    private static final String VM_REQ_SC = "vmCreateRequestF2";
    private static final String THE_NAME_SC = "name2";
    private static final String THE_IMAGE_REF_SC = "imageRef2";
    private static final String THE_UUID_SC = "uuid2";
    private static final String THE_FLAVOR_REF_SC = "flavorRef2";
    public String theFirst_SC;
    public String theSecond_SC;
    public String theThird_SC;
    public String theFourth_SC;
    //xiaoyun shanchu
    private static final String THERECEIVE_SCD = "receive message";

    private static final String Getmes ="get message";
    private static final String HI_SGP_RESP_SCD = "vmDeleteResponseF2";
    private static final String VM_REQ_SCD = "vmDeleteRequestF2";
    private static final String THE_ID_SCD = "id2";
    public String theID_SC;

    //huakeyun jianli xuniji
    private static final String THERECEIVE_HKC = "receive message";
    private static final String HI_SGP_RESP_HKC = "hiResponseH";
    private static final String VM_REQ_HKC = "vmCreateRequestH";
    private static final String THE_NAME_HKC = "name3";
    private static final String THE_IMAGE_REF_HKC = "imageRef3";
    private static final String THE_UUID_HKC = "uuid3";
    private static final String THE_FLAVOR_REF_HKC = "flavorRef3";
    public String theFirst_HKC;
    public String theSecond_HKC;
    public String theThird_HKC;
    public String theFourth_HKC;
    //huakeyun shanchu
    private static final String THERECEIVE_HKCD = "receive message";
    private static final String HI_SGP_RESP_HKCD = "vmDeleteResponseH";
    private static final String VM_REQ_HKCD = "vmDeleteRequestH";
    private static final String THE_ID_HKCD = "id3";
    public String theID_HKC;


    private String thePost="{\n" +
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

    private String thePost_xiaoyun="{\n" +
            "    \"server\": {\n" +
            "        \"accessIPv4\": \"1.2.3.4\",\n" +
            "        \"accessIPv6\": \"80fe::\",\n" +
            "        \"name\": \"new-server-test1\",\n" +
            "        \"imageRef\": \"9a48731e-108f-4b9e-bfee-f86fa35f4cea\",\n" +
            "        \"flavorRef\": \"4U8G100G\",\n" +
            "        \"networks\": [\n" +
            "            {\n" +
            "                \"uuid\": \"f653a2f1-c78a-42f9-85ad-04fc202a06be\"\n" +
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
    private String thetPost_huake="{\n" +
            "    \"server\": {\n" +
            "        \"accessIPv4\": \"1.2.3.4\",\n" +
            "        \"accessIPv6\": \"80fe::\",\n" +
            "        \"name\": \"new-server-test\",\n" +
            "        \"imageRef\": \"f73aeb6f-2780-4ed4-98f5-51370d1ed006\",\n" +
            "        \"flavorRef\": \"4U8G100G\",\n" +
            "        \"networks\": [\n" +
            "            {\n" +
            "                \"uuid\": \"375024fb-bfc6-4b3c-a0c4-8323497bcf16\"\n" +
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
    private String thereturn = null;
    private String thereturn1 = null;
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
                new SayHelloWorld(),
                new SayHelloDCI(),
                new SayHelloFitos(),
                new SayHelloDCN(),
                new DeleteFitosVm(),
                new DeleteDCI(),
                new SaySmallCloud(),
                new SaySmallCloudDelete(),
                new SayHuaKeCloud(),
                new SayHuaKeCloudDelete()
//                new Huake_Cloud_Create_Vm()
        );
    }

    private String num_i=null;

    private final class SayHelloWorld extends RequestHandler {
        private SayHelloWorld(){super(HELLOWORLD_REQQQ);}
        @Override
            public void process(ObjectNode payload) {
            try {

                num_i = string(payload, NUM_GET_MESSAGE);
                switch (num_i) {
                    case "1":
                        String url_d1 = "https://172.23.20.23:8774/v2.1/servers/detail";
                        String Delete_News = sendFitOsGet(url_d1, token_huake);
                        log.info("succuss{}", Delete_News);
                        RoadmFlowViewMessageHandler mm=new RoadmFlowViewMessageHandler();
                        mm.receive(8888);
                        ObjectNode GetMessage = objectNode();
                        GetMessage.put(Getmes,Delete_News);
                        sendMessage(HI_SGP_RESP, GetMessage);
                        break;
                    case "2":
                        String url_d2 = "https://10.190.85.44:8774/v2.1/servers/detail";
                        String Delete_News2 = sendFitOsGet(url_d2, token);
                        log.info("succuss{}", Delete_News2);
                        ObjectNode GetMessage2 = objectNode();
                        GetMessage2.put(Getmes,Delete_News2);
                        sendMessage(HI_SGP_RESP, GetMessage2);
                        break;
                    case "3":
                        String url_d3 = "https://10.190.85.154:8774/v2.1/servers/detail";
                        String Delete_News3 = sendFitOsGet(url_d3, token_xiaoyun);
                        log.info("succuss{}", Delete_News3);
                        ObjectNode GetMessage3 = objectNode();
                        GetMessage3.put(Getmes,Delete_News3);
                        sendMessage(HI_SGP_RESP, GetMessage3);
                        break;

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private final class SayHelloDCI extends RequestHandler {
        private SayHelloDCI() {
            super(HELLO_DCI_REQ);
        }

        public void process(ObjectNode payload) {
            theDCI_1 = string(payload, THE_DCI_SRC_NEID);
            log.info("The DCI first word : {}", theDCI_1);
            theDCI_2 = string(payload, THE_DCI_SRC_CLIENTEP);
            log.info("The DCI second word : {}", theDCI_2);
            theDCI_3 = string(payload, THE_DCI_SRC_PHYSICALCHANNEL);
            log.info("The DCI third word : {}", theDCI_3);
            theDCI_4 = string(payload, THE_DCI_DST_NEID);
            log.info("The DCI fourth word : {}", theDCI_4);
            theDCI_5 = string(payload, THE_DCI_DST_CLIENTEP);
            log.info("The DCI fifth word : {}", theDCI_5);
            theDCI_6 = string(payload, THE_DCI_PHYSICALCHANNEL);
            log.info("The DCI sixth word : {}", theDCI_6);
            //String theResponse_DCI = "{\n" +
            thedciPost = "{\n" +
                    "    \"fec\": \"super\",\n" +
                    "    \"serviceType\": \"10GE-LAN\",\n" +
                    "    \"mappingType\": \"10GE-LAN->ODU2->ODU4->ODUC2->OTUC2\",\n" +
                    "    \"mappingMode\": \"GFP6.2\",\n" +
                    "    \"modemType\": 3,\n" +
                    "    \"planCheck\": false,\n" +
                    "    \"plan\": {\n" +
                    "        \"start\": 0,\n" +
                    "        \"end\": 0\n" +
                    "    },\n" +
                    "    \"srcNeId\": \"78q\",\n" +
                    "    \"srcClientEp\": \"78_PORT-1-1-C1\",\n" +
                    "    \"srcPhysicalChannel\": \"2m\",\n" +
                    "    \"dstNeId\": \"79q\",\n" +
                    "    \"dstClientEp\": \"79_PORT-1-1-C1\",\n" +
                    "    \"dstPhysicalChannel\": \"3m\",\n" +
                    "    \"waveFrequency\": \"CE30(193.10000THz-1552.52nm)\",\n" +
                    "    \"protectType\": \"NO_PROTECTION\",\n" +
                    "    \"protectionInfo\": null,\n" +
                    "    \"relaPrimaryLinks\": [\n" +
                    "        {\n" +
                    "            \"uuid\": \"19-16\",\n" +
                    "            \"srcNeUuid\": \"78q\",\n" +
                    "            \"dstNeUuid\": \"79q\",\n" +
                    "            \"srcPort\": \"78_PORT-1-1-L1\",\n" +
                    "            \"dstPort\": \"79_PORT-1-1-L1\",\n" +
                    "            \"id\": \"16-19\",\n" +
                    "            \"config\": true,\n" +
                    "            \"length\": 100,\n" +
                    "            \"status\": \"ACTIVE\",\n" +
                    "            \"name\": \"link\",\n" +
                    "            \"delay\": 0,\n" +
                    "            \"srcCard\": \"78_LINECARD-1-1\",\n" +
                    "            \"dstCard\": \"79_LINECARD-1-1\",\n" +
                    "            \"srcCardModel\": \"2OC2EA_D\",\n" +
                    "            \"dstCardModel\": \"2OC2EA_D\",\n" +
                    "            \"srcMergeNe\": \"78q\",\n" +
                    "            \"dstMergeNe\": \"79q\",\n" +
                    "            \"srcNeIp\": \"10.190.85.78q/24\",\n" +
                    "            \"dstNeIp\": \"10.190.85.79q/24\",\n" +
                    "            \"srcPortShowName\": \"OCH-1\",\n" +
                    "            \"dstPortShowName\": \"OCH-1\",\n" +
                    "            \"srcCardLocation\": \"1\",\n" +
                    "            \"dstCardLocation\": \"1\",\n" +
                    "            \"srcChassisLocation\": \"1\",\n" +
                    "            \"dstChassisLocation\": \"1\",\n" +
                    "            \"links\": [\n" +
                    "                {\n" +
                    "                    \"uuid\": \"19\",\n" +
                    "                    \"srcNeUuid\": \"78q\",\n" +
                    "                    \"dstNeUuid\": \"79q\",\n" +
                    "                    \"srcPort\": \"78_PORT-1-1-L1\",\n" +
                    "                    \"dstPort\": \"79_PORT-1-1-L1\",\n" +
                    "                    \"id\": null,\n" +
                    "                    \"config\": true,\n" +
                    "                    \"length\": 100,\n" +
                    "                    \"status\": \"ACTIVE\",\n" +
                    "                    \"name\": \"link\",\n" +
                    "                    \"delay\": 0,\n" +
                    "                    \"srcCard\": \"78_LINECARD-1-1\",\n" +
                    "                    \"dstCard\": \"79_LINECARD-1-1\",\n" +
                    "                    \"srcCardModel\": \"2OC2EA_D\",\n" +
                    "                    \"dstCardModel\": \"2OC2EA_D\",\n" +
                    "                    \"srcMergeNe\": \"78q\",\n" +
                    "                    \"dstMergeNe\": \"79q\",\n" +
                    "                    \"srcNeIp\": \"10.190.85.78q/24\",\n" +
                    "                    \"dstNeIp\": \"10.190.85.79q/24\",\n" +
                    "                    \"srcPortShowName\": \"OCH-1\",\n" +
                    "                    \"dstPortShowName\": \"OCH-1\",\n" +
                    "                    \"srcCardLocation\": \"1\",\n" +
                    "                    \"dstCardLocation\": \"1\",\n" +
                    "                    \"srcChassisLocation\": \"1\",\n" +
                    "                    \"dstChassisLocation\": \"1\",\n" +
                    "                    \"links\": null,\n" +
                    "                    \"srcNeName\": \"78q\",\n" +
                    "                    \"dstNeName\": \"79q\"\n" +
                    "                },\n" +
                    "                {\n" +
                    "                    \"uuid\": \"16\",\n" +
                    "                    \"srcNeUuid\": \"79q\",\n" +
                    "                    \"dstNeUuid\": \"78q\",\n" +
                    "                    \"srcPort\": \"79_PORT-1-1-L1\",\n" +
                    "                    \"dstPort\": \"78_PORT-1-1-L1\",\n" +
                    "                    \"id\": null,\n" +
                    "                    \"config\": true,\n" +
                    "                    \"length\": 100,\n" +
                    "                    \"status\": \"ACTIVE\",\n" +
                    "                    \"name\": \"link\",\n" +
                    "                    \"delay\": 0,\n" +
                    "                    \"srcCard\": \"79_LINECARD-1-1\",\n" +
                    "                    \"dstCard\": \"78_LINECARD-1-1\",\n" +
                    "                    \"srcCardModel\": \"2OC2EA_D\",\n" +
                    "                    \"dstCardModel\": \"2OC2EA_D\",\n" +
                    "                    \"srcMergeNe\": \"79q\",\n" +
                    "                    \"dstMergeNe\": \"78q\",\n" +
                    "                    \"srcNeIp\": \"10.190.85.79q/24\",\n" +
                    "                    \"dstNeIp\": \"10.190.85.78q/24\",\n" +
                    "                    \"srcPortShowName\": \"OCH-1\",\n" +
                    "                    \"dstPortShowName\": \"OCH-1\",\n" +
                    "                    \"srcCardLocation\": \"1\",\n" +
                    "                    \"dstCardLocation\": \"1\",\n" +
                    "                    \"srcChassisLocation\": \"1\",\n" +
                    "                    \"dstChassisLocation\": \"1\",\n" +
                    "                    \"links\": null,\n" +
                    "                    \"srcNeName\": \"79q\",\n" +
                    "                    \"dstNeName\": \"78q\"\n" +
                    "                }\n" +
                    "            ],\n" +
                    "            \"srcNeName\": \"78q\",\n" +
                    "            \"dstNeName\": \"79q\"\n" +
                    "        }\n" +
                    "    ],\n" +
                    "    \"relaBackupLinks\": [],\n" +
                    "    \"priLinksType\": \"OCH\",\n" +
                    "    \"backLinksType\": \"\"\n" +
                    "}";
            String replace_78="fuiqwehuifhuin";
            String replace_78_PORT="fhweuihfuifewh";
            thedciPost = thedciPost.replace("78q",replace_78);
            thedciPost = thedciPost.replace("78_PORT-1-1-C1",replace_78_PORT);
            thedciPost = thedciPost.replace("2m",theDCI_3);
            thedciPost = thedciPost.replace("79q",theDCI_4);
            thedciPost = thedciPost.replace("79_PORT-1-1-C1",theDCI_5);
            thedciPost = thedciPost.replace("3m",theDCI_6);
            thedciPost = thedciPost.replace(replace_78,theDCI_1);
            thedciPost = thedciPost.replace(replace_78_PORT,theDCI_2);
            log.info("The dci post input word : {}", thedciPost);
            String theResponse_DCI_GETINFO = null;
            String theResponse_DCI_POST = null;
            try{
                theResponse_DCI_GETINFO = sendDCIGetInfo();
                theResponse_DCI_POST = sendDCIPost(thedciPost);
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("The dci get word : {}", theResponse_DCI_GETINFO);
            log.info("The dci post word : {}", theResponse_DCI_POST);
            ObjectNode test_DCI = objectNode();
            test_DCI.put(THE_DCI_GET, theResponse_DCI_GETINFO);
            test_DCI.put(THE_DCI_POST, theResponse_DCI_POST);
            sendMessage(HI_SGP_RESP_DCI, test_DCI);

         }
    }
    private final class DeleteDCI extends RequestHandler{
        private DeleteDCI() {
            super(DCI_DEL_REQ);
        }

        public void process(ObjectNode payload) {
            DCI_ID = string(payload, UU_ID);
            String mylist ="[\"8a8a8a8d85fb53350187e4ab50980097\"]";
            mylist=mylist.replace("8a8a8a8d85fb53350187e4ab50980097",DCI_ID);
            log.info("The DCI_ID  word : {}", mylist);
            String Delete_News = null;
            try{
                //String url = "https://10.190.85.44:8774/v2.1/servers";
                /*
                da yun: https://10.190.85.44:8774/v2.1/servers
                xiao yun: https://10.190.85.154:8774/v2.1/servers
                huake yun: https://172.23.20.23:8774/v2.1/servers
                 */
                //String url = "https://10.190.85.44:8774/v2.1/servers/{server_id}";
                //url = url.replace("{server_id}",BACK_VM_ID);
                //log.info("The url  word : {}", url);
                Delete_News=sendDCIDelete(mylist);
               // Delete_News=sendFitOsDelete(url,token);
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("The Delete_news: {}", Delete_News);
            ObjectNode test = objectNode();
            test.put(THERECEIVE,Delete_News);
            sendMessage(DCI_Delete_Response, test);
        }
    }

    private final class DeleteFitosVm extends RequestHandler{
        private DeleteFitosVm() {
            super(VM_DEL_REQ);
        }

        public void process(ObjectNode payload) {
            BACK_VM_ID = string(payload, VM_ID);
            log.info("The BACK_VM_ID  word : {}", BACK_VM_ID);
            String Delete_News = null;
            try{
                //String url = "https://10.190.85.44:8774/v2.1/servers";
                /*
                da yun: https://10.190.85.44:8774/v2.1/servers
                xiao yun: https://10.190.85.154:8774/v2.1/servers
                huake yun: https://172.23.20.23:8774/v2.1/servers
                 */
                String url = "https://10.190.85.44:8774/v2.1/servers/{server_id}";
                url = url.replace("{server_id}",BACK_VM_ID);
                log.info("The url  word : {}", url);
                Delete_News=sendFitOsDelete(url,token);
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("The Delete_news: {}", Delete_News);
            ObjectNode test = objectNode();
            test.put(THERECEIVE,Delete_News);
            sendMessage(VM_Delete_Response, test);
        }
    }

    private final class SayHelloFitos extends RequestHandler{
        private SayHelloFitos() {
                super(VM_REQ);
        }

        public void process(ObjectNode payload) {
            theFirst = string(payload, THE_NAME);
            log.info("The first word : {}", theFirst);
            theSecond = string(payload, THE_IMAGE_REF);
            log.info("The second word : {}", theSecond);
            theThird = string(payload, THE_UUID);
            log.info("The Third word : {}", theThird);
            theFourth = string(payload, THE_FLAVOR_REF);
            log.info("The Fourth word : {}", theFourth);

            thePost ="{\n" +
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
            thePost = thePost.replace("show-test2-2023-2-22",theFirst);
            thePost = thePost.replace("8113da59-e608-40ab-a7e9-d4191127dc88",theSecond);
            thePost = thePost.replace("8a98c674-bea1-423f-a671-b4a64da0b3a9",theThird);
            thePost = thePost.replace("4U8G100G",theFourth);
            //theFifth = string(payload, thePost);
           // log.info("The Post word : {}", thePost);
            String thereturn = null;
            String thereturn1 = null;

            //recive vm

            //log.info("")

            try{
                //String url = "https://10.190.85.44:8774/v2.1/servers";
                /*
                da yun: https://10.190.85.44:8774/v2.1/servers
                xiao yun: https://10.190.85.154:8774/v2.1/servers
                huake yun: https://172.23.20.23:8774/v2.1/servers
                 */
                String url = "https://10.190.85.44:8774/v2.1/servers/detail";
                String urlpost="https://10.190.85.44:8774/v2.1/servers";
                thereturn1 =  sendFitOsPost(urlpost,token,thePost);
                thereturn = sendFitOsGet(url,token);
            }catch (Exception e){
                e.printStackTrace();
            }
            //log.info("The Return word : {}", thereturn);
            ObjectNode test = objectNode();
            //test.put(THERECEIVE, theUSER_ID);
            test.put(THERECEIVE,thereturn);
            sendMessage(HI_SGP_RESP, test);
        }
    }

    private final class SayHelloDCN extends RequestHandler {
        private SayHelloDCN() {
            super(HELLO_DCN_REQ);
        }
        public void process(ObjectNode payload) {
            theDCN = string(payload, THE_DCN_STATUS);
            log.info("The QINGHUA word : {}", theDCN);
            String theResponse_DCN_DEVICE = null;
            String theResponse_DCN_PORT = null;
            String theResponse_DCN_CONTROL = null;
            try{
                theResponse_DCN_DEVICE = getDcnBoardDevice();
                theResponse_DCN_PORT = getDcnBoardPort();
                theResponse_DCN_CONTROL = getDcnNodeInfo();
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("The Response_DCN_DEVICE word : {}", theResponse_DCN_DEVICE);
            log.info("The Response_DCN_PORT word : {}", theResponse_DCN_PORT);
            log.info("The Response_DCN_CONTROL word : {}", theResponse_DCN_CONTROL);
            ObjectNode test_DCN = objectNode();
            test_DCN.put(DEVICE, theResponse_DCN_DEVICE);
            test_DCN.put(PORT, theResponse_DCN_PORT);
            test_DCN.put(CONTROL, theResponse_DCN_CONTROL);
            sendMessage(HI_SGP_RESP_DCN, test_DCN);
        }
    }

    private final class SaySmallCloud extends RequestHandler {
        private SaySmallCloud() {
            super(VM_REQ_SC);
        }
        @Override
        public void process(ObjectNode payload) {
            theFirst_SC = string(payload, THE_NAME_SC);
            log.info("The first word : {}", theFirst_SC);
            theSecond_SC = string(payload, THE_IMAGE_REF_SC);
            log.info("The second word : {}", theSecond_SC);
            theThird_SC = string(payload, THE_UUID_SC);
            log.info("The Third word : {}", theThird_SC);
            theFourth_SC = string(payload, THE_FLAVOR_REF_SC);
            log.info("The Fourth word : {}", theFourth_SC);

            //token="gAAAAABkVccL-AIhdDZNmzO-ssaHg4TiNom_PcYsLWCdqUkUzQ3R1AIIKVGij21LReCBQ5VcS5vfA7Kbi0uj2eCBwv5EJd1NoC4QdHYQyPUYHdC9pJSNW5xP5DAlDg89R-D-vogUjEWg0rQynI1qX5jUOXuRPb0qmOpB0jwvsX7tOBc2qRdpiW0"
            try{
                //String url = "https://10.190.85.44:8774/v2.1/servers";
                /*
                da yun: https://10.190.85.44:8774/v2.1/servers
                xiao yun: https://10.190.85.154:8774/v2.1/servers
                huake yun: https://172.23.20.23:8774/v2.1/servers
                 */
                String url = "https://10.190.85.154:8774/v2.1/servers/detail";
                String urlpost="https://10.190.85.154:8774/v2.1/servers";

                thePost_xiaoyun = thePost_xiaoyun.replace("new-server-test1",theFirst_SC);
                //thePost_xiaoyun = thePost_xiaoyun.replace("8113da59-e608-40ab-a7e9-d4191127dc88",theSecond_SC);
                //thePost_xiaoyun = thePost_xiaoyun.replace("8a98c674-bea1-423f-a671-b4a64da0b3a9",theThird_SC);
                //thePost_xiaoyun = thePost_xiaoyun.replace("4U8G100G",theFourth_SC);
                thereturn1 =  sendFitOsPost(urlpost,token_xiaoyun,thePost_xiaoyun);
                thereturn = sendFitOsGet(url,token_xiaoyun);
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("huake_return_news:{}",thereturn);
            ObjectNode test_sc = objectNode();
            test_sc.put(THERECEIVE_SC, thereturn);
            sendMessage(HI_SGP_RESP_SC, test_sc);

        }
    }


    private final class SaySmallCloudDelete extends RequestHandler {
        private SaySmallCloudDelete() {
            super(VM_REQ_SCD);
        }

        public void process(ObjectNode payload) {
            theID_SC = string(payload, THE_ID_SCD);
            log.info("The QINGHUA word : {}", theID_SC);
            String Delete_News = null;

            try{
                //String url = "https://10.190.85.44:8774/v2.1/servers";
                /*
                da yun: https://10.190.85.44:8774/v2.1/servers
                xiao yun: https://10.190.85.154:8774/v2.1/servers
                huake yun: https://172.23.20.23:8774/v2.1/servers
                 */

                String url = "https://10.190.85.154:8774/v2.1/servers/{server_id}";
                url = url.replace("{server_id}",theID_SC);
                log.info("The url  word : {}", url);
                Delete_News=sendFitOsDelete(url,token_xiaoyun);
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("The Delete_news: {}", Delete_News);

            //tring theResponse_smd = "success delete small cloud";
            ObjectNode test_smd = objectNode();
            test_smd.put(THERECEIVE_SCD, Delete_News);
            sendMessage(HI_SGP_RESP_SCD, test_smd);

        }
    }


    private final class SayHuaKeCloud extends RequestHandler {
        private SayHuaKeCloud() {
            super(VM_REQ_HKC);
        }
        @Override
        public void process(ObjectNode payload) {
            theFirst_HKC = string(payload, THE_NAME_HKC);
            log.info("The first word : {}", theFirst_HKC);
            theSecond_HKC = string(payload, THE_IMAGE_REF_HKC);
            log.info("The second word : {}", theSecond_HKC);
            theThird_HKC = string(payload, THE_UUID_HKC);
            log.info("The Third word : {}", theThird_HKC);
            theFourth_HKC = string(payload, THE_FLAVOR_REF_HKC);
            log.info("The Fourth word : {}", theFourth_HKC);

            //token="gAAAAABkVccL-AIhdDZNmzO-ssaHg4TiNom_PcYsLWCdqUkUzQ3R1AIIKVGij21LReCBQ5VcS5vfA7Kbi0uj2eCBwv5EJd1NoC4QdHYQyPUYHdC9pJSNW5xP5DAlDg89R-D-vogUjEWg0rQynI1qX5jUOXuRPb0qmOpB0jwvsX7tOBc2qRdpiW0"
            try{
                //String url = "https://10.190.85.44:8774/v2.1/servers";
                /*
                da yun: https://10.190.85.44:8774/v2.1/servers
                xiao yun: https://10.190.85.154:8774/v2.1/servers
                huake yun: https://172.23.20.23:8774/v2.1/servers
                 */
                String url = "https://172.23.20.23:8774/v2.1/servers/detail";
                String urlpost="https://172.23.20.23:8774/v2.1/servers";
                thetPost_huake = thetPost_huake.replace("new-server-test",theFirst_HKC);
                thereturn1 =  sendFitOsPost(urlpost,token_huake,thetPost_huake);
                thereturn = sendFitOsGet(url,token_huake);

            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("huake_return_news:{}",thereturn);
            log.info("huake_return1_news:{}",thereturn1);
            ObjectNode test_hkc = objectNode();
            test_hkc.put(THERECEIVE_HKC, thereturn);
            sendMessage(HI_SGP_RESP_HKC, test_hkc);

        }
    }



    private final class SayHuaKeCloudDelete extends RequestHandler {
        private SayHuaKeCloudDelete() {
            super(VM_REQ_HKCD);
        }

        public void process(ObjectNode payload) {
            theID_HKC = string(payload, THE_ID_HKCD);
            log.info("The QINGHUA word : {}", theID_HKC);
            String Delete_News = null;
            try{
                //String url = "https://10.190.85.44:8774/v2.1/servers";
                /*
                da yun: https://10.190.85.44:8774/v2.1/servers
                xiao yun: https://10.190.85.154:8774/v2.1/servers
                huake yun: https://172.23.20.23:8774/v2.1/servers
                 */

                String url = "https://172.23.20.23:8774/v2.1/servers/{server_id}";
                url = url.replace("{server_id}",theID_HKC);
                log.info("The url  word : {}", url);
                Delete_News=sendFitOsDelete(url,token_huake);
            }catch (Exception e){
                e.printStackTrace();
            }
            log.info("The Delete_news: {}", Delete_News);

            //String theResponse_hkdel = "success delete small cloud";
            ObjectNode test_hkdel = objectNode();
            test_hkdel.put(THERECEIVE_HKCD, Delete_News);
            sendMessage(HI_SGP_RESP_HKCD, test_hkdel);

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
                if (range == null) {
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
                    case 0:
                        return ChannelSpacing.CHL_100GHZ;
                    case 1:
                        return ChannelSpacing.CHL_50GHZ;
                    case 2:
                        return ChannelSpacing.CHL_25GHZ;
                    case 3:
                        return ChannelSpacing.CHL_12P5GHZ;
                    // 6.25GHz cannot be used with ChannelSpacing.newDwdmSlot
                    // case 4: return ChannelSpacing.CHL_6P25GHZ;
                    default:
                        return ChannelSpacing.CHL_50GHZ;
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

        private static String sendFitOsGet(String url,String token) throws Exception {
            /*
             get.'url' = post.'url' + '/details'
             */
            //String url = "https://10.190.85.44:8774/v2.1/servers/details";
            ignoreSsl(); //跳过https验证
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //默认值我GET
            con.setRequestMethod("GET");

            //添加请求头
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("X-Auth-Token", token);

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

        private static String sendFitOsPost(String url,String token, String requestJsonBody) throws Exception {

            //String url = "https://10.190.85.44:8774/v2.1/servers";
            ignoreSsl();
            URL obj = new URL(url);
            HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

            //添加请求头
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            con.setRequestProperty("X-Auth-Token", token);
            con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
            con.setRequestProperty("Content-Type", "application/json");
            String urlParameters = requestJsonBody;
            /*urlParameters = "{\n" +
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
             */

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

    private void osmmessage(String request){
            //String request = "0:[3,name1,name2,name3]:[2,name1,name2]:[3,name1,name2,name3]";
            //String request ="1 :[2, HUST-1, HUST-2]:[1, fiberhome_bigcloud-1]:[2, fiberhome_smallcloud-1, fiberhome_smallcloud-2]"
            String[] request1 = request.split(":");
            //log.info("osmmessage{}",request);
            //log.info("{}",request1);
            if (request1[0].equals("1 ")) {
                log.info("keyi");
                for (int i = 1; i < 4; i++) {
                    String[] cloud = request1[i].split(", ");
                    //log.info("cloud{}",cloud);
                    String result = cloud[0].substring(1, 2);
                    //System.out.println(result);
                    int mmm = Integer.parseInt(result);
                    log.info("{}",mmm);
                    //System.out.println(mmm);
                    for (int j = 1; j <= mmm; j++) {
                        //result1[mmm]
                        //cloud[j]就是name
                        switch (i) {
                            case 1:
                                if (j != mmm) {
                                    //System.out.println("1");
                                    //System.out.println(cloud[j]);
                                    try{
                                        String thetPost_huake="{\n" +
                                                "    \"server\": {\n" +
                                                "        \"accessIPv4\": \"1.2.3.4\",\n" +
                                                "        \"accessIPv6\": \"80fe::\",\n" +
                                                "        \"name\": \"new-server-test\",\n" +
                                                "        \"imageRef\": \"f73aeb6f-2780-4ed4-98f5-51370d1ed006\",\n" +
                                                "        \"flavorRef\": \"4U8G100G\",\n" +
                                                "        \"networks\": [\n" +
                                                "            {\n" +
                                                "                \"uuid\": \"375024fb-bfc6-4b3c-a0c4-8323497bcf16\"\n" +
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
                                        String url = "https://172.23.20.23:8774/v2.1/servers/detail";
                                        String urlpost="https://172.23.20.23:8774/v2.1/servers";
                                        thetPost_huake = thetPost_huake.replace("new-server-test",cloud[j]);
                                        thereturn1 =  sendFitOsPost(urlpost,token_huake,thetPost_huake);
                                        log.info("cloud[j]{}",cloud[j]);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;

                                } else {
                                    String lastname = cloud[j].substring(0, cloud[j].lastIndexOf("]"));
                                    //log.info("{}",lastname);
                                    //System.out.println(lastname);
                                    try{
                                        String thetPost_huake="{\n" +
                                                "    \"server\": {\n" +
                                                "        \"accessIPv4\": \"1.2.3.4\",\n" +
                                                "        \"accessIPv6\": \"80fe::\",\n" +
                                                "        \"name\": \"new-server-test\",\n" +
                                                "        \"imageRef\": \"f73aeb6f-2780-4ed4-98f5-51370d1ed006\",\n" +
                                                "        \"flavorRef\": \"4U8G100G\",\n" +
                                                "        \"networks\": [\n" +
                                                "            {\n" +
                                                "                \"uuid\": \"375024fb-bfc6-4b3c-a0c4-8323497bcf16\"\n" +
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
                                        String url = "https://172.23.20.23:8774/v2.1/servers/detail";
                                        String urlpost="https://172.23.20.23:8774/v2.1/servers";
                                        thetPost_huake = thetPost_huake.replace("new-server-test",lastname);
                                        thereturn1 =  sendFitOsPost(urlpost,token_huake,thetPost_huake);
                                        log.info("lastname[j]{}",lastname);
                                        //log.info("{}",thereturn1);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;
                                }

                            case 2:
                                if (j != mmm) {
                                    //System.out.println("1");
                                    //System.out.println(cloud[j]);
                                    try{
                                        String thePost="{\n" +
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
                                        thePost = thePost.replace("show-test2-2023-2-22",cloud[j]);
                                        String url = "https://10.190.85.44:8774/v2.1/servers/detail";
                                        String urlpost="https://10.190.85.44:8774/v2.1/servers";
                                        String thereturn1 =  sendFitOsPost(urlpost,token,thePost);
                                        log.info("cloud[j]{}",cloud[j]);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;

                                } else {
                                    String lastname = cloud[j].substring(0, cloud[j].lastIndexOf("]"));
                                    //System.out.println(lastname);
                                    try{
                                        String thePost="{\n" +
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
                                        thePost = thePost.replace("show-test2-2023-2-22",lastname);
                                        String url = "https://10.190.85.44:8774/v2.1/servers/detail";
                                        String urlpost="https://10.190.85.44:8774/v2.1/servers";
                                        String thereturn1 =  sendFitOsPost(urlpost,token,thePost);
                                        log.info("cloud[j]{}",lastname);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;
                                }
                            case 3:
                                if (j != mmm) {
                                    //System.out.println("1");
                                    //System.out.println(cloud[j]);
                                    try{

                                        String url = "https://10.190.85.154:8774/v2.1/servers/detail";
                                        String urlpost="https://10.190.85.154:8774/v2.1/servers";
                                        String thePost_xiaoyun="{\n" +
                                                "    \"server\": {\n" +
                                                "        \"accessIPv4\": \"1.2.3.4\",\n" +
                                                "        \"accessIPv6\": \"80fe::\",\n" +
                                                "        \"name\": \"new-server-test1\",\n" +
                                                "        \"imageRef\": \"9a48731e-108f-4b9e-bfee-f86fa35f4cea\",\n" +
                                                "        \"flavorRef\": \"4U8G100G\",\n" +
                                                "        \"networks\": [\n" +
                                                "            {\n" +
                                                "                \"uuid\": \"f653a2f1-c78a-42f9-85ad-04fc202a06be\"\n" +
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
                                        thePost_xiaoyun = thePost_xiaoyun.replace("new-server-test1",cloud[j]);
                                        String thereturn1 =  sendFitOsPost(urlpost,token_xiaoyun,thePost_xiaoyun);
                                        //log.info("thereturn1:{}",);
                                        log.info("cloud[j]{}",cloud[j]);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;

                                } else {
                                    String lastname = cloud[j].substring(0, cloud[j].lastIndexOf("]"));
                                    //System.out.println(lastname);
                                    try{
                                        String thePost_xiaoyun="{\n" +
                                                "    \"server\": {\n" +
                                                "        \"accessIPv4\": \"1.2.3.4\",\n" +
                                                "        \"accessIPv6\": \"80fe::\",\n" +
                                                "        \"name\": \"new-server-test1\",\n" +
                                                "        \"imageRef\": \"9a48731e-108f-4b9e-bfee-f86fa35f4cea\",\n" +
                                                "        \"flavorRef\": \"4U8G100G\",\n" +
                                                "        \"networks\": [\n" +
                                                "            {\n" +
                                                "                \"uuid\": \"f653a2f1-c78a-42f9-85ad-04fc202a06be\"\n" +
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
                                        String url = "https://10.190.85.154:8774/v2.1/servers/detail";
                                        String urlpost="https://10.190.85.154:8774/v2.1/servers";
                                        //thePost_xiaoyun=thePost_xiaoyun.replace("")
                                        thePost_xiaoyun = thePost_xiaoyun.replace("new-server-test1",lastname);
                                        String thereturn1 =  sendFitOsPost(urlpost,token_xiaoyun,thePost_xiaoyun);
                                        log.info("cloud[j]{}",lastname);
                                        //log.info("thereturn1:{}",thereturn1);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                    break;

                                }
                        }
                    }
                    // System.out.println(result);
                }
            }
            if (request1[0].equals("0 ")) {
                for (int i = 1; i < 4; i++) {
                    String[] cloud = request1[i].split(", ");
                    String result = cloud[0].substring(1, 2);
                    //System.out.println(result);
                    int mmm = Integer.parseInt(result);
                    //System.out.println(mmm);
                    for (int j = 1; j <= mmm; j++) {
                        //result1[mmm]
                        //cloud[j]就是name
                        switch (i) {
                            case 1:
                                if (j != mmm) {
                                    //System.out.println("1");
                                    //System.out.println(cloud[j]);
                                    try{
                                        String url_d = "https://172.23.20.23:8774/v2.1/servers/{server_id}";
                                        String url = "https://172.23.20.23:8774/v2.1/servers/detail";
                                        RoadmFlowViewMessageHandler roadmFlowViewMessageHandler=new RoadmFlowViewMessageHandler();
                                        roadmFlowViewMessageHandler.getid(cloud[j],url,url_d,token_huake);
                                        log.info("{}",cloud[j]);

                                    }
                                    catch (Exception e){
                                        e.printStackTrace();

                                    }
                                    break;
                                } else {
                                    String lastname = cloud[j].substring(0, cloud[j].lastIndexOf("]"));
                                    //System.out.println(lastname);

                                    //log.info("The url  word : {}", url)
                                    try{
                                        String url_d = "https://172.23.20.23:8774/v2.1/servers/{server_id}";
                                        String url = "https://172.23.20.23:8774/v2.1/servers/detail";
                                        RoadmFlowViewMessageHandler roadmFlowViewMessageHandler=new RoadmFlowViewMessageHandler();
                                        roadmFlowViewMessageHandler.getid(lastname,url,url_d,token_huake);
                                        log.info("{}",lastname);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }
                                break;
                            case 2:
                                if (j != mmm) {
                                    //System.out.println("1");
                                    //System.out.println(cloud[j]);
                                    //da yun: https://10.190.85.44:8774/v2.1/servers
                                    //xiao yun: https://10.190.85.154:8774/v2.1/servers
                                    //huake yun: https://172.23.20.23:8774/v2.1/servers
                                    try{

                                        String url_d = "https://10.190.85.44:8774/v2.1/servers/{server_id}";
                                        String url = "https://10.190.85.44:8774/v2.1/servers/detail";
                                        RoadmFlowViewMessageHandler roadmFlowViewMessageHandler=new RoadmFlowViewMessageHandler();
                                        roadmFlowViewMessageHandler.getid(cloud[j],url,url_d,token);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    String lastname = cloud[j].substring(0, cloud[j].lastIndexOf("]"));
                                    //System.out.println(lastname);
                                    try{
                                        String url_d = "https://10.190.85.44:8774/v2.1/servers/{server_id}";
                                        String url = "https://10.190.85.44:8774/v2.1/servers/detail";
                                        RoadmFlowViewMessageHandler roadmFlowViewMessageHandler=new RoadmFlowViewMessageHandler();
                                        roadmFlowViewMessageHandler.getid(lastname,url,url_d,token);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                                break;
                            case 3:
                                if (j != mmm) {
                                    //System.out.println("1");
                                    //System.out.println(cloud[j]);
                                    try{

                                        String url = "https://10.190.85.154:8774/v2.1/servers/detail";
                                        String url_d = "https://10.190.85.154:8774/v2.1/servers/{server_id}";
                                        //String url = "https://172.23.20.23:8774/v2.1/servers/detail";
                                        RoadmFlowViewMessageHandler roadmFlowViewMessageHandler=new RoadmFlowViewMessageHandler();
                                        roadmFlowViewMessageHandler.getid(cloud[j],url,url_d,token_xiaoyun);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                } else {
                                    String lastname = cloud[j].substring(0, cloud[j].lastIndexOf("]"));
                                    //System.out.println(lastname);
                                    try{
                                        String url_d = "https://10.190.85.154:8774/v2.1/servers/{server_id}";
                                        String url = "https://10.190.85.154:8774/v2.1/servers/detail";
                                        RoadmFlowViewMessageHandler roadmFlowViewMessageHandler=new RoadmFlowViewMessageHandler();
                                        roadmFlowViewMessageHandler.getid(lastname,url,url_d,token_xiaoyun);
                                    }
                                    catch (Exception e){
                                        e.printStackTrace();
                                    }

                                }
                                break;
                        }
                    }
                }
            }

    }
    private void getid(String searchname,String url,String url_d,String token){
        //log.info("chenggong:{}",thereturn);
        try{
            thereturn = sendFitOsGet(url,token);
            JSONObject jsonArray=new JSONObject(thereturn);
            JSONArray array=jsonArray.getJSONArray("servers");

            // 遍历 JSONArray，查找匹配名称的对象的ID
            String targetId = null;
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String name = object.getString("name");
                //log.info("name:{}",name);
                if (name.equals(searchname)) {
                    targetId = object.getString("id");
                    break;
                }
            }
            if (targetId != null){
                url_d=url_d.replace("{server_id}",targetId);
                String Delete_News=sendFitOsDelete(url_d,token);
                log.info("succuss{}",Delete_News);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    private void receive(int port){
        try {
            ServerSocket serverSocket = new ServerSocket(port); // 监听指定的端口
            log.info("start receive things");
            while (true) {
                Socket socket = serverSocket.accept(); // 阻塞等待客户端连接
                InputStream is = socket.getInputStream();
                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);
                String info = null;
                while ((info = br.readLine()) != null) { // 阻塞等待接收客户端发送的消息
                    log.info("receive message is {}", info);
                    RoadmFlowViewMessageHandler roadmFlowViewMessageHandler=new RoadmFlowViewMessageHandler();
                    roadmFlowViewMessageHandler.osmmessage(info);
                }
                socket.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private static String sendFitOsDelete(String url,String token) throws Exception {
        /*
        no requestbody, add server_id on the end of url
         */
        //String url = "https://10.190.85.44:8774/v2.1/servers/{server_id}";
        ignoreSsl();
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //添加请求头
        con.setRequestMethod("DELETE");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("X-Auth-Token", token);
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

        private static String sendDCIGetInfo() throws Exception {

            String url = "http://10.190.85.75:8082/portal_be/ne-manage/total/ne";
            ignoreSsl();
            URL obj = new URL(url);
            HttpURLConnection con = (HttpURLConnection) obj.openConnection();

            //添加请求头
            con.setRequestMethod("POST");
            con.setRequestProperty("User-Agent", USER_AGENT);
            //  dci-GET no token
            // con.setRequestProperty("Authorization", "Bearer JTdCJTIybmFtZSUyMiUzQSUyMnNkbiUyMiUyQyUyMnBhc3N3b3JkJTIyJTNBJTIyc2RuJTIyJTJDJTIycm9sZSUyMiUzQSUyMm9wZXJhdGlvbiUyMiUyQyUyMnVzZXJuYW1lVmFpbGQlMjIlM0F0cnVlJTJDJTIycGFzc3dvcmRWYWlsZCUyMiUzQXRydWUlMkMlMjJ0b3BvRGF0YVZhaWxkJTIyJTNBdHJ1ZSUyQyUyMmluZm9SZW1lbWJlciUyMiUzQWZhbHNlJTJDJTIyc3RhdHVzJTIyJTNBJTIyYWN0aXZlJTIyJTJDJTIydGVuYW50SWQlMjIlM0ElMjIlMjIlMkMlMjJlbWFpbCUyMiUzQSUyMnNkbiU0MGZpYmVyaG9tZS5jb20lMjIlMkMlMjJ1aWQlMjIlM0ElMjJmZjgwODE4MTZjNDZkY2NjMDE2YzQ2ZTAwMWJhMDAxZSUyMiUyQyUyMm5vQ2hhbmdlUGFzc3dvcmQlMjIlM0FmYWxzZSU3RA==");
            con.setRequestProperty("Content-Type", "application/json");

            String urlParameters = "{\"currentPage\":\"1\",\"size\":\"10\"}";


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



        private static String sendDCIPost(String requestBody) throws Exception {

        String url = "http://10.190.85.75:8082/portal_be/business/business";
        ignoreSsl();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //添加请求头
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");

        String urlParameters = requestBody;


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

    //http://10.190.85.75:8082/portal_be/business/business/batchDeleteBusinesses
    private static String sendDCIDelete(String requestBody) throws Exception {


        String url = "http://10.190.85.75:8082/portal_be/business/business/batchDeleteBusinesses";
        ignoreSsl();
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //添加请求头
        con.setRequestMethod("POST");
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
        con.setRequestProperty("Content-Type", "application/json");

        String urlParameters = requestBody;

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


    private static String getDcnBoardDevice() throws Exception {
        String url = "http://10.190.85.68:8082/inventory/getDashBoardDevice";
        ignoreSsl(); //跳过https验证
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        //添加请求头
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Authorization", "Bearer JTdCJTIybmFtZSUyMiUzQSUyMnNkbiUyMiUyQyUyMnBhc3N3b3JkJTIyJTNBJTIyc2RuJTIyJTJDJTIycm9sZSUyMiUzQSUyMm9wZXJhdGlvbiUyMiUyQyUyMnVzZXJuYW1lVmFpbGQlMjIlM0F0cnVlJTJDJTIycGFzc3dvcmRWYWlsZCUyMiUzQXRydWUlMkMlMjJ0b3BvRGF0YVZhaWxkJTIyJTNBdHJ1ZSUyQyUyMmluZm9SZW1lbWJlciUyMiUzQWZhbHNlJTJDJTIyc3RhdHVzJTIyJTNBJTIyYWN0aXZlJTIyJTJDJTIydGVuYW50SWQlMjIlM0ElMjIlMjIlMkMlMjJlbWFpbCUyMiUzQSUyMnNkbiU0MGZpYmVyaG9tZS5jb20lMjIlMkMlMjJ1aWQlMjIlM0ElMjJmZjgwODE4MTZjNDZkY2NjMDE2YzQ2ZTAwMWJhMDAxZSUyMiUyQyUyMm5vQ2hhbmdlUGFzc3dvcmQlMjIlM0FmYWxzZSU3RA==");

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

    private static String getDcnBoardPort() throws Exception {
        String url = "http://10.190.85.68:8082/inventory/getDashBoardPort";
        ignoreSsl(); //跳过https验证
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        //添加请求头
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Authorization", "Bearer JTdCJTIybmFtZSUyMiUzQSUyMnNkbiUyMiUyQyUyMnBhc3N3b3JkJTIyJTNBJTIyc2RuJTIyJTJDJTIycm9sZSUyMiUzQSUyMm9wZXJhdGlvbiUyMiUyQyUyMnVzZXJuYW1lVmFpbGQlMjIlM0F0cnVlJTJDJTIycGFzc3dvcmRWYWlsZCUyMiUzQXRydWUlMkMlMjJ0b3BvRGF0YVZhaWxkJTIyJTNBdHJ1ZSUyQyUyMmluZm9SZW1lbWJlciUyMiUzQWZhbHNlJTJDJTIyc3RhdHVzJTIyJTNBJTIyYWN0aXZlJTIyJTJDJTIydGVuYW50SWQlMjIlM0ElMjIlMjIlMkMlMjJlbWFpbCUyMiUzQSUyMnNkbiU0MGZpYmVyaG9tZS5jb20lMjIlMkMlMjJ1aWQlMjIlM0ElMjJmZjgwODE4MTZjNDZkY2NjMDE2YzQ2ZTAwMWJhMDAxZSUyMiUyQyUyMm5vQ2hhbmdlUGFzc3dvcmQlMjIlM0FmYWxzZSU3RA==");

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

    private static String getDcnNodeInfo() throws Exception {
        String url = "http://10.190.85.68:8082/Cluster/getAllNodeUseRatio/true";
        ignoreSsl(); //跳过https验证
        URL obj = new URL(url);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //默认值我GET
        con.setRequestMethod("GET");

        //添加请求头
        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Authorization", "Bearer JTdCJTIybmFtZSUyMiUzQSUyMnNkbiUyMiUyQyUyMnBhc3N3b3JkJTIyJTNBJTIyc2RuJTIyJTJDJTIycm9sZSUyMiUzQSUyMm9wZXJhdGlvbiUyMiUyQyUyMnVzZXJuYW1lVmFpbGQlMjIlM0F0cnVlJTJDJTIycGFzc3dvcmRWYWlsZCUyMiUzQXRydWUlMkMlMjJ0b3BvRGF0YVZhaWxkJTIyJTNBdHJ1ZSUyQyUyMmluZm9SZW1lbWJlciUyMiUzQWZhbHNlJTJDJTIyc3RhdHVzJTIyJTNBJTIyYWN0aXZlJTIyJTJDJTIydGVuYW50SWQlMjIlM0ElMjIlMjIlMkMlMjJlbWFpbCUyMiUzQSUyMnNkbiU0MGZpYmVyaG9tZS5jb20lMjIlMkMlMjJ1aWQlMjIlM0ElMjJmZjgwODE4MTZjNDZkY2NjMDE2YzQ2ZTAwMWJhMDAxZSUyMiUyQyUyMm5vQ2hhbmdlUGFzc3dvcmQlMjIlM0FmYWxzZSU3RA==");

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
         *
         * @throws Exception
         */
        public static void ignoreSsl() throws Exception {
            HostnameVerifier hv = new HostnameVerifier() {
                public boolean verify(String urlHostName, SSLSession session) {
                    return true;
                }
            };
            trustAllHttpsCertificates();
            HttpsURLConnection.setDefaultHostnameVerifier(hv);
        }

}

