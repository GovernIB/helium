package net.conselldemallorca.helium.jbpm3.helper;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandler;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.ClientRequest;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.filter.ClientFilter;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.util.ArrayList;

public class RestClientHelper {

    private final static int CONNECT_TIMEOUT = 5000;
    private final static int READ_TIMEOUT = 30000;

    public static Client generarClient() {
        Client jerseyClient = Client.create();
        jerseyClient.setConnectTimeout(CONNECT_TIMEOUT);
        jerseyClient.setReadTimeout(READ_TIMEOUT);
//        jerseyClient.addFilter(new RedirectFilter());
//        jerseyClient.addFilter(new CookieFilter());
        return jerseyClient;
    }

    static class RedirectFilter extends ClientFilter {
        @Override
        public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
            ClientHandler ch = getNext();
            ClientResponse resp = ch.handle(request);

            if (resp.getStatusInfo().getFamily() != Response.Status.Family.REDIRECTION) {
                return resp;
            } else {
                String redirectTarget = resp.getHeaders().getFirst("Location");
                request.setURI(UriBuilder.fromUri(redirectTarget).build());
                return ch.handle(request);
            }
        }
    }

    static class CookieFilter extends ClientFilter {
        private ArrayList<Object> cookies;
        @Override
        public ClientResponse handle(ClientRequest request) throws ClientHandlerException {
            if (cookies != null) {
                request.getHeaders().put("Cookie", cookies);
            }
            ClientResponse response = getNext().handle(request);
            if (response.getCookies() != null) {
                if (cookies == null) {
                    cookies = new ArrayList<Object>();
                }
                cookies.addAll(response.getCookies());
            }
            return response;
        }
    }

}
