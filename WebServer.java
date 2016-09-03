import java.net.* ;

public final class WebServer {

	public static void main(String args[]) throws Exception {
		// Ajuste do número da porta
		int port = 1028;

		// Estabelecimento do socket de escuta
		ServerSocket welcomeSocket = new ServerSocket(port); 

		// Processamento da requisição de serviço HTTP 
		while(true) { 
			Socket connectionSocket = welcomeSocket.accept(); 
			
			// Construção de um objeto para processamento da mensagem de requisição HTTP
			HttpRequest request = new HttpRequest(connectionSocket);
			// Criação de um novo thread para processar a requisição.
			Thread thread = new Thread(request);
			// Execução do thread.
			thread.start();	
		}
	}
}
