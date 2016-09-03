import java.io.* ;
import java.net.* ;

public final class HttpRequest implements Runnable {
	
	final static String CRLF = "\r\n";
	Socket socket;

	// Construtor 
	public HttpRequest(Socket socket) throws Exception {
		this.socket = socket;
	}

	// Interface Runnable
	public void run() {
		try {
			processRequest();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private void processRequest() throws Exception {

		// Referências para os trechos de entrada e saída do socket
		String clientSentence;
		String capitalizedSentence;
		
		InputStream is = this.socket.getInputStream();
		DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// Ajuste do filtro do trecho de entrada
		clientSentence = br.readLine(); 

		// Exibição das linhas de cabeçalho
		String headerLine = null;
		while ((headerLine = br.readLine()).length() != 0) {
			System.out.println(headerLine);
		}		

		// Fechamento das cadeias e socket
		os.close();
		br.close();
		socket.close();	
	}

}
