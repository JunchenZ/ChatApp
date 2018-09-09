import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

public class Room {

	String roomName;
	ArrayList<Response> user;
	ArrayList<Message> message;
	
	public Room(String name) throws FileNotFoundException {
		roomName = name;
		user = new ArrayList<Response>();
		message = new ArrayList<Message>();
	}
	
	public void addUser(Response r) throws Exception {
		user.add(r);
		for(Message m: message) {
			r.sendMessage(m);
		}
	}
	
	public synchronized void postMessage(Message msg) throws IOException {
		message.add((msg));
		for(Response r: user) {
			r.sendMessage(msg);
		}
	}
	
	public ArrayList<Message> getMessage(){
		return message;
	}
	
}
