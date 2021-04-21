
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;


public class Outil implements Serializable{


	/*attributs*/

	private static final long serialVersionUID = 2L;

	private String moyenGenerique; 
    private String detailMoyen;
    private boolean utilisationAuto;
    private String fabricant;
    private String refFabricant;
    private String numSerie;
    private String outilsAssocies;
    private int indiceOutil;
    private String refPlanOuLogiciel;
    private int versionPlanOuLogiciel;
    private String nomLogiciel;
    private String raccourciEmplacement;
    private String maintenance;
    private String refCalibration;
    private int quantite;
	private String lienPhoto;

	/*Liste des paramètres de l'outil*/
	private ArrayList<String> listParam;

	/*Liste des "titres" des paramètres des outils de test*/
	private static ArrayList<String> listParamTitle;

	/*Liste des paramètres ajoutés manuellement et stockées*/
	private ArrayList<String> addedParam;

	
	/*Constructeurs*/
    public Outil(String moyenGenerique, String detailMoyen){
        this.moyenGenerique = moyenGenerique;
		this.detailMoyen = detailMoyen;
		this.utilisationAuto = false;
		this.maintenance = "N/A";
		this.listParam = new ArrayList<String>();
		this.addedParam = new ArrayList<String>();
    }



	/*Getter et setter*/

	public String getMoyenGenerique() {
		return moyenGenerique;
	}


	public void setMoyenGenerique(String moyenGenerique) {
		this.moyenGenerique = moyenGenerique;
	}

	public String getDetailMoyen() {
		return detailMoyen;
	}

	public void setDetailMoyen(String detailMoyen) {
		this.detailMoyen = detailMoyen;
	}

	public boolean isUtilisationAuto() {
		return utilisationAuto;
	}

	public void setUtilisationAuto(boolean utilisationAuto) {
		this.utilisationAuto = utilisationAuto;
	}

	public String getFabricant() {
		return fabricant;
	}

	public void setFabricant(String fabricant) {
		this.fabricant = fabricant;
	}

	public String getRefFabricant() {
		return refFabricant;
	}

	public void setRefFabricant(String refFabricant) {
		this.refFabricant = refFabricant;
	}

	public String getNumSerie() {
		return numSerie;
	}

	public void setNumSerie(String numSerie) {
		this.numSerie = numSerie;
	}

	public String getOutilsAssocies() {
		return outilsAssocies;
	}

	public void setOutilsAssocies(String outilsAssocies) {
		this.outilsAssocies = outilsAssocies;
	}

	public int getIndiceOutil() {
		return indiceOutil;
	}

	public void setIndiceOutil(int indiceOutil) {
		this.indiceOutil = indiceOutil;
	}

	public String getRefPlanOuLogiciel() {
		return refPlanOuLogiciel;
	}

	public void setRefPlanOuLogiciel(String refPlanOuLogiciel) {
		this.refPlanOuLogiciel = refPlanOuLogiciel;
	}

	public int getVersionPlanOuLogiciel() {
		return versionPlanOuLogiciel;
	}

	public void setVersionPlanOuLogiciel(int versionPlanOuLogiciel) {
		this.versionPlanOuLogiciel = versionPlanOuLogiciel;
	}

	public String getNomLogiciel() {
		return nomLogiciel;
	}

	public void setNomLogiciel(String nomLogiciel) {
		this.nomLogiciel = nomLogiciel;
	}

	public String getRaccourciEmplacement() {
		return raccourciEmplacement;
	}

	public void setRaccourciEmplacement(String raccourciEmplacement) {
		this.raccourciEmplacement = raccourciEmplacement;
	}

	public String getMaintenance() {
		return maintenance;
	}

	public void setMaintenance(String maintenance) {
		this.maintenance = maintenance;
	}

	public String getRefCalibration() {
		return refCalibration;
	}

	public void setRefCalibration(String refCalibration) {
		this.refCalibration = refCalibration;
	}

	public int getQuantite() {
		return quantite;
	}

	public void setQuantite(int quantite) {
		this.quantite = quantite;
	}

	public String getLienPhoto() {
		return lienPhoto;
	}

	public void setLienPhoto(String lienPhoto) {
		this.lienPhoto = lienPhoto;
	}


	public String toString(){
		return this.moyenGenerique + " " + this.detailMoyen;
	}



	/*Fonctions relatives à la liste de paramètres*/

	/**
	 * Crée la liste contenant les valeurs des attributs de l'Outil 
	 * en chaine de caractère dans l'optique du futur affichage
	 */
	public void setListParam(){
		this.listParam = new ArrayList<String>();
		listParam.add(this.moyenGenerique);
		listParam.add("" + this.quantite);
		if(this.isUtilisationAuto()){
			listParam.add("AUTO");
		}
		else{
			listParam.add("MANUEL");
		}
		listParam.add(this.detailMoyen);
		listParam.add(this.fabricant);
		listParam.add(this.refFabricant);
		listParam.add(this.numSerie);
		listParam.add(this.outilsAssocies);
		listParam.add("" + this.indiceOutil);
		listParam.add(this.refPlanOuLogiciel);
		listParam.add("" + this.versionPlanOuLogiciel);
		listParam.add(this.nomLogiciel);
		listParam.add(this.raccourciEmplacement);
		listParam.add(this.maintenance);
		listParam.add(this.refCalibration);
		listParam.add(this.lienPhoto);
		/*Mise en place et ajout des paramètres correspondant à des titres ajoutés manuellements*/
		setAddedParams();
		for(int i=0; i<unserializeTitles().size();i++){
			listParam.add(addedParam.get(i));
		}
	}

