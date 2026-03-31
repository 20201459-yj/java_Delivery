package api;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import javax.net.ssl.HttpsURLConnection;

import db.CustomerDAO;
import vo.CustomerDTO;
import vo.LocationXY;
import vo.StoreDTO;

public class NaverMap {
    CustomerDAO customerDAO = new CustomerDAO();
    String clientId = "8xcog8gfax";
    String clientSecret = "kIAM6o7YOSfN3DeWuiVzil1CVZ6yymfOva8GVagJ";

    public LocationXY getLocation(String addr) {
        LocationXY loc = null;
        try {
            String address = URLEncoder.encode(addr, StandardCharsets.UTF_8);
            String requestURL = "https://maps.apigw.ntruss.com/map-geocode/v2/geocode?query=" + address;
            URL url = new URL(requestURL);
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-ncp-apigw-api-key-id", clientId);
            con.setRequestProperty("x-ncp-apigw-api-key", clientSecret);

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    JSONTokener tokener = new JSONTokener(bufferedReader);
                    JSONObject obj = new JSONObject(tokener);
                    JSONArray arr = obj.getJSONArray("addresses");

                    if (arr.length() > 0) {
                        double x = Double.parseDouble(arr.getJSONObject(0).getString("x"));
                        double y = Double.parseDouble(arr.getJSONObject(0).getString("y"));
                        loc = new LocationXY(x, y);
                    } else {
                        return null;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return loc;
    }

    public int getDuration(LocationXY start, LocationXY end) {
        try {
            String requestURL = "https://maps.apigw.ntruss.com/map-direction/v1/driving?" +
                    "goal=" + end.getX() + "%2C" + end.getY() +
                    "&start=" + start.getX() + "%2C" + start.getY() + "&option=trafast";
            URL url = new URL(requestURL);

            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();
            con.setRequestMethod("GET");
            con.setRequestProperty("x-ncp-apigw-api-key-id", clientId);
            con.setRequestProperty("x-ncp-apigw-api-key", clientSecret);

            int responseCode = con.getResponseCode();
            if (responseCode == 200) {
                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
                    JSONTokener tokener = new JSONTokener(bufferedReader);
                    JSONObject obj = new JSONObject(tokener);

                    if (obj.getInt("code") == 0) {
                        JSONObject route = obj.getJSONObject("route");
                        JSONArray trafast = route.getJSONArray("trafast");
                        JSONObject summary = trafast.getJSONObject(0).getJSONObject("summary");
                        return (int) (summary.getLong("duration") / 60000);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public List<StoreDTO> showStore(String type, CustomerDTO loginCustomer) {
        LocationXY csLoc = getLocation(loginCustomer.getCs_address());

        List<StoreDTO> list = customerDAO.selectStore(type);
        List<CompletableFuture<StoreDTO>> futures = list.stream()
                .map(store -> CompletableFuture.supplyAsync(() -> {
                    LocationXY stLoc = getLocation(store.getSt_address());
                    if (stLoc != null) {
                        int duration = getDuration(stLoc, csLoc);
                        if (duration > 0 && duration < 30) {
                            return store;
                        }
                    }
                    return null;
                }))
                .collect(Collectors.toList());
        List<StoreDTO> result = futures.stream()
                .map(CompletableFuture::join)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        return result;
    }

    public boolean isValidAddress(String address) {
        if (getLocation(address) == null) {
            return false;
        } else {
            return true;
        }
    }
}
