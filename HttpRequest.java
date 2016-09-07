import java.io.* ;
import java.net.* ;
import java.util.* ;

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

	// Retorno do MIME para o Content-Type do cabeçalho
	
	String line = "Content-type: ";
	
	private String contentType(String fileName) {
		if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
			return line + "text/html";
		}

		if (fileName.endsWith(".jpg") || fileName.endsWith(".jpeg")) {
			return line +  "image/jpeg";
		}

		if (fileName.endsWith(".gif")) {
			return line +  "image/gif";
		}

		if (fileName.endsWith(".pdf")) {
			return line +  "application/pdf";
		}

		return line +  "application/octet-stream";
	}

	// Envio do arquivo solicitado
	private void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
		// Construir um buffer de 1K para comportar os bytes no caminho para o socket.
		byte[] buffer = new byte[1024];
		int bytes = 0;
		// Copiar o arquivo requisitado dentro da cadeia de saída do socket
		while((bytes = fis.read(buffer)) != -1) {
			os.write(buffer, 0, bytes);
		}
	}

	void response(String fileName, String httpVersion, DataOutputStream os)
			throws Exception {
		FileInputStream fis = null;
		Boolean fileExists = true;

		// Tenta abrir o arquivo requisitado
		try {
			fis = new FileInputStream(fileName);
		} catch (FileNotFoundException e) {
			fileExists = false;
		}
		// Constrói a mensagem de resposta
		String statusLine = null;
		String contentTypeLine = null;
		String entityBody = null;

		if (fileExists) {
			statusLine = httpVersion + "200" + CRLF;
			contentTypeLine = contentType(fileName) + CRLF;
		} else {
			statusLine = httpVersion + "404" + CRLF;
			contentTypeLine  = contentType(".htm") + CRLF;
			System.out.println(contentTypeLine);
			entityBody =   "<HTML>"
					+ "<HEAD><TITLE>Not Found</TITLE></HEAD>" 
					+ "<BODY>" + fileName + " não encontrado</BODY>"
					+ "</HTML>";
		}
		
		// Enviar a linha de status.
		os.writeBytes(statusLine);
		// Enviar a linha de tipo de conteúdo.
		os.writeBytes(contentTypeLine);
		// Enviar uma linha em branco para indicar o fim das linhas de cabeçalho.		
		os.writeBytes(CRLF);

		if(fileExists) {
			sendBytes(fis, os);
			fis.close();
		} else {
			os.writeBytes(entityBody);
		}		
	}

	// Processamento da requisição
	private void processRequest() throws Exception {
		InputStream is = this.socket.getInputStream();
		DataOutputStream os = new DataOutputStream(this.socket.getOutputStream());
		BufferedReader br = new BufferedReader(new InputStreamReader(is));

		// Extração do nome do arquivo a linha de requisição.
		StringTokenizer tokens = new StringTokenizer(br.readLine());
		// Ignora o método, que deve ser "GET"
		tokens.nextToken();
		// Obtenção do nome do arquivo
		String fileName = tokens.nextToken(); 
		// Obtenção da versão do HTML
		String httpVersion = tokens.nextToken();
		// Ajuste para que o arquivo seja buscado no diretório local
		fileName = "." + fileName;

		response(fileName, httpVersion, os);

		// Fechamento das cadeias e socket
		os.close();
		br.close();
		socket.close();		
	}
}