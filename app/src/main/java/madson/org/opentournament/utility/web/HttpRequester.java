package madson.org.opentournament.utility.web;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.net.HttpURLConnection;
import java.net.URL;

import java.nio.charset.Charset;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Provides functionality to make web requests.
 */
public class HttpRequester {

    /**
     * Perform a get on the given url. The response body is returned as a byte array.
     *
     * @param  url  url to query
     *
     * @return  the {@link Response} with the response body as byte array
     *
     * @throws  IOException
     * @throws  HttpRequestException
     */
    public Response<byte[]> get(String url) throws IOException, HttpRequestException {

        HttpURLConnection connection = prepareConnection(url, "", "GET");

        return doRequestForBytes(connection, null);
    }


    /**
     * Perform a get on the given url. The response body is returned as a String.
     *
     * @param  url  url to query
     *
     * @return  the {@link Response} with the response body as String
     *
     * @throws  IOException
     * @throws  HttpRequestException
     */
    public Response<String> getString(String url) throws IOException, HttpRequestException {

        HttpURLConnection connection = prepareConnection(url, "", "GET");

        return doRequestForString(connection, null);
    }


    /**
     * Perform a post on the given url with the given body as content.
     *
     * @param  url  url to send the post to
     * @param  body  body of the post
     *
     * @return  result as String, can be null
     *
     * @throws  IOException
     * @throws  HttpRequestException
     */
    public Response<String> postJsonString(String url, String body) throws IOException, HttpRequestException {

        HttpURLConnection urlConnection = prepareConnection(url, body, "POST");
        setContentType(urlConnection, "application/json");

        return doRequestForString(urlConnection, body);
    }


    /**
     * Perform a put on the given url with the given body as content.
     *
     * @param  url  url to send the put to
     * @param  body  body of the put
     *
     * @return  result as String, can be null
     *
     * @throws  IOException
     * @throws  HttpRequestException
     */
    public Response<String> putJsonString(String url, String body) throws IOException, HttpRequestException {

        HttpURLConnection connection = prepareConnection(url, body, "PUT");

        setContentType(connection, "application/json");

        return doRequestForString(connection, body);
    }


    /**
     * Perform a delete on the given url.
     *
     * @param  url  url to send the delete to
     *
     * @throws  IOException
     * @throws  HttpRequestException
     */
    public void delete(String url) throws IOException, HttpRequestException {

        HttpURLConnection connection = prepareConnection(url, "", "DELETE");

        doRequestForBytes(connection, "");
    }


    /**
     * Called before a HttpUriRequest is executed. Override this method if you need to alter the request or add header
     * values.
     *
     * @param  connection  the request that is about to get executed.
     * @param  body  the body of the request as String
     *
     * @return  the altered request.
     *
     * @throws  HttpRequestException  if extending classes throw an exception
     */
    protected HttpURLConnection beforeRequestExecute(HttpURLConnection connection, String body)
        throws HttpRequestException {

        return connection;
    }


    /**
     * Prepare the connection for usage. This includes verifying the url, opening the connection, setting the request
     * method, and calling {@link #beforeRequestExecute(HttpURLConnection, String)}.
     *
     * @param  httpUrl  the url that should be called
     * @param  body  the request body, may be null or empty
     * @param  method  the request method
     *
     * @return  the opened HttpUrlConnection
     *
     * @throws  IOException
     * @throws  HttpRequestException  if extending classes throw an exception
     */
    protected HttpURLConnection prepareConnection(String httpUrl, String body, String method) throws IOException,
        HttpRequestException {

        URL url = new URL(httpUrl);

        if (!url.getProtocol().equalsIgnoreCase("https") && !url.getProtocol().equalsIgnoreCase("http")) {
            throw new IllegalArgumentException("Only http and https protocol are supported!");
        }

        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        urlConnection.setRequestMethod(method);

        urlConnection = beforeRequestExecute(urlConnection, body);

        return urlConnection;
    }


