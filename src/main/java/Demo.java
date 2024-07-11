
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class Demo {

    // essential URL structure is built using constants
    public static final String ACCESS_KEY = "e476a0400ba9fbe69000464d5a868305";
    public static final String BASE_URL = "http://api.exchangerate.host/";
    public static final String ENDPOINT = "convert";

    // this object is used for executing requests to the (REST) API
    static CloseableHttpClient httpClient = HttpClients.createDefault();

    public static void main(String[] args) throws IOException {

        Scanner input = new Scanner(System.in);

        System.out.println("Enter currency you want to convert from(GBP, USD, AUD....: ");
        String fromCurrency = input.nextLine();

        System.out.println("Enter currency you want to convert to:(CAD, EUR, USD...: ");
        String toCurrency = input.nextLine();

        System.out.println("Enter Amount you want to convert: ");
        double amount = input.nextInt();

        sendLiveRequest(fromCurrency, toCurrency, amount);
        httpClient.close();
        new BufferedReader(new InputStreamReader(System.in)).readLine();
        input.close();
    }

    public static void sendLiveRequest(String fromCurrency, String toCurrency, double amount) throws IOException {

        // The following line initializes the HttpGet Object with the URL in order to send a request
        HttpGet get = new HttpGet(BASE_URL + ENDPOINT + "?access_key=" + ACCESS_KEY + "&from=" + fromCurrency + "&to=" + toCurrency + "&amount=" + amount);
        DecimalFormat f = new DecimalFormat("0.00");

        try {
            CloseableHttpResponse response =  httpClient.execute(get);
            HttpEntity entity = response.getEntity();

            // the following line converts the JSON Response to an equivalent Java Object
            JSONObject exchangeRates = new JSONObject(EntityUtils.toString(entity));

            double rate = exchangeRates.getDouble("result");

            System.out.println("Live Currency Exchange Rates");

            Date timeStampDate = new Date(exchangeRates.getJSONObject("info").getLong("timestamp")*1000);
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a");
            String formattedDate = dateFormat.format(timeStampDate);

            System.out.println(f.format(amount) + " " + fromCurrency + " is " + f.format(rate) + " " + toCurrency + " (Date: " + formattedDate + ")");
            System.out.println("\n");
            response.close();

        } catch (ClientProtocolException e) {
            System.out.println("Error: Client Protocol Exception");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Error: IO Exception");
            e.printStackTrace();
        } catch (JSONException e) {
            System.out.println("Error: JSON Exception");
            e.printStackTrace();
        }
    }
}