	/**
	 * @return le liste contenant les valeurs des attributs de l'Outil en chaine de caractère
	 */
	public ArrayList<String> getListParam(){
		setListParam();
		return this.listParam;
	}

	/**
	 * Crée la liste des "titres" de chaque attribut de l'Outil qui sont utilisés dans l'affichage
	 */
	public static void setListParamTitle(){
		/*Attributs de base*/
		listParamTitle = new ArrayList<String>();
		listParamTitle.add("Moyens Génériques");
		listParamTitle.add("Quantité");
		listParamTitle.add("Utilisation pour test Manuel ou Auto (déverminage)");
		listParamTitle.add("Détail du moyen");
		listParamTitle.add("Fabricant");
		listParamTitle.add("Référence Fabricant");
		listParamTitle.add("Numéro de série");
		listParamTitle.add("Outils associés");
		listParamTitle.add("Indice outils");
		listParamTitle.add("Référence plan / logiciel");
		listParamTitle.add("Versions plan ou logiciel");
		listParamTitle.add("Nom du logiciel");
		listParamTitle.add("Raccourci vers emplacement");
		listParamTitle.add("Maintenance / Calibration");
		listParamTitle.add("Doc de référence pour calibration");
		listParamTitle.add("Raccourci vers photo");
		/*Ajout des titres de colonnes créees manuellement et stockées dans titles.ser*/
		for(String title: unserializeTitles()){
			listParamTitle.add(title);
		}
	}

	/**
	 * @return la liste des "titres" de chaque attribut de l'Outil
	 */
	public static ArrayList<String> getParamTitle(){
		Outil.setListParamTitle(); //initialisation de la liste
		return listParamTitle;
	}



	/*Fonctions sur les paramètres ajoutées*/

	/**
	 * Initialise ou complète la liste addedParam en fonction du nombre de titres d'attributs 
	 * crées manuellement et enregistrés
	 */
	public void setAddedParams(){
		for(int i=addedParam.size(); i<unserializeTitles().size();i++){
			addedParam.add("");
		}
	}

	/**
	 * Modifie la valeur d'un paramètre corrspondant à un attribut ajouté manuellement
	 * @param title le titre du paramètre (l'attribut)
	 * @param value la nouvelle valeur du paramètre
	 */
	public void setParam(String title, String value){
		ArrayList<String> listTitles = unserializeTitles();
		for(int i=0; i<listTitles.size();i++){
			if(listTitles.get(i).equals(title)){
				addedParam.set(i, value);
			}
		}
	}



	/*Sérialisation des objets Outil*/

	/**
	 * Sérialise des objets Outil afin qu'ils puissent être sérailisés en même temps 
	 * que les objets Element
	 */
	public void serializeOutil(){
        try {
			FileOutputStream fichier = new FileOutputStream("data/outils.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			oos.writeObject(this);
            oos.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	
    }

	/**
	 * Lis des objets Outil sérialisés afin qu'ils puissent être lu et traduits en même temps 
	 * que les objets element
	 */
	public void unserializeOutil(){
        try (ObjectInputStream ois = 
				new ObjectInputStream(
						new FileInputStream("data/outils.ser"))) {
			/* Lecture du fichier*/
			while (true) {
				ois.readObject();
			}
		} catch (IOException e) {
			//Exception lorsqu'on atteint la fin du fichier
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}   
    }



	/**
	 * Ajout d'une nouvelle variable en tant que titre (attribut) dans la base de données
	 * @param title le nom du titre du paramètre
	 */
	public static void addTitle(String title){
        ArrayList<String> titles = unserializeTitles();
		titles.add(title);
        serializeAllTitles(titles);
    }

	/**
	 * Lis le fichier contenant l'ensemble des attributs 
	 * ajoutés manuellement par l'utilisateur
	 * @return la liste des attributs contenus dans le fichier
	 */
    public static ArrayList<String> unserializeTitles(){
        ArrayList<String> listTitles = new ArrayList<String>();
        try (ObjectInputStream ois = 
				new ObjectInputStream(
						new FileInputStream("data/titles.ser"))) {
			/* Lecture du fichier*/
			while (true) {
				listTitles.add((String) ois.readObject());
			}
		} catch (IOException e) {
			//Exception lorsqu'on atteint la fin du fichier
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}   
        return listTitles;

    }

	/**
	 * Eregistre dans le fichier correpondant, les attributs entrés manuellement
	 * @param allTitles la liste des titres à enregistrer
	 */
    public static void serializeAllTitles(ArrayList<String> allTitles){
        try {
			FileOutputStream fichier = new FileOutputStream("data/titles.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			for(String title: allTitles){
                oos.writeObject(title);
            }
            oos.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
    }
}