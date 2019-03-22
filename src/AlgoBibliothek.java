import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class AlgoBibliothek {
	
	private static ArrayList<SpielerModel> spielerHatKeinTrainer = new ArrayList<SpielerModel>();
	
	public static ArrayList<SpielerModel> getSpielerHatKeinTrainer() {
		return spielerHatKeinTrainer;
	}
	
	private static ArrayList<SpielerModel> spielerKeineGruppeZuo = new ArrayList<SpielerModel>();
	
	public static ArrayList<SpielerModel> getSpielerKeineGruppeZuo() {
		return spielerKeineGruppeZuo;
	}
	
	private static ArrayList<SpielerModel> zweierSpielerKeineGruppeZuo = new ArrayList<SpielerModel>();
	
	public static ArrayList<SpielerModel> getZweierSpielerKeineGruppeZuo() {
		return zweierSpielerKeineGruppeZuo;
	}
	
	private static ArrayList<GruppeModel> zweierGruppe = new ArrayList<GruppeModel>();
	
	public static ArrayList<GruppeModel> getZweierGruppe() {
		return zweierGruppe;
	}
	
	private static ArrayList<ArrayList<ZeitEndModel>> vorLaeufigeMoeglichkeiten = new ArrayList<ArrayList<ZeitEndModel>>();
	
	public static ArrayList<ArrayList<ZeitEndModel>> getVorlaeufigeMoeglichkeiten() {
		return vorLaeufigeMoeglichkeiten;
	}
	
	private static ArrayList<ZeitenModel> zeiten = new ArrayList<ZeitenModel>();
	
	public static ArrayList<ZeitenModel> getZeiten() {
		return zeiten;
	}
	
	private static ArrayList<ZeitenModel> zeitenKopie = new ArrayList<ZeitenModel>();
	
	@SuppressWarnings("unchecked")
	public static ArrayList<ZeitenModel> ErstellePlan(ArrayList<SpielerModel> spieler, ArrayList<TrainerModel> trainer,
			ArrayList<Platz> plaetze) {
		
		ArrayList<ArrayList<GruppeModel>> gruppen = SpielerGruppeZuo(spieler);
		gruppen = GruppenLoeschen(gruppen);
		gruppen = GruppenSplitten(gruppen);
		gruppen = KontrolleSpielerBereitsZuo(gruppen);
		gruppen = WiederBefuellungZweierGruppe(gruppen);
		gruppen = KontrolleSpielerBereitsZuo(gruppen);// ein zweites Mal wegen Spieler ohne Zuo
		zeiten = PruefePlatzUndTrainer(plaetze, trainer, gruppen);
		zeiten = LoscheZeitenOhneTrainer(zeiten);
		spielerHatKeinTrainer = SpielerOhneZuo(spieler, zeiten);
		
		zeitenKopie = (ArrayList<ZeitenModel>) zeiten.clone();
		
		Algorythmus(spieler, trainer, plaetze);
		
		return zeiten;
		
	}
	
	public static ArrayList<ZeitEndModel> Algorythmus(ArrayList<SpielerModel> spieler, ArrayList<TrainerModel> trainer, ArrayList<Platz> plaetze) {
		
		SpielerOhneZuo(spieler, zeitenKopie);
		TrainerPauseZwischenTrainingszeitenProTag(trainer);
		
		vorLaeufigeMoeglichkeiten = gruppenZuordnen(spieler, trainer, plaetze);
		
		return berechneHash(vorLaeufigeMoeglichkeiten);
		
	}
	
	@SuppressWarnings({ "unchecked" })
	private static ArrayList<ZeitEndModel> berechneHash(ArrayList<ArrayList<ZeitEndModel>> endZeiten) {
		ArrayList<ArrayList<ZeitEndModel>> vorlaeufigeClone = (ArrayList<ArrayList<ZeitEndModel>>) endZeiten.clone();
		ArrayList<ZeitEndModel> besteMoeglichkeit = new ArrayList<>();
		Hash niedrigsterHash = new Hash(1000000000);
		
		for (int i = 0; i < vorlaeufigeClone.size(); i++) {
			Hash dummy = new Hash(0);
			TrainerMinTermineTraining(TrainerModel.getAlleTrainer(), vorlaeufigeClone.get(i));
			for (int j = 0; j < TrainerModel.getAlleTrainer().size(); j++) {
				if (TrainerModel.getAlleTrainer().get(j).getAktAnzTraining() - TrainerModel.getAlleTrainer().get(j).getMinAnzTraining() == 0) dummy.addHash(0);
				else if (TrainerModel.getAlleTrainer().get(j).getAktAnzTraining() - TrainerModel.getAlleTrainer().get(j).getMinAnzTraining() == 1) dummy.addHash(1);
				else if (TrainerModel.getAlleTrainer().get(j).getAktAnzTraining() - TrainerModel.getAlleTrainer().get(j).getMinAnzTraining() < 1) dummy.addHash(5);
				else if (TrainerModel.getAlleTrainer().get(j).getAktAnzTraining() - TrainerModel.getAlleTrainer().get(j).getMinAnzTraining() < 3) dummy.addHash(10);
				
				for (int k = 0; k < 6; k++) {
					if (TrainerModel.getAlleTrainer().get(j).getPauseProTag().get(k).size() < 1) dummy.addHash(0);
					else if (TrainerModel.getAlleTrainer().get(j).getPauseProTag().get(k).size() == 1) dummy.addHash(4);
					else if (TrainerModel.getAlleTrainer().get(j).getPauseProTag().get(k).size() == 2) dummy.addHash(5);
					else if (TrainerModel.getAlleTrainer().get(j).getPauseProTag().get(k).size() > 2) dummy.addHash(2);
				}
			}
			
			for (int j = 0; j < SpielerModel.getAlleSpieler().size(); j++) {
				try {
					SpielerHatFuerAlleTermineTraining(SpielerModel.getAlleSpieler(), vorlaeufigeClone.get(i));
					if (SpielerModel.getAlleSpieler().get(j).getTrainingsAnzahlAktuell() < SpielerModel.getAlleSpieler().get(j).getTrainingsAnzahl()) dummy.addHash(3);
				} catch (ZuVieleTrainingsBeiSpieler e) {
					e.printStackTrace();
				}
			}
			
			if (niedrigsterHash.getHash() > dummy.getHash()) {
				besteMoeglichkeit = vorlaeufigeClone.get(i);
			}
			
		}
		return besteMoeglichkeit;
		
	}
	
	public static int anzUntergruppen() {
		int zaehler = 0;
		for (int i = 0; i < zeiten.size(); i++) {
			if (zeiten.get(i) == null) continue;
			zaehler += zeiten.get(i).getGruppen().size();
		}
		return zaehler;
	}
	
	public static GruppeModel getUntergruppeById(int var) {
		int zaehler = 0;
		
		for (int i = 0; i < zeiten.size(); i++) {
			for (int j = 0; j < zeiten.get(i).getGruppen().size(); j++) {
				int indexGruppe = 0;
				if (zeiten.get(i) == null) continue;
				zaehler++;
				indexGruppe++;
				
				if (zaehler == var) {
					GruppeModel dummy = zeiten.get(i).getGruppen().get(indexGruppe);
					return dummy;
				}
			}
		}
		
		return null;
	}
	
	@SuppressWarnings("unchecked")
	public static ArrayList<ArrayList<ZeitEndModel>> gruppenZuordnen(ArrayList<SpielerModel> spieler, ArrayList<TrainerModel> trainer, ArrayList<Platz> plaetze) {
		
		ArrayList<ArrayList<ZeitEndModel>> dummy = new ArrayList<ArrayList<ZeitEndModel>>();
		int anzUntergruppen = anzUntergruppen();
		
		naechsteMoeglichkeit:
		for (int i2 = 0; i2 < anzUntergruppen; i2++) {
			
			ArrayList<ZeitEndModel> dummyInnenListe = new ArrayList<ZeitEndModel>();
			
			ArrayList<SpielerModel> copyAlleSpieler = (ArrayList<SpielerModel>) SpielerModel.getAlleSpieler().clone();
			for (int i = 0; i < spielerHatKeinTrainer.size(); i++) {
				copyAlleSpieler.remove(spielerHatKeinTrainer.get(i));
			}
			
			ArrayList<SpielerModel> copyAlleSpielerMehrTraining = (ArrayList<SpielerModel>) SpielerModel.getAlleSpielerMehrTraining().clone();
			for (int i = 0; i < spielerHatKeinTrainer.size(); i++) {
				copyAlleSpielerMehrTraining.remove(spielerHatKeinTrainer.get(i));
			}
			
			int reset = i2;
			
			for (int i = 0; i < 2; i++) {
				
				for (int j = reset; j < SpielerModel.getZeitenArrayGroesse(); j++) {
					
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
						ZeitEndModel zeitEndDummy = new ZeitEndModel(zeiten.get(j).getTrainer().get(trainerZaehler), zeiten.get(j).getPlatz().get(platzZaehler), zeiten.get(j).getGruppen().get(k), (j));
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
										System.out.println("Stimmt wieder was nicht mit der befuellung, beim removen wird aus einer der beiden gesammt SpielerListen wird die exeption geworffen, oberer Teil");
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
		
		@SuppressWarnings("unused")
		ArrayList<ArrayList<ZeitEndModel>> ausgabe = new ArrayList<ArrayList<ZeitEndModel>>();
		
		for (int i = 0; i < dummy.size(); i++) {
			for (int j = 0; j < dummy.get(i).size(); j++) {
				// if()break;
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
	
	private static void TrainerPauseZwischenTrainingszeitenProTag(ArrayList<TrainerModel> trainer) {
		
		for (int i = 0; i < trainer.size(); i++) {
			
			ArrayList<ArrayList<PauseModel>> pausen = new ArrayList<ArrayList<PauseModel>>();
			ArrayList<PauseModel> dummy = new ArrayList<PauseModel>();
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
					dummy = new ArrayList<PauseModel>();
					
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
							dummy.add(new PauseModel(j - pause + 1, j));
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
	
	private static void LoscheGewolltePausen(TrainerModel trainer) {
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
	
	private static void TrainerMinTermineTraining(ArrayList<TrainerModel> trainer, ArrayList<ZeitEndModel> zeitEnd) {
		
		for (int j = 0; j < trainer.size(); j++) {
			int zaehlerAnzTraining = 0;
			for (int i = 0; i < zeitEnd.size(); i++) {
				if (zeitEnd != null) {
					if (zeitEnd.get(i).getTrainer().equals(trainer.get(j))) {
						zaehlerAnzTraining++;
					}
				}
			}
			trainer.get(j).setAktAnzTraining(zaehlerAnzTraining);
		}
	}
	
	private static void SpielerHatFuerAlleTermineTraining(ArrayList<SpielerModel> spieler, ArrayList<ZeitEndModel> zeiten) throws ZuVieleTrainingsBeiSpieler {
		for (int i = 0; i < spieler.size(); i++) {
			if (spieler.get(i).getTrainingsAnzahl() == 1)
				continue;
			int var = 0;
			int zeit = 0;
			for (int j = 0; j < zeiten.size(); j++) {
				if (zeiten.get(j) != null)
					for (int z = 0; z < zeiten.get(j).getGruppe().getSpieler().size(); z++) {
					
					if (zeiten.get(j).getGruppe().getSpieler().contains(spieler.get(i))) {
						
					if (zeit + 14 > zeiten.get(j).getZeit())
						continue;
					var++;
					zeit = zeiten.get(j).getZeit();
					}
					}
					
			}
			if (var <= spieler.get(i).getTrainingsAnzahl()) {
				spieler.get(i).setTrainingsAnzahlAktuell(var);
			}
			if (var > spieler.get(i).getTrainingsAnzahl()) {
				throw new ZuVieleTrainingsBeiSpieler();
			}
			
		}
	}
	
	private static ArrayList<SpielerModel> SpielerOhneZuo(ArrayList<SpielerModel> spieler, ArrayList<ZeitenModel> zeiten) {
		
		ArrayList<SpielerModel> dummy = new ArrayList<>();
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
	
	private static ArrayList<ZeitenModel> LoscheZeitenOhneTrainer(ArrayList<ZeitenModel> zeiten) {
		for (int i = zeiten.size() - 1; i >= 0; i--) {
			if (zeiten.get(i).getTrainer().isEmpty()) {
				zeiten.set(i, null);
			}
		}
		return zeiten;
	}
	
	private static ArrayList<ArrayList<GruppeModel>> SpielerGruppeZuo(ArrayList<SpielerModel> spieler) {
		// Die Funktion erstellt alle Moeglichen Untergruppen und Teilt diesen alle
		// Moeglichen Spieler zu
		
		ArrayList<ArrayList<GruppeModel>> gruppen = new ArrayList<ArrayList<GruppeModel>>();
		
		for (int i = 0; i < SpielerModel.getZeitenArrayGroesse(); i++) {
			
			ArrayList<GruppeModel> untergruppen = new ArrayList<GruppeModel>();
			
			untergruppen.add(new GruppeModel("U6", i + 1, 6, 0, 6, 'O'));
			untergruppen.add(new GruppeModel("U10 SpielStaerke 7 - 9", i + 1, 5, 7, 9, 'O'));
			untergruppen.add(new GruppeModel("U10 SpielStaerke 8 - 10", i + 1, 5, 8, 10, 'O'));
			
			for (int j = 11; j < 20; j++) {
				untergruppen.add(new GruppeModel("M SpielStaerke " + j + " - " + (j + 2), i + 1, 5, j, (j + 2), 'm'));
				untergruppen.add(new GruppeModel("W SpielStaerke " + j + " - " + (j + 2), i + 1, 5, j, (j + 2), 'w'));
			}
			
			gruppen.add(untergruppen);
			
		}
		
		for (int i = 0; i < spieler.size(); i++) {
			
			int[] dummy = spieler.get(i).getZeiten();
			
			for (int j = 0; j < SpielerModel.getZeitenArrayGroesse(); j++) {
				
				if (dummy[j] == 1) {
					
					ArrayList<GruppeModel> untergruppen = gruppen.get(j);
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
	
	private static ArrayList<ArrayList<GruppeModel>> GruppenLoeschen(ArrayList<ArrayList<GruppeModel>> gruppen) {
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
	
	private static int calcGruppenAnz(int gruppenGroesse, GruppeModel gruppe) {
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
	
	private static ArrayList<GruppeModel> setDreierGruppen(GruppeModel gruppe) {
		
		ArrayList<GruppeModel> dummy = new ArrayList<GruppeModel>();
		int gruppenAnz = calcGruppenAnz(3, gruppe);
		for (int i = 0; i < gruppenAnz; i++) {
			dummy.add(new GruppeModel((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
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
	
	private static ArrayList<GruppeModel> setViererGruppen(GruppeModel gruppe) {
		
		ArrayList<GruppeModel> dummy = new ArrayList<GruppeModel>();
		int gruppenAnz = calcGruppenAnz(4, gruppe);
		for (int i = 0; i < gruppenAnz; i++) {
			dummy.add(new GruppeModel((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
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
	
	private static ArrayList<GruppeModel> setFuenferGruppen(GruppeModel gruppe) {
		
		ArrayList<GruppeModel> dummy = new ArrayList<GruppeModel>();
		int gruppenAnz = calcGruppenAnz(5, gruppe);
		for (int i = 0; i < gruppenAnz; i++) {
			dummy.add(new GruppeModel((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
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
	
	private static ArrayList<GruppeModel> setSechserGruppen(GruppeModel gruppe) {
		
		ArrayList<GruppeModel> dummy = new ArrayList<GruppeModel>();
		int gruppenAnz = calcGruppenAnz(5, gruppe);
		for (int i = 0; i < gruppenAnz; i++) {
			dummy.add(new GruppeModel((i + 1) + "/" + gruppe.getName(), gruppe.getStunde(), gruppe.getMaxAnzahlSpieler(),
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
	
	private static ArrayList<ArrayList<GruppeModel>> GruppenSplitten(ArrayList<ArrayList<GruppeModel>> gruppen) {
		
		for (int i = 0; i < gruppen.size(); i++) {
			for (int j = 0; j < gruppen.get(i).size(); j++) {
				
				if (gruppen.get(i).get(j).getSpieler().size() <= 3)
					continue;
				
				else if (gruppen.get(i).get(j).getSpieler().size() == 4) {
					ArrayList<GruppeModel> dummy = setDreierGruppen(gruppen.get(i).get(j));
					for (int k = 0; k < dummy.size(); k++) {
						gruppen.get(i).add(dummy.get(k));
					}
				}
				
				else if (gruppen.get(i).get(j).getSpieler().size() == 5) {
					ArrayList<GruppeModel> dummy = setDreierGruppen(gruppen.get(i).get(j));
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
						ArrayList<GruppeModel> dummy = setDreierGruppen(gruppen.get(i).get(j));
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
						ArrayList<GruppeModel> dummy = setDreierGruppen(gruppen.get(i).get(j));
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
				GruppeModel gruppe = gruppen.get(i).get(j);
				
				gruppen.get(i).get(j).setName("1/" + gruppe.getName());
				
			}
			
		}
		return gruppen;
		
	}
	
	private static ArrayList<ArrayList<GruppeModel>> WiederBefuellungZweierGruppe(ArrayList<ArrayList<GruppeModel>> gruppen) {
		// wenn in der Kontrollfunktion herraus kommt das ein Zweierspieler in keiner
		// anderen Gruppe ist werden seine Gruppen wieder in die Hauptgruppe aufgenommen
		
		if (zweierSpielerKeineGruppeZuo.isEmpty())
			return gruppen;
		
		ArrayList<GruppeModel> dummy = new ArrayList<GruppeModel>();
		
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
	
	private static ArrayList<ArrayList<GruppeModel>> KontrolleSpielerBereitsZuo(ArrayList<ArrayList<GruppeModel>> gruppen) {
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
	
	public static int[] alleMoeglichenTrainingsZeiten(ArrayList<Platz> plaetze, ArrayList<TrainerModel> trainer) {
		int[] dummy = new int[SpielerModel.getZeitenArrayGroesse()];
		
		for (int i = 0; i < trainer.size(); i++) {
			for (int j = 0; j < SpielerModel.getZeitenArrayGroesse(); j++) {
				if (trainer.get(i).getZeiten()[j] == 1) dummy[j] = 1;
			}
		}
		
		for (int i = 0; i < plaetze.size(); i++) {
			for (int j = 0; j < SpielerModel.getZeitenArrayGroesse(); j++) {
				if(plaetze.get(i).getZeiten()[j] == 0) dummy[j] = 0;
			}
		}
		
		return dummy;
	}
	
	private static ArrayList<ZeitenModel> PruefePlatzUndTrainer(ArrayList<Platz> plaetze, ArrayList<TrainerModel> trainer, ArrayList<ArrayList<GruppeModel>> gruppen) {
		
		for (int i = 0; i < SpielerModel.getZeitenArrayGroesse(); i++) {
			zeiten.add(new ZeitenModel(i));
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
			
			Collections.sort(zeiten.get(i).getTrainer(), new Comparator<TrainerModel>() {
				@Override
				public int compare(TrainerModel t1, TrainerModel t2) {
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

class ZuVieleTrainingsBeiSpieler extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	ZuVieleTrainingsBeiSpieler() {
		super("Min ein Spieler hat zu oft Training");
	}
}
