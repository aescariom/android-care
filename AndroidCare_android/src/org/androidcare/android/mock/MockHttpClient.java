package org.androidcare.android.mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpResponseFactory;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.impl.DefaultHttpResponseFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.message.BasicStatusLine;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HttpContext;

import android.content.Context;
import android.content.res.AssetManager;

//@comentario bien hecho. De hacer falta, esto puede extenderse en el futuro para tener un mapa que contenga
//URLs  y archivos con el JSON asociado a una petición a esa URL, de tal modo que se pueda
//programáticamente añadir  URLs e indicar cuál debe ser la respuesta,  para facilitar el hacer
//test
public class MockHttpClient implements HttpClient {
    protected HttpResponse injectedResponse = null;
    protected Context context = null;

    /** The cookie store. */
    private CookieStore cookieStore;

    public MockHttpClient(Context applicationContext) {
        super();
        this.context = applicationContext;
    }

    public HttpResponse execute(HttpUriRequest req) throws IOException, ClientProtocolException {

        getCookieStore();
        cookieStore.addCookie(getAppEngineAuthCookie());

        if (injectedResponse != null) {
            return injectedResponse;
        }

        String path = req.getURI().getPath();
        HttpResponse response = getBasicHttpResponse();
        BasicHttpEntity entity = new BasicHttpEntity();
        AssetManager assetManager = context.getAssets();
        if (assetManager == null) {
            return response;
        }

        if (path.equalsIgnoreCase("/api/retrieveReminders")) {
            entity.setContent(assetManager.open("retrieveReminders.json"));
        }
        if (path.equalsIgnoreCase("/api/addReminderLog")) {
            entity.setContent(assetManager.open("addReminderLog.json"));
        }
        if (path.equalsIgnoreCase("/api/addPosition")) {
            entity.setContent(assetManager.open("addPosition.json"));
        }

        response.setEntity(entity);
        return response;
    }

    private HttpResponse getBasicHttpResponse() {
        HttpResponseFactory factory = new DefaultHttpResponseFactory();
        return factory.newHttpResponse(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, null),
                null);
    }

    public void setHttpResponse(HttpResponse response) {
        injectedResponse = response;
    }

    public void setHttpResponse(String str) {
        HttpResponse response = getBasicHttpResponse();
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(str.getBytes()));
        response.setEntity(entity);
        injectedResponse = response;
    }

    public synchronized final CookieStore getCookieStore() {
        if (cookieStore == null) {
            cookieStore = createCookieStore();
        }
        return cookieStore;
    }

    private CookieStore createCookieStore() {
        return new BasicCookieStore();
    }

    public HttpResponse execute(HttpUriRequest request, HttpContext context) throws IOException,
            ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    public HttpResponse execute(HttpHost target, HttpRequest request) throws IOException,
            ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1) throws IOException,
            ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    public HttpResponse execute(HttpHost target, HttpRequest request, HttpContext context)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    public <T> T execute(HttpUriRequest arg0, ResponseHandler<? extends T> arg1, HttpContext arg2)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    public <T> T execute(HttpHost arg0, HttpRequest arg1, ResponseHandler<? extends T> arg2, HttpContext arg3)
            throws IOException, ClientProtocolException {
        throw new UnsupportedOperationException();
    }

    public ClientConnectionManager getConnectionManager() {
        throw new UnsupportedOperationException();
    }

    public HttpParams getParams() {
        throw new UnsupportedOperationException();
    }

    private Cookie getAppEngineAuthCookie() {
        Cookie c = new Cookie() {

            public boolean isSecure() {
                throw new UnsupportedOperationException();
            }

            public boolean isPersistent() {
                throw new UnsupportedOperationException();
            }

            public boolean isExpired(Date date) {
                return false; // it shoud never expire
            }

            public int getVersion() {
                throw new UnsupportedOperationException();
            }

            public String getValue() {
                throw new UnsupportedOperationException();
            }

            public int[] getPorts() {
                throw new UnsupportedOperationException();
            }

            public String getPath() {
                throw new UnsupportedOperationException();
            }

            public String getName() {
                return "ACSID";
            }

            public Date getExpiryDate() {
                throw new UnsupportedOperationException();
            }

            public String getDomain() {
                throw new UnsupportedOperationException();
            }

            public String getCommentURL() {
                throw new UnsupportedOperationException();
            }

            public String getComment() {
                throw new UnsupportedOperationException();
            }
        };
        return c;
    }

}
