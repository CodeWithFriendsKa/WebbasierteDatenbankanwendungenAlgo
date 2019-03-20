
public class ZeitEnd {

	Trainer trainer;
	Platz platz;
	Gruppe gruppe;
	int zeit;
	
	public ZeitEnd(Trainer trainer, Platz platz, Gruppe gruppe, int zeit) {
		super();
		this.trainer = trainer;
		this.platz = platz;
		this.gruppe = gruppe;
		this.zeit = zeit;
	}
	public Trainer getTrainer() {
		return trainer;
	}
	public void setTrainer(Trainer trainer) {
		this.trainer = trainer;
	}
	public Platz getPlatz() {
		return platz;
	}
	public void setPlatz(Platz platz) {
		this.platz = platz;
	}
	public Gruppe getGruppe() {
		return gruppe;
	}
	public void setGruppe(Gruppe gruppe) {
		this.gruppe = gruppe;
	}
	public int getZeit() {
		return zeit;
	}
	public void setZeit(int zeit) {
		this.zeit = zeit;
	}	
}
