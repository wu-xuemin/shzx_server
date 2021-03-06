package com.eservice.api.service.common;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import sun.misc.BASE64Decoder;

import javax.imageio.ImageIO;
import javax.net.ssl.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Class Description:通用服务类
 *
 * @author Wilson Hu
 * @date 22/12/2017
 */
@Service
public class CommonService {
    static Logger logger = Logger.getLogger(CommonService.class);

    @Value("${debug.flag}")
    private String debugFlag;

    private static String SHZX_URL_GET_BUS = "";
    private static String SHZX_URL_GET_CLASS = "";
    private static String SHZX_URL_GET_STUDENT = "";

    /**
     * @param path      保存文件的总路径
     * @param rowData   照片文件的base64
     * @param tag1，比如学号
     * @param tag2      比如姓名
     * @return 文件路径
     * eg: xh333.jpg
     */
    public String saveFile(String path,
                           String rowData,
                           @RequestParam(defaultValue = "") String tag1,
                           @RequestParam(defaultValue = "") String tag2) throws IOException {
        String targetFileName = null;
        try {
            if (path != null) {
                //防止学号和姓名中有“/”
                targetFileName = path + formatFileName(tag1.replaceAll("/", "-"), tag2.replaceAll("/", "-"));
                if (debugFlag.equalsIgnoreCase("true")) {
                    logger.info("====CommonService.saveFile(): targetFileName  ========" + targetFileName);
                }
                BufferedOutputStream out = new BufferedOutputStream(
                        new FileOutputStream(new File(targetFileName)));
                BASE64Decoder decoder = new BASE64Decoder();
                // 解密
                byte[] b = decoder.decodeBuffer(rowData);
                // 处理数据
                for (int i = 0; i < b.length; ++i) {
                    if (b[i] < 0) {
                        b[i] += 256;
                    }
                }
                out.write(b);
                out.flush();
                out.close();
                if (debugFlag.equalsIgnoreCase("true")) {
                    logger.info("====CommonService.saveFile(): success ========" + targetFileName);
                }
            }
        } catch (IOException e) {
            logger.info("====CommonService.saveFile(): fail ========" + e.toString());
            throw e;
        }
        return targetFileName;
    }

    public void reduceImg(String srcImageFile, String destImageFile, int width, int height, boolean isScale)
            throws IOException {

        InputStream inputStream = new FileInputStream(srcImageFile);
        OutputStream outputStream = new FileOutputStream(destImageFile);

        BufferedImage bufferedImage = ImageIO.read(inputStream);
        int sWidth = bufferedImage.getWidth();
        int sHeight = bufferedImage.getHeight();
        int diffWidth = 0;
        int diffHeight = 0;
        if (isScale) {
            if ((double) sWidth / width > (double) sHeight / height) {
                int height2 = width * sHeight / sWidth;
                diffHeight = (height - height2) / 2;
            } else if ((double) sWidth / width < (double) sHeight / height) {
                int width2 = height * sWidth / sHeight;
                diffWidth = (width - width2) / 2;
            }
        }
        BufferedImage nbufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        //填充整个屏幕
        nbufferedImage.getGraphics().fillRect(0, 0, width, height);
        // 绘制缩小后的图
        nbufferedImage.getGraphics().drawImage(bufferedImage, diffWidth, diffHeight, width - diffWidth * 2,
                height - diffHeight * 2, null);
        ImageIO.write(nbufferedImage, "JPEG", outputStream);
        outputStream.close();
        inputStream.close();
    }

    public String formatFileName(String tag1, String tag2) {
        String suffixName = ".jpg";
        String targetFileName = tag1 + "_" + tag2 + suffixName;
        logger.info("targetFileName:" + targetFileName);
        return targetFileName;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url 发送请求的 URL
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, Map<String, ?> paramMap) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        String param = "";
        Iterator<String> it = paramMap.keySet().iterator();
        String message = null;

