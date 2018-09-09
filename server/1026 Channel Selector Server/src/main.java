
public class main {

	public static void main(String[] args) throws Exception  {
		
		Server server = new Server();
		
		while(true) {				
			try {
				Client client = new Client(server);
			}catch(Exception e) {
				System.out.println("Error in main" + e);
			}			
		}
		
	}
	
}
