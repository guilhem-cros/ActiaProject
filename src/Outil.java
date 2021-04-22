
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
    private String indiceOutil;
    private String refPlanOuLogiciel;
    private String versionPlanOuLogiciel;
    private String nomLogiciel;
    private String raccourciEmplacement;
    private String maintenance;
    private String refCalibration;
    private String quantite;
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

	public String getIndiceOutil() {
		return indiceOutil;
	}

	public void setIndiceOutil(String indiceOutil) {
		this.indiceOutil = indiceOutil;
	}

	public String getRefPlanOuLogiciel() {
		return refPlanOuLogiciel;
	}

	public void setRefPlanOuLogiciel(String refPlanOuLogiciel) {
		this.refPlanOuLogiciel = refPlanOuLogiciel;
	}

	public String getVersionPlanOuLogiciel() {
		return versionPlanOuLogiciel;
	}

	public void setVersionPlanOuLogiciel(String versionPlanOuLogiciel) {
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

	public String getQuantite() {
		return quantite;
	}

	public void setQuantite(String quantite) {
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

	public ArrayList<String> getAddedParam() {
		return addedParam;
	}

	public void setAddedParam(ArrayList<String> addedParam) {
		this.addedParam = addedParam;
	}


	/*Fonctions relatives à la liste de paramètres*/

	/**
	 * Crée la liste contenant les valeurs des attributs de l'Outil 
	 * en chaine de caractère dans l'optique du futur affichage
	 */
	public void setListParam(){
		this.listParam = new ArrayList<String>();
		listParam.add(this.moyenGenerique);
		listParam.add(this.quantite);
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
		listParam.add(this.indiceOutil);
		listParam.add(this.refPlanOuLogiciel);
		listParam.add(this.versionPlanOuLogiciel);
		listParam.add(this.nomLogiciel);
		listParam.add(this.raccourciEmplacement);
		listParam.add(this.maintenance);
		listParam.add(this.refCalibration);
		listParam.add(this.lienPhoto);
		/*Mise en place et ajout des paramètres correspondant à des titres ajoutés manuellements*/
		for(int i=16; i<unserializeTitles().size(); i++){
			listParam.add("");
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
		listParamTitle = new ArrayList<String>();
		listParamTitle.clear();
		/*Ajout des titres stockées dans titles.ser*/
		for(String title: unserializeTitles()){
			listParamTitle.add(title);
		}
	}

	/**
	 * @return la liste des "titres" de chaque attribut de l'Outil
	 */
	public static ArrayList<String> getParamTitle(){
		Outil.setListParamTitle();
		return listParamTitle;
	}



	/*Fonctions sur les paramètres ajoutées*/

	/**
	 * Initialise ou complète la liste addedParam en fonction du nombre de titres d'attributs 
	 * crées manuellement et enregistrés
	 */
	public void setAddedParams(){
		for(int i=16; i<unserializeTitles().size();i++){
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


	/**
	 * Trie une liste d'objets Outil par ordre alphabétique des moyens génériques
	 * @param list la liste à trier
	 */
	public static void sortOutilsByMoyen(ArrayList<Outil> list){
		for(int i=0; i<list.size()-1; i++){
            for(int j=i+1;j<list.size(); j++){
                if(list.get(i).moyenGenerique.compareTo(list.get(j).moyenGenerique) > 0){
                    Outil aux = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, aux);
                }
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
	 * s'il n'est pas déjà présent
	 * @param title le nom du titre du paramètre
	 */
	public static void addTitle(String title){
        ArrayList<String> titles = unserializeTitles();
		//vérifier présence titre
		int i=0;
		for(String t: titles){
			if(t.equals(title)){
				i++;
			}
		}
		if(i==0){
			titles.add(title);
		}
        serializeAllTitles(titles);
    }

	/**
	 * Suppression d'un variable dans la base de données
	 * @param title le nom de la variable à supprimer
	 */
	public static void removeTitle(String title){
		ArrayList<String> titles = unserializeTitles();
		titles.remove(title);
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

	/**
	 * Supprime une valeur de la liste de param pour tous les 
	 * objets Outil enregistrés
	 * @param index l'index du param dans les listes des Outils
	 */
	public static void removeFromAllOutil(int index){
		ArrayList<Element> allElt = Element.unserializeElement();
		for(Element e: allElt){
			for(Outil o: e.getOutils()){
				o.setListParam();
				o.getListParam().remove(index);
			}
		}
		Element.serializeAllElements(allElt);
	}
}