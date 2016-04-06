package objects;

public interface Observer {
	//constants for "updateType" field
	static final int SELLING = 0;
	static final int BUYING = 1;
	static final int NEW_MESSAGE = 2;
	static final int DELETE = 3;
	static final int READ = 4;
	
	public void update(Message message, int updateType);
}