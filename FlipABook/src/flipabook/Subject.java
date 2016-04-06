package flipabook;

public interface Subject {
	static final int SELLER_TO_BUYER = 0;
	static final int BUYER_TO_SELLER = 1;

	public void registerObservers(Observer o0, Observer o1);
	public void removeObserver(Observer o);
	public void notifyObservers(int updateType);
}