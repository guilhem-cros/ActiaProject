package Controller;

import java.util.ArrayList;

import Model.Colonne;
import Model.Element;
import Model.Logs;

import Model.Outil;



public class Main{

    public static void main(String[] args) {

        
        Colonne.addNewCol("Moyens Génériques", false);
        Colonne.addNewCol("Quantité", false);
        Colonne.addNewCol("Utilisation pour test Manuel ou Auto (déverminage)", false);
        Colonne.addNewCol("Détail du moyen", false);
        Colonne.addNewCol("Fabricant", false);
        Colonne.addNewCol("Référence Fabricant", false);
        Colonne.addNewCol("Numéro de série", false);
        Colonne.addNewCol("Outils associés", false);
        Colonne.addNewCol("Indice outils", false);
        Colonne.addNewCol("Référence plan / logiciel", false);
        Colonne.addNewCol("Version plan ou logiciel", false);
        Colonne.addNewCol("Nom du logiciel", false);
        Colonne.addNewCol("Raccourci vers emplacement", false);
        Colonne.addNewCol("Maintenance / Calibration", false);
        Colonne.addNewCol("Doc de référence pour calibration", false);
        Colonne.addNewCol("Raccourci vers photo", true);

        System.out.println(Colonne.getTitles());
        
        Element elmt= new Element("test", "9555");
        Element elt2= new Element("test2", "9545");
        Element elt3= new Element("test3", "9355");
        Element elt4= new Element("testttttttttttttttttttttttttttttttttttttt4", "9595");
        Element elt5= new Element("test5", "9458");
        Element elt6= new Element("test5", "9558");
        elmt.addElement(elt3);
        elmt.addElement(elt4);
        elt4.addElement(elt5); 
        Outil t1 = new Outil("Logiciel", "il faut tester");
        Outil t2 = new Outil("Bande passante", "splitter vidéo composite 1 voie vers 16 voies encore encore core");
        Outil t3 = new Outil("Bande passante", "test num 2");
        t2.getListParam().set(15, "C:/Users/g.cros/Documents/Projet/media/logoActia.png");
        for(int i=4; i<15; i++){
            t2.getListParam().set(i, i +"" );
        }
        
        elt3.addOutil(t1);
        elt3.addOutil(t2);
        elt3.addOutil(t3);
        Logs l1 = new Logs("1.2", "admin", "admin");
        Logs l2 = new Logs("1.2.8", "admin", "admin");
        System.out.println(l1.compareByParagraphe(l2));
        elt3.addLogs(l1);
        elt3.addLogs(l2);
        Logs.sortLog(elt3.getListLogsElement());
        System.out.println(elt3.getListLogsElement());
        ArrayList<Element> list = new ArrayList<Element>();
        list.add(elmt);
        list.add(elt2);
        list.add(elt3);
        list.add(elt4);
        list.add(elt5);
        list.add(elt6);
        System.out.println("list" + elt5.getAllParents(list));
        Element.serializeAllElements(list);
    
        
        ArrayList<Integer> ordre = new ArrayList<Integer>();
        for(int i=0; i<Colonne.unserializeCols().size(); i++){
            ordre.add(i);
        }
        Outil.serializeOrdre(ordre);
        /*
        Logs l1 = new Logs("1.28.57", "admin", "admin");
        Logs l2 = new Logs("1.1", "aded", "aezez");
        Logs l3 = new Logs("1.3", "adedt", "aezez");
        System.out.println(l1.compareByParagraphe(l2));
        ArrayList<Logs> lst = new ArrayList<>();
        lst.add(l1);
        lst.add(l2);
        lst.add(l3);
        Logs.sortLog(lst);
        System.out.println(lst);
        

        System.out.println(Element.selectByName("test3"));
            */
    }
}