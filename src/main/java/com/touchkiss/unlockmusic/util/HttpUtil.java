package com.touchkiss.unlockmusic.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.HttpClientUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class HttpUtil {
    /**
     * Get请求
     */
    public final static String METHOD_GET = "GET";
    /**
     * Post请求
     */
    public final static String METHOD_POST = "POST";
    /**
     * Head请求
     */
    public final static String METHOD_HEAD = "HEAD";
    /**
     * Options请求
     */
    public final static String METHOD_OPTIONS = "OPTIONS";
    /**
     * Put请求
     */
    public final static String METHOD_PUT = "PUT";
    /**
     * Delete请求
     */
    public final static String METHOD_DELETE = "DELETE";
    /**
     * Trace请求
     */
    public final static String METHOD_TRACE = "TRACE";
    /**
     * http客户端
     */
    private static CloseableHttpClient httpClient = HttpClients.createDefault();

    /**
     * @param proxyHost 代理地址
     * @param port      代理端口
     * @param account   认证账号
     * @param password  认证密码
     */
    public static void auth(String proxyHost, int port, final String account, final String password) {
        System.setProperty("https.proxyHost", proxyHost);
        System.setProperty("https.proxyPort", port + "");
        Authenticator.setDefault(new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(account, new String(password).toCharArray());
            }
        });
    }

    /**
     * @param url
     * @param auth 认证信息(username+":"+password)
     * @return (true : 连接成功, false : 连接失败)
     * @description 判断服务连通性
     * @author yi.zhang
     * @time 2017年4月19日 下午6:00:40
     */
    public static boolean checkConnection(String url, String auth) {
        boolean flag = false;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(5 * 1000);
            if (auth != null && !"".equals(auth)) {
                String authorization = "Basic " + new String(Base64.encodeBase64(auth.getBytes()));
                connection.setRequestProperty("Authorization", authorization);
            }
            connection.connect();
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                flag = true;
            }
            connection.disconnect();
        } catch (Exception e) {
            log.error("--Server Connect Error !", e);
        }
        return flag;
    }

    /**
     * @param url    请求URL
     * @param method 请求URL
     * @param param  json参数(post|put)
     * @param auth   认证信息(username+":"+password)
     * @return
     */
    public static String httpRequest(String url, String method, String param, String auth) {
        String result = null;
        HttpResponse httpResponse = null;
        try {
            HttpRequestBase http = new HttpGet(url);
            if (method.equalsIgnoreCase(METHOD_POST)) {
                http = new HttpPost(url);
                StringEntity body = new StringEntity(param, ContentType.APPLICATION_JSON);
                body.setContentType("application/json");
                ((HttpPost) http).setEntity(body);
            } else if (method.equalsIgnoreCase(METHOD_PUT)) {
                http = new HttpPut(url);
                StringEntity body = new StringEntity(param, ContentType.APPLICATION_JSON);
                body.setContentType("application/json");
                ((HttpPut) http).setEntity(body);
            } else if (method.equalsIgnoreCase(METHOD_DELETE)) {
                http = new HttpDelete(url);
            } else if (method.equalsIgnoreCase(METHOD_HEAD)) {
                http = new HttpHead(url);
            } else if (method.equalsIgnoreCase(METHOD_OPTIONS)) {
                http = new HttpOptions(url);
            } else if (method.equalsIgnoreCase(METHOD_TRACE)) {
                http = new HttpTrace(url);
            }
            if (auth != null && !"".equals(auth)) {
                String authorization = "Basic " + new String(Base64.encodeBase64(auth.getBytes()));
                http.setHeader("Authorization", authorization);
            }
            httpResponse = httpClient.execute(http);
            HttpEntity entity = httpResponse.getEntity();
            result = EntityUtils.toString(entity, Consts.UTF_8);
        } catch (Exception e) {
            log.error("--http request error !", e);
            result = e.getMessage();
        } finally {
            HttpClientUtils.closeQuietly(httpResponse);
        }
        return result;
    }

    /**
     * @param url    请求URL
     * @param method 请求URL
     * @param param  json参数(post|put)
     * @return
     */
    public static String urlRequest(String url, String method, String param, String auth) {
        String result = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(60 * 1000);
            connection.setRequestMethod(method.toUpperCase());
            if (auth != null && !"".equals(auth)) {
                String authorization = "Basic " + new String(Base64.encodeBase64(auth.getBytes()));
                connection.setRequestProperty("Authorization", authorization);
            }
            if (param != null && !"".equals(param)) {
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                dos.write(param.getBytes(Consts.UTF_8));
                dos.flush();
                dos.close();
            } else {
                connection.connect();
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = in.read(buff, 0, buff.length)) > 0) {
                    out.write(buff, 0, len);
                }
                byte[] data = out.toByteArray();
                in.close();
                result = data != null && data.length > 0 ? new String(data, Consts.UTF_8) : null;
            } else {
                result = "{\"status\":" + connection.getResponseCode() + ",\"msg\":\"" + connection.getResponseMessage() + "\"}";
            }
            connection.disconnect();
        } catch (Exception e) {
            log.error("--http request error !", e);
        }
        return result;
    }

    /**
     * @param url    请求URL
     * @param method 请求URL
     * @param param  json参数(post|put)
     * @return
     */
    public static String urlRequestwithHeader(String url, String method, String param, Map<String, String> header) {
        String result = null;
        try {
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setConnectTimeout(60 * 1000);
            connection.setRequestMethod(method.toUpperCase());
            if (header != null) {
                Set<String> set = header.keySet();
                for (String s : set) {
                    connection.setRequestProperty(s, header.get(s));
                }
            }
            if (param == null || "".equals(param)) {
                connection.connect();
            } else {
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.connect();
                DataOutputStream dos = new DataOutputStream(connection.getOutputStream());
                dos.write(param.getBytes(Consts.UTF_8));
                dos.flush();
                dos.close();
            }
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream in = connection.getInputStream();
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = in.read(buff, 0, buff.length)) > 0) {
                    out.write(buff, 0, len);
                }
                byte[] data = out.toByteArray();
                in.close();
                result = data != null && data.length > 0 ? new String(data, Consts.UTF_8) : null;
            } else {
                result = "{\"status\":" + connection.getResponseCode() + ",\"msg\":\"" + connection.getResponseMessage() + "\"}";
            }
            connection.disconnect();
        } catch (Exception e) {
            log.error("--http request error !", e);
        }
        return result;
    }


    public static HashMap<String, String> get(String url) {
        HashMap<String, String> map = new HashMap<String, String>();
        String context = "";

        if (httpClient == null) {
            httpClient = HttpClients.createDefault();
        }
        HttpGet httpGet = new HttpGet(url);

        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(8000).setConnectionRequestTimeout(8000)
                .setSocketTimeout(8000).build();
        httpGet.setConfig(requestConfig);
        // 结果
        CloseableHttpResponse response = null;
        String content = "";
        try {
            // 执行get方法
            response = httpClient.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == 200) {
                content = EntityUtils.toString(response.getEntity(), "utf-8");
                map.put("err_code", "");
                map.put("err_msg", "");
                map.put("result_code", "ok");
                map.put("result_data", content);
            } else {
                log.error((url + " 调用url status: " + response.getStatusLine().getStatusCode()));
                map.put("err_code", response.getStatusLine().getStatusCode() + "");
                map.put("err_msg", content);
                map.put("result_code", "fail");
            }
        } catch (ClientProtocolException e) {
            map.put("err_code", "500");
            map.put("err_msg", e.getMessage());
            map.put("result_code", "fail");
            log.error("调用【url：" + url + "】出现异常", e);
        } catch (IOException e) {
            map.put("err_code", "500");
            map.put("err_msg", e.getMessage());
            map.put("result_code", "fail");
            log.error("调用【url：" + url + "】出现异常", e);
        } finally {
            try {
                if (httpGet != null)
                    httpGet.abort();
                if (response != null)
                    response.close();
            } catch (IOException e) {
                log.error("关闭连接出现异常", e);
            }
        }
        return map;
    }

    /**
     * @param target
     * @return
     * @decription URL编码
     * @author yi.zhang
     * @time 2017年9月15日 下午3:33:38
     */
    public static String encode(String target) {
        String result = target;
        try {
            result = URLEncoder.encode(target, Consts.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("--http encode error !", e);
        }
        return result;
    }

    /**
     * @param target
     * @return
     * @decription URL解码
     * @author yi.zhang
     * @time 2017年9月15日 下午3:33:38
     */
    public static String decode(String target) {
        String result = target;
        try {
            result = URLDecoder.decode(target, Consts.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            log.error("--http decode error !", e);
        }
        return result;
    }

    /**
     * 通过url下载图片到file对象里（含有ua头信息）
     *
     * @param imageUrl 图片地址
     * @param file     图片下载后的文件
     * @throws IOException
     */
    /* public static void fetchContent(String imageUrl, File file) throws IOException {
     *//*        ReadableByteChannel readableByteChannel = null;
        FileOutputStream fileOutputStream = null;
        try {
            URL url = new URL(imageUrl);
            readableByteChannel = Channels.newChannel(url.openStream());
            fileOutputStream = new FileOutputStream(file);
            fileOutputStream.getChannel()
                    .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
            log.error("文件下载复制完成："+imageUrl);
        } catch (Exception e) {
            System.out.println("文件下载失败:" + imageUrl);
        } finally {
            readableByteChannel.close();
            fileOutputStream.close();
        }*//*
        URL url = new URL(imageUrl);
        URLConnection conn = url.openConnection();
        conn.setConnectTimeout(30000);
        InputStream input = conn.getInputStream();
        log.error("文件下载流完成：" + imageUrl+"文件大小"+conn.getContentLength());
        BufferedInputStream bufferedInputStream = new BufferedInputStream(input);
        OutputStream output = new FileOutputStream(file);
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(output);
        log.error("文件下载开始写入：" + imageUrl);
//        IOUtils.copy(bufferedInputStream,bufferedOutputStream);
        byte[] buffer = new byte[4096];
        int len;
        while ((len = bufferedInputStream.read(buffer)) != -1) {
            bufferedOutputStream.write(buffer, 0, len);
        }
        log.error("文件下载复制完成：" + imageUrl);
        bufferedOutputStream.close();
//        bufferedInputStream.close();
        output.flush();
        output.close();
//        input.close();
    }*/
    public static void fetchContent(String imageUrl, File file) throws IOException {
        if (imageUrl.contains("|")) {
            imageUrl = imageUrl.replace("|", "%7c");
        }
        HttpGet httpget = new HttpGet(imageUrl);
        httpget.setHeader("User-Agent", "yidian-pro/4.6.2 (iPhone; iOS 11.3; Scale/3.00)");
        System.out.println("executing request " + httpget.getURI());
        CloseableHttpResponse response = httpClient.execute(httpget);
        try {
            HttpEntity entity = response.getEntity();
            if (response.getStatusLine().getStatusCode() >= 400) {
                throw new IOException("Got bad response, error code = " + response.getStatusLine().getStatusCode() + " imageUrl: " + imageUrl);
            }
            if (entity != null) {
                InputStream input = entity.getContent();
                OutputStream output = new FileOutputStream(file);
                IOUtils.copy(input, output);
                output.flush();
                output.close();
                input.close();
            }
        } finally {
            response.close();
        }
    }
}
