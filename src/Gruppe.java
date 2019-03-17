import java.util.ArrayList;

public class Gruppe {
	
	private static int zaehler=0;
	private int id;
	private String name;
	private int stunde;
	private int anzahlMaxSpieler;
	private int spielerStaerkeVon;
	private int spielerStaerkeBis;
	private char geschlecht;
	private ArrayList<Spieler> spieler;

	public Gruppe(String name,int stunde,int anzahlMaxSpieler,int spielerStaerkeVon,int spielerStaerkeBis,char geschlecht) {
		spieler = new ArrayList<Spieler>();	
		this.name=name;
		this.stunde=stunde;
		this.anzahlMaxSpieler=anzahlMaxSpieler;
		this.spielerStaerkeVon=spielerStaerkeVon;
		this.spielerStaerkeBis=spielerStaerkeBis;
		this.setGeschlecht(geschlecht);
		this.id=zaehler;
		zaehler++;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setGeschlecht(char geschlecht) {
		this.geschlecht = geschlecht;
	}	

	public void setStunde(int stunde) {
		this.stunde = stunde;
	}

	public void setSpieler(ArrayList<Spieler> spieler) {
		this.spieler = spieler;
	}
	
	public void addSpieler(Spieler spieler) {
		this.spieler.add(spieler);
	}

	public void setMaxAnzahlSpieler(int anzahlMaxSpieler) {
		this.anzahlMaxSpieler = anzahlMaxSpieler;
	}

	public void setSpielerStaerkeBis(int spielerStaerkeBis) {
		this.spielerStaerkeBis = spielerStaerkeBis;
	}

	public void setSpielerStaerkeVon(int spielerStaerkeVon) {
		this.spielerStaerkeVon = spielerStaerkeVon;
	}



	public int getSpielerStaerkeVon() {
		return spielerStaerkeVon;
	}

	public int getSpielerStaerkeBis() {
		return spielerStaerkeBis;
	}

	public int getMaxAnzahlSpieler() {
		return anzahlMaxSpieler;
	}

	public int getId() {
		return id;
	}


	public ArrayList<Spieler> getSpieler() {
		return spieler;
	}

	public int getStunde() {
		return stunde;
	}

	public char getGeschlecht() {
		return geschlecht;
	}

	public String getName() {
		return name;
	}

}
