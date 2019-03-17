import java.util.ArrayList;

public class Trainer {
	
	private static int zaehler = 0;
	private int id;
	private String name;
	private int zeiten[];
	private int prio;
	private  ArrayList<Integer> minTrainingProTag;
	private int minAnzTraining;
	private int aktAnzTraining;
	private ArrayList<ArrayList<Pause>> pauseProTag = new ArrayList<ArrayList<Pause>>();

	
	
	public ArrayList<Integer> getMinTrainingProTag() {
		return minTrainingProTag;
	}

	public void setMinTrainingProTag(ArrayList<Integer> minTrainingProTag) {
		this.minTrainingProTag = minTrainingProTag;
	}

	public int getAktAnzTraining() {
		return aktAnzTraining;
	}

	public void setAktAnzTraining(int aktAnzTraining) {
		this.aktAnzTraining = aktAnzTraining;
	}

	public Trainer(String name, int[] zeiten, int prio, int minAnzTraining) {
		this.name = name;
		this.zeiten = zeiten;
		this.prio = prio;
		this.id = zaehler;
		this.minAnzTraining = minAnzTraining;
		zaehler++;
	}

	public void setMinAnzTraining(int minAnzTraining) {
		this.minAnzTraining = minAnzTraining;
	}



	public void setZeiten(int[] zeiten) {
		this.zeiten = zeiten;
	}



	public int[] getZeiten() {
		return zeiten;
	}

	public int getMinAnzTraining() {
		return minAnzTraining;
	}



	public String getName() {
		return name;
	}

	public int getPrio() {
		return prio;
	}

	public int getId() {
		return id;
	}

	public ArrayList<ArrayList<Pause>> getPauseProTag() {
		return pauseProTag;
	}

	public void setPauseProTag(ArrayList<ArrayList<Pause>> pauseProTag) {
		this.pauseProTag = pauseProTag;
	}




}
