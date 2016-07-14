package com.parking.management.externalservices;

import com.ning.http.util.Base64;
import com.parking.management.externalservices.bean.AuthenticationInfo;
import com.parking.management.externalservices.rest.RestClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by VST on 07-07-2016.
 */
@Component
public class AuthenticationHelper {

    @Autowired
    private AuthenticationInfo authenticationInfo;

    public String login() throws URISyntaxException, UnsupportedEncodingException {
        String url = "http://" + authenticationInfo.getHost() + ":" + authenticationInfo.getPort()
                + "/umc/rest/sessions?tenant=" + authenticationInfo.getTenantId()
                + "&name=" + authenticationInfo.getUserName() + "&password=" +authenticationInfo.getPassword();
        RestClient.Response response = RestClient.CLIENT.post(new URI(url));
        return Base64.encode(response.getBody().getBytes("UTF-8"));
    }

    public boolean logout(String sessionId) {
        authenticationInfo.getHost();
        return true;
    }
}
