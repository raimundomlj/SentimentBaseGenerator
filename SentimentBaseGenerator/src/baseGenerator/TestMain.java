package baseGenerator;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONObject;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;



public class TestMain {

	public static void main(String[] args) throws IOException {
		ArrayList<TweetTraduzido> tweetsTraduzidos = new ArrayList<>();
		TweetTraduzido tt;
		
		String keyYandex = "trnsl.1.1.20170210T155503Z.a81b1d1be6f81de0.4fcc199cd762a88393b073dec8c444cbb8348d7c";

		Client client = Client.create();
		WebResource webResource = client
				.resource("https://translate.yandex.net/api/v1.5/tr.json/translate?lang=pt-en&key=" + keyYandex);
		
		ClientResponse response; 
		String output;

		BufferedReader br = new BufferedReader(new FileReader("C:/Users/raimundo.martins/Desktop/correios.csv"));
		String line = "";
		String cvsSplitBy = ";";
		
		int qtd = 1;
		while ((line = br.readLine()) != null) {	
			String[] text = line.split(cvsSplitBy);			
			System.out.println("Leu: "+qtd);
			tt = new TweetTraduzido();
			tt.setPt(text[0]);
			tt.setEnGoogle(text[1]);			
			
			tweetsTraduzidos.add(tt);
			qtd++;
		}
		
		int cont = 1;
		for(int i = 0; i<tweetsTraduzidos.size();i++){
			response =  webResource.queryParam("text", tweetsTraduzidos.get(i).getPt()).queryParam("format", "plain")
					.accept("text/plain").get(ClientResponse.class);
			if (response.getStatus() != 200) {
				throw new RuntimeException("Failed : HTTP error code : " + response.getStatus());
			}
			
			output = response.getEntity(String.class);
			JSONObject jsonObj = new JSONObject(output);			
			tweetsTraduzidos.get(i).setEnYandex(jsonObj.get("text").toString());
			System.out.println("Traduzidos: "+cont+" de "+tweetsTraduzidos.size());
			cont++;
		}
		
		ImprimeArquivo print = new ImprimeArquivo("correios_all",tweetsTraduzidos);
		print.start();
		br.close();
	}

}
