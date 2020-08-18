import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONObject;

import java.net.URI;

public class Utility {

    public static JSONObject post(URI uri) throws Exception {
        HttpPost httpPost = new HttpPost(uri);
        return execute(httpPost);
    }

    public static JSONObject get(URI uri) throws Exception {
        HttpGet httpGet = new HttpGet(uri);
        return execute(httpGet);
    }

    private static JSONObject execute(HttpRequestBase httpRequestBase) throws Exception {
        CloseableHttpClient closeableHttpClient = HttpClients.createDefault();

        CloseableHttpResponse closeableHttpResponse = closeableHttpClient.execute(httpRequestBase);

        String responseString = EntityUtils.toString(closeableHttpResponse.getEntity(), "UTF-8");

        JSONObject responseJSON = new JSONObject(responseString);

        if (responseJSON.getInt("ret_code") != 0)
            throw new Exception("Code " + responseJSON.getInt("ret_code") + " received");

        return responseJSON;
    }

}
