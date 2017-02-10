package baseGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Status;
import twitter4j.TwitterException;

public class Main {

	static final String KEY = "AIzaSyBnEjV4GOemk7lRwE9kQ7uCxrNTGpgNgCE";

	public static void main(String[] args) throws GeneralSecurityException, IOException, TwitterException {

		// Brazil
		// Recife
		// #SeEuFosseUmMagico

		// System.out.println("QTD tweets 1: "+tweets.size());
		// ArrayList<String> temp = new ArrayList<>(tweets);
		// tweets.clear();
		// for (String tweet : temp) {
		// tweets.add(tweet.replaceAll("^[A-Z0-9 _]*$", ""));
		// }
		//
		// System.out.println("QTD tweets 2: "+tweets.size());

		// Util.imprimeAvaibleTrends();
		//String busca = "Correios";
		//String lang = "pt";

		//ArrayList<String> tweets = getTweets(busca, lang);

		// ImprimeArquivo print = new ImprimeArquivo("correios_all",translateYandexFromFile("C:/Users/raimundo.martins/Desktop/correios.csv"));
		// ImprimeArquivo print = new ImprimeArquivo("correios",
		// translateGoogle(tweets));
		// print.start();
	}

	public static ArrayList<TweetTraduzido> translateYandexFromFile(String file) throws IOException {
		ArrayList<TweetTraduzido> tweetsTraduzidos = new ArrayList<>();
		TweetTraduzido tt;

		String keyYandex = "trnsl.1.1.20170210T155503Z.a81b1d1be6f81de0.4fcc199cd762a88393b073dec8c444cbb8348d7c";

		Client client = Client.create();
		WebResource webResource = client
				.resource("https://translate.yandex.net/api/v1.5/tr.json/translate?lang=pt-en&key=" + keyYandex);
		
		BufferedReader br = new BufferedReader(new FileReader(file));
		String line = "";
		String cvsSplitBy = ";";
		String output = "";
		
		while ((line = br.readLine()) != null) {			
			String[] text = line.split(cvsSplitBy);
			
			ClientResponse response = webResource.queryParam("text", text[0]).queryParam("format", "plain")
					.accept("text/plain").get(ClientResponse.class);
			
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			
			output = response.getEntity(String.class);
			JSONObject jsonObj = new JSONObject(output);
			
			tt = new TweetTraduzido();
			tt.setPt(text[0]);
			tt.setEnGoogle(text[1]);
			tt.setEnYandex(jsonObj.get("text").toString());
			
			tweetsTraduzidos.add(tt);
		}
		br.close();

		return tweetsTraduzidos;
	}

	public static ArrayList<TweetTraduzido> translateGoogle(ArrayList<String> tweets)
			throws GeneralSecurityException, IOException {
		ArrayList<TweetTraduzido> tweetsTraduzidos = new ArrayList<>();
		TweetTraduzido tt;

		for (String tweet : tweets) {
			tt = new TweetTraduzido();
			tt.setPt(tweet);
			tweetsTraduzidos.add(tt);
			System.out.println(tweet);
		}

		final TranslateRequestInitializer KEY_INITIALIZER = new TranslateRequestInitializer(KEY);

		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

		final Translate translate = new Translate.Builder(httpTransport, jsonFactory, null)
				.setApplicationName("sentimentbasegenerator").setTranslateRequestInitializer(KEY_INITIALIZER).build();

		// temp.clear();
		ArrayList<String> temp = new ArrayList<>();
		int count = 1;
		for (int i = 0; i < tweets.size(); i++) {
			if (temp.size() == 50 || (i + 1) == tweets.size()) {
				Translate.Translations.List list;
				try {
					list = translate.new Translations().list(temp, "EN");
					TranslationsListResponse responseGoogle = list.execute();
					for (TranslationsResource tr : responseGoogle.getTranslations()) {
						tweetsTraduzidos.get(count - 1).setEnGoogle(tr.getTranslatedText());
						System.out.println("traduzindo: " + count + "/" + (tweets.size() - 1));
						count++;
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				temp.clear();
			}
			temp.add(tweets.get(i));
		}

		return tweetsTraduzidos;
	}

	public static ArrayList<String> getTweets(String busca, String lang) {
		ArrayList<String> tweets = new ArrayList<>();
		int totalTweets = 0;
		long maxID = -1;
		try {
			Query q = new Query(busca + " -filter:retweets -filter:links -filter:replies -filter:images");
			q.setCount(Util.TWEETS_PER_QUERY);
			q.resultType(Query.ResultType.recent);
			q.setMaxId(maxID);
			q.setLang(lang);
			q.setLocale(lang);
			QueryResult r = Util.getTwitter().search(q);
			do {
				for (Status s : r.getTweets()) {
					totalTweets++;
					if (maxID == -1 || s.getId() < maxID) {
						maxID = s.getId();
					}
					if (!tweets.contains(Util.cleanText(s.getText())))
						tweets.add(Util.cleanText(s.getText()));
				}
				q = r.nextQuery();
				if (q != null) {
					q.setMaxId(maxID);
					r = Util.getTwitter().search(q);
					System.out.println("Total tweets: " + totalTweets);
					System.out.println("Maximo ID: " + maxID);
					Util.imprimirRateLimit(Util.RATE_LIMIT_OPTION_SEARCH_TWEETS);
				}
			} while (totalTweets <= 2900);
			// while (q != null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tweets;
	}
}
