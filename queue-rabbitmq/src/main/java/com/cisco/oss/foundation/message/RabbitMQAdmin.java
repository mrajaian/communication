package com.cisco.oss.foundation.message;

import com.cisco.oss.foundation.configuration.ConfigUtil;
import com.cisco.oss.foundation.configuration.ConfigurationFactory;
import com.cisco.oss.foundation.http.HttpClient;
import com.cisco.oss.foundation.http.HttpMethod;
import com.cisco.oss.foundation.http.HttpRequest;
import com.cisco.oss.foundation.http.HttpResponse;
import com.cisco.oss.foundation.http.apache.ApacheHttpClientFactory;
import com.google.common.collect.Lists;
import com.jayway.jsonpath.JsonPath;
import com.rabbitmq.client.Address;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.configuration.Configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by Yair Ogen (yaogen) on 09/03/2016.
 */
public enum RabbitMQAdmin {
    INSTANCE;

    private RabbitMQAdmin() {
        Configuration configuration = ConfigurationFactory.getConfiguration();

        final Map<String, Map<String, String>> serverConnections = ConfigUtil.parseComplexArrayStructure("service.rabbitmq.admin.connections");
        final ArrayList<String> serverConnectionKeys = Lists.newArrayList(serverConnections.keySet());
        Collections.sort(serverConnectionKeys);

        for (int i = 0; i < serverConnectionKeys.size(); i++) {


            Map<String, String> serverConnection = serverConnections.get(serverConnectionKeys.get(i));
            String host = serverConnection.get("host");
            int port = Integer.parseInt(serverConnection.get("port"));
            configuration.setProperty("rabbitMqAdmin." + i + ".host", host);
            configuration.setProperty("rabbitMqAdmin." + i + ".port", port);
        }

        configuration.setProperty("rabbitMqAdmin.http.exposeStatisticsToMonitor", "false");
        configuration.setProperty("rabbitMqAdmin.http.autoEncodeUri", "false");

    }

    private HttpClient httpClient;
    private String authStringEncoded;

    public void initAdmin(String adminUser, String adminPwd) {
        if (httpClient == null) {
            httpClient = ApacheHttpClientFactory.createHttpClient("rabbitMqAdmin");
            String authString = adminUser + ":" + adminPwd;
            byte[] authEncBytes = Base64.encodeBase64(authString.getBytes());
            authStringEncoded = new String(authEncBytes);
        }
    }

    public void initAdmin() {
        String adminUser = ConfigurationFactory.getConfiguration().getString("service.rabbitmq.admin.userName");
        String adminPwd = ConfigurationFactory.getConfiguration().getString("service.rabbitmq.admin.password");
        initAdmin(adminUser, adminPwd);
    }

    public int getQueueLength(String queueName) {


        String uri = "api/queues/%2f/" + queueName;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .httpMethod(HttpMethod.GET)
                .header("Accept", "application/json")
                .header("Authorization", "Basic " + authStringEncoded)
                .build();

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpRequest);
        } catch (Exception e) {
            throw new RabbitMQAdminException("Can't execute rabbitmq admin request. error is: " + e, e);
        }
        int status = httpResponse.getStatus();
        String responseAsString = httpResponse.getResponseAsString();
        if (status == 200) {
            Object queueLength = JsonPath.read(responseAsString, "$.backing_queue_status.len");
            return (Integer) queueLength;
        } else {
            throw new RabbitMQAdminException("Got bad response from rabbitmq server. Code: " + status + ". Reason: " + responseAsString);
        }
    }

    public String getOverview() {
        return getOverview(false);
    }

    public String getOverview(boolean getFullOverview) {

        String uri = "api/overview";
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .httpMethod(getFullOverview ? HttpMethod.GET : HttpMethod.HEAD)
                .header("Accept", "application/json")
                .header("Authorization", "Basic " + authStringEncoded)
                .build();

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpRequest);
        } catch (Exception e) {
            throw new RabbitMQAdminException("Can't execute rabbitmq admin request. error is: " + e, e);
        }
        int status = httpResponse.getStatus();
        String responseAsString = httpResponse.getResponseAsString();
        if (status == 200) {
            return responseAsString;
        } else {
            throw new RabbitMQAdminException("Got bad response from rabbitmq server. Code: " + status + ". Reason: " + responseAsString);
        }
    }

    public void deleteQueue(final String queueName){
        String uri = "api/queues/%2f/" + queueName;
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(uri)
                .httpMethod(HttpMethod.DELETE)
                .header("Accept", "application/json")
                .header("Authorization", "Basic " + authStringEncoded)
                .build();

        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpRequest);
        } catch (Exception e) {
            throw new RabbitMQAdminException("Can't execute rabbitmq admin request. error is: " + e, e);
        }
        int status = httpResponse.getStatus();
        String responseAsString = httpResponse.getResponseAsString();
        if (status != 204) {
            throw new RabbitMQAdminException("Got bad response from rabbitmq server. Code: " + status + ". Reason: " + responseAsString);
        }
    }
}
