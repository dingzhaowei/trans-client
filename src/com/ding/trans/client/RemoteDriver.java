package com.ding.trans.client;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpCookie;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

public class RemoteDriver {

    public static final int DEFAULT_CONNECT_TIMEOUT = 15000;

    public static final int DEFAULT_SOCKET_TIMEOUT = 5000;

    public static final int DEFAULT_CONNECTION_REQ_TIMEOUT = 5000;

    public static final int MAX_RETRY_ON_EXCEPTION = 3;

    private static RemoteDriver instance = new RemoteDriver();

    private String remoteUrl;

    private AtomicBoolean logined;

    private Map<String, String> cookies;

    private RemoteDriver() {
        this.logined = new AtomicBoolean();
        this.cookies = new HashMap<>();
        if (System.getProperty("debug") != null) {
            this.remoteUrl = "http://localhost:18020";
        } else {
            this.remoteUrl = Config.getRemoteUrl();
        }
    }

    public static RemoteDriver instance() {
        return instance;
    }

    public boolean isLogined() {
        return logined.get();
    }

    public void login(String username, String password) throws RemoteDriverException {
        CloseableHttpClient httpClient = buildClient();
        List<NameValuePair> params = new ArrayList<>();
        params.add(new BasicNameValuePair("username", username));
        params.add(new BasicNameValuePair("password", password));
        request(httpClient, "/login", params, resp -> {
            storeCookies(resp);
            logined.set(true);
        } , "登录失败");
    }

    public void logout() throws RemoteDriverException {
        CloseableHttpClient httpClient = buildClient();
        List<NameValuePair> params = new ArrayList<>();
        request(httpClient, "/logout", params, resp -> {
            logined.set(false);
            clearCookies();
        } , "退出失败");
    }

    private CloseableHttpClient buildClient() {
        CloseableHttpClient httpClient = null;
        if (System.getProperty("debug") != null) {
            return HttpClients.createDefault();
        } else {
            RequestConfig config = RequestConfig.custom().setConnectTimeout(DEFAULT_CONNECT_TIMEOUT)
                    .setConnectionRequestTimeout(DEFAULT_CONNECTION_REQ_TIMEOUT)
                    .setSocketTimeout(DEFAULT_SOCKET_TIMEOUT).build();
            httpClient = HttpClientBuilder.create().setDefaultRequestConfig(config)
                    .setRedirectStrategy(new LaxRedirectStrategy()).build();
        }
        return httpClient;
    }

    private void request(CloseableHttpClient httpClient, String path, List<NameValuePair> params,
            SuccessfulResponseHandler successHandler, String errorTip) throws RemoteDriverException {
        HttpPost httpPost = new HttpPost(remoteUrl + path);
        CloseableHttpResponse resp = null;
        try {
            UrlEncodedFormEntity entity = new UrlEncodedFormEntity(params, "UTF-8");
            httpPost.setEntity(entity);
            httpPost.setHeader("Cookie", makeCookies());

            int retry = 0;
            while (true) {
                try {
                    resp = httpClient.execute(httpPost);
                    break;
                } catch (IOException e) {
                    if (retry == MAX_RETRY_ON_EXCEPTION) {
                        throw e;
                    }
                    retry++;
                }
            }

            StatusLine sl = resp.getStatusLine();
            if (sl.getStatusCode() == HttpStatus.SC_OK) {
                if (successHandler != null) {
                    successHandler.onSuccess(resp);
                }
            } else {
                String reason = URLDecoder.decode(sl.getReasonPhrase(), "UTF-8");
                throw new RemoteDriverException(reason);
            }
            EntityUtils.consume(resp.getEntity());
        } catch (Exception e) {
            if (e instanceof ConnectException) {
                throw new RemoteDriverException(errorTip + ": 无法连接到后台服务器");
            } else if (e instanceof ConnectTimeoutException) {
                throw new RemoteDriverException(errorTip + ": 连接到后台服务器超时");
            } else {
                throw new RemoteDriverException(errorTip + ": " + e.getMessage(), e);
            }
        } finally {
            if (resp != null) {
                try {
                    resp.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private synchronized String makeCookies() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> cookie : cookies.entrySet()) {
            if (sb.length() > 0) {
                sb.append(';');
            }
            sb.append(cookie.getKey()).append('=').append(cookie.getValue());
        }
        return sb.toString();
    }

    private synchronized void storeCookies(CloseableHttpResponse resp) {
        for (Header h : resp.getHeaders("Set-Cookie")) {
            List<HttpCookie> httpCookies = HttpCookie.parse(h.toString());
            for (HttpCookie httpCookie : httpCookies) {
                String name = httpCookie.getName();
                String value = httpCookie.getValue();
                cookies.put(name, value);
            }
        }
    }

    private synchronized void clearCookies() {
        cookies.clear();
    }

}
