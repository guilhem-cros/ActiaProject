package Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Element;
import Model.Logs;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ControllerAffichageLogs implements Initializable{

    /*L'élément sélectionné depuis l'accueil*/
    private Model.Element selectedElt;

    /*La liste des logs de l'élément sélectionné*/
    private ArrayList<Logs> listLogs;

    /*La liste des lignes clicables (sélectionnables) dans la grille*/
    private ArrayList<ArrayList<TextField>> clicableItems;

    /*La liste de tous les champs de texte contenus dans la grille*/
    private ArrayList<TextField> valuesInGrid;

    /*L'objet Logs (la ligne) courament sélectionnée*/
    private Logs currentLogs;


    /*Initialisation des objets XML utilisés*/

    @FXML
    private GridPane titleGrid;

    @FXML
    private GridPane logsGrid;

    @FXML
    private Pane pane;

    @FXML
    private ScrollPane sp;

    @FXML
    private Label infoLabel;

    @FXML
    private Label titleLabel;

    @FXML
    private Button updateButton;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    @FXML 
    private Button saveButton;

    @FXML
    private Button cancelButton;
    

    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        /*Mise en place de la liste des ControllerAffichages "ouverts"*/
        if(Controller.getOpenedControllerLogs() == null){
            Controller.setOpenedControllerLogs(new ArrayList<ControllerAffichageLogs>());
        }
        if(!Controller.getOpenedControllerLogs().contains(this)){
            Controller.getOpenedControllerLogs().add(this);
        }
        currentLogs = null;
        setParam();
        initGrid();
        selectForModif();
        setVisibility();
    }



    /*Fonctions d'events FXML*/

    /**
     * Appelée lors d'un appui sur le bouton "Ajouter"
     * Crée une ligne éditable vide dans le tableau de logs
     * Ajuste l'affichage de la page
     * @param action
     */
    @FXML
    public void setAddMode(ActionEvent action){
        resetLabels();
        currentLogs= null;
        saveButton.setVisible(true);
        cancelButton.setVisible(true);
        int line = clicableItems.size();
        for(int i=0; i<3; i++){
            TextField tf = new TextField();
            setParamTextF(tf);
            tf.setEditable(true);
            logsGrid.add(tf, i, line);
            valuesInGrid.add(tf);
            sp.setPrefHeight(sp.getHeight()+40);
        }
        sp.setVisible(true);
        titleGrid.setVisible(true);
        infoLabel.setVisible(false);
    }

    /**
     * Appelée lors d'nun appuie sur le bouton "Enregistrer"
     * Récupère toutes les données de la grille sous forme de logs
     * et met à jour la liste de Logs de l'éléments sélectionné
     * Actualise la page
     * @param action
     */
    @FXML
    public void saveChanges(ActionEvent action){
        if(protectUpdate()){
            if(setUpEmptyLogError()){
                ArrayList<Logs> listLogs = readTable();
                selectedElt.setListLogsElement(listLogs);
                Element.serializeAllElements(Controller.getAllElements());
                cancelButton.setVisible(false);
                saveButton.setVisible(false);
                finalize();
            }
        }
    }

    /**
     * Appelée lors d'un appuie sur le bouton "Annuler"
     * Désactive la possibilité de modification de la grille
     * Rend invisible "Enregistrer" et "Annuler"
     * @param action
     */
    @FXML
    public void cancelChanges(ActionEvent action){
        cancelButton.setVisible(false);
        saveButton.setVisible(false);
        this.initialize(null, null);
    }

    @FXML
    public void deleteLogs(ActionEvent action){
        if(protectUpdate()){
            if(currentLogs==null){
                Alert alert = new Alert(AlertType.WARNING);
                Controller.setAlert("Erreur, sélection invalide", "Veuillez sélectionner une ligne à supprimer.", "Erreur", alert);
            }
            else{
                Alert alert = new Alert(AlertType.CONFIRMATION, "Supprimer la ligne des données?", ButtonType.YES, ButtonType.CANCEL);
                alert.showAndWait();
                if(alert.getResult()==ButtonType.YES){
                    /*Suppression de currentLog dans la liste*/
                    for(int i=0; i<listLogs.size(); i++){
                        if(listLogs.get(i).equals(currentLogs)){
                            listLogs.remove(i);
                        }
                    }
                    selectedElt.setListLogsElement(listLogs);
                    Element.serializeAllElements(Controller.getAllElements());
                    finalize();
                    currentLogs=null;
                }
            }
        }
    }



    /*Paramètres d'affichage et initialisation de l'onglet'*/

    /**
     * Initialise les variables nécessaires et met en place les paramètres visuels de base
     */
    public void setParam(){
        if(selectedElt==null){
            selectedElt = Controller.getCurrentElement(); //récupération de l'élèments sélectionnés précédemment dans la page d'accueil
        }
        listLogs = selectedElt.getListLogsElement();
        titleLabel.setText(selectedElt.getCodeElt() + " " + selectedElt.getNom());
        sp.setStyle("-fx-background-color:transparent;");
        BackgroundFill bf = new BackgroundFill(Color.rgb(171, 171, 171), CornerRadii.EMPTY , Insets.EMPTY);
        titleGrid.setBackground(new Background(bf));
    }

    /**
     * Initialise le tableau de login pour l'affichage
     * Si l'élément sélectionné ne possède pas d'objets Logs, 
     * affiche seulement un message informatif
     */
    public void initGrid(){
        logsGrid.getChildren().clear();
        clicableItems = new ArrayList<ArrayList<TextField>>();
        valuesInGrid = new ArrayList<TextField>();
        /*Si l'élément n'a pas de Logs*/
        if(!selectedElt.hasLogs()){
            infoLabel.setText("Aucune information concernant les mots de passes pour l'ensemble " + selectedElt.getCodeElt()); //message informatif
            infoLabel.setVisible(true);//affichage du message
            sp.setVisible(false); 
            titleGrid.setVisible(false); //invisibilité du tableau
        }
        else{
            int count = 0; //l'indice de ligne courante du tableau
            /*Pour tous les Logs de l'élément sélectionné*/
            for(Logs l: listLogs){ 
                ArrayList<TextField> line = new ArrayList<TextField>();
                /*création d'un champ de txt pour chaque paramètre*/
                TextField tf1 = new TextField(l.getParagraphe());
                TextField tf2 = new TextField(l.getLogin());
                TextField tf3 = new TextField(l.getPassword());
                /*mise en place de l'affichage des labels*/
                setParamTextF(tf1); 
                setParamTextF(tf2);
                setParamTextF(tf3);
                /*Ajout des labels à la grille*/
                logsGrid.add(tf1, 0, count);
                logsGrid.add(tf2, 1, count);
                logsGrid.add(tf3, 2, count);
                count++;
                /*Ajout des labels à la liste des objets clicables*/
                line.add(tf1);
                line.add(tf2);
                line.add(tf3);
                clicableItems.add(line);
                valuesInGrid.addAll(line);
            }
            resetLabels();
            sp.setPrefHeight(40*listLogs.size() + 15); //adaptation de la taille du scrollPane à la taille du tableau si ppssible
        }
    }

    /**
     * Met en place des paramètres visuels d'un champ de texte
     * @param t le champs dont on change les paramètres visuels
    */ 
    public void setParamTextF(TextField t){
        t.setFont(Font.font(18));
        t.setPrefHeight(40);
        t.setPrefWidth(140);
        t.setAlignment(Pos.CENTER);
        t.setStyle("-fx-border-color: grey;");
        t.setEditable(false);
    }

    /**
     * Adapte la visibilité des boutons et l'affichage global de la
     * fenêtre en fonction de l'activation du mode admin
     */
    public void setVisibility(){
        addButton.setVisible(Controller.isAdmin());
        updateButton.setVisible(Controller.isAdmin());
        deleteButton.setVisible(Controller.isAdmin());
    }

     /**
     * Passe la couleur de background de tous les éléments de la grille en blanc.
     */
    public void resetLabels(){
        BackgroundFill bf = new BackgroundFill(Color.WHITE, CornerRadii.EMPTY , Insets.EMPTY);
        for(ArrayList<TextField> line: clicableItems){
            for(TextField tf: line){
                tf.setBackground(new Background(bf));
            }
        }    
    }
    


    /*Fonctions nécessaires au mode admin*/
    
    /**
     * Instancie l'évènement de type clic sur toutes les lignes de la grille 
     * si le logiciel est en mode admin. 
     * L'objet Logs courant sélectionné prend la valeur de l'objet de la ligne 
     * cliqué et le fond de la ligne change de couleur.
     */
    public void selectForModif(){
        if(Controller.isAdmin()){ //ne fonctionne que dans le cas ou le logiciel est en mode admin
            BackgroundFill bf = new BackgroundFill(Color.rgb(204, 255, 179), CornerRadii.EMPTY , Insets.EMPTY); //la couleur de la ligne cliqué
            /*parcours de toutes les lignes de la grille*/
            for(int i=0; i<clicableItems.size(); i++){
                /*Parcours de chaque case de la grille*/
                ArrayList<TextField> listAux = clicableItems.get(i);
                for(TextField tf : listAux){
                    tf.setOnMouseClicked(event -> { //ajout de l'évènement à chaque case
                        resetLabels();
                        /*Changement de couleur sur toute la ligne sélectionnée*/
                        for(TextField textF: listAux){
                            textF.setBackground(new Background(bf));
                        }
                        currentLogs = new Logs(listAux.get(0).getText(), listAux.get(1).getText(), listAux.get(2).getText());
                    });
                }
            }
        }
    }

    /**
     * Récupère toutes les chaines de caractères stockées dans la table et les 
     * traduit sous forme d'objets Logs
     * @return la liste des tous les objets Logs traduits depuis les données de la grille
     */
    public ArrayList<Logs> readTable(){
        ArrayList<String> listString = new ArrayList<String>();
        ArrayList<Logs> listL = new ArrayList<Logs>();
        for(TextField tf: valuesInGrid){
            listString.add(tf.getText());
        }
        for(int i=0; i<listString.size(); i+=3){
            Logs l = new Logs(listString.get(i), listString.get(i+1), listString.get(i+2));
            listL.add(l);
            
        }
        return listL;
    }



    /*Mise en place des erreurs utilisateurs*/

    /**
     * Ouvre un ounglet d'erreur dans le cas ou plusieurs onglet d'affichages de Logs
     * du même ensemble selectedElt sont ouverts afin d'éviter les conflits de modifications
     * @return false si plus d'un onglet est ouvert, true sinon
     */
    public boolean protectUpdate(){
        int count =0;
        for(ControllerAffichageLogs c: Controller.getOpenedControllerLogs()){
            if(c.getSelectedElt().getCodeElt().equals(this.selectedElt.getCodeElt())){
                count++;
            }
        }
        if(count>1){
            Alert alert = new Alert(AlertType.WARNING);
            Controller.setAlert("Erreur, conflits potentiels", "Un ou plusieurs autres onglets de mots de passe de l'ensemble " + selectedElt.getCodeElt() + " sont ouverts, veuillez les fermer pour continuer.", "Erreur", alert);
            return false;
        }
        return true;
    }

    /**
     * Ouvre une fenêtre d'erreur dans le cas ou une case du tableau est vide
     * @return true si aucune case est vide, false sinon
     */
    public boolean setUpEmptyLogError(){
        for(TextField tf : valuesInGrid){
            if(tf.getText()==null || tf.getText().length()<1){
                Alert alert = new Alert(AlertType.WARNING);
                Controller.setAlert("Erreur, un ou plusieurs champs sont vides", "Veuillez compléter tous les champs avant d'enregistrer les modifications", "Erreur", alert);
                return false;
            }
        }
        return true;
    }



    /**
     * Instancie et affiche un onglet de confirmation
     * Actualise la page courante
     */
    public void finalize(){
        Alert alert = new Alert(AlertType.INFORMATION);
        Controller.setAlert("Modifications effectuées", "Les modifications apportées ont bien été enregistrées.", "Confirmation", alert);
        /*Actualisation de toutes les page d'affichage ouvertes*/
        this.initialize(null, null);
    }



    /*Getter et setter*/

    public Model.Element getSelectedElt() {
        return selectedElt;
    }

    public void setSelectedElt(Model.Element selectedElt) {
        this.selectedElt = selectedElt;
    }

    public Pane getPane() {
        return pane;
    }

    public void setPane(Pane pane) {
        this.pane = pane;
    }

    
}
