package baseGenerator;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.translate.Translate;
import com.google.api.services.translate.TranslateRequestInitializer;
import com.google.api.services.translate.model.TranslationsListResponse;
import com.google.api.services.translate.model.TranslationsResource;

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
		// Util.imprimeAvaibleTrends();
		String busca = "Correios";
		String lang = "pt";

		ArrayList<String> tweets = getTweets(busca, lang);
		ArrayList<String> tweetsEN = new ArrayList<>();
		
		for (String tweet : tweets) {			
			System.out.println(tweet);			
		}
		
//		System.out.println("QTD tweets 1: "+tweets.size());
//		ArrayList<String> temp = new ArrayList<>(tweets);
//		tweets.clear();
//		for (String tweet : temp) {			
//			tweets.add(tweet.replaceAll("^[A-Z0-9 _]*$", ""));			
//		}
//		
//		System.out.println("QTD tweets 2: "+tweets.size());

//		final TranslateRequestInitializer KEY_INITIALIZER = new TranslateRequestInitializer(KEY);
//
//		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
//		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
//		
//		final Translate translate = new Translate.Builder(httpTransport, jsonFactory, null)
//				.setApplicationName("sentimentbasegenerator")
//				.setTranslateRequestInitializer(KEY_INITIALIZER)
//				.build();
//		
//		temp.clear();
//		int count = 1;
//		for(int i = 0; i < tweets.size(); i++){
//			if(temp.size() == 50 || (i+1) == tweets.size()){
//				Translate.Translations.List list;
//				try {
//					list = translate.new Translations().list(temp, "EN");
//					TranslationsListResponse response = list.execute();
//					for(TranslationsResource tr : response.getTranslations()) {			            
//			            tweetsEN.add(tr.getTranslatedText());
//			            System.out.println("traduzindo: "+count+"/"+(tweets.size()-1));
//			            count++;
//			        }
//				} catch (Exception e) {					
//					e.printStackTrace();
//				}
//				temp.clear();
//			}			
//			temp.add(tweets.get(i));			
//		}
        ImprimeArquivo pt = new ImprimeArquivo("pt", tweets);
//        ImprimeArquivo en = new ImprimeArquivo("en", tweetsEN);     	
        pt.start();
//        en.start();
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
			} while (totalTweets <= 100);
			//while (q != null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return tweets;
	}
}
