
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
    private String quantite;

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

	public String getQuantite() {
		return quantite;
	}

	public void setQuantite(String quantite) {
		this.quantite = quantite;
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
		if(listParam.isEmpty()){
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
		}
		/*Mise en place et ajout des paramètres correspondant à des titres ajoutés manuellements*/
		for(int i=listParam.size(); i<unserializeTitles().size(); i++){
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
	 * Modifie la valeur d'un paramètre correspondant à un attribut ajouté manuellement
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
	 * s'il n'est pas déjà présent et modification de la liste de position des paramètres
	 * @param title le nom du titre du paramètre
	 */
	public static void addTitle(String title){
        ArrayList<String> titles = unserializeTitles();
		ArrayList<Integer> ordre = unserializeOrdre();
		//vérifier présence titre
		int i=0;
		for(String t: titles){
			if(t.equals(title)){
				i++;
			}
		}
		if(i==0){
			titles.add(title);
			ordre.add(ordre.size());
		}
        serializeAllTitles(titles);
		serializeOrdre(ordre);
    }

	/**
	 * Suppression d'un variable dans la base de données et modification de la liste
	 * de position des paramètres à l'affichage en fonction
	 * @param title le nom de la variable à supprimer
	 */
	public static void removeTitle(String title){
		ArrayList<Integer> ordre = unserializeOrdre();
		ArrayList<String> titles = unserializeTitles();
		int index = titles.indexOf(title);
		titles.remove(title);
		int val = ordre.indexOf(index);
		ordre.remove(index);
		for(int i = 4; i<ordre.size(); i++){
			if(ordre.get(i)>val){
				ordre.set(i, ordre.get(i)-1);
			}
		}
		serializeAllTitles(titles);
		serializeOrdre(ordre);
	}

	/**
	 * Lis le fichier contenant l'ensemble des attributs de l'Outil
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
		/*Si aucun attribut enregistrées, ajout des 4 colonnes de bases*/
		if(listTitles.isEmpty()){
			ArrayList<Integer> ordre = unserializeOrdre();
			listTitles.add("Moyens Génériques");
			listTitles.add("Quantité");
			listTitles.add("Utilisation pour test Manuel ou Auto (déverminage)");
			listTitles.add("Détail du moyen");
			for(int i=0; i<4; i++){
				ordre.add(i);
			}
			serializeAllTitles(listTitles);
			serializeOrdre(ordre);
		}
        return listTitles;

    }

	/**
	 * Eregistre dans le fichier correpondant, les attributs de l'Outil
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
		ArrayList<Element> allElt = Controller.getAllElements();
		for(Element e: allElt){
			for(Outil o: e.getOutils()){
				o.setListParam();
				o.getListParam().remove(index);
			}
		}
		Element.serializeAllElements(allElt);
	}

	/**
	 * Eregistre dans le fichier correpondant, l'ordre d'affichage 
	 * des paramètres
	 * @param allTitles la liste de positions d'affichage de chaque paramètre
	 */
	public static void serializeOrdre(ArrayList<Integer> ordre){
		try {
			FileOutputStream fichier = new FileOutputStream("data/ordre.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			for(Integer index: ordre){
                oos.writeObject(index);
            }
            oos.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * Lis le fichier contenant l'ordre de lecture des paramètres 
	 * dans le cadre de l'affichage
	 * @return la liste de la position à afficher de chaque paramètre 
	 */
	public static ArrayList<Integer> unserializeOrdre(){
		ArrayList<Integer> ordre = new ArrayList<Integer>();
        try (ObjectInputStream ois = 
				new ObjectInputStream(
						new FileInputStream("data/ordre.ser"))) {
			/* Lecture du fichier*/
			while (true) {
				ordre.add((Integer) ois.readObject());
			}
		} catch (IOException e) {
			//Exception lorsqu'on atteint la fin du fichier
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}   
        return ordre;
	}
}