package view;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import model.admin.AdminModel;

public class MainWindowController implements Initializable{
	
	@FXML
	private ListView myListView;
	
	private AdminModel adminModel;
	
	private ListProperty<Integer> listProp= new SimpleListProperty<>();


	@FXML
	private void handleButtonAction(ActionEvent event) {
		updateList();
	}

	
	private void updateList() {
		listProp.set(FXCollections.observableArrayList(adminModel.getClients()));
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		adminModel = AdminModel.getInstance();      
		myListView.itemsProperty().bind(listProp);
		listProp.set(FXCollections.observableArrayList(adminModel.getClients()));
		
	}

}
