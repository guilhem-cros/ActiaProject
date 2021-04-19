
import java.util.ArrayList;



public class Main{

    public static void main(String[] args) {
        
        /*
        Element elmt= new Element("test", "9555");
        Element elt2= new Element("test2", "9545");
        Element elt3= new Element("test3", "9355");
        Element elt4= new Element("testttttttttttttttttttttttttttttttttttttt4", "9595");
        Element elt5= new Element("test5", "9458");
        Element elt6= new Element("test5", "9558");
        elmt.addElement(elt3);
        elmt.addElement(elt4);
        elt4.addElement(elt5); 
        Test t1 = new Test("Logiciel", "il faut tester");
        Test t2 = new Test("Bande passante", "splitter vidéo composite 1 voie vers 16 voies encore encore core");
        t2.setLienPhoto("C:/Users/g.cros/Documents/Projet/media/logoActia.png");
        elt3.addTest(t1);
        elt3.addTest(t2);
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
        System.out.println(MoyenGenerique.addMoyen("Logiciel"));
        MoyenGenerique.removeMoyen("Logiciel2");
        System.out.println(MoyenGenerique.addMoyen("Bande passante"));
        System.out.println(MoyenGenerique.unserializeMoyenGene());
        */

        Config.serializeConfig("admin1", "login");
        Config.unserializeConfig();
        System.out.println(Config.getLogin() + Config.getPassword());
    }
}