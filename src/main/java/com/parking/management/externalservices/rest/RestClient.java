package com.parking.management.externalservices.rest;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import com.ning.http.client.*;
import com.ning.http.client.Cookie;
import com.ning.http.client.resumable.ResumableIOExceptionFilter;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

/**
 * Client to access a server with a RESTful interface. Is based on a HTTP client.
 */
public class RestClient
{
    private static final int CONNECT_TIMEOUT_MS = 10000;
    private static final int READ_TIMEOUT_MS = 21600000;
    private int IDLE_TIMEOUT_MS = 180000;
    private static final int MAX_CONNECTIONS_PER_HOST = 25;
    private int MAX_CONNECTIONS_TOTAL = 100;
    private static final int MAX_RETRY_COUNT = 5;

    private static final String URI_IS_NULL_ERROR = "The URI to send HTTP request to is null.";
    private static final String INVALID_BODY_ERROR = "The body to send with the HTTP request is invalid: ";
    private static final String MALFORMED_URL_ERROR = "The URL to send the HTTP request to is malformed: ";
    private static final String REQUEST_EXECUTION_ERROR = "Could not execute HTTP request: ";
    private static final String NO_RESPONSE_ERROR = "Could not get HTTP response: ";
    private static final String UNKNOWN_STATUS_CODE_ERROR = "Unknown status code in HTTP response: ";

    public static RestClient CLIENT = new RestClient(1000 * 60 * 6);

    public Response post(URI uri) {
        validateParameter(uri);

        return executeRequestAndReturnResponse(HttpMethod.POST, uri, null, null);
    }

    private enum HttpMethod
    {
        POST("POST"),

        PUT("PUT"),

        GET("GET");

        private String httpMethodString;

        private HttpMethod(String httpMethodString)
        {
            this.httpMethodString = httpMethodString;
        }

        public String getAsString()
        {
            return httpMethodString;
        }
    }

    public enum StatusCode
    {
        OK(200), CREATED(201), BAD_REQUEST(400), NOT_FOUND(404);

        private int statusCodeNumber;

        private StatusCode(int statusCodeNumber)
        {
            this.statusCodeNumber = statusCodeNumber;
        }
    }

    public class Response
    {
        private StatusCode statusCode;
        private String body;

        public Response(StatusCode statusCode, String body)
        {
            this.statusCode = statusCode;
            this.body = body;
        }

        public StatusCode getStatusCode()
        {
            return statusCode;
        }

        public String getBody()
        {
            return body;
        }
    }

    private AsyncHttpClient asyncHttpClient;

    public RestClient(int idleTimeOut)
    {
        IDLE_TIMEOUT_MS = idleTimeOut;
        asyncHttpClient = createHttpClient();
    }

    /**
     * Method to be overridden by test classes, so that the HTTP client can be mocked.
     *
     * @return HTTP client
     */
    protected AsyncHttpClient createHttpClient()
    {
        AsyncHttpClientConfig.Builder builder = new AsyncHttpClientConfig.Builder();
        builder.setAllowPoolingConnection(true).setAllowSslConnectionPool(true).setCompressionEnabled(true)
                .setFollowRedirects(false).setMaximumConnectionsTotal(MAX_CONNECTIONS_TOTAL)
                .setMaximumConnectionsPerHost(MAX_CONNECTIONS_PER_HOST).setConnectionTimeoutInMs(CONNECT_TIMEOUT_MS)
                .setIdleConnectionTimeoutInMs(IDLE_TIMEOUT_MS).setRequestTimeoutInMs(READ_TIMEOUT_MS)
                .setMaxRequestRetry(MAX_RETRY_COUNT).addIOExceptionFilter(new ResumableIOExceptionFilter());

        ExecutorService callbackExecutor = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setDaemon(true)
                .setNameFormat("EcpAsyncCallback-%s").build());
        builder.setExecutorService(callbackExecutor);

        ScheduledExecutorService idleConnectionReaper = Executors.newScheduledThreadPool(Runtime.getRuntime()
                .availableProcessors(), new ThreadFactoryBuilder().setDaemon(true).setNameFormat(
                "EcpIdleConnectionReaper-%s").build());
        builder.setScheduledExecutorService(idleConnectionReaper);

        builder.setHostnameVerifier(HttpsURLConnection.getDefaultHostnameVerifier());

