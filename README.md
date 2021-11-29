# paypal-java-capstone-project

# Alexa Skill: CongressWatch

## Before Getting Started

Our original idea for the project was to build an Alexa skill that would give daily briefings on bill dockets and
results. To do this we had to research not only how to build an Alexa skill, but also how to utilize a 3rd party API to retrieve
the information.  

After reviewing a few sites we settled on ProPublica.org as they appear to be the main API source for other sites. We
were able to aquire an API key. Initial research in how to utilize a 3rd party API with a key was difficult as we were unable to get the site to accept
the key.  

We decided to build a test Alexa skill where we could get everything working with an open API key. We highly suggest reviewing this project to familiarize yourself with Amazon's Alexa Developer console and AWS Lambda tool. This is the link to our repository and additional ReadMe for this app.

https://git.generalassemb.ly/matthompson/cat-facts-test

We highly encourage taking Udemy's [The Ultimate AWS Alexa Skill Builder Course](https://www.udemy.com/course/ultimate-aws-certified-alexa-skill-builder-specialty/) to help give a background on how each resource works.

## Technologies Used
| __IntelliJ IDE__ | <img src="https://upload.wikimedia.org/wikipedia/commons/9/9c/IntelliJ_IDEA_Icon.svg" alt="IntelliJ IDE" width="150"/> | __Java__ | <img src="https://cdn.freebiesupply.com/logos/thumbs/2x/java-4-logo.png" alt="Java" width="150"/> | 
| :------- | :-------: | :------- | :-----: |
| __Alexa Skills Kit__ | <img src="https://d3ogm7ac91k97u.cloudfront.net/content/dam/alexa/alexa-brand-guidelines-2021-refresh-/Alexa_Logo_RGB_BLUE.png" alt="Alexa Skills Kit" width="150"/> | __Amazon Web Services__ | <img src="https://d1.awsstatic.com/logos/aws-logo-lockups/poweredbyaws/PB_AWS_logo_RGB_stacked.547f032d90171cdea4dd90c258f47373c5573db5.png" alt="Amazon Web Services" width="150"/> 
| __Microsoft Teams__ | <img src="https://www.marshall.edu/it/files/microsoft-team-2019-300x300.png" alt="Microsoft Teams" width="150"/> | __Postman__ | <img src="https://www.postman.com/assets/logos/postman-logo-stacked.svg" alt="Postman" width="150"/> |


## User Stories  

    User: Hey Alexa, I would like to know the status of today’s bills in the Senate/House of Representatives.  
    Alexa: “Congress is not in session today/Here are today’s pending bills/Here are the results of today’s votes. 

    User: Hey Alexa, what is today’s bill schedule? 
    Alexa: “The Senate/House of Representatives is not in session today/Here are the bills tabled for votes:” 

    User: Hey Alexa, how did my representatives/senators vote on bill “ “? 
    Alexa: Your representatives/senators “Name stated and then vote result.” 

    User: Hey Alexa, please describe bill “ ”. 
    Alexa: Bill “ “ regards “ “. 

    User: Hey Alexa, how did my representatives vote today? 
    Alexa: Your representatives in the Senate/House of Representatives voted “for/against” bill “ “. 
    
    User: Hey Alexa, what is the Covid status of the members of Congress.  
    Alexa:  On 3/9/20, Rep. Julia Brownley (CA-26) came in contact with someone who tested positive. 
    They self-quarantined until 3/18/2020, DC office to  q  telework. 
    
    User: Hey Alexa, who are the representatives of “state” 
    Alexa: The representatives of “state” are “ ” 
    
    User: Hey Alexa, who are the representatives of my state 
    Alexa: The representatives of your state are “ “ 
    
    User: Hey Alexa, call my Senator 
    Alexa: Calling “ “ 
    
    User: Hey Alexa, call my representative 
    Alexa: Calling “ “ 
    
    User: Hey Alexa, what is the most recent bill regarding healthcare 
    Alexa: Bill “ “ regarding “ “ was signed into law on “ “ 

    User: Hey Alexa, what is the most recent bill regarding the environment 
    Alexa: Bill “ “ regarding “ “ was signed into law on “ “ 

    User: Hey Alexa, what is the most recent bill regarding the military 
    Alexa: Bill “ “ regarding “ “ was signed into law on “ “ 

    User: Hey Alexa, what is the most recent bill  
    Alexa: Bill “ “ regarding “ “ was signed into law on “ “ 

    User: Hey Alexa, who is the most recent confirmed nominee 
    Alexa: “(name)” was confirmed for “(position)” on “(date)” 

    User: Hey Alexa, who is the most recent withdrawn nominee 
    Alexa: “nomination description” was withdrawn “description” on “date”  


