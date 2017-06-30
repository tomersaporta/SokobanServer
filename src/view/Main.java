package view;
	
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import server.MyServer;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {
			MyServer server=new MyServer(9780);
			
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					server.start();
					
				}
			}).start();
			
			FXMLLoader loader=new FXMLLoader(getClass().getResource("MainWindow.fxml"));
			AnchorPane root = (AnchorPane)loader.load();
			MainWindowController view=loader.getController();
			
			Scene scene = new Scene(root,600,600);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.show();
			
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
