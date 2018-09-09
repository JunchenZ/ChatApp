import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.channels.Channels;
import java.nio.channels.Pipe;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Iterator;
import java.util.Set;

public class Response {
	private File f;
	private FileInputStream fis;
	private Room room;
	private Pipe pipe;
	private String userName;
	private Boolean join;
	
	public Response(Socket socket, Request request, Server server) throws Exception {
		
		if(request.isHandshake()) {			
			handshake(socket, request, server);				
		}else {			
			httpRequest(socket, request);			
		}		
	}
	
	private void handshake(Socket socket, Request request, Server server) throws Exception {
		
		writeHeader(request, socket);		
		while(waitforJoin(socket, server));
		
		pipe = Pipe.open();
		
		if(join) {
			room.addUser(this);
			join = false;
		}
		
		Selector sel = Selector.open();
		
		socket.getChannel().configureBlocking(false);
		socket.getChannel().register(sel, SelectionKey.OP_READ);

		pipe.source().configureBlocking(false);
		pipe.source().register(sel, SelectionKey.OP_READ);
		
		while(!socket.isClosed()) {
			
			sel.select();
			Set<SelectionKey> Keys = sel.selectedKeys();
			Iterator<SelectionKey> iterator = Keys.iterator();			

			while(iterator.hasNext()) {
				
				SelectionKey Key = iterator.next();
				
				if(Key.isReadable()) {
					
					if(Key.channel()==socket.getChannel()) {
						socket.getChannel().keyFor(sel).cancel();
						socket.getChannel().configureBlocking(true);
						Message message = new Message(new String(decode(socket)));
						room.postMessage(message);	
						socket.getChannel().configureBlocking(false);
						sel.selectNow();
						socket.getChannel().register(sel, SelectionKey.OP_READ);
					}
					
					if(Key.channel()==pipe.source()) {
						pipe.source().keyFor(sel).cancel();	
						socket.getChannel().keyFor(sel).cancel();
						pipe.source().configureBlocking(true);
						socket.getChannel().configureBlocking(true);
						ObjectInputStream ois = new ObjectInputStream(Channels.newInputStream(pipe.source()));
						Message msg = (Message) ois.readObject();
						respond(socket, msg.getMsg());
						pipe.source().configureBlocking(false);
						sel.selectNow();
						pipe.source().register(sel, SelectionKey.OP_READ);
						socket.getChannel().configureBlocking(false);
						socket.getChannel().register(sel, SelectionKey.OP_READ);
					}
				}	
			}					
		}
	}
	
	public void sendMessage(Message msg) throws IOException {
		ObjectOutputStream oos = new ObjectOutputStream(Channels.newOutputStream(pipe.sink()));
		oos.writeObject(msg);
		oos.flush();
	}
	
	public void respond(Socket socket, String msg) throws Exception {
		
		DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
		
		dataOutputStream.writeByte((byte) (0x81));
		dataOutputStream.writeByte(msg.length());
		dataOutputStream.write(msg.getBytes());				
	}
	
	private boolean waitforJoin(Socket socket, Server server) throws Exception {
		
		byte[] decodeData = decode(socket);
		String[] joinRoom = new String(decodeData).split(" ");
		String roomName = "";
		for(int i=1; i<joinRoom.length-1; i++) {
			roomName += joinRoom[i] + " ";
		}
		roomName += joinRoom[joinRoom.length-1];
		if(joinRoom[0].equals("join")) {
			room = server.getRoom(roomName);
			join = true;
			return false;
		}else return true;				
	}
	
	public byte[] decode(Socket socket) throws IOException {
		InputStream inputStream = socket.getInputStream();
		DataInputStream dataInputStream = new DataInputStream(inputStream);
		byte[] header = new byte[10];
		byte[] maskingKey = new byte[4];

		header[0] = dataInputStream.readByte();
		header[1] = dataInputStream.readByte();
		
		int payloadLength = header[1]&(0x7F);
		int i;
		
		if(payloadLength==126){
			for(i=2; i<4; i++) {
				header[i] = dataInputStream.readByte();
			}
			payloadLength = header[2]*256 + header[3];
		}else if(payloadLength==127) {
			payloadLength = 0;
			for(i=2; i<10; i++) {
				header[i] = dataInputStream.readByte();
				payloadLength = payloadLength*256 + header[i];
			}
		}
		
		byte[] encodeData = new byte[payloadLength];
		byte[] decodeData = new byte[payloadLength];
		
		for(i=0; i<4; i++) {
			maskingKey[i] = dataInputStream.readByte();
		}
		
		for(i=0; i<payloadLength; i++) {
			encodeData[i] = dataInputStream.readByte();
			decodeData[i] = (byte) (encodeData[i]^(maskingKey[i%4]));
		}
		return decodeData;
	}
	
	public void writeHeader(Request request, Socket socket) throws Exception {
		String magicString = request.getHashMap().get("Sec-WebSocket-Key") + "258EAFA5-E914-47DA-95CA-C5AB0DC85B11";
		byte[] hashByte = magicString.getBytes();
		MessageDigest hash = MessageDigest.getInstance("SHA-1");
		String encode = Base64.getEncoder().encodeToString(hash.digest(hashByte));
		PrintWriter printWriter = new PrintWriter(socket.getOutputStream());
		printWriter.print("HTTP/1.1 101 Switching Protocols");
		printWriter.print("\r\n");						
		printWriter.print("Upgrade: websocket");
		printWriter.print("\r\n");						
		printWriter.print("Connection: Upgrade");
		printWriter.print("\r\n");
		printWriter.print("Sec-WebSocket-Accept: " + encode);
		printWriter.print("\r\n");
		printWriter.print("\r\n");
		printWriter.flush();
	}
	
private void httpRequest(Socket socket, Request request) throws Exception {
		
		try{
			if(request.getFilePath().equals("./")) {
				f = new File("index.html");
				fis = new FileInputStream("index.html");
			}else {
				f = new File(request.getFilePath());
				fis = new FileInputStream(request.getFilePath());
			}
		}catch(FileNotFoundException e) {
			System.out.println("Error in response, file is not found: " + e);
			f = new File("404.html");
			fis = new FileInputStream("404.html");
		}finally {
			OutputStream outputStream = socket.getOutputStream();
			PrintWriter printWriter = new PrintWriter(outputStream);
			printWriter.println("HTTP/1.1 200 OK");
			printWriter.println("Content-Type: text/html");
			printWriter.println("Content-Length: " + f.length());
			printWriter.print("\r\n");						
			printWriter.flush();
			sendBytes(fis,outputStream);
		}
	}

	
	private void sendBytes(FileInputStream fis, OutputStream outputStream) throws Exception{
		byte[] buffer = new byte [1024];
		int bytes = 0;
		while((bytes = fis.read(buffer)) != -1) {
			outputStream.write(buffer,0,bytes);
			outputStream.flush();
			Thread.sleep(2);
		}
	}
	
}
