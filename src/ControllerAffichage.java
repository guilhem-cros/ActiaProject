

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.HostServices;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class ControllerAffichage implements Initializable{

    /*L'élément sélectionné depuis l'accueil*/
    private Element selectedElt;

    /*La liste des test de selectedElt*/
    private ArrayList<Test> listTest;

    /*La liste de tous les Moyens génériques*/
    private  ArrayList<MoyenGenerique> moyensGene = MoyenGenerique.unserializeMoyenGene();

    /*La liste des titres des colonnes*/
    private static ArrayList<String> paramTitles = Test.getParamTitle();
    
    private HostServices hostServices;

    /*Initialisation des objets XML utilisés*/

    @FXML
    private ImageView logoActia;

    @FXML
    private Label title;

    @FXML
    private GridPane grid;

    @FXML 
    private Pane pane;

    @FXML
    private ScrollPane sp;

    @FXML
    private Label emptyLabel;

    @FXML
    private ComboBox<String> listMoyenGene;
    
    
    /**
     * Fonction appelée à l'ouverture de la fenêtre
     * Initialise les champs et variables
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setParamOfElt();
        listMoyenGene.getItems().clear();
        setComboBoxMoyenGene();
        setTable(null);
    }

    /**
     * récupère la valeur de la comboBox et met à jour
     * l'affichage en fonction de celle-ci
     * @param action
     */
    @FXML
    public void selectMoyenGene(ActionEvent action){
        String value = listMoyenGene.getValue();
        setTable(value);
    }

    /*Permet d'utiliser les hostsservices depuis le controller*/
    public void setGetHostController(HostServices hostServices){
        this.hostServices = hostServices;
    }       
    
    /**
     * Récupère l'élément sélectionné depuis la page accueil
     * Récupère les tests correspondants à cet élément en
     * fonction du mode de test (auto / manuel)
     * Crée le titre de la page
     */
    public void setParamOfElt(){
        selectedElt = Controller.getCurrentElement();
        if(Controller.isAuto() && Controller.isManuel()){ //si les 2 checkboxs ont été cochées
            listTest = selectedElt.getTests();
        }   
        else { //si une seule des checkbox a été cochée
            listTest = selectedElt.getTestsByMode(Controller.isAuto());
        }
        title.setText(selectedElt.getCodeElt() + " " + selectedElt.getNom());
    }


    /**
     * Si la liste Test est vide : renvoie un message 
     * indiquant qu'aucun moyen de test n'est diponible
     * Sinon : Rempli la grille avec les attributs 
     * correspondant à chaque moyens de test de l'élément
     * en fonction du moyen générique sélectionné (renvoie
     * le même message si l'élément ne contient aucun test 
     * pour ce moyen générique).
     */
    public void setTable(String selectedMoyenGene){
        grid.getChildren().clear();//la grille est réinitialisée
        ArrayList<Test> toPrintTests = initPrintedTests();
        if(toPrintTests.isEmpty()){
            /*invisibilité de la grille et affichage du label*/
            grid.setVisible(false);
            sp.setVisible(false);
            emptyLabel.setText("Aucun outil de test correspondant à la recherche pour l'élément " + selectedElt.getCodeElt());
            emptyLabel.setVisible(true);
        }
        else{
            initColumnTitle(grid); //mise en place des titres
            int count = 1; //le numéro de la ligne actuelle
            /*Parcours de tous les test de l'élément*/
            for(Test t: toPrintTests){
                ArrayList<String> listParam = t.getListParam();
                int countP=0; //me numéro de la colonne actuelle
                /*Parcours de tous les attributs du test courant*/
                for(int i=0; i<t.getListParam().size(); i++){ 
                    String param = listParam.get(i);
                    if((i==12||i==15)&& param!=null){ //s'il s'agit d'un attribut de type lien
                        grid.add(setLink(param), countP, count); //ajout / remplissage de la case courante de grid
                    }
                    else{
                        Label text = new Label(param);
                        /*Les paramètres visuels de text*/
                        //text.setBorder(new Border());
                        text.setWrapText(true); 
                        text.setAlignment(Pos.CENTER);
                        text.setTextAlignment(TextAlignment.CENTER);
                        text.setFont(Font.font(16));
                        text.maxHeight(100);
                        text.setPrefHeight(100);
                        text.maxWidth(160);
                        text.setPrefWidth(160);
                        text.setStyle("-fx-border-color: grey;");
                        grid.add(text, countP, count); //ajout / remplissage de la case courante de grid
                    }
                    countP++;
                }
                /*ajustement de la taille du scrollpane en fct de la taille du gridPane*/
                sp.setPrefHeight(100*(count+1)+20); //incrémentation de la hauteur du scrollpane en fct du nb de lignes de grid
                count++;
            }
            /*affichage de la grille et invisibilité du label*/
            grid.setVisible(true);
            sp.setVisible(true);
            emptyLabel.setVisible(false);
        }
    }

    /**
     * Récupère les moyens de tests de l'élément en fonction 
     * d'un moyen générique
     * @param moyen le nom du moyen générique voulu
     * @return l'ensemble des tests de l'élément pour lesquels
     * le moyen générique correspond au paramètre
     */
    public ArrayList<Test> getTestsByMoyenGene(String moyen){
        ArrayList<Test> listTestByMoy = new ArrayList<Test>();
        /*Parcours de l'ensemble des tests de l'élément*/
        for(Test t: listTest){
            if(moyen.equals(t.getMoyenGenerique())){
                listTestByMoy.add(t);
            }
        }
        return listTestByMoy;
    }

    /**
     * @return la liste de test correspondant à la valeur courante 
     * de la ComboBox
     */
    public ArrayList<Test> initPrintedTests(){
        if(listMoyenGene.getValue()==null||listMoyenGene.getValue().equals("Tous")){ //retourne tous les tests de l'élément si la Combobox n'a pas encore de valeur
            return listTest;
        }
        else{
            return getTestsByMoyenGene(listMoyenGene.getValue());
        }
    }

    /**
     * Met en place les "titres" des colonnes du tableau 
     * sur la premiere ligne de grid
     */
    public void initColumnTitle(GridPane gridp){
        gridp.getChildren().clear();
        int count = 0; // la colonne courante
        /*Parcours de la liste des "titres"(~nom des variables de Test) du tableau*/
        for(String s : paramTitles){
            Label txt = new Label(s);
            txt.setWrapText(true);
            txt.setAlignment(Pos.CENTER);
            txt.setTextAlignment(TextAlignment.CENTER);
            txt.setFont(Font.font(18));
            txt.maxHeight(100);
            txt.setPrefHeight(100);
            txt.maxWidth(160);
            txt.setPrefWidth(160);
            txt.setStyle("-fx-border-color: grey;");
            BackgroundFill bf = new BackgroundFill(Color.rgb(171, 171, 171), CornerRadii.EMPTY , Insets.EMPTY);
            txt.setBackground(new Background(bf));
            gridp.add(txt, count, 0); //ajout de la case courante remplie à grid
            count++;
        }
    }

    /**
     * Crée une comboBox contenant la liste des Moyens génériques enregistrés
     * @return la combo box contenant tous les moyens génériques
     */
    public void setComboBoxMoyenGene(){
        listMoyenGene.getItems().clear();
        listMoyenGene.setStyle("-fx-font-f: 16px;");
        MoyenGenerique.sortMoyenGen(moyensGene);//tri par ordre alphabétique
        for(MoyenGenerique m : moyensGene){
            String chaine = m.getNom();
            listMoyenGene.getItems().add(chaine);
        }
        
    }

    /**
     * Création d'un lien hypertexte
     * Ouvre une fenêtre vers le fichier pointé
     * @param link chaine de caractère correspondant au 
     * lien absolu vers le fichier visé
     * @return le lien hypextexte ouvrant le fichier visé
     */
    public Hyperlink setLink(String link){
        setGetHostController(hostServices); //initialisation des hostServices pour l'ouverture de fichier
        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialDirectory(new File(link));
        Hyperlink text = new Hyperlink(link);
        text.setOnAction(e -> { 
            Lanceur l = new Lanceur();
            File selectedFile = new File(link);
            /*si le fichier selectionné n'existe pas ou n'a pas été trouvé, un onglet alert est ouvert*/
            if(!selectedFile.isFile()){
                /*instanciation de l'alerte*/
                Alert alert = new Alert(AlertType.WARNING);
                alert.setTitle("Erreur");
                TilePane alertpane = new TilePane();
                Scene scene = new Scene(alertpane, 320, 240);
                Stage stage = new Stage();
                stage.setScene(scene);
                try {
                    alert.setHeaderText("Erreur : fichier introuvable");
                    alert.setContentText("Le fichier recherché semble avoir été renommé ou déplacé. Veuillez contacter un administrateur afin de mettre à jour son emplacement.");
                    alert.showAndWait(); //ouverture de l'onglet d'erreur
                    /*L'onglet d'affichage reste ouvert mais innaccessible tant que l'onglet erreur n'a pas été fermé*/
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
            /*si le fichier selectionné existe il est ouvert*/
            else{
               l.getHostServices().showDocument(selectedFile.getAbsolutePath());  
            } 
        }); 
        /*paramètres visuels du lien*/
        text.setWrapText(true);
        text.setWrapText(true); 
        text.setAlignment(Pos.CENTER);
        text.setTextAlignment(TextAlignment.CENTER);
        text.setFont(Font.font(16));
        text.maxHeight(100);
        text.setPrefHeight(100);
        text.maxWidth(160);
        text.setPrefWidth(160);
        return text;
    }

    
}
