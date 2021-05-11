package Model;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

import Controller.Controller;


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


	/*Constructeurs*/
    public Outil(String moyenGenerique, String detailMoyen){
        this.moyenGenerique = moyenGenerique;
		this.detailMoyen = detailMoyen;
		this.utilisationAuto = false;
		this.listParam = new ArrayList<String>();
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
		for(int i=listParam.size(); i<Colonne.unserializeCols().size(); i++){
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



	/*Fonctions relatives aux titres des colonnes / paramètres des Outils*/

	/**
	 * Crée la liste des "titres" de chaque attribut de l'Outil qui sont utilisés dans l'affichage
	 */
	public static void setListParamTitle(){
		listParamTitle = new ArrayList<String>();
		listParamTitle.clear();
		/*Ajout des titres stockées dans le fichier de données*/
		for(String title: Colonne.getTitles()){
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

	/**
	 * Compare deux outils en selon leur moyenGenerique, leur detailMoyen et 
	 * leut utilisationAuto.
	 * @param outil l'outil comparé avec this
	 * @return true si les outils sont les "mêmes", false sinon
	 */
	public boolean equals(Outil outil){
		if(this.moyenGenerique.equals(outil.moyenGenerique) && this.detailMoyen.equals(outil.detailMoyen) && this.utilisationAuto==outil.utilisationAuto){
			return true;
		}
		return false;
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



	/*Fonctions liées à l'odre de stockage / affichage des paramètres*/

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

	/**
	 * Ajoute un nouvel entier valant la taille de la liste d'ordre avant ajout
	 * dans la liste des ordres.
	 * Enregistre la nouvelle liste dans le fichier lié.
	 */
	public static void addOrdre(){
		ArrayList<Integer> ordre = unserializeOrdre();
		ordre.add(ordre.size());
		serializeOrdre(ordre);
	}

	/**
	 * Supprime un ordre de la liste des ordres et  adapte les autres ordres à 
	 * cette supression. Càd que les ordres supérieurs à l'ordre supprimé seront tous
	 * décrémentés. Enregistre la nouvelle liste dans le fichier lié.
	 * @param index la position dans la liste de l'odre supprimé
	 */
	public static void removeOrdre(int index){
		ArrayList<Integer> ordre = unserializeOrdre();
		int val = ordre.get(index);
		ordre.remove(index);
		for(int i = 4; i<ordre.size(); i++){
			if(ordre.get(i)>val){
				ordre.set(i, ordre.get(i)-1);
			}
		}
		serializeOrdre(ordre);
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

}