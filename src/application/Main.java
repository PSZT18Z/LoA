package application;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application{

	@Override
	public void start(Stage primaryStage) throws Exception{
		try{
			FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("LoA.fxml"));
			Pane root = fxmlLoader.load();
			Scene scene = new Scene(root);
			primaryStage.setScene(scene);
			primaryStage.setTitle("LoA");
			primaryStage.setResizable(false);
			//LoAController loaController = fxmlLoader.getController();
			//primaryStage.setOnHiding(e -> loaController.close());
			primaryStage.show();
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}

	public static void main(String[] args){
		launch(args);
	}

}