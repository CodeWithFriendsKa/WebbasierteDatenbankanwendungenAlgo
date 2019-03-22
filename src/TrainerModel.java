import java.util.ArrayList;

public class TrainerModel {
	
	private static int zaehler = 0;
	private int id;
	private String name;
	private int zeiten[];
	private int prio;
	private  ArrayList<Integer> minTrainingProTag;
	private int minAnzTraining;
	private int aktAnzTraining;
	private ArrayList<ArrayList<PauseModel>> pauseProTag = new ArrayList<ArrayList<PauseModel>>();
	private static ArrayList<TrainerModel> alleTrainer = new ArrayList<TrainerModel>();
	
	
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

	public TrainerModel(String name, int[] zeiten, int prio, int minAnzTraining) {
		this.name = name;
		this.zeiten = zeiten;
		this.prio = prio;
		this.id = zaehler;
		this.minAnzTraining = minAnzTraining;
		zaehler++;
		alleTrainer.add(this);
	}
	
	

	public static ArrayList<TrainerModel> getAlleTrainer() {
		return alleTrainer;
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

	public ArrayList<ArrayList<PauseModel>> getPauseProTag() {
		return pauseProTag;
	}

	public void setPauseProTag(ArrayList<ArrayList<PauseModel>> pauseProTag) {
		this.pauseProTag = pauseProTag;
	}




}