    /**
     * Executes the given request and return the result as a byte array.
     *
     * @param  connection  the request to execute.
     * @param  body  optional request body to send
     *
     * @return  a {@link Response} with the response data as byte[]. The inner responseBody can be null.
     *
     * @throws  IOException
     * @throws  HttpRequestException  if extending classes throw an exception
     */
    protected Response<byte[]> doRequestForBytes(HttpURLConnection connection, String body) throws IOException,
        HttpRequestException {

        connection.setDoInput(true);

        Response response;
        int statusCode = -1;
        Map<String, List<String>> headerFields = new HashMap<>();

        try {
            writeBody(connection, body);

            statusCode = connection.getResponseCode();
            headerFields = connection.getHeaderFields();

            byte[] result;

            if (statusCode == 200) {
                result = getContentAsBytes(connection.getInputStream());
            } else {
                result = new byte[0];
            }

            response = new Response<>(result, headerFields, statusCode);
        } catch (FileNotFoundException e) {
            response = new Response<>("", headerFields, statusCode);
        } finally {
            connection.disconnect();
        }

        return afterRequestExecute(response);
    }


    /**
     * Executes the given request and parses the result to String.
     *
     * @param  connection  the request to execute.
     * @param  body  optional request body to send
     *
     * @return  a {@link Response} with the response data as String. The inner responseBody can be null.
     *
     * @throws  IOException
     * @throws  HttpRequestException  if extending classes throw an exception
     */
    protected Response<String> doRequestForString(HttpURLConnection connection, String body) throws IOException,
        HttpRequestException {

        connection.setDoInput(true);

        Response response = null;
        int statusCode = -1;
        Map<String, List<String>> headerFields = new HashMap<>();

        try {
            writeBody(connection, body);

            statusCode = connection.getResponseCode();

            headerFields = connection.getHeaderFields();

            String result = getContentAsString(connection.getInputStream());

            response = new Response<>(result, headerFields, statusCode);
        } catch (FileNotFoundException e) {
            response = new Response<>("", headerFields, statusCode);
        } finally {
            connection.disconnect();
        }

        return afterRequestExecute(response);
    }


    private void writeBody(HttpURLConnection connection, String body) throws IOException {

        if (body != null && !body.isEmpty()) {
            connection.setDoOutput(true);

            byte[] content = body.getBytes("UTF-8");
            connection.getOutputStream().write(content);
        }
    }


    /**
     * Called after successful request execution.
     *
     * @param  response  the response of the request
     *
     * @return  the (maybe edited) response
     *
     * @throws  HttpRequestException  if extending classes throw an exception
     */
    protected Response afterRequestExecute(Response response) throws HttpRequestException {

        // override me
        return response;
    }


    /**
     * Gets the content from a HttpResponse as a String.
     *
     * @param  in  input stream to get the content from
     *
     * @return  content as string
     *
     * @throws  IOException
     */
    private String getContentAsString(InputStream in) throws IOException {

        StringBuilder payload = new StringBuilder();

        BufferedReader buffer = null;

        try {
            buffer = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));

            String line = buffer.readLine();

            while (line != null) {
                payload.append(line);
                line = buffer.readLine();
            }
        } finally {
            buffer.close();
        }

        return payload.toString();
    }


    /**
     * Gets the content from a HttpResponse as a byte[].
     *
     * @param  in  input stream to get the content from
     *
     * @return  content as byte[]
     *
     * @throws  IOException
     */
    private byte[] getContentAsBytes(InputStream in) throws IOException {

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        byte[] data;

        try {
            byte[] buffer = new byte[16384];
            int bytecount;

            while ((bytecount = in.read(buffer)) != -1) {
                bos.write(buffer, 0, bytecount);
            }

            data = bos.toByteArray();
        } finally {
            bos.flush();
            bos.close();
        }

        return data;
    }


    /**
     * Set the given content type to the header of the connection.
     *
     * @param  connection  to set the content type to
     * @param  contentType  to set
     */
    protected void setContentType(HttpURLConnection connection, String contentType) {

        connection.setRequestProperty("Content-Type", contentType);
    }
}
