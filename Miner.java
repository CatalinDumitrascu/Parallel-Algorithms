import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Set;

/**
 * Class for a miner.
 */
public class Miner extends Thread {
	
	private Integer hashCount;
	public static Set<Integer> solved; 
	private CommunicationChannel channel;
	private ArrayList<Message> buf = new ArrayList<Message>();
	private Message parent = null, current = null;
	/**
	 * Creates a {@code Miner} object.
	 * 
	 * @param hashCount
	 *            number of times that a miner repeats the hash operation when
	 *            solving a puzzle.
	 * @param solved
	 *            set containing the IDs of the solved rooms
	 * @param channel
	 *            communication channel between the miners and the wizards
	 */
	public Miner(Integer hashCount, Set<Integer> solved, CommunicationChannel channel) {
		this.hashCount = hashCount;
		Miner.solved = solved;
		this.channel = channel;
	}
	
	private static String encryptMultipleTimes(String input, Integer count) {
        String hashed = input;
        for (int i = 0; i < count; ++i) {
            hashed = encryptThisString(hashed);
        }

        return hashed;
    }

    private static String encryptThisString(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] messageDigest = md.digest(input.getBytes(StandardCharsets.UTF_8));
            
            // convert to string
            StringBuffer hexString = new StringBuffer();
            for (int i = 0; i < messageDigest.length; i++) {
            String hex = Integer.toHexString(0xff & messageDigest[i]);
            if(hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
    
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

	@Override
	public void run() {
		
		while (true) {
			/** 
			 * Retrieves the parent node and checks for END/empty queue
			 */
			
			buf = channel.getMessageWizardChannel();
			if (buf == null) {
				continue;
			}
			parent = buf.get(0);
			if(parent == null || parent.getData().equals("END")) {
				continue;
			} else if(parent.getData().equals("EXIT")) {
				break;
			}

			
			/**
			 * Retrieves the current node
			 */
			current = buf.get(1);
			
			if(current == null) continue;
			
			if(solved.contains(current.getCurrentRoom())) {
				continue;
			}
	

			if(current.getCurrentRoom() == -1) {
				continue;
			}
			
			/**
			 * Mines the current node
			 */
			String hashed = encryptMultipleTimes(current.getData(), hashCount);
			
			/**
			 * Puts the parent and current rooms, along with the solution in the miner channel
			 */
			if(parent.getCurrentRoom() == -1) {
				parent.setCurrentRoom(current.getCurrentRoom());
			}
			channel.putMessageMinerChannel(new Message(parent.getCurrentRoom(), current.getCurrentRoom(), hashed));
			
			/**
			 * Adds the current room to the set of solved puzzles
			 */
//			synchronized (solved) {
//				if (!solved.contains(current.getCurrentRoom())) {
//					solved.add(current.getCurrentRoom());
//				}
//			}
			solved.add(current.getCurrentRoom());
			
			
		}
		
	}
}
