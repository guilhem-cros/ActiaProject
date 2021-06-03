package Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Element;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.VBox;

public class ControllerFormEnsembles implements Initializable{
    
    /*L'ensemble sélectionné depuis le menu d'accueil*/
    public Element selectedElement;

    /*Liste des champs complétables des paramètres de l'ensemble*/
    public ArrayList<TextField> listParamField;

    /*Le controller ayant ouvert le formulaire*/
    public static Controller originControll;

    public Element selectedSub;


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

    @FXML
    private TextField searchField;

    @FXML
    private ScrollPane sPane;

    @FXML
    private VBox resultsBox;

    @FXML
    private Button addSubEButton;

    @FXML
    private ComboBox<String> listSubElt;

    @FXML 
    private Button removeSubButton;

    @FXML
    private CheckBox isProdBox;

    /**
     * Appelée à l'ouverture de la fenêtre
     * Instancie les variables met en place l'affichage de la page
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        selectedElement = Controller.getCurrentElement();
        if(Controller.getForm().equals("updateElementForm")){
            saveButton.setText("Enregistrer");
            titleLabel.setText(selectedElement.toString());
            fillFields();
        }
        else if(Controller.getForm().equals("addElementForm")){
            saveButton.setText("Créer le produit");
            titleLabel.setText("Nouveau produit");
        }
        else if(Controller.getForm().equals("addSubForm")){
            search();
            titleLabel.setText(selectedElement.toString());
        }
        else if(Controller.getForm().equals("removeSubForm")){
            setListSub();
            titleLabel.setText(selectedElement.toString());
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
        /*Si le remplissage des champs est correct*/
        else{
            if(isInt()){
                Element e = makeElementByForm(isProdBox.isSelected());
                ArrayList<Element> allElt = Controller.getAllElements();
                /*Dans les cas ou l'action est d'ajouter un element*/
                if(Controller.getForm().equals("addElementForm") && !isAlreadyDefine()){
                    allElt.add(e);
                    saveDatas(allElt);
                    Controller.setCurrentElement(null);
                    Controller.setDataSaved(false);
                    finalize(action);
                    Alert alert = new Alert(AlertType.INFORMATION);
                    Controller.setAlert("Modifications effctuées", "Pensez à enregistrer les modifications avant de fermer le logiciel.", "Confirmation", alert);
                }
                /*Dans le cas ou l'action est de modifier un element*/
                else if(Controller.getForm().equals("updateElementForm") && (!isAlreadyDefine() || selectedElement.getCodeElt().equals(codeField.getText()))){
                    int i = allElt.indexOf(selectedElement);
                    /*Mise à jour de la liste de sous Element des Elements contenant selectedElement*/
                    e.setListLogsElement(selectedElement.getListLogsElement());
                    e.setListeOutils(selectedElement.getListeOutils());
                    e.setListeSousElements(selectedElement.getListeSousElements());
                    for(Element elt: allElt){
                        for(Element subElt : elt.getListeSousElements()){
                            if(subElt.getCodeElt().equals(selectedElement.getCodeElt())){
                                elt.updateElement(subElt, e);
                            }
                        }
                    }
                    allElt.set(i, e);
                    saveDatas(allElt);
                    Controller.setDataSaved(false);
                    Controller.setCurrentElement(null);
                    finalize(action);
                    Controller.updateConcernedWindows(selectedElement, e);
                    selectedElement = null;
                    Alert alert = new Alert(AlertType.INFORMATION);
                    Controller.setAlert("Modifications effectuées", "Pensez à enregistrer les modifications avant de fermer le logiciel.", "Confirmation", alert);
                }
                else{
                    Alert alert = new Alert(AlertType.WARNING);
                    Controller.setAlert("Erreur : ensemble déjà existant", "Le code saisi pour l'ensemble correspond à un ensemble déjà existant.", "Erreur", alert);
                }
            }          
        }
    }

    /**
     * Appelé lors d'un clic sur le bouton "Ajouter le sous-ensemble".
     * Renvoie une erreur si aucun ensemble n'a été sélectionné.
     * Ajoute le sous-ensemble sélectionné à la liste de sous-ensembles de
     * l'élément correspondant au formulaire (selectedElement) et sauvegarde cet ajout
     * @param action
     */
    @FXML
    public void addSubElt(ActionEvent action){
        if(selectedSub == null){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur : aucun ensemble sélectionné", "Veuillez sélectionner un ensemble à ajouter en tant que sous-ensemble.", "Erreur", alert);
        }
        else{
            selectedElement.addElement(selectedSub);
            saveDatas(Controller.getAllElements());
            selectedElement = null;
            selectedSub = null;
            Controller.setDataSaved(false);
            finalize(action);
            Alert alert = new Alert(AlertType.INFORMATION);
            Controller.setAlert("Modifications effectuées", "Pensez à enregistrer les modifications avant de fermer le logiciel.", "Confirmation", alert);
        }
    }

    /**
     * Appelée lors d'un clic sur le bouton "Enregistrer" du formulaire de 
     * suppression de sous-ensemble.
     * Ouvre un onglet d'erreur si aucun sous-ensemble n'a été sélectionné.
     * Retire le sous-ensemble sélectionné de la liste de sous-ensemble de 
     * l'ensemble sélectionné précedemment depuis l'accueil.
     * @param action
     */
    @FXML
    public void removeSubElt(ActionEvent action){
        if(selectedSub == null){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur : aucun ensemble sélectionné", "Veuillez sélectionner un sous-ensemble à retirer.", "Erreur", alert);
        }
        else{
            selectedElement.removeElement(selectedSub);
            saveDatas(Controller.getAllElements());
            selectedElement=null;
            selectedSub=null;
            Controller.setDataSaved(false);
            finalize(action);
            Alert alert = new Alert(AlertType.INFORMATION);
            Controller.setAlert("Modifications effectuées", "Pensez à enregistrer les modifications avant de fermer le logiciel.", "Confirmation", alert);
        }
    }

    /**
     * Appelée lors d'un changement de valeur sélectionné dans la ComboBox (liste)
     * de sous-éléments du formulaire de suppression de sous-ensemble.
     * Change la valeur du sous-enseble courrant sélectionné par la valeur courrante
     * de la ComboBox.
     */
    @FXML
    public void selectSub(ActionEvent action){
        selectedSub = Controller.getElementByCode(Controller.sliceCode(listSubElt.getValue()));
    }

    /**
     * Appelé lors d'un clic sur le bouton "Annuler"
     * Décrémente le nombre de formulaire ouvert et ferme le formulaire courant
     * @param action
     */
    @FXML
    public void closeForm(ActionEvent action){
        Controller.setCountOpenedForm(Controller.getCountOpenedForm()-1); //décrémentation du nb de form ouverts
        (((Node) action.getSource())).getScene().getWindow().hide();//fermeture du formulaire
    }



    /*Construction et modification d'objets Element */

    /**
     * Construit un objet Element  de type produit avec les données entrées dans les champs de
     * textes du formulaire de création d'ensemble courant
     */
    public Element makeElementByForm(boolean isProduct){
        String code = codeField.getText();
        String nom = nameField.getText();
        return new Element(nom, code, isProduct);
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

    /**
     * Pré-rempli les champs de saisi du formulaire de modification d'objet Element
     */
    public void fillFields(){
        nameField.setText(selectedElement.getNom());
        codeField.setText(selectedElement.getCodeElt());
        isProdBox.setSelected(selectedElement.isProduit());
    }

    /**
     * Vérifie que tous les caractères du champs de saisi du code de l'Element
     * sont tous des chiffres
     * @return true si la chaine n'est composé que de chiffres, false sinon
     */
    public boolean isInt(){
        for(int i=0; i<codeField.getText().length(); i++){
            if(!Character.isDigit(codeField.getText().charAt(i))){
                Alert alert = new Alert(AlertType.WARNING);
                Controller.setAlert("Erreur  de saisi", "Le code saisi pour l'element doit être composé uniquement de chiffres", "Erreur", alert);
                return false;
            }
        }
        return true;
    }



    /*Ajout de sous-ensemble*/

    /**
     * Instancie l'évènement provoquant la mise à jour de la liste d'éléments 
     * correspondant à la recherche courante
     * L'événement est appelé à chaque modification du texte dans le champs de recherche
     */
    public void search(){
        searchField.textProperty().addListener(new ChangeListener<String>(){
            public void changed(ObservableValue<? extends String> observable,
                String oldValue, String newValue){
                    fillResultsBox(searchField.getText());
                }
        });
    }

    /**
     * Rempli la liste des résultats obtenus depuis la saisi dans le champs de recherche
     * avec des Labels contenant nom et code de chaque ensemble correspondant à la recherche.
     * Affiche les résultats de recherche s'il en existe
     * @param value la valeur sur laquelle s'effectue la recherche
     */
    public void fillResultsBox(String value){
        resultsBox.getChildren().clear();
        /*Si le champs de saisi n'est pas vide*/
        if(value.length()>2){
            sPane.setPrefHeight(5);
            for(String s : setSearchedElements(value)){
                Label l = new Label(s);
                l.setPrefHeight(26);
                l.setPrefWidth(350); 
                Controller.setMouseOverEvent(l);
                Controller.setMouseOutEvent(l);
                setClickedEvent(l);
                resultsBox.getChildren().add(l);
                sPane.setPrefHeight(sPane.getPrefHeight() + 26);
            }
            /*S'il existe des résultat à la recherhe*/
            if(resultsBox.getChildren().size()!=0){
                sPane.setVisible(true);
                resultsBox.setVisible(true);
            }
            /*Si aucun Element similaire à la rcherche n'a été trouvé*/
            else{
                sPane.setVisible(false);
                resultsBox.setVisible(false);
            }  
        }
        else{
            sPane.setVisible(false);
            resultsBox.setVisible(false);
        }
    }

    /**
     * Instancie la liste de chaines de caractère dans laquelle va être effectué 
     * la recherche d'ensembles pour l'ajout en tant que sous-ensemble.
     * Un ensemble est ajoutable en tant que sous-ensemble s'il n'est pas égal au parent,
     * s'il n'est pas déjà un sous ensemble et s'il n'est pas parent du parent.
     * @param value la valeur sur laquelle s'effectue la recherche
     * @return une liste de chaiîne de caractère correspondant à tous les ensembles
     * ajoutables en tant que sous ensemble
     */
    public ArrayList<String> setSearchedElements(String value){
        ArrayList<String> elementsList = Controller.setSearchedElements(value);
        for(int i=0; i<elementsList.size(); i++){
            /*Si le code de l'Element est au début de la chaine de caractères*/
            if(Character.isDigit(elementsList.get(i).charAt(0))){
                /*On retire tous les ensembles non ajoutables de la liste*/
                if(Controller.sliceCode(elementsList.get(i)).equals(selectedElement.getCodeElt())
                    || selectedElement.isASubElt(Controller.sliceCode(elementsList.get(i)))
                    || Controller.getElementByCode(Controller.sliceCode(elementsList.get(i))).isParent(Controller.getAllElements(), selectedElement)){
                    elementsList.remove(i);
                    i--;
                }
            }
            /*Si le code de l'Element est à la fin de la chaine de caractères*/
            else{
                /*On retire tous les ensembles non ajoutables de la liste*/
                if(Controller.sliceInvertCode(elementsList.get(i)).equals(selectedElement.getCodeElt())
                    || selectedElement.isASubElt(Controller.sliceInvertCode(elementsList.get(i)))
                    || Controller.getElementByCode(Controller.sliceInvertCode(elementsList.get(i))).isParent(Controller.getAllElements(), selectedElement)){
                    elementsList.remove(i);
                    i--;
                }
            }
        }
        return elementsList;
    }

    /**
     * Instancie l'événement appelé lorsqu'un label de la liste des rsultats de
     * recherche est cliqué
     * Récupère l'Element lié au label et donne cette valeur au sous-élement courant
     * Masque les résultats de recherche et compléte le champs avec le texte du labe
     * @param l un label résultat de recherche
     */
    public void setClickedEvent(Label l){
        l.setOnMouseClicked((event) -> {
            searchField.setText(l.getText());
            if(Character.isDigit(l.getText().charAt(0))){
                selectedSub = Controller.getElementByCode(Controller.sliceCode(l.getText()));
            }
            else{
              selectedSub = Controller.getElementByCode(Controller.sliceInvertCode(l.getText()));
            }
            sPane.setVisible(false);
        });
    }



    /*Suppression de sous-ensembles*/

    /**
     * Met en place la ComboBox de sous-ensembles liée à l'élement sélectionné.
     * Ajoute sous forme de chaine de caractères tous les sous-Elements de 
     * l'élément sélectionné à la liste de choix de la ComboBox.
     */
    public void setListSub(){
        ArrayList<Element> subElements = selectedElement.getListeSousElements();
        Element.sortElements(subElements);
        listSubElt.getItems().clear();
        for(Element e: subElements){
            String choice = e.toString();
            listSubElt.getItems().add(choice);
        }
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

    /**
     * Met à jour les données dans le fichier de stockage et dans la liste de 
     * tous les objets Element du Controller
     * @param listElt la nouvelle liste d'elements à sauvegarder
     */
    public void saveDatas(ArrayList<Element> listElt){
        Element.sortElements(listElt);
        Controller.setAllElements(listElt);
    }



    /*Getter et setter*/

    public static Controller getOriginControll() {
        return originControll;
    }

    public static void setOriginControll(Controller originControll) {
        ControllerFormEnsembles.originControll = originControll;
    } 

}
