package com.djj.bj.ai.common.httpclient;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.ClientTlsStrategyBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.TlsSocketStrategy;
import org.apache.hc.core5.ssl.SSLContextBuilder;

import javax.net.ssl.SSLContext;

/**
 * HttpClient
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.ai.common.httpclient
 * @className HttpClient
 * @date 2026/3/5 23:52
 */
public class HttpClient {
    public static CloseableHttpClient createSSLClientDefault() {
        try {
            SSLContext sslContext = SSLContextBuilder.create()
                    .loadTrustMaterial((chain, authType) -> true)
                    .build();

            TlsSocketStrategy tlsSocketStrategy = new DefaultClientTlsStrategy(
                    sslContext,
                    NoopHostnameVerifier.INSTANCE
            );
            PoolingHttpClientConnectionManager connectionManager =
                    PoolingHttpClientConnectionManagerBuilder.create()
                            .setTlsSocketStrategy(tlsSocketStrategy)
                            .build();
            return HttpClients.custom()
                    .setConnectionManager(connectionManager)
                    .build();
        }catch (Exception e){
            e.printStackTrace();
        }
        return HttpClients.createDefault();
    }
}
