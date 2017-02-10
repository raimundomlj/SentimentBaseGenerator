package baseGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ImprimeArquivo extends Thread {

	private String nome;
	private ArrayList<String> dados;
	
	public ImprimeArquivo(String nome, ArrayList<String> dados){
		this.nome = nome;
		this.dados = dados;
	}
	public void run() {
		try {
			FileWriter arquivo = new FileWriter(new File("C:/Users/Raimundo/Desktop/"+nome+".txt"));
			//BufferedWriter arquivo = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("C:/Users/Raimundo/Desktop/"+nome+".txt"),"ISO-8859-1"));
			for(String d : dados){
				arquivo.write(d+";\n");
			}
			arquivo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
