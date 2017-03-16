package vn.hust.soict.lung_function.net;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Arrays;

import vn.hust.soict.lung_function.R;
import vn.hust.soict.lung_function.model.LungFunction;
import vn.hust.soict.lung_function.model.Profile;

/**
 * Created by tulc on 05/10/2016.
 */
public class RestRequest {
    private RestTemplate mRestTemplate;
    private HttpHeaders mHttpHeaders;
    private MultiValueMap<String, String> params;


    public RestRequest() {
        this.mRestTemplate = new RestTemplate();
    }

    public void setHeaders(Profile profile) {
        mHttpHeaders = new HttpHeaders();
        mHttpHeaders.setAccept(Arrays.asList(MediaType.ALL));
//        mHttpHeaders.setContentType(MediaType.TEXT_PLAIN);
//        mHttpHeaders.set("Content-Type","text/plan");
        params = new LinkedMultiValueMap<String, String>();
        mHttpHeaders.add(WebGlobal.HEADER_NAME, profile.getName());
        mHttpHeaders.add(WebGlobal.HEADER_AGE, profile.getAge() + "");
        mHttpHeaders.add(WebGlobal.HEADER_GENDER, profile.isMale() ? "1" : "0");
        mHttpHeaders.add(WebGlobal.HEADER_HEIGHT, profile.getHeight() + "");
        mHttpHeaders.add(WebGlobal.HEADER_WEIGHT, profile.getWeight() + "");
        switch (profile.getRegion()) {
            case Profile.REGION_NORTHEN:
                mHttpHeaders.add(WebGlobal.HEADER_REGION, "0");
                break;
            case Profile.REGION_CENTRAL:
                mHttpHeaders.add(WebGlobal.HEADER_REGION, "1");
                break;
            case Profile.REGION_SOUTH:
                mHttpHeaders.add(WebGlobal.HEADER_REGION, "2");
                break;
            default:
                mHttpHeaders.add(WebGlobal.HEADER_REGION, "-1");
        }
        mHttpHeaders.add(WebGlobal.HEADER_SMOKING, profile.isSmoking() ? "1" : "0");
    }


    public LungFunction postResponse(String url, String filename) {
        LungFunction lungFunction = new LungFunction();
        File file = new File(filename);
//            FileInputStream fileInputStream = new FileInputStream(file);
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
//            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        HttpEntity<?> requestEntity = new HttpEntity<Object>((Object) new FileSystemResource(file), mHttpHeaders);

        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//            mRestTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
        ResponseEntity<String> result = mRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
//            ResponseEntity<String> result = mRestTemplate.postForEntity(url, params, String.class);
        try {
            Log.i("RestRequest", result.toString());
            lungFunction.parse(new JSONObject(result.getBody()));
            return lungFunction;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}