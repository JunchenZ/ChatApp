import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

public class Server {
	private ServerSocketChannel ssc;
	private Selector sel;
	private HashMap<String, Room> hashMapRoom;
	
	public Server() throws Exception {
		try {
			ssc = ServerSocketChannel.open();
			ssc.bind(new InetSocketAddress(8080));
			
			sel = Selector.open();
			ssc.configureBlocking(false);
			ssc.register(sel, SelectionKey.OP_ACCEPT);
				
			hashMapRoom = new HashMap<String, Room>();
			
		}catch(Exception e) {
			System.out.println("Server Error" + e);
		}
	}

	public SocketChannel accept() throws IOException {
		return ssc.accept();
	}
	
	public ServerSocketChannel getServerSocketChannel() {
		return ssc;
	}
	
	public Selector getSelector() {
		return sel;
	}
	
	public Room getRoom(String roomName) throws FileNotFoundException {
		if(hashMapRoom.containsKey(roomName)) {
			return hashMapRoom.get(roomName);
		}else {
			hashMapRoom.put(roomName, new Room(roomName));
			return hashMapRoom.get(roomName);
		}
		
	}
	
	
}
