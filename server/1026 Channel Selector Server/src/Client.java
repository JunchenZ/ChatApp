import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.Iterator;
import java.util.Set;

public class Client {
	private SocketChannel sc;
	
	public Client(Server server) throws Exception {
		try{
			
			server.getSelector().select();
			Set<SelectionKey> keys = server.getSelector().selectedKeys();
			Iterator<SelectionKey> it = keys.iterator();
			
			while(it.hasNext()) {
				SelectionKey key = it.next();
				if(key.isAcceptable()) {
					it.remove();
					sc = server.accept();
					Connection connection = new Connection(sc.socket(), server);
					Thread thread = new Thread(connection);
					thread.start();
				}
			}		
			
		}catch(Exception e) {
			System.out.println("Client Error" + e);
		}
	}
	
	public InputStream getInputStream() throws Exception {
		return sc.socket().getInputStream();
	}
	
	public OutputStream getOutputStream() throws Exception {
		return sc.socket().getOutputStream();
	}
	
	public void close() throws Exception {
		sc.close();
	}
	
	public boolean isOpen() {
		return sc.isOpen();
	}
	
	public SocketChannel getSockChannel() {
		return sc;
	}
	
}
