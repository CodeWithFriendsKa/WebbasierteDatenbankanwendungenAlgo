import java.util.ArrayList;

public class Spieler {

	private static int zaehler=0;
	private int id;
	private final static int zeitenArraygroesse=168;
	private String name;
	private int zeiten[];
	private int alter;
	private char geschlecht;
	private int trainingsAnzahl;
	private int trainingsAnzahlAktuell;
	private static ArrayList<Spieler> alleSpieler = new ArrayList<>(); 
	private static ArrayList<Spieler> alleSpielerMehrTraining = new ArrayList<>(); 

	private SpielerStaerke spielerStaerke=SpielerStaerke.NICHTVORHANDEN;

	public Spieler(String name,int[] zeiten,int alter, char geschlecht, int trainingsAnzahl) {
		this.name = name;
		this.zeiten = zeiten;
		this.alter = alter;
		this.geschlecht = geschlecht;
		this.trainingsAnzahl = trainingsAnzahl;
		this.id=zaehler;
		zaehler++;
		alleSpieler.add(this);
		for (int i = 1; i < trainingsAnzahl; i++) {
			alleSpielerMehrTraining.add(this);
		}
	}

	public Integer verrechneSpielerStaerkeMitAlter() {											
		//hier wird das Spieleralter mit der Bewertung der Trainers verrechnet um die Spielerstärke zu erhalten

		if(this.spielerStaerke.Staerke()==null) return this.alter;
		if(this.spielerStaerke.Staerke()==2)return 3 + this.alter;

		return this.spielerStaerke.Staerke() + this.alter;
	}
	
	
	
	public static ArrayList<Spieler> getAlleSpieler() {
		return alleSpieler;
	}

	public static ArrayList<Spieler> getAlleSpielerMehrTraining() {
		return alleSpielerMehrTraining;
	}

	public int getTrainingsAnzahlAktuell() {
		return trainingsAnzahlAktuell;
	}

	public void setTrainingsAnzahlAktuell(int trainingsAnzahlAktuell) {
		this.trainingsAnzahlAktuell = trainingsAnzahlAktuell;
	}

	public void setSpielerStaerke(Integer spielerStaerke) {
		this.spielerStaerke =  SpielerStaerke.valueOf(spielerStaerke);
	}	

	public void setZeiten(int[] zeiten) {
		this.zeiten = zeiten;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setAlter(int alter) {
		this.alter = alter;
	}
	public void setGeschlecht(char geschlecht) {
		this.geschlecht = geschlecht;
	}

	public void setTrainingsAnzahl(int trainingsAnzahl) {
		this.trainingsAnzahl = trainingsAnzahl;
	}



	public String getName() {
		return this.name;
	}

	public int[] getZeiten() {
		return this.zeiten;
	}

	public int getAlter() {
		return  this.alter;
	}

	public char getGeschlecht() {
		return this.geschlecht;
	}

	public int getTrainingsAnzahl() {
		return this.trainingsAnzahl;
	}

	public int getID() {
		return id;
	}

	public static int getZeitenArrayGroesse() {
		return zeitenArraygroesse;
	}	

	public String getInfo() {
		String infoString = ""+this.name+" "+this.alter+" "+this.geschlecht;
		return infoString;
	}


}

enum SpielerStaerke {																						
	//Hier wird eine Variable erstell mit der die Berechnung der Spielerstärke vereinfacht wird

	NICHTVORHANDEN( null ),BRONZE( -2 ), IRON( -1 ), SILBER( 0 ), GOLD( 1 ), DIAMANT( 2 );

	private final Integer staerke;


	SpielerStaerke( Integer staerke ) { this.staerke = staerke; }


	public static SpielerStaerke valueOf(Integer spielerStaerke) {


		if(spielerStaerke == null)return NICHTVORHANDEN;
		if(spielerStaerke == 2)return DIAMANT;
		if(spielerStaerke == -2)return BRONZE;
		if(spielerStaerke == -1)return IRON;
		if(spielerStaerke == 0)return SILBER;
		if(spielerStaerke == 1)return GOLD;

		return NICHTVORHANDEN;
	}


	public Integer Staerke() { 		
		return staerke; 
	}

}
