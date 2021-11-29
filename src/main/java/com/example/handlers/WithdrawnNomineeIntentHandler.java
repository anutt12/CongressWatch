package com.example.handlers;

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

public class WithdrawnNomineeIntentHandler implements RequestHandler, InfoRetriever {
    @Override
    public boolean canHandle(HandlerInput handlerInput) {
        return handlerInput.matches(intentName("WithdrawnIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput handlerInput) {
        return handlerInput.getResponseBuilder()
                .withSpeech(nomineeResponse)
                .build();
    }

    @Override
    public String getProPublica() throws IOException {
        URL url = new URL("https://api.propublica.org/congress/v1/117/nominees/withdrawn.json");
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

        // ***Test the following to adjust the file into proper json format***
        String nomineeResults = stringBuilder.toString();

        return nomineeResults;
    }

    @Override
    public JSONObject createObject(String text) throws IOException, JSONException {
        return new JSONObject(text);
    }

    @Override
    public String mostRecent() throws IOException, JSONException {
        String getInfo = getProPublica();
        JSONObject nominee = createObject(getInfo);
        String nominationDescription = (String) nominee.getJSONArray("results").getJSONObject(0).get("description");
        String latestAction = (String) nominee.getJSONArray("results").getJSONObject(0).get("latest_action_text");
        String latestActionDate = (String) nominee.getJSONArray("results").getJSONObject(0)
                .get("latest_action_date");
        return nominationDescription + " " + latestAction + " on " + latestActionDate;
    }

    String nomineeResponse;

    {
        try {
            nomineeResponse = mostRecent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

