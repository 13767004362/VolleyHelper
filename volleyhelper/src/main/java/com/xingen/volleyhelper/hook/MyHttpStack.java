package com.xingen.volleyhelper.hook;

import com.android.volley.AuthFailureError;
import com.android.volley.Header;
import com.android.volley.Request;
import com.android.volley.toolbox.HttpResponse;
import com.android.volley.toolbox.HurlStack;
import com.xingen.volleyhelper.request.SingleFileRequest;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Author by {xinGen}
 * Date on 2018/8/1 10:50
 */
public class MyHttpStack extends HurlStack {
    public MyHttpStack() {
    }
    @Override
    public HttpResponse executeRequest(Request<?> request, Map<String, String> additionalHeaders) throws IOException, AuthFailureError {
        // Log.i(TAG, " MyHttpStack  executeRequest " + request.getUrl());
        if (request instanceof SingleFileRequest) {
            HttpURLConnection connection = this.createConnection(new URL(request.getUrl()));
            int timeoutMs = request.getTimeoutMs();
            connection.setConnectTimeout(timeoutMs);
            connection.setReadTimeout(timeoutMs);
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setRequestMethod("POST");
            addFileBody(connection, (SingleFileRequest) request);
            connection.connect();
            int responseCode = connection.getResponseCode();
            if (responseCode == -1) {
                throw new IOException("Could not retrieve response code from HttpUrlConnection.");
            } else {
                return !hasResponseBody(request.getMethod(), responseCode) ? new HttpResponse(responseCode, convertHeaders(connection.getHeaderFields())) : new HttpResponse(responseCode, convertHeaders(connection.getHeaderFields()), connection.getContentLength(), connection.getResponseCode()==200?connection.getInputStream():connection.getErrorStream());
            }
        } else {
            return super.executeRequest(request, additionalHeaders);
        }
    }
    private static boolean hasResponseBody(int requestMethod, int responseCode) {
        return requestMethod != 4 && (100 > responseCode || responseCode >= 200) && responseCode != 204 && responseCode != 304;
    }
    static List<Header> convertHeaders(Map<String, List<String>> responseHeaders) {
        List<Header> headerList = new ArrayList(responseHeaders.size());
        Iterator var2 = responseHeaders.entrySet().iterator();
        while(true) {
            Map.Entry entry;
            do {
                if (!var2.hasNext()) {
                    return headerList;
                }
                entry = (Map.Entry)var2.next();
            } while(entry.getKey() == null);
            Iterator var4 = ((List)entry.getValue()).iterator();
            while(var4.hasNext()) {
                String value = (String)var4.next();
                headerList.add(new Header((String)entry.getKey(), value));
            }
        }
    }
    /**
     * 添加文件，避免内存巨大
     *
     * @param connection
     * @param request
     * @throws IOException
     * @throws AuthFailureError
     */
    private static void addFileBody(HttpURLConnection connection, SingleFileRequest request) throws IOException, AuthFailureError {
        final String HEADER_CONTENT_TYPE = "Content-Type";
        String filePath = request.getFilePath();
        if (filePath == null) {
            throw new IOException("File文件路径为空");
        }
        File file = new File(filePath);
        if (file == null || !file.exists()) {
            throw new IOException("File 文件不存在");
        }
        //设置post请求方法，允许写入客户端传递的参数
        connection.setDoOutput(true);
        //设置标头的Content-Type属性
        connection.addRequestProperty(HEADER_CONTENT_TYPE, request.getBodyContentType());
        DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
        FileInputStream fileInputStream = new FileInputStream(file);
        long total = 0;
        int read;
        outputStream.writeUTF(request.getContentHeader());
        byte[] buffer = new byte[SingleFileRequest.READ_SIZE];
        while ((read = fileInputStream.read(buffer)) != -1) {
            if (request.isCanceled()) {
                fileInputStream.close();
                outputStream.close();
                throw new IOException("SingleRequest 被取消");
            }
            outputStream.write(buffer, 0, read);
            outputStream.flush();
            total += read;
            int progress = (int) (total * 100 / file.length());
            request.deliverProgress(progress);
        }
        outputStream.writeUTF(request.getContentFoot());
        outputStream.flush();
        fileInputStream.close();
        outputStream.close();
    }
}
