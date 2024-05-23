package view;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Cup;
import util.ConnectDB;

public class CupManagement {
	private Stage stage;
	private Scene scene;
	private BorderPane bp;
	private GridPane gp;

	private Label lbTitle, lbCupName, lbPrice;

	private TextField tfname, tfPrice;

	private TableView<Cup> tbView;

	private Button btInsert, btUpdate, btDelete;

	private VBox cn;

	private Menu userMenu;
	private MenuBar mbUser;
	private MenuItem miCupmanagement, miLogout;

	private void createCupManagement() {
		bp = new BorderPane();
		gp = new GridPane();

		lbTitle = new Label("Cup Management");
		lbCupName = new Label("Cup Name");
		lbPrice = new Label("Cup Price");

		tfname = new TextField();
		tfPrice = new TextField();

		tbView = new TableView<Cup>();

		btInsert = new Button("Add Cup");
		btUpdate = new Button("Update Price");
		btDelete = new Button("Remove Cup");

		cn = new VBox(10);
		userMenu = new Menu("Menu");
		mbUser = new MenuBar();
		miCupmanagement = new MenuItem("Cup Management");
		miLogout = new MenuItem("Log Out");

		mbUser.getMenus().add(userMenu);
		userMenu.getItems().addAll(miCupmanagement, miLogout);
		scene = new Scene(bp, 800, 700);

		bp.setBottom(gp);
		bp.setTop(mbUser);
		gp.add(lbTitle, 0, 0);
		gp.add(tbView, 0, 1);
		gp.add(cn, 1, 1);
		cn.getChildren().addAll(lbCupName, tfname, lbPrice, tfPrice, btInsert, btUpdate, btDelete);
		gp.setVgap(10);
		gp.setHgap(10);
		cn.setAlignment(Pos.CENTER_LEFT);
		lbTitle.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		lbCupName.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		lbPrice.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		gp.setPadding(new Insets(10));
	}

	@SuppressWarnings("unchecked")
	private void initTbView() {
		TableColumn<Cup, String> colCupName = new TableColumn<Cup, String>("Cup Name");
		TableColumn<Cup, Integer> colPrice = new TableColumn<Cup, Integer>("Cup Price");

		colCupName.setCellValueFactory(new PropertyValueFactory<>("cupName"));
		colPrice.setCellValueFactory(new PropertyValueFactory<>("cupPrice"));

		tbView.getColumns().addAll(colCupName, colPrice);

		colCupName.setMinWidth(bp.getWidth() / 4);
		colPrice.setMinWidth(bp.getWidth() / 4);

		refreshTableView();
	}

	private void refreshTableView() {
		ObservableList<Cup> cupList = FXCollections.observableArrayList(ConnectDB.getDB().getAllCups());
		tbView.setItems(cupList);
	}

	private void navigationHandler() {
		miCupmanagement.setOnAction(e -> {
			CupManagement manage = new CupManagement(stage);
		});
		miLogout.setOnAction(e -> {
			Login login = new Login(stage);
		});
	}
	
	private void eventHandling() {

		tbView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
			if (newSelection != null) {
				// Fill the input fields with selected item's data
				tfname.setText(newSelection.getCupName());
				tfPrice.setText(String.valueOf(newSelection.getCupPrice()));
			}
		});

		btDelete.setOnAction(e -> handleRemoveCup());
		btUpdate.setOnAction(e -> handleUpdatePrice());
		btInsert.setOnAction(e -> handleAddCup());
	}

	private void handleRemoveCup() {
		Cup selectedCup = tbView.getSelectionModel().getSelectedItem();
		if (selectedCup != null) {
			ConnectDB.getDB().removeCup(selectedCup.getCupName());
			tbView.getItems().remove(selectedCup);
			showAlert("Cup Removed", "The cup has been successfully removed.");
		} else {
			showAlert("No Cup Selected", "Please select a cup to remove.");
		}
		refreshTableView();
	}

	private void handleUpdatePrice() {
		Cup selectedCup = tbView.getSelectionModel().getSelectedItem();
		if (selectedCup != null) {
			try {
				// Parse and validate the updated price
				int updatedPrice = Integer.parseInt(tfPrice.getText().trim());
				if (updatedPrice >= 5000 && updatedPrice <= 1000000) {
					ConnectDB.getDB().updateCupPrice(selectedCup.getCupName(), updatedPrice);
					selectedCup.setCupPrice(updatedPrice);
					showAlert("Price Updated", "The cup price has been successfully updated.");
				} else {
					showAlert("Invalid Price", "Please enter a price between 5000 and 1000000.");
				}
			} catch (NumberFormatException e) {
				showAlert("Invalid Price", "Please enter a valid numeric price.");
			}
		} else {
			showAlert("No Cup Selected", "Please select a cup to update its price.");
		}
		refreshTableView();
	}

	private void handleAddCup() {
		String cupName = tfname.getText();
		String cupPriceStr = tfPrice.getText();
		  if (isCupNameUnique(cupName)) {
		if (!cupName.isEmpty() && !cupPriceStr.isEmpty()) {
			try {
				int cupPrice = Integer.parseInt(cupPriceStr);
				if (cupPrice >= 5000 && cupPrice <= 1000000) {
					ConnectDB.getDB().addCup(cupName, cupPrice, ConnectDB.getDB().generateCupId());
					Cup newCup = new Cup(ConnectDB.getDB().generateCupId(), cupName, cupPrice, 0, 0);
					tbView.getItems().add(newCup);
					showAlert("Cup Added", "The new cup has been successfully added.");
				} else {
					showAlert("Invalid Price", "Please enter a price between 5000 and 1000000.");
				}
			} catch (NumberFormatException e) {
				showAlert("Invalid Price", "Please enter a valid numeric price.");
			}
		} else {
			showAlert("Incomplete Information", "Please enter cup name and price.");
		}
		  } else {
		        showAlert("Duplicate Cup Name", "The cup name is already in use. Please choose a different name.");
		    }
		refreshTableView();
	}
	
	private boolean isCupNameUnique(String cupName) {
	    ObservableList<Cup> cupList = tbView.getItems();
	    for (Cup cup : cupList) {
	        if (cup.getCupName().equalsIgnoreCase(cupName)) {
	            return false; 
	        }
	    }
	    return true; 
	}

	private void showAlert(String title, String content) {
		Alert alert = new Alert(AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(content);
		alert.showAndWait();
	}

	public CupManagement(Stage stage) {
		createCupManagement();
		initTbView();
		eventHandling();
		navigationHandler();
		this.stage = stage;
		this.stage.setScene(scene);
		this.stage.show();
	}

}
