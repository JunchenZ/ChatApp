import java.net.Socket;

public class Connection implements Runnable{
	
	private Socket socket;
	private Server server;
	
	public Connection(Socket sct, Server svr) {
		socket = sct;
		server = svr;
	}

	@Override
	public void run() {
		
		try {
			Request request = new Request(socket);
			Response response = new Response(socket, request, server);
			socket.close();						
		}catch(Exception e) {
			System.out.println("Error in run(): " + e);
		}		
	}
	

}
