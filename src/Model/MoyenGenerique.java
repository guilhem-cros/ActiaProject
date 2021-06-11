package Model;

import Controller.AppLaunch;
import Controller.Lanceur;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.io.Serializable;

public class MoyenGenerique implements Serializable{

    /*Attributs*/

    private static final long serialVersionUID = 1L;

    /*Nom du moyen*/
    private String nom;


	/*Constructeurs*/
    public MoyenGenerique(String nom){
        this.nom=nom;
    }



	/*Getter et setter*/

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}



    /*Sérialisation des moyens génériques*/

	/**
	 * Stocke les noms des moyens génériques dans un fichier .ser
	 * @param moyensGene ArrayList de l'ensemble des objets MoyenGenerique
	 */
	public static void serializeMoyenGene(ArrayList<MoyenGenerique> moyensGene){
        try {
			FileOutputStream fichier = new FileOutputStream(Lanceur.getCurrentPath() + "data/moyensGeneriques.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
			for(MoyenGenerique moyen: moyensGene){
				oos.writeUTF(moyen.nom);//écriture du nom dans le fichier
			}
            oos.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	
    }

	/**
	 * Récupère les noms des moyens génériques stockés dans le fichier 
	 * moyensGeneriques.ser et les transforme en objet de type MoyenGenerique
	 * @return arraylist contentant l'ensemble des moyens lus dans le fichier
	 */
	public static ArrayList<MoyenGenerique> unserializeMoyenGene(){
		ArrayList<MoyenGenerique> allMoyensGene = new ArrayList<MoyenGenerique>();
        try (ObjectInputStream ois = 
				new ObjectInputStream(
						new FileInputStream(Lanceur.getCurrentPath() + "data/moyensGeneriques.ser"))) {
			/* Lecture du fichier*/
			while (true) {
				String currentName = ois.readUTF();//lecture du nom dans le fichier
				MoyenGenerique currentMoy = new MoyenGenerique(currentName);
				allMoyensGene.add(currentMoy);
			}
		} catch (IOException e) {
			//Exception lorsqu'on atteint la fin du fichier
		} catch (Exception e) {
			e.printStackTrace();
		}
		return allMoyensGene; 
    }



	/*Fonctions relative à la modification de la liste de moyens génériques*/

	/**
	 * Crée et ajoute un moyen générique à partir de son nom
	 * dans le fichier stockant l'ensemble des moyens si 
	 * celui-ci n'y est pas déjà stocké
	 * @param name le nom du moyen générique à créer
	 * @return la réussite ou l'échec de l'opération
	 */
	public static boolean addMoyen(String name){
		ArrayList<MoyenGenerique> allMoyens = unserializeMoyenGene(); //lecture du fichier de stockage
		MoyenGenerique moyen = new MoyenGenerique(name);
		if(isAlrdyInList(allMoyens, name)){ //s'il existe déjà : pas de duplication
			System.out.println("Moyen déjà existant");
			return false; //l'ajout n'a pas été effectué
		}
		else{
			allMoyens.add(moyen);
			sortMoyenGen(allMoyens);
			serializeMoyenGene(allMoyens); //réécriture du fichier de stockage avec ajout
			return true; //l'ajout a  été effectué
		}
	}

	/**
	 * vérifie la présence d'un moyen générique dans une liste de moyen 
	 * générique à partir de son nom
	 * @param list la liste à parcourrir 
	 * @param nomMoy le nom du moyen dont est vérifié la présence dans list
	 */
	public static boolean isAlrdyInList(ArrayList<MoyenGenerique> list, String nomMoy){
		for(MoyenGenerique moy: list){
			if(moy.nom.equals(nomMoy)){
				return true;
			}
		}return false;
	}

	/**
	 * Trie une liste d'objets MoyenGenerique par ordre alphabétique
	 * @param list la liste à trier
	 */
	public static void sortMoyenGen(ArrayList<MoyenGenerique> list){
		for(int i=0; i<list.size()-1; i++){
            for(int j=i+1;j<list.size(); j++){
                if(list.get(i).nom.compareTo(list.get(j).nom) > 0){
                    MoyenGenerique aux = list.get(i);
                    list.set(i, list.get(j));
                    list.set(j, aux);
                }
            }
        }
	}
} 