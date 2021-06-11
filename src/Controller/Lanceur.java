package Controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;

import java.io.File;
import java.io.IOException;
import java.net.URLDecoder;

public class Lanceur extends Application{

	private static String currentPath;

    public static void main(String[] args){
    	setPath();
        launch(args);
    }

	/**
	 * Appelée au lancement de l'application.
	 * Met en place la fenêtre d'accueil du logiciel.
	 * @param stage
	 */
	@Override
    public void start(Stage stage){
        try {
			Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("View/accueil.fxml"));
			Scene scene = new Scene(root);
			stage.setScene(scene);
			/*Evenement lors de la fermeture de la fenêtre : erreur si des données n'ont pas été enregistrées*/
			stage.setOnCloseRequest(event ->{
				if(!Controller.isDataSaved()){
					Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Quitter sans enregistrer ?", ButtonType.YES, ButtonType.CANCEL);
					alert.showAndWait();
					if(alert.getResult()==ButtonType.YES){
						stage.close();
					}
					else{
						event.consume();
					}
				}
			});
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

	/**
	 * Vérifie la présence des dossiers de stockage du logiciel
	 * @return true si les dossiers sont présent, false si au moins un
	 * d'entre eux est absent
	 */
	public static boolean hasData(){
		File f1 = new File(currentPath + "data/config.ser");
		File f2 = new File(currentPath + "data/columns.ser");
		File f3 = new File(currentPath + "data/element.ser");
		File f4 = new File(currentPath + "data/moyensGeneriques.ser");
		File f5 = new File(currentPath + "data/ordre.ser");
		if(!f1.isFile() || !f2.isFile() || !f3.isFile()|| !f4.isFile()|| !f5.isFile()){
			return false;
		}
		return true;
	}

	public static String getCurrentPath() {
		return currentPath;
	}

	/**
	 * Récupère l'emplacement du fichier depuis lequel est lancé le soft.
	 * Adapte le lien vers le document d'aide en fonction de cet emplacement.
	 */
	public static void setPath(){
		try {
			String path = Lanceur.class.getProtectionDomain().getCodeSource().getLocation().getPath();
			String decodedPath = URLDecoder.decode(path, "UTF-8");
			String folderPath = decodedPath.substring(0, decodedPath.lastIndexOf('/') + 1);
			currentPath = folderPath;
		}
		catch (IOException e){
			e.printStackTrace();
		}
	}
}