## ERD

![Congress Watch ERD](https://git.generalassemb.ly/matthompson/paypal-java-capstone-project/blob/master/Photos/paypal-java-capstone-project.png)

## Getting Started
We created the project as a Java based application. We wanted to use SpringBoot but __did not utilize SpringInitializer because it would cause errors when compiling our .jar file.__  
We had set up our API information in the application.properties folder. We first used a tutorial on RapidAPI's website called [How To Use an API with Spring RestTemplate](https://rapidapi.com/blog/how-to-use-an-api-with-spring-resttemplate/) to begin our project. This tutorial uses an API key that retrieves COVID data. We quickly realized we would have to create a RestClient class for each request we plan in making to the API. We decided to request Senate Member data as our first run through. Our plan was to direct Alexa to retrieve a single fact about a Senator as a base test. Then, we can go and customize the information we want to return. 

__This did not work, and we researched additional ways to pass our API Key.__ We also experienced binding errors while using the RestTemplate. We were able to troubleshoot by using the documentation on: [SLF4J Multiple Binding Error](http://www.slf4j.org/codes.html#multiple_bindings)

We decided to focus on retrieving only one piece of information from ProPublica, as we can easily replicate successful code. These are our steps from start to finish for one piece of information. 
 
### SkillStreamHandler
The SkillStreamHandler abstract class represents the Lambda function in Amazon Web Services. This class is fairly straight forward, as the Alexa Skill intents are added to this stream.

<details><summary>The following is an example of our CongressSkillStreamHandler and a custom LaunchRequestHandler:</summary>
<p>
    
    public class CongressSkillStreamHandler extends SkillStreamHandler {

    public CongressSkillStreamHandler(){
        super(Skills.standard()
                .addRequestHandler(new CongressWatchLaunchRequestHandler())
                .build());
        }
    }
</p>
LaunchRequestHandler is a built in Alexa Intent that can be customized to return a unique response for your skill to let the user know it is functional.
<p>
    
    public class CongressWatchLaunchRequestHandler implements LaunchRequestHandler {
    private static Logger logger = getLogger(CongressWatchLaunchRequestHandler.class);

    @Override
    public boolean canHandle(HandlerInput input, LaunchRequest launchRequest) {
        return input.matches(requestType(LaunchRequest.class));
    }

    @Override
    public Optional<Response> handle(HandlerInput input, LaunchRequest launchRequest) {
        logger.info("Received unrecognized request: " + input.getRequestEnvelopeJson());
        return input.getResponseBuilder()
                .withSpeech("Welcome to Congress Watch")
                .build();
        }
    }
</p>
</details>

Each IntentHandler we created relies on two interfaces. One is RequestHandler from Amazon's ASK SDK Library. The second is one we created called __InfoRetriever__ which is used to pull the information from [ProPublica](propublica.org). We will break down each method in the order we build them.

First, we built out the method __getProPublica()__. We realized we could not use the exact same code for this method because each endpoint from ProPublica had to be edited. The reason being JSONParser and related methods in Java's libraries have been deprecated. We originally had issues parsing through the json file because it had so many nested objects and arrays. 

We decided to pivot and try gathering data from an RSS feed to send through Alexa. The [RSS feeds with Java - Tutorial](https://www.vogella.com/tutorials/RSSFeed/article.html) helped us understand how to retrieve data from RSS feeds. We learned how to convert an XML document into a JSON file to pass to Alexa. Our code was functional, but we continued to receive errors. We utilized the logs in AWS's CloudWatch management to identify the specific reason our output was incorrect. Unfortunately, our requests overloaded [Congress.gov's RSS feeds](congress.gov/rss) identified by the 503 HTTP return error:  Service Unavailable: The code worked in our IDEs, but not as a whole. 

We utilized the substring method to edit the RSS XML document to make it easier to convert. We realized we could use the same technique to simplify the previously retrieved json files from ProPublica.

It is important to build out getProPublica() first because it allows you to print to the console the json file you are retrieving. Later on in the method, we remove any extraneous json objects/arrays that complicate parsing through the file by using substring.

<details><summary>Here is our getProPublica() method broken down:</summary>
    
<p>
    
    @Override    
    String getProPublica() throws IOException {
    URL url = new URL("https://api.propublica.org/congress/v1/bills/search.json?query=");
    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestProperty("X-API-Key", "<INSERT API KEY>");
        conn.setRequestProperty("Content-Type", "application/json");
    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

    String output;

    StringBuilder stringBuilder = new StringBuilder();
        while ((output = in.readLine()) != null) {
        stringBuilder.append(output);
    }
        in.close();

    ***Test the following to adjust the file into proper json format***
    String billResults = stringBuilder.substring(<int>, <int>);

        return billResults;
    }
    
</p>
</details>

Next, we created a method to instantiate the edited json text into a JSONObject

        @Override
        JSONObject createObject(String text) throws IOException, JSONException{
        return new JSONObject(text);
        }
    
Our last method, __mostRecent()__ brings it all together for Alexa. This method utilizes the beforementioned methods to construct a response Alexa can recognize and deliver. We identified different keys in each bill object and converted their values to String. We pass these through Alexa's built in methods that need to implement and override.

<details><summary>Here is how we parsed the json data:</summary>
<p>
    
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
    
</p>
</details>

Now, we can attribute mostRecent()'s value into a String which Alexa will recognize and output. It is important to pay attention to the Intent name you provide in your code, as it must match in the Alexa Skill Developer console.

<details><summary>Here is our code to prepare and send it to Alexa:</summary>
Each response needs to be sent through an IOException because the Lambda function will not work correctly if you do not catch it.
<p>
    
        String response;

    {
        try {
            response = mostRecent();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
</p>
Here we declare the intent as BillIntent and pass the response through Alexa Skills Kit's handle method:
<p>
    
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
</p>
</details>

Lastly, let's compile our .jar file to upload into Lambda!

    mvn assembly:assembly -DdescriptorId=jar-with-dependencies package
    
Please refer to the [Build an Alexa Skill and Lambda Function](https://git.generalassemb.ly/matthompson/cat-facts-test#build-an-alexa-skill-and-lambda-function) section in our Cat Facts Repository for screenshots and explanations on how to get Alexa how to speak your code!

### What's Next:
There is still some additional coding that would need to be done before releasing this project to the public. We would need to refactor the code in order to hide our API. We would also like to troubleshoot the issues we had with Spring so we could simplify our code and utilize endpoints in a more efficient manner. This would also allow us to remove repeated code that was done out of necessity in order to work around compiling issues. Additionally, we would like to build a method to allow the user to search for more specific information. We hope to build a feature for users to call their specific representatives as the office phone numbers are publicly available in the data we pulled.  

### Resources
[ProPublica](propublica.org) - This project would not be possible without receiving a private API key from ProPublica.  
Udemy: [The Ultimate AWS Alexa Skill Builder Course](https://www.udemy.com/course/ultimate-aws-certified-alexa-skill-builder-specialty/)  
[RSS feeds with Java - Tutorial](https://www.vogella.com/tutorials/RSSFeed/article.html)
