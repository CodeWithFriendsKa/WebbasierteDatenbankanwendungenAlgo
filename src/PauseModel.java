
public class PauseModel {

	private int vonZeit;

	private int bisZeit;
	
	
	
	public PauseModel(int vonZeit, int bisZeit) {
		super();
		this.vonZeit = vonZeit;
		this.bisZeit = bisZeit;
	}
	public int getVonZeit() {
		return vonZeit;
	}
	public void setVonZeit(int vonZeit) {
		this.vonZeit = vonZeit;
	}
	public int getBisZeit() {
		return bisZeit;
	}
	public void setBisZeit(int bisZeit) {
		this.bisZeit = bisZeit;
	}
	
}
