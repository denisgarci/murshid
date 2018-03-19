package com.murshid.ingestor;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import java.net.ConnectException;

import static com.google.common.base.Preconditions.checkArgument;

public class ClientBuilderUtil {

    private static final int MILLIS_PER_MINUTE = 60 * 1000;
    public static final int CONNECT_TIMEOUT_MILLIS = 10 * MILLIS_PER_MINUTE;
    public static final int REQUEST_TIMEOUT_MILLIS = 30 * MILLIS_PER_MINUTE;
    public static final String CONNECT_TIMEOUT = "jersey.config.client.connectTimeout";
    public static final String READ_TIMEOUT = "jersey.config.client.readTimeout";


    /**
     * Created a new client and set the connection and request timeouts. If a connection timeout occurs,
     * {@link ConnectException} will be thrown. If a request timeout occurs, {@link java.net.SocketTimeoutException} will be
     * thrown
     *
     * @return new Client with default timeout values.
     */
    public static Client createClient() {
        return setTimeout(ClientBuilder.newClient());
    }

    /**
     * Set the connection and request timeouts. If a connection timeout occurs,
     * {@link ConnectException} will be thrown. If a request timeout occurs, {@link java.net.SocketTimeoutException} will be
     * thrown
     *
     * @param client the client to set the default timeout properties on
     */
    public static Client setTimeout(Client client) {
        return setTimeout(client, CONNECT_TIMEOUT_MILLIS, REQUEST_TIMEOUT_MILLIS);

    }

    /**
     * Set the connection and request timeouts. If a connection timeout occurs,
     * {@link ConnectException} will be thrown. If a request timeout occurs, {@link java.net.SocketTimeoutException} will be
     * thrown
     *
     * @param client               the client to set the default timeout properties on
     * @param connectTimeoutMillis Connect timeout interval, in milliseconds.
     * @param requestTimeoutMillis Read timeout interval, in milliseconds.
     */
    public static Client setTimeout(Client client, int connectTimeoutMillis, int requestTimeoutMillis) {
        checkArgument(connectTimeoutMillis > 0, "Connection timeout must be greater than zero");
        checkArgument(requestTimeoutMillis > 0, "Request timeout must be greater than zero");

        client.property(CONNECT_TIMEOUT, connectTimeoutMillis);
        client.property(READ_TIMEOUT, requestTimeoutMillis);
        return client;
    }

}
