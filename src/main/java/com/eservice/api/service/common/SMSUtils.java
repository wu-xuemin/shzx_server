package com.eservice.api.service.common;

import com.wondertek.esmp.esms.empp.EMPPConnectResp;
import com.wondertek.esmp.esms.empp.EmppApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SMSUtils {

    @Value("${passwordSMS}")
    private String passwordSMS; //Shzx20191  todo 按规则设密码
    private final static Logger logger = LoggerFactory.getLogger(SMSUtils.class);
    public void send( String[] phone, String msg) {

        String host = "211.136.163.68";
        int port = 9981;
        String accountId = "10657109031147";
        String serviceId = "555580001";

        EmppApi emppApi = new EmppApi();
        RecvListener listener = new RecvListener(emppApi);

        if(listener == null){
            logger.warn(" make new RecvListener fail");
        }
        try {
            // /建立同服务器的连接
            EMPPConnectResp response = emppApi.connect(host, port, accountId,
                    passwordSMS, listener);
            System.out.println(response);
            if (response == null) {
                logger.warn("error: connect time out");
                return;
            }
            if (!emppApi.isConnected()) {
                logger.warn("error: connect failed, response =" + response.getStatus());
                return;
            }
        } catch (Exception e) {
            logger.warn("error: Exception，cause connect fail");
            e.printStackTrace();
            return;
        }

        /**
         * 这个类的被注释掉的代码，来自中国移动的原始文件，保留这些有助于以后参考。
         */

//		发送短信
        if (emppApi.isSubmitable()) {

            //简单方式发送短信,支持长短信
            try{//13588027825 15715766877
//                emppApi.submitMsgAsync(msg,new String[]{"13588027825"},serviceId);
                emppApi.submitMsgAsync(msg,phone,serviceId);
                logger.info("sendTo: " + phone + "短信内容：" + msg);
                //同步发送方式update 20060307
                //EMPPSubmitSMResp []  resp = emppApi.submitMsg(content,mobiles,serviceId);
                //System.out.println("resp result:"+resp[0].getResult());

            }catch (Exception e1) {
                logger.warn("sent error: exception " + e1.toString());
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

        } else {
            logger.warn("emppApi.isSubmitable() is false, no send");
        }

    }
}
