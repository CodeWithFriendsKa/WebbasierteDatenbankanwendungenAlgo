
public class ZeitEndModel {

	TrainerModel trainer;
	Platz platz;
	GruppeModel gruppe;
	int zeit;
	Hash hash;
	
	public ZeitEndModel(TrainerModel trainer, Platz platz, GruppeModel gruppe, int zeit) {
		super();
		this.trainer = trainer;
		this.platz = platz;
		this.gruppe = gruppe;
		this.zeit = zeit;
	}
	
	
	public Hash getHash() {
		return hash;
	}
	public TrainerModel getTrainer() {
		return trainer;
	}
	public void setTrainer(TrainerModel trainer) {
		this.trainer = trainer;
	}
	public Platz getPlatz() {
		return platz;
	}
	public void setPlatz(Platz platz) {
		this.platz = platz;
	}
	public GruppeModel getGruppe() {
		return gruppe;
	}
	public void setGruppe(GruppeModel gruppe) {
		this.gruppe = gruppe;
	}
	public int getZeit() {
		return zeit;
	}
	public void setZeit(int zeit) {
		this.zeit = zeit;
	}	
}
