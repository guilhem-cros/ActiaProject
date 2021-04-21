

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ControllerLogs implements Initializable{

    /*L'élément sélectionné depuis l'accueil*/
    private Element selectedElt;

    /*La liste des logs de l'élément sélectionné*/
    private ArrayList<Logs> listLogs;


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
    
    

    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setParam();
        initGrid();
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
            int count = 0; //l'indice de ligne courante du tableau
            /*Pour tous les Logs de l'élément sélectionné*/
            for(Logs l: listLogs){ 
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
    }

    
}
