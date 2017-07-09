package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import model.admin.AdminModel;
import server.MyServer;
/**
 * 
 * Manage the GUI of the admin of the server
 *
 */
public class MainWindowController implements Initializable{
	
	@FXML
	private ListView myListView;
	
	private AdminModel adminModel;
	
	private ListProperty<String> listProp= new SimpleListProperty<>();

	private Stage primaryStage;
	
	private MyServer server;

	@FXML
	private void handleButtonAction(ActionEvent event) {
		updateList();
	}

	
	private void updateList() {
		listProp.set(FXCollections.observableArrayList(adminModel.getClients()));
	}

	/**
	 * Binding the list of clients to the list in the modeld
	 */
	@Override
	public void initialize(URL location, ResourceBundle resources) {
		adminModel = AdminModel.getInstance();      
		myListView.itemsProperty().bind(listProp);
		listProp.set(FXCollections.observableArrayList(adminModel.getClients()));
	}
	
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
		exitPrimaryStage();

	}
	
	public void exitPrimaryStage() {
		this.primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent event) {
				System.out.println("Stop the server!");
				server.stop();
			}
		});
	}

	public void setServer(MyServer server){
		this.server=server;
	}
	
	public void exitServer(){
		System.out.println("Stop the server!");
		server.stop();
		Platform.exit();
	}
}
