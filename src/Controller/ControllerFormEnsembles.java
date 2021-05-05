package Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Element;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;

public class ControllerFormEnsembles implements Initializable{
    
    /*L'ensemble sélectionné depuis le menu d'accueil*/
    public Element selectedElement;

    /*Liste des champs complétables des paramètres de l'ensemble*/
    public ArrayList<TextField> listParamField;

    /*Le controller ayant ouvert le formulaire*/
    public static Controller originControll;


    /*Attributs FXML des formulaires*/

    @FXML
    private Label titleLabel;

    @FXML
    private TextField codeField;

    @FXML
    private TextField nameField;

    @FXML
    private Button saveButton;

    @FXML
    private Button cancelButton;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        System.out.println(Controller.getForm());
        selectedElement = Controller.getCurrentElement();
        if(Controller.getForm().equals("updateElementForm")){
            saveButton.setText("Enregistrer");
            fillFields();
        }
        else if(Controller.getForm().equals("addElementForm")){
            saveButton.setText("Créer l'ensemble");
        }
    }

    /*Fonctions FXML*/

    /**
     * Enregistre l'élément saisi dans le formulaire de modif/création et met
     * à jour la liste d'objets Element du Controller ainsi que le fichier de stockage
     * Ouvre un onglet d'alerte si es champs ne sont pas correctement rempli ou si
     * l'element créé possède un code déjà existant
     */
    @FXML
    public void saveElement(ActionEvent action){
        /*Si les champs ne sons pas correctement remplis*/
        if(codeField.getText().length()<3 || nameField.getText().length()<3){
            Alert alert  = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur : champs invalide(s)", "Veuillez saisir au moins 3 caractères pour chaque champs du formulaire", "Erreur", alert);
        } 
        /*S'il s'agit d'une création qui respecte toutes les conditions*/
        else{
            Element e = makeElementByForm();
            ArrayList<Element> allElt = Controller.getAllElements();
            if(Controller.getForm().equals("addElementForm") && !isAlreadyDefine()){
                allElt.add(e);
                saveDatas(allElt);
                finalize(action);
            }
            else if(Controller.getForm().equals("updateElementForm") && (!isAlreadyDefine() || selectedElement.getCodeElt().equals(codeField.getText()))){
                int i = allElt.indexOf(selectedElement);
                Controller.shutDownUpdatedWindows(selectedElement);
                e.setListLogsElement(selectedElement.getListLogsElement());
                e.setListeOutils(selectedElement.getListeOutils());
                e.setListeSousElements(e.getListeSousElements());
                allElt.set(i, e);
                saveDatas(allElt);
                System.out.println("ii");
                finalize(action);
            }
            else{
                Alert alert = new Alert(AlertType.WARNING);
                Controller.setAlert("Erreur : ensemble déjà existant", "Le code saisi pour l'ensemble correspond à un ensemble déjà existant.", "Erreur", alert);
            }   
        }
    }

    /**
     * Appelé lors d'un click sur le bouton "Annuler"
     * Décrémente le nombre de formulaire ouvert et ferme le formulaire courant
     * @param action
     */
    @FXML
    public void closeForm(ActionEvent action){
        Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1); //décrémentation du nb de form ouverts
        (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
    }



    /*Construcion et modification d'objets Element */

    /**
     * Construit un objet Element avec les données entrées dans les champs de 
     * textes du formulaire de création d'ensemble courant
     */
    public Element makeElementByForm(){
        String code = codeField.getText();
        String nom = nameField.getText();
        return new Element(nom, code);
    }

    /**
     * Vérifie la présence d'un code d'element egal à la valeur du chaps de texte "code"
     * dans la liste de tous les éléments
     * @return true si un element avec le code existe, faux sinon
     */
    public boolean isAlreadyDefine(){
        for(Element e: Controller.getAllElements()){
            if(e.getCodeElt().equals(codeField.getText())){
                return true;
            }
        }
        return false;
    }

    public void fillFields(){
        nameField.setText(selectedElement.getNom());
        codeField.setText(selectedElement.getCodeElt());
    }

    /*Fonctions diverses*/

    /**
     * Rafraichi la page d'accueil et ferme le formulaire d'ensemble courant
     * @param action
     */
    public void finalize(ActionEvent action){
        originControll.initialize(null, null);
        Controller.setAdmin(true);
        closeForm(action);
    }

    public void saveDatas(ArrayList<Element> listElt){
        Element.sortElements(listElt);
        Controller.setAllElements(listElt);
        Element.serializeAllElements(Controller.getAllElements());
    }



    /*Getter et setter*/

    public static Controller getOriginControll() {
        return originControll;
    }

    public static void setOriginControll(Controller originControll) {
        ControllerFormEnsembles.originControll = originControll;
    } 

}