package vn.hust.soict.lung_function.net;

import android.util.Log;

import org.json.JSONObject;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

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

    public void setHeaders() {
        mHttpHeaders = new HttpHeaders();
        mHttpHeaders.setAccept(Arrays.asList(MediaType.ALL));
//        mHttpHeaders.setContentType(MediaType.TEXT_PLAIN);
//        mHttpHeaders.set("Content-Type","text/plan");
        params = new LinkedMultiValueMap<String, String>();
//        mHttpHeaders.add(WebGlobal.HEADER_NAME, profile.getName());
//        mHttpHeaders.add(WebGlobal.HEADER_AGE, profile.getAge() + "");
//        mHttpHeaders.add(WebGlobal.HEADER_GENDER, profile.isMale() ? "1" : "0");
//        mHttpHeaders.add(WebGlobal.HEADER_HEIGHT, profile.getHeight() + "");
//        mHttpHeaders.add(WebGlobal.HEADER_WEIGHT, profile.getWeight() + "");
//        switch (profile.getRegion()) {
//            case Profile.REGION_NORTHEN:
//                mHttpHeaders.add(WebGlobal.HEADER_REGION, "0");
//                break;
//            case Profile.REGION_CENTRAL:
//                mHttpHeaders.add(WebGlobal.HEADER_REGION, "1");
//                break;
//            case Profile.REGION_SOUTH:
//                mHttpHeaders.add(WebGlobal.HEADER_REGION, "2");
//                break;
//            default:
//                mHttpHeaders.add(WebGlobal.HEADER_REGION, "-1");
//        }
//        mHttpHeaders.add(WebGlobal.HEADER_SMOKING, profile.isSmoking() ? "1" : "0");
        mHttpHeaders.add(WebGlobal.HEADER_CONTENT_TYPE,WebGlobal.CONTENT_TYPE);
        mHttpHeaders.add(WebGlobal.HEADER_M2M_ORIGIN, WebGlobal.PASS_M2M);
    }


    public LungFunction postResponse(String url, String filename,Profile profile) throws MalformedURLException, IOException {
        LungFunction lungFunction = new LungFunction();
        File file = new File(filename);
//        Log.e("FILE>>>" + file.isFile() + "$", filename);
//            FileInputStream fileInputStream = new FileInputStream(file);
//            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);
//            LinkedMultiValueMap<String, Object> map = new LinkedMultiValueMap<>();

        FileSystemResource fileSystemResource = new FileSystemResource(file);
        String fileContent = null;
        try {
            StringBuilder sb = new StringBuilder();
            DataInputStream input = new DataInputStream(new FileInputStream(file));
            try {
                while (true) {
                    sb.append(Integer.toBinaryString(input.readByte()));
                }
            }
            catch (EOFException eof){

            }
            fileContent = sb.toString();
        }
        catch (FileNotFoundException e){
            e.printStackTrace();
        }
        String isMale = profile.isMale() ? "0" : "1";
        String region;
        switch (profile.getRegion()) {
            case Profile.REGION_NORTHEN:
                region = "0";
                break;
            case Profile.REGION_CENTRAL:
                region = "1";
                break;
            case Profile.REGION_SOUTH:
                region = "2";
                break;
            default:
                region = "-1";
        }
        String isSmoking = profile.isSmoking() ? "0" : "1";

        String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><m2m:cin xmlns:m2m=\"http://www.onem2m.org/xml/protocols\"><cnf>message</cnf>\n" +
                "                <con>\n" +
                "                  &lt;obj&gt;\n" +
                "                    &lt;str name=&quot;appId&quot; val=&quot;"+WebGlobal.APP_ID+"&quot;/&gt;\n" +
                "                    &lt;str name=&quot;name&quot; val=&quot;"+ profile.getName() +"&quot;/&gt;\n" +
                "                    &lt;int name=&quot;age&quot; val=&quot;"+ profile.getAge() + "" + "&quot;/&gt;\n" +
                "                    &lt;int name=&quot;gender&quot; val=&quot;"+ isMale +"&quot;/&gt;\n" +
                "                    &lt;int name=&quot;region&quot; val=&quot;"+ region +"&quot;/&gt;\n" +
                "                    &lt;int name=&quot;smoking&quot; val=&quot;"+ isSmoking +"&quot;/&gt;\n" +
                "                    &lt;int name=&quot;height&quot; val=&quot;"+ profile.getHeight() +"&quot;/&gt;\n" +
                "                    &lt;int name=&quot;weight&quot; val=&quot;"+ profile.getWeight() +"&quot;/&gt;\n" +
                "                    &lt;str name=&quot;fileContent&quot; val=&quot;"+ fileContent +"&quot;/&gt;\n" +
                "                  &lt;/obj&gt;\n" +
                "                </con>\n" +
                "            </m2m:cin>";
//        HttpEntity<?> requestEntity = new HttpEntity<Object>(fileSystemResource, mHttpHeaders);

//        mRestTemplate.getMessageConverters().add(new StringHttpMessageConverter());
//            mRestTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter());
//        ResponseEntity<String> result = mRestTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
//            ResponseEntity<String> result = mRestTemplate.postForEntity(url, params, String.class);

//        try {
//            fileSystemResource.getInputStream().close();
//            fileSystemResource.getOutputStream().close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        HttpURLConnection urlConnection = null;
        URL myUrl = new URL(url);
        urlConnection = (HttpURLConnection) myUrl.openConnection();
        try {
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(15000);
            urlConnection.setRequestMethod("POST");
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty(WebGlobal.HEADER_CONTENT_TYPE,WebGlobal.CONTENT_TYPE);
            urlConnection.setRequestProperty(WebGlobal.HEADER_M2M_ORIGIN, WebGlobal.PASS_M2M);
            OutputStream output = new BufferedOutputStream(urlConnection.getOutputStream());
            output.write(xmlString.getBytes());
            output.flush();
            StringBuilder stringBuilder = new StringBuilder();
            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK){
                InputStreamReader streamReader = new InputStreamReader(
                        urlConnection.getInputStream());
                BufferedReader bufferedReader = new BufferedReader(
                        streamReader);
                String response = null;
                while ((response = bufferedReader.readLine()) != null) {
                    stringBuilder.append(response + "\n");
                }
                bufferedReader.close();
                Log.d("Result Value ", stringBuilder.toString());
                JSONObject jsonResultText = new JSONObject(
                        stringBuilder.toString());
                lungFunction.parse(jsonResultText);
                return lungFunction;
            }
            else {
                Log.e("Error = ",urlConnection.getResponseMessage());
                return null;
            }
        }
        catch (Exception e) {
            Log.e("Error = ", e.toString());
            return null;
        }
        finally {
            urlConnection.disconnect();
        }
    }
}