        return new AsyncHttpClient(builder.build());
    }

    /**
     * Executes a request with the POST method for a specific URI and with a specific request body
     *
     * @param uri URI where the POST request is sent to
     * @param body The body of the request
     * @return The response body
     */
    public Response post(URI uri, String body)
    {
        validateParameters(uri, body);

        return executeRequestAndReturnResponse(HttpMethod.POST, uri, body, null);
    }

    public Response post(URI uri, String body, Cookie cookie)
    {
        validateParameters(uri, body);

        return executeRequestAndReturnResponse(HttpMethod.POST, uri, body, cookie);
    }

    public Response put(URI uri)
    {
        validateParameter(uri);

        return executeRequestAndReturnResponse(HttpMethod.PUT, uri, null, null);
    }

    public Response get(URI uri, Cookie cookie)
    {
        validateParameter(uri);

        return executeRequestAndReturnResponse(HttpMethod.GET, uri, null, cookie);
    }

    private Response executeRequestAndReturnResponse(HttpMethod httpMethod, URI uri, String body, Cookie cookie)
    {
        com.ning.http.client.Response response = executeRequest(httpMethod, uri, body, cookie);

        String responseBody = null;

        try
        {
            responseBody = response.getResponseBody();
        }
        catch (IOException e)
        {
            handleStateError(e.toString());
        }

        return new Response(getStatusCodeFromNumber(response.getStatusCode()), responseBody);
    }

    private com.ning.http.client.Response executeRequest(HttpMethod httpMethod, URI uri, String body, Cookie cookie)
    {
        Request request = createRequest(httpMethod, uri, body, cookie);

        return executeRequest(request);
    }

    private Request createRequest(HttpMethod httpMethod, URI uri, String body, Cookie cookie)
    {
        RequestBuilder requestBuilder = new RequestBuilder();
        requestBuilder.setMethod(httpMethod.getAsString());
        requestBuilder.setUrl(getURLString(uri));

        if (body != null)
        {
            requestBuilder.setBodyEncoding("UTF-8");
            requestBuilder.setBody(body);
            requestBuilder.setHeader("Content-Type", "application/json");
        }

        if (cookie != null) {
            requestBuilder.setHeader(cookie.getName(), cookie.getValue());
        }

        return requestBuilder.build();
    }

    private String getURLString(URI uri)
    {
        URL url = null;

        try
        {
            url = uri.toURL();
        }
        catch (MalformedURLException e)
        {
            handleArgumentError(MALFORMED_URL_ERROR + uri.toString());
        }

        return url.toString();
    }

    private com.ning.http.client.Response executeRequest(Request request)
    {
        ListenableFuture<com.ning.http.client.Response> listenableFuture = null;

        try
        {
            listenableFuture = asyncHttpClient.executeRequest(request);
        }
        catch (IOException e)
        {
            handleStateError(REQUEST_EXECUTION_ERROR + e.getMessage());
        }

        if (listenableFuture == null)
        {
            handleStateError(NO_RESPONSE_ERROR + listenableFuture);
        }

        com.ning.http.client.Response response = null;

        try
        {
            response = listenableFuture.get();
        }
        catch (InterruptedException e)
        {
            handleStateError(NO_RESPONSE_ERROR + e.getMessage());
        }
        catch (ExecutionException e)
        {
            handleStateError(NO_RESPONSE_ERROR + e.getMessage());
        }

        return response;
    }

    private StatusCode getStatusCodeFromNumber(int statusCodeNumber)
    {
        StatusCode statusCode = null;

        System.out.println("Status code number is " + statusCodeNumber);

        if (statusCodeNumber == StatusCode.OK.statusCodeNumber)
        {
            statusCode = StatusCode.OK;
        }
        else if (statusCodeNumber == StatusCode.CREATED.statusCodeNumber)
        {
            statusCode = StatusCode.CREATED;
        }
        else if (statusCodeNumber == StatusCode.NOT_FOUND.statusCodeNumber)
        {
            statusCode = StatusCode.NOT_FOUND;
        }
        else if ( statusCodeNumber == StatusCode.BAD_REQUEST.statusCodeNumber){
            statusCode = StatusCode.BAD_REQUEST;
        }
        else
        {
            handleStateError(UNKNOWN_STATUS_CODE_ERROR + statusCodeNumber);
        }

        return statusCode;
    }

    /**
     * Closes the internal http client
     */
    public void close()
    {
        asyncHttpClient.close();
    }

    private void validateParameters(URI uri, String body)
    {
        validateParameter(uri);

        if (body == null || body.trim().isEmpty())
        {
            handleArgumentError(INVALID_BODY_ERROR + body);
        }
    }

    private void validateParameter(URI uri)
    {
        if (uri == null)
        {
            handleArgumentError(URI_IS_NULL_ERROR);
        }
    }

    private void handleArgumentError(String errorMessage)
    {
        handleError(errorMessage, new IllegalArgumentException(errorMessage));
    }

    private void handleStateError(String errorMessage)
    {
        handleError(errorMessage, new IllegalStateException(errorMessage));
    }

    private void handleError(String errorMessage, RuntimeException runtimeException)
    {
        throw runtimeException;
    }

    public int getMaxConnectionsTotal() {
        return MAX_CONNECTIONS_TOTAL;
    }

    public void setMaxConnectionsTotal(int maxConnectionsTotal) {
        this.MAX_CONNECTIONS_TOTAL = maxConnectionsTotal;
    }
}
