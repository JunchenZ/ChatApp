import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Scanner;

public class Request {
	private String file_path;
	private HashMap<String, String> hashMap;
	
	public Request(Socket socket) throws Exception {
		
		try {
			
			InputStream is = socket.getInputStream();
			Scanner s = new Scanner(is);				
			String[] filePath = s.nextLine().split(" ");
			
			if(!filePath[0].equals("GET")) {
				throw new BadRequestException("the first words is not GET");
			}else if(!filePath[2].equals("HTTP/1.1")) {
				throw new BadRequestException("the last word on the first line is not HTTP/1.1");
			}
			
			filePath[1] = "." + filePath[1];
			file_path = filePath[1];
			
			hashMap = new HashMap<String, String>();
			while(true) {
				String line = s.nextLine();
				if(line.isEmpty()) {
					break;
				}
				String[] string = line.split(": ");
				hashMap.put(string[0], string[1]);
			}	
			
		}catch(IOException e) {
			throw new BadRequestException("Error in request(): " + e);
		}
	}
	
	public String getFilePath() {
		return file_path;
	}
	
	public HashMap<String, String> getHashMap() {
		return hashMap;
	}
	
	public boolean isHandshake() {
		if(hashMap.containsKey("Sec-WebSocket-Key")) {
			return true;
		}else return false;
	}
}
