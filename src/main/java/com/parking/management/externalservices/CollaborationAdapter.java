package com.parking.management.externalservices;

import com.parking.management.externalservices.bean.AuthenticationInfo;
import com.parking.management.externalservices.builder.MessageBuilder;
import com.parking.management.externalservices.rest.RestClient;
import org.springframework.beans.factory.annotation.Autowired;

import com.ning.http.client.Cookie;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by VST on 07-07-2016.
 */
public class CollaborationAdapter {

    @Autowired
    AuthenticationHelper authenticationHelper;

    @Autowired
    AuthenticationInfo authenticationInfo;

    public void postUpdateToFeed(String locationName, String slotName, boolean parked) {
        return;
        /*String sessionId = null;
        try {
            sessionId = authenticationHelper.login();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        RestClient.Response response = null;
        Cookie cookie = new Cookie(authenticationInfo.getHost(), "Authorization", sessionId, "", 999, false);
        createParkingLocationFeed(locationName, cookie);

        postToFeed(locationName, slotName, parked, cookie);*/
    }

    private boolean postToFeed(String locationName, String slotName, boolean parked, Cookie cookie) {
        RestClient.Response response = null;
        String url = "http://" + authenticationInfo.getHost() + ":" + authenticationInfo.getPort()
                + "/collaboration/rest/groups/" + locationName.replaceAll("\\s+","").toLowerCase() + "/activities";

        try {
            response = RestClient.CLIENT.post(new URI(url),
                    MessageBuilder.createPostMessageBody(locationName, slotName, parked), cookie);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }

        assert response != null;
        return response.getStatusCode() == RestClient.StatusCode.OK;
    }

    private boolean isFeedExist(String name, Cookie cookie) {
        String url = "http://" + authenticationInfo.getHost() + ":" + authenticationInfo.getPort()
                + "/collaboration/rest/groups/" + name.replaceAll("\\s+","").toLowerCase() + "/followers";
        RestClient.Response response = null;
        try {
            response = RestClient.CLIENT.get(new URI(url), cookie);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }
        return response.getStatusCode() == RestClient.StatusCode.OK;
    }

    private boolean createParkingLocationFeed(String name, Cookie cookie) {
        RestClient.Response response = null;
        if (isFeedExist(name, cookie)) {
            return true;
        }
        String url = "http://" + authenticationInfo.getHost() + ":" + authenticationInfo.getPort()
                + "/collaboration/rest/groups";
        try {
            response = RestClient.CLIENT.post(new URI(url),
                    MessageBuilder.createFeedMessageBody(name), cookie);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        }

        assert response != null;
        return response.getStatusCode() == RestClient.StatusCode.OK;
    }
}
