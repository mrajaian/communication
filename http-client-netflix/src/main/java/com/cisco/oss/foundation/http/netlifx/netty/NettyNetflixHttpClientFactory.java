/*
 * Copyright 2015 Cisco Systems, Inc.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.cisco.oss.foundation.http.netlifx.netty;

import com.cisco.oss.foundation.configuration.ConfigurationFactory;
import com.cisco.oss.foundation.http.ClientException;
import com.cisco.oss.foundation.http.HttpClient;
import com.cisco.oss.foundation.http.HttpRequest;
import com.cisco.oss.foundation.http.netlifx.netty.NettyNetflixHttpResponse;
import com.cisco.oss.foundation.loadbalancer.LoadBalancerStrategy;
import org.apache.commons.configuration.Configuration;

import javax.net.ssl.HostnameVerifier;

/**
 * Created by Yair Ogen (yaogen) on 13/12/2015.
 */
public class NettyNetflixHttpClientFactory {

    public static HttpClient<HttpRequest, NettyNetflixHttpResponse> createHttpClient(String apiName, boolean enableLoadBalancing) {
        return createHttpClient(apiName, ConfigurationFactory.getConfiguration(), enableLoadBalancing);
    }

    public static HttpClient<HttpRequest, NettyNetflixHttpResponse> createHttpClient(String apiName) {
        return createHttpClient(apiName, ConfigurationFactory.getConfiguration());
    }

    public static HttpClient<HttpRequest, NettyNetflixHttpResponse> createHttpClient(String apiName, HostnameVerifier hostnameVerifier) {
        return createHttpClient(apiName, ConfigurationFactory.getConfiguration(), hostnameVerifier);
    }

    public static HttpClient<HttpRequest,NettyNetflixHttpResponse> createHttpClient(String apiName, Configuration configuration, HostnameVerifier hostnameVerifier){
        return createHttpClient(apiName, configuration, true, hostnameVerifier);
    }

    public static HttpClient<HttpRequest, NettyNetflixHttpResponse> createHttpClient(String apiName, Configuration configuration, boolean enableLoadBalancing) {
        return createHttpClient(apiName, configuration, enableLoadBalancing, null);
    }


    public static HttpClient<HttpRequest, NettyNetflixHttpResponse> createHttpClient(String apiName, Configuration configuration) {
        return createHttpClient(apiName, configuration, true);
    }





    public static HttpClient<HttpRequest, NettyNetflixHttpResponse> createHttpClient(String apiName, Configuration configuration, boolean enableLoadBalancing, HostnameVerifier hostnameVerifier) {
        try {
            HttpClient client = null;
            client = new NettyNetflixHttpClient(apiName, configuration, enableLoadBalancing, hostnameVerifier);
            return client;
        } catch (Exception e) {
            throw new ClientException(e.toString(), e);
        }

    }

}
