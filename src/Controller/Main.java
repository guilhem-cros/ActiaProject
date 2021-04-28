package Controller;

import java.util.ArrayList;

import Model.Element;
import Model.Logs;
import Model.MoyenGenerique;
import Model.Outil;



public class Main{

    public static void main(String[] args) {


        
        Outil.addTitle("Moyens Génériques");
       Outil.addTitle("Quantité");
       Outil.addTitle("Utilisation pour test Manuel ou Auto (déverminage)");
       Outil.addTitle("Détail du moyen");
       Outil.addTitle("Fabricant");
       Outil.addTitle("Référence Fabricant");
       Outil.addTitle("Numéro de série");
       Outil.addTitle("Outils associés");
       Outil.addTitle("Indice outils");
       Outil.addTitle("Référence plan / logiciel");
       Outil.addTitle("Version plan ou logiciel");
       Outil.addTitle("Nom du logiciel");
       Outil.addTitle("Raccourci vers emplacement");
       Outil.addTitle("Maintenance / Calibration");
       Outil.addTitle("Doc de référence pour calibration");
       Outil.addTitle("Raccourci vers photo");

        System.out.println(Outil.unserializeTitles().size());
        
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
        System.out.println(t2.getListParam());
        t2.getListParam().set(15, "C:/Users/g.cros/Documents/Projet/media/logoActia.png");
        for(int i=4; i<15; i++){
            t2.getListParam().set(i, i +"" );
        }
        
        
        System.out.println(t2.getListParam());
        elt3.addOutil(t1);
        elt3.addOutil(t2);
        elt3.addOutil(t3);
        Logs l1 = new Logs("1.2", "admin", "admin");
        Logs l2 = new Logs("1.2.8", "admin", "admin");
        elt3.addLogs(l1);
        elt3.addLogs(l2);
        ArrayList<Element> list = new ArrayList<Element>();
        list.add(elmt);
        list.add(elt2);
        list.add(elt3);
        list.add(elt4);
        list.add(elt5);
        list.add(elt6);
        Element.serializeAllElements(list);
        System.out.println(MoyenGenerique.addMoyen("Tous"));
    
        /*
        ArrayList<Integer> ordre = new ArrayList<Integer>();
        for(int i=0; i<Outil.unserializeTitles().size(); i++){
            ordre.add(i);
        }
        Outil.serializeOrdre(ordre);
        System.out.println(ordre);
        */

    }
}