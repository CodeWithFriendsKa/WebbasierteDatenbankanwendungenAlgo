import java.util.ArrayList;

public class Zeiten {

	private static int groesstegruppe=0;
	private static int zaehler=1;
	int id;
	int zeit;
	ArrayList<Trainer> trainer;
	ArrayList<Platz> platz;
	ArrayList<Gruppe> gruppen;

	public Zeiten(int zeit, ArrayList<Trainer> trainer, ArrayList<Platz> platz) {
		this.zeit = zeit;
		this.trainer = trainer;
		this.platz = platz;
		this.id = zaehler;
		zaehler++;
		
	}

	public Zeiten(int zeit) {

		this.zeit = zeit;
		this.trainer = new ArrayList<Trainer>();;
		this.platz = new ArrayList<Platz>();;
		this.id = zaehler;
		zaehler++;
	}
	

	public static String intToString(int zeitInt) {
		String dummy="";
		String[] wochentage = {"Montag","Dienstag","Mittwoch","Donnerstag","Freitag","Samstag","Sonntag"}; 

		int wt = zeitInt / 24;
		int h = zeitInt % 24;

		dummy += wochentage[wt] + " " + h;

		return dummy;
	}

	public int getTrainingsGruppenMoeglichkeiten() {					
		//Wenn mehr Plätze als Trainer zur Verfügung stehen kann MaxAnzTrainer.size() Training gegeben werden die Anz der Trainer werden hier zurück gegeben

		if(trainer.size()<platz.size())return trainer.size();

		return platz.size();
	}



	public void setGruppen(ArrayList<Gruppe> gruppen) {
		if(gruppen.size()>groesstegruppe) {
			groesstegruppe=gruppen.size();
		}
		this.gruppen = gruppen;
	}	

	public void setPlatz(ArrayList<Platz> platz) {
		this.platz = platz;
	}

	public void setZeit(int zeit) {
		this.zeit = zeit;
	}

	public void setTrainer(ArrayList<Trainer> trainer) {
		this.trainer = trainer;
	}



	public static int getGroessteGruppe() {
		return groesstegruppe;
	}

	public ArrayList<Trainer> getTrainer() {
		return trainer;
	}

	public ArrayList<Platz> getPlatz() {
		return platz;
	}

	public ArrayList<Gruppe> getGruppen() {
		return gruppen;
	}

	public int getZeit() {
		return zeit;
	}

	public int getId() {
		return id;
	}





}