        while (it.hasNext()) {
            String key = it.next();
            param += key + "=" + paramMap.get(key) + "&";
        }

        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            URLConnection conn = realUrl.openConnection();
            // 设置通用的请求属性
            conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("Accept-Charset", "utf-8");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            System.out.print(e.getMessage());
            message = "sendPost error, " + e.getMessage();
            return message;
        }
        //使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result;
    }

    /**
     * 发起Https请求
     *
     * @param reqUrl        请求的URL地址
     * @param requestMethod
     * @return 响应后的字符串
     */
    public String getHttpsResponse(String reqUrl, String requestMethod) {
        URL url;
        InputStream is;
        String resultData = "";
        try {
            url = new URL(reqUrl);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            TrustManager[] tm = {xtm};

            SSLContext ctx = SSLContext.getInstance("TLS");
            ctx.init(null, tm, null);

            con.setSSLSocketFactory(ctx.getSocketFactory());
            con.setHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
            con.setDoInput(true); //允许输入流，即允许下载

            //在android中必须将此项设置为false
            con.setDoOutput(false); //允许输出流，即允许上传
            con.setUseCaches(false); //不使用缓冲
            if (null != requestMethod && !requestMethod.equals("")) {
                con.setRequestMethod(requestMethod); //使用指定的方式
            } else {
                con.setRequestMethod("GET"); //使用get请求
            }
            is = con.getInputStream();   //获取输入流，此时才真正建立链接
            /**
             * 中文支持，比如微信nickname
             */
            InputStreamReader isr = new InputStreamReader(is, "UTF-8");
            BufferedReader bufferReader = new BufferedReader(isr);
            String inputLine;
            while ((inputLine = bufferReader.readLine()) != null) {
                resultData += inputLine + "\n";
            }
            logger.info(resultData);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return resultData;
    }

    X509TrustManager xtm = new X509TrustManager() {
        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        @Override
        public void checkServerTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }

        @Override
        public void checkClientTrusted(X509Certificate[] arg0, String arg1)
                throws CertificateException {
        }
    };

    public String httpsRequest(String requestUrl, String requestMethod, String outputStr) {
        try {
            URL url = new URL(requestUrl);
            HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setUseCaches(false);
            // 设置请求方式（GET/POST）
            conn.setRequestMethod(requestMethod);
            conn.setRequestProperty("content-type", "application/x-www-form-urlencoded");
            // 当outputStr不为null时向输出流写数据
            if (null != outputStr) {
                OutputStream outputStream = conn.getOutputStream();
                // 注意编码格式
                outputStream.write(outputStr.getBytes("UTF-8"));
                outputStream.close();
            }
            // 从输入流读取返回内容
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String str = null;
            StringBuffer buffer = new StringBuffer();
            while ((str = bufferedReader.readLine()) != null) {
                buffer.append(str);
            }
            // 释放资源
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
            inputStream = null;
            conn.disconnect();
            return buffer.toString();
        } catch (ConnectException ce) {
            System.out.println("连接超时：{}");
        } catch (Exception e) {
            System.out.println("https请求异常：{}");
        }
        return null;

    }

    public static String getValue(HSSFCell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }

    public static String getBusModeByTime(HSSFCell stationTimeRemarkCell) {//Date time, SimpleDateFormat sdf
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
        Date time = null;
        try {
            time = sdf.parse(CommonService.getValue(stationTimeRemarkCell));
            String str = sdf.format(time);
            int a = Integer.parseInt(str.split(":")[0]);
            if (a >= 0 && a <= 12) {
                return Constant.BUS_MODE_MORNING;
            } else if (a > 12 && a <= 18) {
                return Constant.BUS_MODE_AFTERNOON;
            } else if (a > 18 && a <= 23) {
                return Constant.BUS_MODE_NIGHT;
            } else {
                logger.info("getBusModeByTime() error: " + "wrong time: " + a);
                return "wrong time: " + a;
            }
        } catch (ParseException e) {
            logger.info("getBusModeByTime() error: " + e.toString());
            e.printStackTrace();
            return Constant.FAIL;
        }
    }


    /**
     * 文件重命名
     *
     * @param path    文件路径
     * @param oldname 原有的文件名
     * @param newname 新的文件名
     */
    public static boolean renameFile(String path, String oldname, String newname) {
        if (!oldname.equals(newname)) {
            File oldfile = new File(path + "/" + oldname);
            File newfile = new File(path + "/" + newname);
            if (!oldfile.exists()) {
                logger.error("需要重命名的文件不存在");
                return false;// 重命名文件不存在
            }
            if (newfile.exists()) {// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
                logger.error(newname + "已经存在！");
                return false;
            } else {
                boolean isSuccess = oldfile.renameTo(newfile);
                return isSuccess;
            }
        } else {
            logger.error("新文件名和旧文件名相同...");
        }
        return false;
    }

    /**
     * 从URL读取返回
     */
    public static String getUrlResponse(String urlStr) {
        URL url = null;
        HttpURLConnection httpConn = null;
        BufferedReader in = null;
        StringBuffer sb = new StringBuffer();

        switch (urlStr) {
            case Constant.SHZX_URL_GET_CLASS:
                if(!SHZX_URL_GET_CLASS.equals("")) {
                    return SHZX_URL_GET_CLASS;
                }
                break;
            case Constant.SHZX_URL_GET_BUS:
                if(!SHZX_URL_GET_BUS.equals("")) {
                    return SHZX_URL_GET_BUS;
                }
                break;
            case Constant.SHZX_URL_GET_STUDENT:
                if(!SHZX_URL_GET_STUDENT.equals("")) {
                    return SHZX_URL_GET_STUDENT;
                }
                break;
                default:
        }

        try{
            url = new URL(urlStr);
            in = new BufferedReader(new InputStreamReader(url.openStream(),"UTF-8") );
            String str = null;
            //一行一行进行读入
            while((str = in.readLine()) != null) {
                sb.append( str );
            }
        } catch (Exception ex) {
        } finally{
            try{
                if(in!=null) {
                    in.close(); //关闭流
                }
            }catch(IOException ex) {

            }
        }
        switch (urlStr) {
            case Constant.SHZX_URL_GET_CLASS:
                SHZX_URL_GET_CLASS = sb.toString();
                break;
            case Constant.SHZX_URL_GET_BUS:
                SHZX_URL_GET_BUS =  sb.toString();
                break;
            case Constant.SHZX_URL_GET_STUDENT:
                SHZX_URL_GET_STUDENT =  sb.toString();
                break;
            default:
        }
        return sb.toString();
    }
}
