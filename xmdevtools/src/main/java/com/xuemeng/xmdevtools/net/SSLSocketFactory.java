package com.xuemeng.xmdevtools.net;


import com.xuemeng.xmdevtools.utils.Preconditions;

import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;


/**
 * SSL创建类证书
 */
public class SSLSocketFactory {

    public static X509TrustManager trustManager = new X509TrustManager() {
        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    };

    /**
     * 获取AppHttps的策略
     *
     * @return
     */
    public static SSLContext getSSLstrategy() {
        return getTrustAllSSLSocketFactory();
    }


    /**
     * 获取信任所有https网站的证书
     *
     * @return
     */
    public static SSLContext getTrustAllSSLSocketFactory() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new X509TrustManager[]{trustManager}, new SecureRandom());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sslContext;
    }
}
