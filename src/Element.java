
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

    /*Le code correspondant à lélément*/
    private String codeElt;

    /*La liste des outils de test enregistrés de l'élément*/
    private ArrayList<Test> listeTests;

    /*La liste des sous élément de l'élément*/
    private ArrayList<Element> listeSousElements;

    private ArrayList<Logs> listLogsElement;
    
    /*Constructeurs*/
    public Element(String nom, String codeElt){
        this.nom=nom;
        this.codeElt=codeElt;
        listeTests = new ArrayList<Test>();
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

    public ArrayList<Test> getTests() {
        return listeTests;
    }

    public ArrayList<Element> getListeSousElements() {
        return listeSousElements;
    }

    public ArrayList<Logs> getListLogsElement() {
        return listLogsElement;
    }


    /*modifications sur la liste de tests*/

    public void addTest(Test t){
       listeTests.add(t);  
    }

    public void removeTest(Test t){
        listeTests.remove(t);
    }

    /**
     * Tri des outils de test en fct de leur mode (auto/manuel)
     * @param auto vrai pour select les outils auto, faux pour manuel
     * @return la liste des tests vérifiant auto
     */
    public ArrayList<Test> getTestsByMode(boolean auto){
        ArrayList<Test> sortedList = new ArrayList<Test>();
        for(Test t: this.listeTests){
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


    /*modifications sur la liste de sous élements*/

    /**
	 * Ajoute un élément à la liste de sous éléments de 
     * l'objet visé si celui-ci n'y est pas déjà présent
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

    public void removeElement(Element e){
        listeSousElements.remove(e);
    }


    public String toString(){
        return this.codeElt + " " + this.nom + " ";
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


    /**
     * Fonction triant une liste d'éléments dans  
     * l'ordre décroisant de leur code
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
    
}
