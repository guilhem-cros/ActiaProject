package Model;

import Controller.AppLaunch;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;

public class Colonne implements Serializable{

    /*boolean représentant le fait que la colonne affiche des valeurs de type "liens"*/
    private boolean isHyperLink;

    /*Le titre de la colonne*/
    private String title;

    /*Constructeurs*/
    public Colonne(String title, boolean isHyperLink){
        this.title = title;
        this.isHyperLink = isHyperLink;
    }



    /*Getter et setter*/

    public boolean isHyperLink() {
        return isHyperLink;
    }

    public void setHyperLink(boolean isHyperLink) {
        this.isHyperLink = isHyperLink;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }



    /*Sélection / tri d'objets Colonne*/

    /**
     * Vérifie la présence d'un paramètre title de Colonne dans le fichier
     * de stockage de ces données.
     * @param title le titre dont on vérifie la présence.
     * @return true si le titre est présent, false sinon
     */
    public static boolean isAlrdySaved(String title){
        ArrayList<Colonne> allCols = unserializeCols();
        for(Colonne c : allCols){
            if(c.title.equals(title)){
                return true;
            }
        }
        return false;
    }

    /**
     * Recherche la position d'une colonne dans une liste de colonne en 
     * fonction de son titre.
     * @param allCols une liste contenant tous les objets Colonne
     * @param title le titre de la colonne dont on cherche la position
     * @return l'index de la Colonne dans la liste de toutes les colonnes
     */
    public static int getIndexByTitle(ArrayList<Colonne> allCols, String title){
        for(int i=0; i<allCols.size(); i++){
            if(allCols.get(i).getTitle().equals(title)){
                return i;
            }
        }
        return allCols.size();
    }


    
    /*Modifications d'objet Colonne*/

    /**
     * Modifie les paramètre de this.
     * @param title le nouveau pramètre title de this
     * @param isLink le nouveau paramètre isHyperLink de this
     */
    public void updateCol(String title, Boolean isLink){
        if(!isAlrdySaved(title)){
            this.title = title;
        }
        this.isHyperLink = isLink;
    }



    /*Sérialisation des Objets Colonne*/

    /**
    * Enregistre dans le fichier correpondant, les objets Colonne d'une liste de 
    * colonne passée en paramètre.
    * @param columns une liste de colonnes
    */
   public static void serialCols(ArrayList<Colonne> columns){
       try {
           FileOutputStream fichier = new FileOutputStream(AppLaunch.getCurrentPath() + "data/columns.ser");
           ObjectOutputStream oos = new ObjectOutputStream(fichier);
           for(Colonne col : columns){
               oos.writeObject(col);
           }
           oos.close();
       } catch (IOException e) {
           System.out.println(e.toString());
       }
   }
   
   /**
	 * Lis le fichier contenant l'ensemble des objets Colonne.
	 * @return la liste des colonnes contenues dans le fichier
	 */
    public static ArrayList<Colonne> unserializeCols(){
        ArrayList<Colonne> columns = new ArrayList<Colonne>();
        try (ObjectInputStream ois = 
				new ObjectInputStream(
						new FileInputStream(AppLaunch.getCurrentPath() + "data/columns.ser"))) {
			/* Lecture du fichier*/
			while (true) {
				columns.add((Colonne) ois.readObject());
			}
		} catch (IOException e) {
			//Exception lorsqu'on atteint la fin du fichier
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}   
        return columns;
    }  



    /*Modifications sur le fichier de stockage columns.ser*/

    /**
     * Crée et ajoute un nouvelle colonne dans le fichier de stockage columns.ser 
     * si le titre en paramètre n'est pas déjà enregistré.
     * @param title le paramètre title du nouvel objet Colonne
     * @param isLink le paramètre iHyperLink du nouvel objet Colonne
     */
    public static void addNewCol(String title, boolean isLink){
        if(!isAlrdySaved(title)){
            ArrayList<Colonne> allCols = unserializeCols();
            Colonne col = new Colonne(title, isLink);
            allCols.add(col);
            serialCols(allCols);
        } 
    }

    /**
     * Retire un objet Colonne du fichier de stockage columns.ser en fonction
     * de son titre
     * @param title le titre de la colonne retirée
     */
    public static void removeCol(String title){
        ArrayList<Colonne> allCols = unserializeCols();
        for(int i=0; i<allCols.size(); i++){
            if(allCols.get(i).getTitle().equals(title)){
                allCols.remove(i);
                i=allCols.size();
            }
        }
        serialCols(allCols);
    }



    /*Récupération de données depuis le fichier columns.ser*/

    /**
     * Récupère l'ensemble des paramètres title de toutes
     * les colonnes enregistrées dans le fichier column.ser.
     * @return une liste de chaine de caractères contenant les titres
     * de toutes les colonnes.
     */
    public static ArrayList<String> getTitles(){
        ArrayList<String> allTitles = new ArrayList<>();
        for(Colonne c: unserializeCols()){
            allTitles.add(c.getTitle());
        }
        return allTitles;
    }
}
