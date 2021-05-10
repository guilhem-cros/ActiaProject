package Model;


import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.io.Serializable;


public class Element implements Serializable{


    /*Attributs*/

    private static final long serialVersionUID = 1L;

    /*Nom de l'élément*/
    private String nom;

    /*Le code correspondant à l'élément*/
    private String codeElt;

    /*La liste des outils enregistrés de l'élément*/
    private ArrayList<Outil> listeOutils;

    /*La liste des sous élément de l'élément*/
    private ArrayList<Element> listeSousElements;

    /*La liste des logs associées à l'éléments*/
    private ArrayList<Logs> listLogsElement;
    


    /*Constructeurs*/
    public Element(String nom, String codeElt){
        this.nom=nom;
        this.codeElt=codeElt;
        listeOutils = new ArrayList<Outil>();
        listeSousElements = new ArrayList<Element>();
        listLogsElement = new ArrayList<Logs>();
    }



    /*Getter et setter*/

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getCodeElt() {
        return codeElt;
    }

    public void setCodeElt(String codeElt) {
        this.codeElt = codeElt;
    }

    public ArrayList<Outil> getOutils() {
        return listeOutils;
    }

    public ArrayList<Element> getListeSousElements() {
        return listeSousElements;
    }

    public ArrayList<Logs> getListLogsElement() {
        return listLogsElement;
    }

    public void setListLogsElement(ArrayList<Logs> listLogsElement) {
        this.listLogsElement = listLogsElement;
    }

    public ArrayList<Outil> getListeOutils() {
        return listeOutils;
    }

    public void setListeOutils(ArrayList<Outil> listeOutils) {
        this.listeOutils = listeOutils;
    }

    public void setListeSousElements(ArrayList<Element> listeSousElements) {
        this.listeSousElements = listeSousElements;
    }




    /*modifications sur la liste d'outils*/

    public void addOutil(Outil t){
        listeOutils.add(t);  
    }

    public void removeOutil(Outil t){
        listeOutils.remove(t);
    }

    /**
     * Tri des outils de test en fct de leur mode (auto/manuel)
     * @param auto vrai pour select les outils auto, faux pour manuel
     * @return la liste des outils vérifiant auto
     */
    public ArrayList<Outil> getOutilsByMode(boolean auto){
        ArrayList<Outil> sortedList = new ArrayList<Outil>();
        for(Outil t: this.listeOutils){
            if(auto){
                if(t.isUtilisationAuto()){
                    sortedList.add(t);
                }
            }
            else{
                if(!t.isUtilisationAuto()){
                    sortedList.add(t);
                }
            }
        }
        return sortedList;
    }

    /**
     * @return vrai si l'element possède un/des outil(s) de mode auto
     */
    public Boolean hasAutoOutils(){
        return !this.getOutilsByMode(true).isEmpty();
    }

    /**
     * @return vrai si l'element possède un/des outil(s) de mode manuel
     */
    public Boolean hasManuelOutils(){
        return !this.getOutilsByMode(false).isEmpty();
    }

    /**
     * @return vrai si l'element ne possède aucun outil
     */
    public Boolean hasNoOutil(){
        return this.getOutilsByMode(true).isEmpty() && this.getOutilsByMode(false).isEmpty();
    }


    
    /*modifications sur la liste de sous élements*/

    /**
	 * Ajoute un élément à la liste de sous éléments de l'objet visé 
     * si celui-ci n'y est pas déjà présent
	 * @param e l'élément qu'on souhaite ajouter à la liste
	 */
    public void addElement(Element e){
        if(listeSousElements.size()>0){
            for(int i=0; i<listeSousElements.size();i++){
                if(listeSousElements.get(i).codeElt.equals(e.codeElt)){ //on évite de dupliquer la présence d'un élément
                    System.out.println("Code element déjà enregistré");
                }
                else{
                    listeSousElements.add(e);
                    i=listeSousElements.size();
                }   
            }
        }
        else{
            listeSousElements.add(e);
        }
    }

    /**
     * Supprime un Element de la liste de sous Element de this si celui-ci y est présent
     * @param e l'element supprimé de la liste
     */
    public void removeElement(Element e){
        ArrayList<Element> aux = this.listeSousElements;
        for(int i=0; i<aux.size(); i++){
            if(aux.get(i).codeElt.equals(e.codeElt)){
                aux.remove(i);
            }
        }
        setListeSousElements(aux);
    }

    /**
     * Remplace un Element de la liste de sous-éléments pas un autre et tri la liste
     * @param oldElt l'ancien élément supprimé de la liste
     * @param newElt le nouvel élément ajouté à la liste
     */
    public void updateElement(Element oldElt, Element newElt){
        int i = listeSousElements.indexOf(oldElt);
        this.listeSousElements.set(i, newElt);
        sortElements(listeSousElements);
    }

    /**
     * Vérifie si code Element correspond à un sous-élément de this en fonction de son code
     * @param code une chaine de caractère correspondant à un code d'Element
     * @return true si le code correspond à un sous-élément, false sinon
     */
    public boolean isASubElt(String code){
        for(Element elt: listeSousElements){
            if(elt.getCodeElt().equals(code)){
                return true;
            }
        }
        return false;
    }

