package com.djj.bj.ai.common.httpclient;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;

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
        PoolingHttpClientConnectionManager connectionManager =
                PoolingHttpClientConnectionManagerBuilder.create()
                        .build();
        return HttpClients.custom()
                .setConnectionManager(connectionManager)
                .build();
    }
}
