package com.eservice.api.service.common;

import com.wondertek.esmp.esms.empp.EMPPConnectResp;
import com.wondertek.esmp.esms.empp.EmppApi;
import org.springframework.beans.factory.annotation.Value;

public class SMSUtils {

    @Value("${passwordSMS}")
    private static String passwordSMS;
    public static void send( String[] phone, String msg) {

        String host = "211.136.163.68";
        int port = 9981;
        String accountId = "10657109031147";
        String serviceId = "555580001";

        EmppApi emppApi = new EmppApi();
        RecvListener listener = new RecvListener(emppApi);

        try {
            // /建立同服务器的连接
            EMPPConnectResp response = emppApi.connect(host, port, accountId,
                    passwordSMS, listener);
            System.out.println(response);
            if (response == null) {
                System.out.println(" connect time out");
                return;
            }
            if (!emppApi.isConnected()) {
                System.out.println("connect failed : response =" + response.getStatus());
                return;
            }
        } catch (Exception e) {
            System.out.println("Exception，cause connect fail");
            e.printStackTrace();
            return;
        }

//		发送短信
        if (emppApi.isSubmitable()) {

            //简单方式发送短信,支持长短信
            try{
                emppApi.submitMsgAsync(msg,new String[]{"13588027825"},serviceId);

                //同步发送方式update 20060307
                //EMPPSubmitSMResp []  resp = emppApi.submitMsg(content,mobiles,serviceId);
                //System.out.println("resp result:"+resp[0].getResult());

            }catch (Exception e1) {
                e1.printStackTrace();
            }

            //		详细设置短信的各个属性,不支持长短信
//			EMPPSubmitSM msg = (EMPPSubmitSM) EMPPObject
//					.createEMPP(EMPPData.EMPP_SUBMIT);
//			String mobile = "13564468230";
//			List dstId = new ArrayList();
//			dstId.add(mobile);
//
//			msg.setDstTermId(dstId);
//			msg.setSrcTermId(accountId);
//			msg.setServiceId(serviceId);
//
//			EMPPShortMsg msgContent = new EMPPShortMsg(
//					EMPPShortMsg.EMPP_MSG_CONTENT_MAXLEN);
//
//			try {
//				msgContent.setMessage("09939可以收发短信");
//				msg.setShortMessage(msgContent);
//				msg.assignSequenceNumber();
//				emppApi.submitMsgAsync(msg);
//
//			} catch (Exception e) {
//				e.printStackTrace();
//			}

        }

    }
}
