import java.io.Serializable;

public class Message implements Serializable{
	private String message;
	
	public Message(String msg) {
		message = toJSON(msg);
	}
	
	public String getMsg() {
		return message;
	}
	
	public String toJSON(String s) {
		String[] str = s.split(" ");
		String msg = "";
		for(int i=1; i<str.length-1; i++) {
			msg += str[i] + " ";
		}
		msg += str[str.length-1];
		String string = "{\"user\":\"" + str[0] + "\", \"message\":\"" + msg + "\"}";
		System.out.println(string);
		return string;
	}
}
