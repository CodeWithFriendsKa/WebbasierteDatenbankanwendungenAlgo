import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlgoBibliothek {
	
	private static ArrayList<Spieler> spielerHatKeinTrainer = new ArrayList<Spieler>();
	
	public static ArrayList<Spieler> getSpielerHatKeinTrainer() {
		return spielerHatKeinTrainer;
	}
	
	private static ArrayList<Spieler> spielerKeineGruppeZuo = new ArrayList<Spieler>();
	
	public static ArrayList<Spieler> getSpielerKeineGruppeZuo() {
		return spielerKeineGruppeZuo;
	}
	
	private static ArrayList<Spieler> zweierSpielerKeineGruppeZuo = new ArrayList<Spieler>();
	
	public static ArrayList<Spieler> getZweierSpielerKeineGruppeZuo() {
		return zweierSpielerKeineGruppeZuo;
	}
	
	private static ArrayList<Gruppe> zweierGruppe = new ArrayList<Gruppe>();
	
	public static ArrayList<Gruppe> getZweierGruppe() {
		return zweierGruppe;
	}
	
	private static ArrayList<ArrayList<ZeitEnd>> vorLaeufigeMoeglichkeiten = new ArrayList<ArrayList<ZeitEnd>>();
	
	public static ArrayList<ArrayList<ZeitEnd>> getVorlaeufigeMoeglichkeiten() {
		return vorLaeufigeMoeglichkeiten;
	}
	
	private static ArrayList<Zeiten> zeiten = new ArrayList<Zeiten>();
	
	public static ArrayList<Zeiten> getZeiten() {
		return zeiten;
	}
	
	private static ArrayList<Zeiten> zeitenKopie = new ArrayList<Zeiten>();
	
	@SuppressWarnings("unchecked")
	public static ArrayList<Zeiten> ErstellePlan(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer,
			ArrayList<Platz> plaetze) {
		
		ArrayList<ArrayList<Gruppe>> gruppen = SpielerGruppeZuo(spieler);
		gruppen = GruppenLoeschen(gruppen);
		gruppen = GruppenSplitten(gruppen);
		gruppen = KontrolleSpielerBereitsZuo(gruppen);
		gruppen = WiederBefuellungZweierGruppe(gruppen);
		gruppen = KontrolleSpielerBereitsZuo(gruppen);// nochmal wegen Spieler ohne Zuo
		zeiten = PruefePlatzUndTrainer(plaetze, trainer, gruppen);
		zeiten = LoscheZeitenOhneTrainer(zeiten);
		spielerHatKeinTrainer = SpielerOhneZuo(spieler, zeiten);
		
		zeitenKopie = (ArrayList<Zeiten>) zeiten.clone();
		
		Algorythmus(spieler, trainer, plaetze);
		
		return zeiten;
		
	}
	
	private static void Algorythmus(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer, ArrayList<Platz> plaetze) {
		
		SpielerOhneZuo(spieler, zeitenKopie);
		SpielerHatFuerAlleTermineTraining(spieler, trainer, plaetze, zeiten);
		TrainerMinTermineTraining(spieler, trainer, plaetze);
		TrainerPauseZwischenTrainingszeitenProTag(spieler, trainer, plaetze);
		
		vorLaeufigeMoeglichkeiten = gruppenZuordnen(spieler, trainer, plaetze);
		// berechneHash(vorLaeufigeMoeglichkeiten);
		
	}
	
	@SuppressWarnings("unchecked")
	private static void berechneHash(ArrayList<ArrayList<ZeitEnd>> endZeiten) {
		ArrayList<ArrayList<Zeiten>> vorläufigeClone = (ArrayList<ArrayList<Zeiten>>) endZeiten.clone();
		
		for (int i = 0; i < vorläufigeClone.size(); i++) {
			for (int j = 0; j < vorläufigeClone.get(i).size(); j++) {
				Hash dummyHash = null;
				
				vorläufigeClone.get(i).get(j).setHash(dummyHash);
			}
			
		}
		
	}
	
	public static int anzUntergruppen() {
		int zaehler = 0;
		for (int i = 0; i < zeiten.size(); i++) {
			if (zeiten.get(i) == null) continue;
			zaehler += zeiten.get(i).getGruppen().size();
		}
		return zaehler;
	}
	
	public static Gruppe getUntergruppeById(int var) {
		int zaehler = 0;
	
		
		for (int i = 0; i < zeiten.size(); i++) {
			for (int j = 0; j < zeiten.get(i).getGruppen().size(); j++) {
				int indexGruppe = 0;
				if (zeiten.get(i) == null) continue;
				zaehler ++;
				indexGruppe++;
				
				if(zaehler == var) {
					Gruppe dummy = zeiten.get(i).getGruppen().get(indexGruppe);
					return dummy;
				}
			}
		}
		
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<ZeitEnd>> gruppenZuordnen(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer, ArrayList<Platz> plaetze) {
		
		ArrayList<ArrayList<ZeitEnd>> dummy = new ArrayList<ArrayList<ZeitEnd>>();
		int anzUntergruppen = anzUntergruppen();
		
		naechsteMoeglichkeit:
		for (int i2 = 0; i2 < anzUntergruppen; i2++) {
			int zaehler = 0;
			
			ArrayList<ZeitEnd> dummyInnenListe = new ArrayList<ZeitEnd>();
			
			ArrayList<Spieler> copyAlleSpieler = (ArrayList<Spieler>) Spieler.getAlleSpieler().clone();
			for (int i = 0; i < spielerHatKeinTrainer.size(); i++) {
				copyAlleSpieler.remove(spielerHatKeinTrainer.get(i));
			}
			
			ArrayList<Spieler> copyAlleSpielerMehrTraining = (ArrayList<Spieler>) Spieler.getAlleSpielerMehrTraining().clone();
			for (int i = 0; i < spielerHatKeinTrainer.size(); i++) {
				copyAlleSpielerMehrTraining.remove(spielerHatKeinTrainer.get(i));
			}
			
			int reset = i2;
			
			for (int i = 0; i < 2; i++) {
				
				for (int j = reset; j < Spieler.getZeitenArrayGroesse(); j++) {
					
					if (zeiten.get(j) == null)
						continue;
					int trainerZaehler = 0;
					int platzZaehler = 0;
					
					outerloop:
					for (int k = 0; k < zeiten.get(j).getGruppen().size(); k++) {
						if (trainerZaehler >= zeiten.get(j).getTrainer().size() || platzZaehler >= zeiten.get(j).getPlatz().size()) break;
						for (int l = 0; l < zeiten.get(j).getGruppen().get(k).getSpieler().size(); l++) {
							if (!copyAlleSpieler.contains(zeiten.get(j).getGruppen().get(k).getSpieler().get(l)) || (!copyAlleSpieler.contains(zeiten.get(j).getGruppen().get(k).getSpieler().get(l)) && !copyAlleSpielerMehrTraining.contains(zeiten.get(j).getGruppen().get(k).getSpieler().get(l)))) continue outerloop;
						}
						ZeitEnd zeitEndDummy = new ZeitEnd(zeiten.get(j).getTrainer().get(trainerZaehler), zeiten.get(j).getPlatz().get(platzZaehler), zeiten.get(j).getGruppen().get(k), (j));
						trainerZaehler++;
						platzZaehler++;
						dummyInnenListe.add(zeitEndDummy);
						
						for (int l = 0; l < dummyInnenListe.size(); l++) {
							for (int z = 0; z < dummyInnenListe.get(l).getGruppe().getSpieler().size(); z++) {
								if (dummyInnenListe.get(l).getGruppe().getSpieler().get(z) == null) break;
								if (copyAlleSpieler.contains(dummyInnenListe.get(l).getGruppe().getSpieler().get(z))) {
									copyAlleSpieler.remove(dummyInnenListe.get(l).getGruppe().getSpieler().get(z));
								} else {
									try {
										copyAlleSpielerMehrTraining.remove(dummyInnenListe.get(l).getGruppe().getSpieler().get(z));
									} catch (Exception e) {
										System.out.println("Stimmt wieder was nicht mit der befüllung, beim removen wird aus einer der beiden gesammt SpielerListen wird die exeption geworffen, oberer Teil");
										throw e;
									}
								}
								if (copyAlleSpieler.isEmpty()) {
									dummy.add(dummyInnenListe);
									continue naechsteMoeglichkeit;
								} else {
									continue;
								}
							}
							if (copyAlleSpieler.isEmpty()) {
								dummy.add(dummyInnenListe);
								continue naechsteMoeglichkeit;
							} else {
								continue;
							}
						}
						if (copyAlleSpieler.isEmpty()) {
							dummy.add(dummyInnenListe);
							continue naechsteMoeglichkeit;
						} else {
							continue;
						}
					}
					if (copyAlleSpieler.isEmpty()) {
						dummy.add(dummyInnenListe);
						continue naechsteMoeglichkeit;
					} else {
						continue;
					}
				}
				reset = 0;
				if (copyAlleSpieler.isEmpty()) {
					dummy.add(dummyInnenListe);
					continue naechsteMoeglichkeit;
				} else {
					continue;
				}
			}

			System.out.println(((double) i2 / (double) anzUntergruppen) * 100 + " prozent");
			
			if (copyAlleSpieler.isEmpty()) {
				dummy.add(dummyInnenListe);
			} else {
				continue;
			}
			
		}
		

		ArrayList<ArrayList<ZeitEnd>> ausgabe = new ArrayList<ArrayList<ZeitEnd>>();
		
		for (int i = 0; i < dummy.size(); i++) {
			for (int j = 0; j < dummy.get(i).size(); j++) {
				//if()break;
			}
		}
		
		return dummy;
		
	}
	
	/*
	 * @SuppressWarnings("unchecked") private static void
	 * gruppenZuordnen(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer,
	 * ArrayList<Platz> plaetze) {
	 * 
	 * // ArrayList<zeitEnd> dummy = new ArrayList<>();
	 * 
	 * for (int z = 0; z < 7; z++) {
	 * 
	 * for (int i = z * 24; i < zeiten.size(); i++) {
	 * 
	 * if (zeiten.get(z * 24) == null) continue; for (int j = 0; j <
	 * Zeiten.getGroessteGruppe(); j++) { ArrayList<Zeiten> dummy = GruppenZuo(i,
	 * j); vorLaeufigeMoeglichkeiten.add(dummy); } } for (int i = 0; i < z * 24;
	 * i++) { if (zeiten.get(z * 24) == null) continue; for (int j = 0; j <
	 * Zeiten.getGroessteGruppe(); j++) { ArrayList<Zeiten> dummy = GruppenZuo(i,
	 * j); vorLaeufigeMoeglichkeiten.add(dummy); } } }
	 * 
	 * ArrayList<Spieler> dummy = new ArrayList<>(); dummy = (ArrayList<Spieler>)
	 * Spieler.getAlleSpieler().clone(); for (int i = 0; i <
	 * spielerHatKeinTrainer.size(); i++) {
	 * dummy.remove(spielerHatKeinTrainer.get(i)); }
	 * 
	 * for (int i = vorLaeufigeMoeglichkeiten.size() - 1; i >= 0; i--) { if
	 * (!SpielerOhneZuo(dummy, vorLaeufigeMoeglichkeiten.get(i)).isEmpty()) {
	 * vorLaeufigeMoeglichkeiten.remove(i); } }
	 * 
	 * for (int i = vorLaeufigeMoeglichkeiten.size() - 1; i >= 0; i--) { if
	 * (SpielerHatFuerAlleTermineTraining(spieler, trainer, plaetze,
	 * vorLaeufigeMoeglichkeiten.get(i)) == 0) vorLaeufigeMoeglichkeiten.remove(i);
	 * }
	 * 
	 * }
	 */
	
	/*
	 * @SuppressWarnings({ "unchecked" }) private static ArrayList<ZeitEnd>
	 * GruppenZuo(int var, int var2) { ArrayList<Spieler> copyAlleSpieler =
	 * (ArrayList<Spieler>) Spieler.getAlleSpieler().clone(); for (int i = 0; i <
	 * spielerHatKeinTrainer.size(); i++) {
	 * copyAlleSpieler.remove(spielerHatKeinTrainer.get(i)); }
	 * 
	 * ArrayList<Spieler> copyAlleSpielerMehrTraining = (ArrayList<Spieler>)
	 * Spieler.getAlleSpielerMehrTraining().clone(); for (int i = 0; i <
	 * spielerHatKeinTrainer.size(); i++) {
	 * copyAlleSpielerMehrTraining.remove(spielerHatKeinTrainer.get(i)); }
	 * 
	 * ArrayList<ZeitEnd> zeitendummy = new ArrayList<ZeitEnd>();
	 * 
	 * for (int i = var; i < zeiten.size(); i++) { if (zeiten.get(i) != null)
	 * outerloop: for (int j = var2; j < zeiten.get(i).getGruppen().size(); j++) {
	 * 
	 * if (j - var2 > zeiten.get(i).getTrainer().size()) break;
	 * 
	 * for (int j = 0; j < zeiten.get(i).getGruppen().get(j).getSpieler().size();
	 * j++) {
	 * 
	 * if
	 * (!copyAlleSpieler.contains(zeiten.get(i).getGruppen().get(j).getSpieler().get
	 * (j)) ||
	 * !copyAlleSpielerMehrTraining.contains(zeiten.get(i).getGruppen().get(j).
	 * getSpieler().get(j))) continue outerloop;
	 * 
	 * zeitendummy.add(zeiten.get(i));
	 * 
	 * if
	 * (copyAlleSpieler.contains(zeiten.get(i).getGruppen().get(j).getSpieler().get(
	 * j))) {
	 * copyAlleSpieler.remove(zeiten.get(i).getGruppen().get(j).getSpieler().get(j))
	 * ; } else {
	 * copyAlleSpielerMehrTraining.remove(zeiten.get(i).getGruppen().get(j).
	 * getSpieler().get(j)); } }
	 * 
	 * } }
	 * 
	 * return zeitendummy;
	 * 
	 * }
	 */
	
	private static void TrainerPauseZwischenTrainingszeitenProTag(ArrayList<Spieler> spieler,
			ArrayList<Trainer> trainer, ArrayList<Platz> plaetze) {
		
		for (int i = 0; i < trainer.size(); i++) {
			
			ArrayList<ArrayList<Pause>> pausen = new ArrayList<ArrayList<Pause>>();
			ArrayList<Pause> dummy = new ArrayList<Pause>();
			ArrayList<Integer> training = new ArrayList<Integer>();
			int trainingDummy = 0;
			int pause = 0;
			boolean pauseAktiv = false;
			for (int j = 0; j < zeiten.size(); j++) {
				if (pauseAktiv)
					pause++;
				if (j != 0 && j % 24 == 0) {
					pause = 0;
					pauseAktiv = false;
					pausen.add(dummy);
					dummy = new ArrayList<Pause>();
					
					training.add(trainingDummy);
					trainingDummy = 0;
					
				}
				if (zeiten.get(j) == null)
					continue;
				
				if (zeiten.get(j).getTrainer().contains(trainer.get(i))) {
					trainingDummy++;
					if (pause == 1) {
						pauseAktiv = false;
						
					}
					if (pauseAktiv) {
						pause--;
						if (pause > 0) {
							dummy.add(new Pause(j - pause + 1, j));
							pauseAktiv = false;
							pause = 0;
						}
					} else {
						pauseAktiv = true;
						pause = 0;
					}
					
				}
			}
			trainer.get(i).setMinTrainingProTag(training);
			trainer.get(i).setPauseProTag(pausen);
			LoscheGewolltePausen(trainer.get(i));
		}
		
	}
	
	private static void LoscheGewolltePausen(Trainer trainer) {
		for (int i = 0; i < trainer.getPauseProTag().size(); i++) {
			for (int j = trainer.getPauseProTag().get(i).size() - 1; j >= 0; j--) {
				
				for (int z = trainer.getPauseProTag().get(i).get(j).getVonZeit() - 1; z <= trainer.getPauseProTag()
						.get(i).get(j).getBisZeit(); z++) {
					
					if (z > trainer.getZeiten().length)
						break;
					if (trainer.getZeiten()[z] == 0) {
						trainer.getPauseProTag().get(i).remove(j);
						break;
					}
					
				}
			}
		}
		
	}
	
	private static void TrainerMinTermineTraining(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer,
			ArrayList<Platz> plaetze) {
		for (int i = 0; i < trainer.size(); i++) {
			int zaehlerAnzTraining = 0;
			for (int j = 0; j < zeiten.size(); j++) {
				if (zeiten.get(j) != null)
					if (zeiten.get(j).getTrainer().contains(trainer.get(i))) {
					zaehlerAnzTraining++;
					}
					
			}
			
			trainer.get(i).setAktAnzTraining(zaehlerAnzTraining);
			if (trainer.get(i).getMinAnzTraining() < zaehlerAnzTraining) {
				continue;
			}
		}
		
	}
	
	private static int SpielerHatFuerAlleTermineTraining(ArrayList<Spieler> spieler, ArrayList<Trainer> trainer,
			ArrayList<Platz> plaetze, ArrayList<Zeiten> zeiten) {
		for (int i = 0; i < spieler.size(); i++) {
			if (spieler.get(i).getTrainingsAnzahl() == 1)
				continue;
			int var = 0;
			int zeit = 0;
			for (int j = 0; j < zeiten.size(); j++) {
				if (zeiten.get(j) != null)
					for (int z = 0; z < zeiten.get(j).getGruppen().size(); z++) {
					
					if (zeiten.get(j).getGruppen().get(z).getSpieler().contains(spieler.get(i))) {
						
					if (zeit + 14 > zeiten.get(j).getZeit())
						continue;
					var++;
					zeit = zeiten.get(j).getZeit();
					}
					}
					
			}
			if (var < spieler.get(i).getTrainingsAnzahl()) {
				spieler.get(i).setTrainingsAnzahlAktuell(var);
			}
			if (var > spieler.get(i).getTrainingsAnzahl()) {
				return 0;
			} // muss zaehlen
		}
		return 1;
		
	}
	
	private static ArrayList<Spieler> SpielerOhneZuo(ArrayList<Spieler> spieler, ArrayList<Zeiten> zeiten) {
		
		ArrayList<Spieler> dummy = new ArrayList<>();
		outerloop:
		for (int i = 0; i < spieler.size(); i++) {
			zeiten.size();
			
			for (int j = zeiten.size() - 1; j >= 0; j--) {
				if (zeiten.get(j) != null)
					for (int z = 0; z < zeiten.get(j).getGruppen().size(); z++) {
					
					if (zeiten.get(j).getGruppen().get(z).getSpieler().contains(spieler.get(i)))
						continue outerloop;
					
					}
					
			}
			dummy.add(spieler.get(i));
		}
		
		return dummy;
	}
	
	private static ArrayList<Zeiten> LoscheZeitenOhneTrainer(ArrayList<Zeiten> zeiten) {
		for (int i = zeiten.size() - 1; i >= 0; i--) {
			if (zeiten.get(i).getTrainer().isEmpty()) {
				zeiten.set(i, null);
			}
		}
		return zeiten;
	}
	
	private static ArrayList<ArrayList<Gruppe>> SpielerGruppeZuo(ArrayList<Spieler> spieler) {
		// Die Funktion erstellt alle Moeglichen Untergruppen und Teilt diesen alle
		// Moeglichen Spieler zu
		
		ArrayList<ArrayList<Gruppe>> gruppen = new ArrayList<ArrayList<Gruppe>>();
		
		for (int i = 0; i < Spieler.getZeitenArrayGroesse(); i++) {
			
			ArrayList<Gruppe> untergruppen = new ArrayList<Gruppe>();
			
			untergruppen.add(new Gruppe("U6", i + 1, 6, 0, 6, 'O'));
			untergruppen.add(new Gruppe("U10 SpielStaerke 7 - 9", i + 1, 5, 7, 9, 'O'));
			untergruppen.add(new Gruppe("U10 SpielStaerke 8 - 10", i + 1, 5, 8, 10, 'O'));
			
			for (int j = 11; j < 20; j++) {
				untergruppen.add(new Gruppe("M SpielStaerke " + j + " - " + (j + 2), i + 1, 5, j, (j + 2), 'm'));
				untergruppen.add(new Gruppe("W SpielStaerke " + j + " - " + (j + 2), i + 1, 5, j, (j + 2), 'w'));
			}
			
			gruppen.add(untergruppen);
			
		}
		
		for (int i = 0; i < spieler.size(); i++) {
			
			int[] dummy = spieler.get(i).getZeiten();
			
			for (int j = 0; j < Spieler.getZeitenArrayGroesse(); j++) {
				
				if (dummy[j] == 1) {
					
					ArrayList<Gruppe> untergruppen = gruppen.get(j);
					for (int z = 0; z < untergruppen.size(); z++) {
						
						if (spieler.get(i).verrechneSpielerStaerkeMitAlter() >= gruppen.get(j).get(z)
								.getSpielerStaerkeVon()
								&& spieler.get(i).verrechneSpielerStaerkeMitAlter() <= gruppen.get(j).get(z)
										.getSpielerStaerkeBis()) {
							
							if (spieler.get(i).verrechneSpielerStaerkeMitAlter() < 11) {
								gruppen.get(j).get(z).getSpieler().add(spieler.get(i));
							} else {
								if (spieler.get(i).getGeschlecht() == gruppen.get(j).get(z).getGeschlecht())
									gruppen.get(j).get(z).getSpieler().add(spieler.get(i));
							}
						}
						
					}
				}
			}
		}
		
		return gruppen;
	}
	
	private static ArrayList<ArrayList<Gruppe>> GruppenLoeschen(ArrayList<ArrayList<Gruppe>> gruppen) {
		// Diese Funktion entfernt die nicht benoetigten Gruppen aus der Gesammtliste
		// also die Untergruppen mit 0/1/2 Spielern
		
		for (int i = 0; i < gruppen.size(); i++) {
			for (int j = gruppen.get(i).size() - 1; j >= 0; j--) {
				if (gruppen.get(i).get(j).getSpieler().isEmpty()) {
					gruppen.get(i).remove(j);
					continue;
				}
				if (gruppen.get(i).get(j).getSpieler().size() == 1) {
					if (!spielerKeineGruppeZuo.contains(gruppen.get(i).get(j).getSpieler().get(0)))
						spielerKeineGruppeZuo.add(gruppen.get(i).get(j).getSpieler().get(0));
					gruppen.get(i).remove(j);
					continue;
				}
				if (gruppen.get(i).get(j).getSpieler().size() == 2) {
					if (!zweierSpielerKeineGruppeZuo.contains(gruppen.get(i).get(j).getSpieler().get(0)))
						zweierSpielerKeineGruppeZuo.add(gruppen.get(i).get(j).getSpieler().get(0));
					zweierGruppe.add(gruppen.get(i).get(j));
					gruppen.get(i).remove(j);
					continue;
				}
				
			}
		}
		
		return gruppen;
	}
	
	private static int calcGruppenAnz(int gruppenGroesse, Gruppe gruppe) {
		int zaehler = 0;
		
		if (gruppe != null) {
			int dummy2 = (int) Math.pow(2, gruppe.getSpieler().size());
			
			String[] dummy = new String[dummy2];
			
			for (int i = 1; i < dummy2; i++) {
				dummy[i] = Integer.toBinaryString(i);
			}
			for (int i = 0; i < dummy.length; i++) {
				if (dummy[i] == null)
					continue;
				long dummyZaehler = dummy[i].chars().filter(ch -> ch == '1').count();
				if (dummyZaehler == gruppenGroesse)
					zaehler++;
			}
			
		}
		
		return zaehler;
	}
	
	private static ArrayList<Gruppe> setDreierGruppen(Gruppe gruppe) {
		
		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();
		int gruppenAnz = calcGruppenAnz(3, gruppe);
		for (int i = 0; i < gruppenAnz; i++) {
			dummy.add(new Gruppe((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
					gruppe.getSpielerStaerkeVon(), gruppe.getSpielerStaerkeBis(), gruppe.getGeschlecht()));
		}
		
		int i = 0;
		
		for (int j = 0; j < gruppe.getSpieler().size(); j++) {
			if (j > gruppe.getSpieler().size())
				continue;
			for (int k = 1; k < gruppe.getSpieler().size(); k++) {
				if (k <= j)
					k = j + 1;
				if (k > gruppe.getSpieler().size())
					continue;
				for (int l = 2; l < gruppe.getSpieler().size(); l++) {
					if (l <= k)
						l = k + 1;
					if (l >= gruppe.getSpieler().size())
						continue;
					dummy.get(i).addSpieler(gruppe.getSpieler().get(j));
					dummy.get(i).addSpieler(gruppe.getSpieler().get(k));
					dummy.get(i).addSpieler(gruppe.getSpieler().get(l));
					i++;
				}
			}
		}
		
		return dummy;
		
	}
	
	private static ArrayList<Gruppe> setViererGruppen(Gruppe gruppe) {
		
		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();
		int gruppenAnz = calcGruppenAnz(4, gruppe);
		for (int i = 0; i < gruppenAnz; i++) {
			dummy.add(new Gruppe((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
					gruppe.getSpielerStaerkeVon(), gruppe.getSpielerStaerkeBis(), gruppe.getGeschlecht()));
		}
		
		int i = 0;
		
		for (int j = 0; j < gruppe.getSpieler().size(); j++) {
			if (j > gruppe.getSpieler().size())
				continue;
			for (int k = 1; k < gruppe.getSpieler().size(); k++) {
				if (k <= j)
					k = j + 1;
				if (k > gruppe.getSpieler().size())
					continue;
				for (int l = 2; l < gruppe.getSpieler().size(); l++) {
					if (l <= k)
						l = k + 1;
					if (l >= gruppe.getSpieler().size())
						continue;
					for (int z = 2; z < gruppe.getSpieler().size(); z++) {
						if (z <= l)
							z = l + 1;
						if (z >= gruppe.getSpieler().size())
							continue;
						dummy.get(i).addSpieler(gruppe.getSpieler().get(j));
						dummy.get(i).addSpieler(gruppe.getSpieler().get(k));
						dummy.get(i).addSpieler(gruppe.getSpieler().get(l));
						dummy.get(i).addSpieler(gruppe.getSpieler().get(z));
						i++;
					}
				}
			}
		}
		return dummy;
	}
	
	private static ArrayList<Gruppe> setFuenferGruppen(Gruppe gruppe) {
		
		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();
		int gruppenAnz = calcGruppenAnz(5, gruppe);
		for (int i = 0; i < gruppenAnz; i++) {
			dummy.add(new Gruppe((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
					gruppe.getSpielerStaerkeVon(), gruppe.getSpielerStaerkeBis(), gruppe.getGeschlecht()));
		}
		
		int i = 0;
		
		for (int j = 0; j < gruppe.getSpieler().size(); j++) {
			if (j > gruppe.getSpieler().size())
				continue;
			for (int k = 1; k < gruppe.getSpieler().size(); k++) {
				if (k <= j)
					k = j + 1;
				if (k > gruppe.getSpieler().size())
					continue;
				for (int l = 2; l < gruppe.getSpieler().size(); l++) {
					if (l <= k)
						l = k + 1;
					if (l >= gruppe.getSpieler().size())
						continue;
					for (int z = 2; z < gruppe.getSpieler().size(); z++) {
						if (z <= l)
							z = l + 1;
						if (z >= gruppe.getSpieler().size())
							continue;
						for (int d = 2; d < gruppe.getSpieler().size(); d++) {
							if (d <= z)
								d = z + 1;
							if (d >= gruppe.getSpieler().size())
								continue;
							dummy.get(i).addSpieler(gruppe.getSpieler().get(j));
							dummy.get(i).addSpieler(gruppe.getSpieler().get(k));
							dummy.get(i).addSpieler(gruppe.getSpieler().get(l));
							dummy.get(i).addSpieler(gruppe.getSpieler().get(z));
							dummy.get(i).addSpieler(gruppe.getSpieler().get(d));
							i++;
						}
					}
				}
			}
		}
		return dummy;
	}
	
	private static ArrayList<Gruppe> setSechserGruppen(Gruppe gruppe) {
		
		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();
		int gruppenAnz = calcGruppenAnz(5, gruppe);
		for (int i = 0; i < gruppenAnz; i++) {
			dummy.add(new Gruppe((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
					gruppe.getSpielerStaerkeVon(), gruppe.getSpielerStaerkeBis(), gruppe.getGeschlecht()));
		}
		
		int i = 0;
		
		for (int j = 0; j < gruppe.getSpieler().size(); j++) {
			if (j > gruppe.getSpieler().size())
				continue;
			for (int k = 1; k < gruppe.getSpieler().size(); k++) {
				if (k <= j)
					k = j + 1;
				if (k > gruppe.getSpieler().size())
					continue;
				for (int l = 2; l < gruppe.getSpieler().size(); l++) {
					if (l <= k)
						l = k + 1;
					if (l >= gruppe.getSpieler().size())
						continue;
					for (int z = 2; z < gruppe.getSpieler().size(); z++) {
						if (z <= l)
							z = l + 1;
						if (z >= gruppe.getSpieler().size())
							continue;
						for (int d = 2; d < gruppe.getSpieler().size(); d++) {
							if (d <= z)
								d = z + 1;
							if (d >= gruppe.getSpieler().size())
								continue;
							for (int e = 2; e < gruppe.getSpieler().size(); e++) {
								if (e <= d)
									e = d + 1;
								if (e >= gruppe.getSpieler().size())
									continue;
								dummy.get(i).addSpieler(gruppe.getSpieler().get(j));
								dummy.get(i).addSpieler(gruppe.getSpieler().get(k));
								dummy.get(i).addSpieler(gruppe.getSpieler().get(l));
								dummy.get(i).addSpieler(gruppe.getSpieler().get(z));
								dummy.get(i).addSpieler(gruppe.getSpieler().get(d));
								dummy.get(i).addSpieler(gruppe.getSpieler().get(e));
								
								i++;
							}
						}
					}
				}
			}
		}
		return dummy;
	}
	
	private static ArrayList<ArrayList<Gruppe>> GruppenSplitten(ArrayList<ArrayList<Gruppe>> gruppen) {
		
		for (int i = 0; i < gruppen.size(); i++) {
			for (int j = 0; j < gruppen.get(i).size(); j++) {
				
				if (gruppen.get(i).get(j).getSpieler().size() <= 3)
					continue;
				
				else if (gruppen.get(i).get(j).getSpieler().size() == 4) {
					ArrayList<Gruppe> dummy = setDreierGruppen(gruppen.get(i).get(j));
					for (int k = 0; k < dummy.size(); k++) {
						gruppen.get(i).add(dummy.get(k));
					}
				}
				
				else if (gruppen.get(i).get(j).getSpieler().size() == 5) {
					ArrayList<Gruppe> dummy = setDreierGruppen(gruppen.get(i).get(j));
					for (int k = 0; k < dummy.size(); k++) {
						gruppen.get(i).add(dummy.get(k));
					}
					dummy = setViererGruppen(gruppen.get(i).get(j));
					for (int k = 0; k < dummy.size(); k++) {
						gruppen.get(i).add(dummy.get(k));
					}
				}
				
				else if (gruppen.get(i).get(j).getSpieler().size() >= 6) {
					if (gruppen.get(i).get(j).getMaxAnzahlSpieler() == 6) {
						ArrayList<Gruppe> dummy = setDreierGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
						dummy = setViererGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
						dummy = setFuenferGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
						dummy = setSechserGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
					} else {
						ArrayList<Gruppe> dummy = setDreierGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
						dummy = setViererGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
						dummy = setFuenferGruppen(gruppen.get(i).get(j));
						for (int k = 0; k < dummy.size(); k++) {
							gruppen.get(i).add(dummy.get(k));
						}
					}
					
				}
				Gruppe gruppe = gruppen.get(i).get(j);
				
				gruppen.get(i).get(j).setName("1/" + gruppe.getName());
				
			}
			
		}
		return gruppen;
		
	}
	
	private static ArrayList<ArrayList<Gruppe>> WiederBefuellungZweierGruppe(ArrayList<ArrayList<Gruppe>> gruppen) {
		// wenn in der Kontrollfunktion herraus kommt das ein Zweierspieler in keiner
		// anderen Gruppe ist werden seine Gruppen wieder in die Hauptgruppe aufgenommen
		
		if (zweierSpielerKeineGruppeZuo.isEmpty())
			return gruppen;
		
		ArrayList<Gruppe> dummy = new ArrayList<Gruppe>();
		
		for (int i = zweierSpielerKeineGruppeZuo.size() - 1; i >= 0; i--) {
			for (int j = zweierGruppe.size() - 1; j >= 0; j--) {
				
				if (zweierGruppe.get(j).getSpieler().contains(zweierSpielerKeineGruppeZuo.get(i))) {
					dummy.add(zweierGruppe.get(j));
					
				}
				
				zweierGruppe.remove(j);
				
			}
			
			zweierSpielerKeineGruppeZuo.remove(i);
		}
		gruppen.add(dummy);
		
		return gruppen;
	}
	
	private static ArrayList<ArrayList<Gruppe>> KontrolleSpielerBereitsZuo(ArrayList<ArrayList<Gruppe>> gruppen) {
		// Kontrolliert die Liste der Einzelspieler und Zweierspieler ob diese Spieler
		// in einer anderen Gruppe eingeteillt sind
		
		if (spielerKeineGruppeZuo.isEmpty() && zweierSpielerKeineGruppeZuo.isEmpty())
			return gruppen;
		for (int i = 0; i < gruppen.size(); i++) {
			for (int j = gruppen.get(i).size() - 1; j >= 0; j--) {
				for (int k = 0; k < gruppen.get(i).get(j).getSpieler().size(); k++) {
					if (spielerKeineGruppeZuo.contains(gruppen.get(i).get(j).getSpieler().get(k)))
						spielerKeineGruppeZuo.remove(gruppen.get(i).get(j).getSpieler().get(k));
					if (zweierSpielerKeineGruppeZuo.contains(gruppen.get(i).get(j).getSpieler().get(k)))
						zweierSpielerKeineGruppeZuo.remove(gruppen.get(i).get(j).getSpieler().get(k));
					
					if (spielerKeineGruppeZuo.isEmpty() && zweierSpielerKeineGruppeZuo.isEmpty())
						return gruppen;
				}
				
			}
		}
		return gruppen;
	}
	
	private static ArrayList<Zeiten> PruefePlatzUndTrainer(ArrayList<Platz> plaetze,
			// Die Funktion Erstellt eine neue Liste in dieser werden alle Plaetze, Trainer
			// und Untergruppen verglichen und in die entsprechenden Felder eingeordnet
			
			ArrayList<Trainer> trainer, ArrayList<ArrayList<Gruppe>> gruppen) {
		
		for (int i = 0; i < Spieler.getZeitenArrayGroesse(); i++) {
			zeiten.add(new Zeiten(i));
		}
		
		for (int i = 0; i < zeiten.size(); i++) {
			zeiten.get(i).setGruppen(gruppen.get(i));
		}
		
		for (int i = 0; i < trainer.size(); i++) {
			for (int j = 0; j < zeiten.size(); j++) {
				if (trainer.get(i).getZeiten()[j] == 1) {
					zeiten.get(j).getTrainer().add(trainer.get(i));
				}
			}
		}
		
		for (int i = 0; i < plaetze.size(); i++) {
			for (int j = 0; j < zeiten.size(); j++) {
				if (plaetze.get(i).getZeiten()[j] == 1) {
					zeiten.get(j).getPlatz().add(plaetze.get(i));
				}
				
			}
		}
		
		for (int i = 0; i < zeiten.size(); i++) {
			
			Collections.sort(zeiten.get(i).getTrainer(), new Comparator<Trainer>() {
				@Override
				public int compare(Trainer t1, Trainer t2) {
					return t1.getPrio() - t2.getPrio(); // Ascending
				}
				
			});
			
			if (zeiten.get(i).getTrainer().size() > zeiten.get(i).getTrainingsGruppenMoeglichkeiten()) {
				int trainerSize = zeiten.get(i).getTrainer().size();
				int moeglicheTrainer = zeiten.get(i).getTrainingsGruppenMoeglichkeiten();
				
				for (int k = zeiten.get(i).getTrainer().size() - 1; k > trainerSize - moeglicheTrainer; k--) {
					zeiten.get(i).getTrainer().remove(k);
				}
				
			}
			
		}
		
		return zeiten;
	}
	
}
