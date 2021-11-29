package com.example.handlers;

/* This handler brings the most recent bill sent to the President from Congress.
   It uses a private API Key from ProPublica.org to retrieve the information.
 */

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.example.interfaces.InfoRetriever;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class MostRecentBillIntentHandler implements RequestHandler, InfoRetriever {

    /* The following two methods are standard for different Alexa Intents.
    The "canHandle" method determines the specific intent Alexa will look for.
    The "handle" method is where you provide Alexa with what you want it to say.
     */

    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(intentName("BillIntent"));
    }


    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        return handlerInput.getResponseBuilder()
                .withSpeech(response)
                .build();
    }


    @Override
    public String getProPublica() throws IOException {

        URL url = new URL("https://api.propublica.org/congress/v1/bills/search.json?query=recent");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestProperty("X-API-Key", "<API-Key>");
        conn.setRequestProperty("Content-Type", "application/json");
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

        String output;

        StringBuilder stringBuilder = new StringBuilder();
        while ((output = in.readLine()) != null) {
            stringBuilder.append(output);
        }
        in.close();

        String billResults = stringBuilder.substring(112, 50700);

        return billResults;
    }

    @Override
    public JSONObject createObject(String text) throws IOException, JSONException {
        return new JSONObject(text);
    }

    @Override
    public String mostRecent() throws IOException, JSONException {

        String getInfo = getProPublica();
        JSONObject bills = createObject(getInfo);
        String shortTitle = (String) bills.getJSONArray("bills").getJSONObject(0).get("short_title");
        String shortSummary = (String) bills.getJSONArray("bills").getJSONObject(0).get("summary_short");
        String latestMajorActionDate = (String) bills.getJSONArray("bills").getJSONObject(0)
                .get("latest_major_action_date");
        String latestMajorAction = (String) bills.getJSONArray("bills").getJSONObject(0)
                .get("latest_major_action");
        return shortTitle + ", " + shortSummary + ", " + latestMajorActionDate + ", " + latestMajorAction;
    }


    // This response builds everything together.
    // It must be put through a try/catch statement to catch IOExceptions

    String response;

    {
        try {
            response = mostRecent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
