package com.huangjacky.teleows;

import android.os.Build;
import android.util.Log;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.*;

public class Helper {
    /**
     * 为了演示效果，故意增加一个标志位
     */
    public static boolean SNI = false;
    private Helper() {
    }

    public static synchronized HttpClient getHttpClient() {
        // 初始化工作
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new SSLSocketFactoryEx(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            //允许所有主机的验证
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.DEFAULT_CONTENT_CHARSET);
            HttpProtocolParams.setUseExpectContinue(params, true); // 设置连接管理器的超时
            ConnManagerParams.setTimeout(params, 10000); // 设置连接超时
            HttpConnectionParams.setConnectionTimeout(params, 10000); // 设置socket超时
            HttpConnectionParams.setSoTimeout(params, 10000); // 设置http https支持
            SchemeRegistry schReg = new SchemeRegistry();
            schReg.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            schReg.register(new Scheme("https", sf, 443));
            ClientConnectionManager conManager = new ThreadSafeClientConnManager(params, schReg);
            HttpClient httpClient = new DefaultHttpClient(conManager, params);
            return httpClient;
        } catch (Exception e) {
            e.printStackTrace();
            return new DefaultHttpClient();
        }
    }
}

class SSLSocketFactoryEx extends SSLSocketFactory {
    SSLContext sslContext = SSLContext.getInstance("TLS");

    public SSLSocketFactoryEx(KeyStore truststore) throws NoSuchAlgorithmException, KeyManagementException, KeyStoreException, UnrecoverableKeyException {
        super(truststore);
        TrustManager tm = new X509TrustManager() {
            @Override
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkClientTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            }

            @Override
            public void checkServerTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
            }
        };
        sslContext.init(null, new TrustManager[]{tm}, null);
    }

    @Override
    public Socket createSocket(Socket socket, String host, int port, boolean autoClose) throws IOException, UnknownHostException {
        SSLSocket sslSocket = (SSLSocket) sslContext.getSocketFactory().createSocket(socket, host, port, autoClose);
        /**
         * 当演示标志位打开的情况下，就去设置SNI
         */
        if (Helper.SNI && Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (Log.isLoggable("HJ", Log.DEBUG)) {
                Log.d("HJ", "Enabling SNI for " + host);
            }
            try {
                Method method = sslSocket.getClass().getMethod("setHostname", String.class);
                method.invoke(sslSocket, host);
            } catch (Exception ex) {
                if (Log.isLoggable("HJ", Log.DEBUG)) {
                    Log.d("HJ", "SNI configuration failed", ex);
                }
            }
        }
        return sslSocket;
    }

    @Override
    public Socket createSocket() throws IOException {
        return sslContext.getSocketFactory().createSocket();
    }
}
