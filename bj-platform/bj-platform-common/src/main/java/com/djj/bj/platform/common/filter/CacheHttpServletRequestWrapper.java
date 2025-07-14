package com.djj.bj.platform.common.filter;


import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

import java.io.*;

/**
 * 带缓 HttpServletRequest 包装类
 *
 * @author jj_D
 * @version 1.0
 * @projectName Bonjour
 * @package com.djj.bj.platform.common.filter
 * @className CacheHttpServletRequestWrapper
 * @date 2025/7/14 20:37
 */
public class CacheHttpServletRequestWrapper extends HttpServletRequestWrapper {
    private byte[] requestBody;
    private final HttpServletRequest request;


    public CacheHttpServletRequestWrapper(HttpServletRequest request) {
        super(request);
        this.request = request;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (requestBody == null) {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            IOUtils.copy(request.getInputStream(), bos);
            this.requestBody = bos.toByteArray();
        }
        ByteArrayInputStream bis = new ByteArrayInputStream(this.requestBody);
        return new ServletInputStream() {

            @Override
            public int read() throws IOException {
                return bis.read();
            }

            @Override
            public boolean isFinished() {
                return false;
            }

            @Override
            public boolean isReady() {
                return false;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream()));
    }
}
