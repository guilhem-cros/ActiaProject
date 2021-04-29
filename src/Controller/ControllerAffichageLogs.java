package Controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import Model.Logs;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
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
    private ArrayList<ArrayList<Label>> clicableItems;

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
    

    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        currentLogs = null;
        setParam();
        initGrid();
        selectForModif();
    }


    /**
     * Initialise les variables nécessaires et met en place les paramètres visuels de base
     */
    public void setParam(){
        selectedElt = Controller.getCurrentElement(); //récupération de l'élèments sélectionnés précédemment dans la page d'accueil
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
        /*Si l'élément n'a pas de Logs*/
        if(!selectedElt.hasLogs()){
            infoLabel.setText("Aucune information concernant les mots de passes pour l'ensemble " + selectedElt.getCodeElt()); //message informatif
            infoLabel.setVisible(true);//affichage du message
            sp.setVisible(false); 
            titleGrid.setVisible(false); //invisibilité du tableau
        }
        else{
            clicableItems = new ArrayList<ArrayList<Label>>();
            int count = 0; //l'indice de ligne courante du tableau
            /*Pour tous les Logs de l'élément sélectionné*/
            for(Logs l: listLogs){ 
                ArrayList<Label> line = new ArrayList<Label>();
                /*création d'un label pour chaque paramètre*/
                Label lab1 = new Label(l.getParagraphe());
                Label lab2 = new Label(l.getLogin());
                Label lab3 = new Label(l.getPassword());
                /*mise en place de l'affichage des labels*/
                setParamLabel(lab1); 
                setParamLabel(lab2);
                setParamLabel(lab3);
                /*Ajout des labels à la grille*/
                logsGrid.add(lab1, 0, count);
                logsGrid.add(lab2, 1, count);
                logsGrid.add(lab3, 2, count);
                count++;
                /*Ajout des labels à la liste des objets clicables*/
                line.add(lab1);
                line.add(lab2);
                line.add(lab3);
                clicableItems.add(line);
            }
            sp.setPrefHeight(36*listLogs.size() + 10); //adaptation de la taille du scrollPane à la taille du tableau si ppssible
        }
    }

    /**
     * Met en place des paramètres visuels d'un label
     * @param l le label dont on change les paramètres visuels
    */ 
    public void setParamLabel(Label l){
        l.setFont(Font.font(18));
        l.setPrefHeight(40);
        l.setPrefWidth(140);
        l.setAlignment(Pos.CENTER);
        l.setStyle("-fx-border-color: grey;");
    }

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
                ArrayList<Label> listAux = clicableItems.get(i);
                for(Label lab : listAux){
                    lab.setOnMouseClicked(event -> { //ajout de l'évènement à chaque case
                        resetLabels();
                        /*Changement de couleur sur toute la ligne sélectionnée*/
                        for(Label label: listAux){
                            label.setBackground(new Background(bf));
                        }
                        currentLogs = new Logs(listAux.get(0).getText(), listAux.get(1).getText(), listAux.get(2).getText());
                    });
                }
            }
        }
    }

     /**
     * Passe la couleur de background de tous les éléments de la grille en blanc.
     */
    public void resetLabels(){
        BackgroundFill bf = new BackgroundFill(Color.WHITE, CornerRadii.EMPTY , Insets.EMPTY);
        for(ArrayList<Label> line: clicableItems){
            for(Label lab : line){
                lab.setBackground(new Background(bf));
            }
        }    
    }
    
}