    /**
     * Récupère tous les parents, grands-parents, etc, de l'Element this.
     * @param allElements la liste de tous les élèments connus
     * @return la liste de tous les Element "parent" de this.
     */
    public ArrayList<Element> getAllParents(ArrayList<Element> allElements){
        ArrayList<Element> allParents = new ArrayList<>();
        for(Element e: allElements){
            for(Element child: e.listeSousElements){
                if(child.codeElt.equals(this.codeElt)){
                    allParents.add(e);
                    /*appel récursif afin de remonter toute l'arborescence de l'Element*/
                    allParents.addAll(e.getAllParents(allElements));
                }
            }
        }
        return allParents;
    }
    
    /**
     * Vérifie si this est parent d'un autre Element passé en paramètre.
     * @param allElements la liste de tous les Element
     * @param elt l'element pour lequel on vérifie si this est un parent
     * @return true si this est parent de l'Element, false sinon
     */
    public boolean isParent(ArrayList<Element> allElements, Element elt){
        for(Element e: elt.getAllParents(allElements)){
            if(e.codeElt.equals(this.codeElt)){
                return true;
            }
        }
        return false;
    }



    /*Modifications sur la liste de logs de l'élément*/

    public void addLogs(Logs l){
        listLogsElement.add(l);
    }

    public void removeLogs(Logs l){
        listLogsElement.remove(l);
    }

    public boolean hasLogs(){
        return !listLogsElement.isEmpty();
    }



    /*Tri de liste d'éléments*/

    /**
     * Fonction triant une liste d'éléments dans l'ordre décroisant de leur code
     * @param L la liste d'éléments à trier
     */
    public static void sortElements(ArrayList<Element> L){
        for(int i=0; i<L.size()-1; i++){
            for(int j=i+1;j<L.size(); j++){
                if(Integer.parseInt(L.get(i).getCodeElt()) < Integer.parseInt(L.get(j).getCodeElt())){
                    Element aux = L.get(i);
                    L.set(i, L.get(j));
                    L.set(j, aux);
                }
            }
        }
        
    }

    /**
     * Trie une liste d'élèment dans l'ordre alphabétique de leur nom
     * @param L la liste d'éléments à trier
     */
    public static void sortEltByName(ArrayList<Element> L){
        for(int i=0; i<L.size()-1; i++){
            for(int j=i+1;j<L.size(); j++){
                if(L.get(i).getNom().compareTo(L.get(j).getNom())>0){
                    Element aux = L.get(i);
                    L.set(i, L.get(j));
                    L.set(j, aux);
                }
            }
        }
    }



    /*Sélections particulières d'élèments*/

    /**
     * Sélectionne tous les élèments enregistrés en fonction de la similarité d'une
     * chaine de caractères avec le nom de l'élèment
     * @param value la chaine de caractère devant être similaire au nom
     * @return la liste d'élèments pour lequels le nom débute par la value en param
     */
    public static ArrayList<Element> selectByName(String value){
        ArrayList<Element> selectedElements = new ArrayList<>();
        for(Element e :unserializeElement()){
            int length = value.length();
            if(length<= e.getNom().length() && value.equals(e.getNom().substring(0, length))){
                selectedElements.add(e);
            }
        }
        sortEltByName(selectedElements);
        return selectedElements;
    }

    /**
     * Sélectionne tous les élèments enregistrés en fonction de la similarité d'une 
     * chaine de caractères avec le code e l'élèment
     * @param code la chaine de caractère devant être similaire au code des élèments
     * @return la liste d'élèments pour lesquels le code débute par la valeur en param
     */
    public static ArrayList<Element> selectByCode(String code){
        ArrayList<Element> selectedElements = new ArrayList<>();
        for(Element e :unserializeElement()){
            int length = code.length();
            if(length<= e.getCodeElt().length() && code.equals(e.getCodeElt().substring(0, length))){
                selectedElements.add(e);
            }    
        }
        sortElements(selectedElements);
        return selectedElements;
    }

    /**
     * @return vrai si l'élément est un "produit" ~ codeElt commence par 95
     */
    public boolean isProduit(){
        String chars = this.codeElt;
        char char1 = chars.charAt(0);
        char char2 = chars.charAt(1);
        if(char1 == '9' && char2 =='5'){
            return true;
        }
        return false;
    }

    /**
     * @return vrai si l'élément ne possède aucun sous élément
     * */
    public boolean hasNoSubElmt(){
        return this.listeSousElements.isEmpty();
    }

    public String toString(){
        return this.codeElt + " " + this.nom + " ";
    }



    /*Sérialisation des objets Element*/

    /**
	 * Stocke les éléments dans un fichier .ser
	 * @param allElements ArrayList de l'ensemble des éléments
	 */
    public static void serializeAllElements(ArrayList<Element> allElements){
        try {
			FileOutputStream fichier = new FileOutputStream("data/element.ser");
			ObjectOutputStream oos = new ObjectOutputStream(fichier);
            //Tous les éléments de la liste sont sérializés dans le fichier précédent
            for(Element elt: allElements){
                oos.writeObject(elt); //séralise tous les attributs de l'élément
            }
            oos.close();
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	
    }

    /**
	 * Récupère les ensembles stockés dans le fichier element.ser
	 * @return arraylist contentant l'ensemble des éléments lu dans le fichier
	 */
    public static ArrayList<Element> unserializeElement(){
        ArrayList<Element> list = new ArrayList<Element>();
        try (ObjectInputStream ois = 
				new ObjectInputStream(
						new FileInputStream("data/element.ser"))) {
			// Lecture complète du fichier
			while (true) {
				list.add((Element) ois.readObject());
			}
		} catch (IOException e) {
			//Exception lorsqu'on atteint la fin du fichier
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
        return list; 
    }
}
