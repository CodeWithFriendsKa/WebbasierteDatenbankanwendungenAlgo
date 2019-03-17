
public class Platz {
	private static int zaehler = 0;
	private int id;
	private String name;
	private int zeiten[];

	public Platz(String name, int[] zeiten) {
		this.name = name;
		this.zeiten =zeiten;
		this.id=zaehler;
		zaehler++;		
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int[] getZeiten() {
		return zeiten;
	}
}
