package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.io.File;


public class Lanceur extends Application{

    public static void main(String[] args){
        launch(args);
    }
    
	/*Création et ouverture de la fenêtre d'accueil*/
    @Override
    public void start(Stage stage){
        try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/accueil.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			stage.setTitle("Menu Principal");
			stage.getIcons().add(new Image("media/logoActiaPetit.png"));
			stage.setResizable(false);
			stage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
        if(!hasData()){
        	Alert alert = new Alert(Alert.AlertType.WARNING);
        	Controller.setAlert("Erreur : data introuvable", "Les fichiers de stockage des données du logiciel sont introuvables.", "Erreur", alert);
		}
	}

	public static boolean hasData(){
		File f1 = new File(AppLaunch.getCurrentPath() + "data/config.ser");
		File f2 = new File(AppLaunch.getCurrentPath() + "data/columns.ser");
		File f3 = new File(AppLaunch.getCurrentPath() + "data/element.ser");
		File f4 = new File(AppLaunch.getCurrentPath() + "data/moyensGeneriques.ser");
		File f5 = new File(AppLaunch.getCurrentPath() + "data/ordre.ser");
		if(!f1.isFile() || !f2.isFile() || !f3.isFile()|| !f4.isFile()|| !f5.isFile()){
			return false;
		}
		return true;
	}
    
}
