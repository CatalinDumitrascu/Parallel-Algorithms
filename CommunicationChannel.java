import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;


/**
 * Class that implements the channel used by wizards and miners to communicate.
 */
public class CommunicationChannel {
	/**
	 * Creates a {@code CommunicationChannel} object.
	 */
	private ArrayBlockingQueue<Message> minerMess;
	private ConcurrentHashMap<Long, ArrayBlockingQueue<Message>> wizardPool;
	private ArrayBlockingQueue<Message> buf = new ArrayBlockingQueue<Message>(999999);
	private Message parent = null, current = null;
	private final ReentrantLock lock = new ReentrantLock();

	public CommunicationChannel() {
		minerMess = new ArrayBlockingQueue<Message>(1000000);
		wizardPool = new ConcurrentHashMap<Long, ArrayBlockingQueue<Message>>();
	}

	/**
	 * Puts a message on the miner channel (i.e., where miners write to and wizards
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageMinerChannel(Message message) {
		synchronized(minerMess) {
			minerMess.add(message);
		}
	}

	/**
	 * Gets a message from the miner channel (i.e., where miners write to and
	 * wizards read from).
	 * 
	 * @return message from the miner channel
	 */
	public Message getMessageMinerChannel() {
		synchronized(minerMess) {
			return minerMess.poll();
		}
	}

	/**
	 * Puts a message on the wizard channel (i.e., where wizards write to and miners
	 * read from).
	 * 
	 * @param message
	 *            message to be put on the channel
	 */
	public void putMessageWizardChannel(Message message) {
		Long currentThread = Thread.currentThread().getId();
		
			try {
				if(wizardPool.containsKey(currentThread)) {
					try {
						wizardPool.get(currentThread).add(message);
					} catch (ConcurrentModificationException e) {
						e.printStackTrace();
					}
				} else {
					buf = new ArrayBlockingQueue<>(100000);
					buf.add(message);
					wizardPool.put(currentThread, buf);
				}
			} catch (ConcurrentModificationException e) {
				e.printStackTrace();
			}
		
		
//		if(message.getData().equals("END")) {
//			try {
//				synchronized(wizardMess) {
//					wizardMess.addAll(wizardPool.get(currentThread));
//				}
//			} catch (ConcurrentModificationException e) {
//				e.printStackTrace();
//			}
//			try {
//				wizardPool.remove(currentThread);
//			} catch (ConcurrentModificationException e) {
//				e.printStackTrace();
//			}
//		}
		//System.out.println(wizardMess);
		
	}

	/**
	 * Gets a message from the wizard channel (i.e., where wizards write to and
	 * miners read from).
	 * 
	 * @return message from the miner channel
	 */
	public ArrayList<Message> getMessageWizardChannel() {	
		
		ArrayList<Long> keySet = new ArrayList<Long>(wizardPool.keySet());
		ArrayList<Message> buf = new ArrayList<Message>();
		
		for(Long aux : keySet) {
			
			ArrayBlockingQueue<Message> here = wizardPool.get(aux);
			synchronized(wizardPool.get(aux)) {
				if(here.isEmpty()) {
					continue;
				} else if (here.peek().getData().equals("EXIT")) {
					here.poll();
					buf.add(new Message(0, "EXIT"));
					return buf;
				} else if (here.peek().getData().equals("END")) {
					here.poll();
					continue;
				} else {				
					try {
						parent = here.take();
						buf.add(parent);
						current = here.take();
						buf.add(current);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return buf;		
			}	
		}
		buf.add(new Message(0, "END"));
		return buf;
	}
}
