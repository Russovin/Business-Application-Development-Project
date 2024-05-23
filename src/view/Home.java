package view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.Spinner;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Cup;
import util.ConnectDB;

public class Home{
	private String UserId;
	private Scene scene;
	private Stage stage;
	private BorderPane bp;
	private GridPane gp;

	private Label lbTitle, lbCupName, lbPrice;

	private TableView<Cup> tbView;

	private Spinner<Integer> spCup;

	private Button btInsert;

	private VBox cn;
	private Menu userMenu;
	private MenuBar mbUser;
	private MenuItem miHome, miCart, miLogout;
	
	private void createHome() {
		
		bp = new BorderPane();
		gp = new GridPane();
		
		lbTitle = new Label("Cup List");
		lbCupName = new Label("Cup Name");
		lbPrice = new Label("Price");
		
		tbView = new TableView<>();
		
		spCup = new Spinner<>(1, 100, 1);
		
		btInsert = new Button("Add to Cart");
		
		cn = new VBox(10);
		
		scene = new Scene(bp, 800, 700);  
		
		bp.setBottom(gp);
		
		gp.add(lbTitle, 0, 0);
		gp.add(tbView, 0, 1);
		gp.add(cn, 1, 1);
		cn.getChildren().addAll(lbCupName, spCup, lbPrice, btInsert);
		gp.setVgap(10);
		gp.setHgap(10);
		cn.setAlignment(Pos.CENTER_LEFT);
		lbTitle.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		lbCupName.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		lbPrice.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		gp.setPadding(new Insets(10));
		userMenu = new Menu("Menu");
		mbUser = new MenuBar();
		miHome = new MenuItem("Home");
		miCart = new MenuItem("Cart");
		miLogout = new MenuItem("Log Out");
		
		mbUser.getMenus().add(userMenu);
		userMenu.getItems().addAll(miHome, miCart, miLogout);
		
		bp.setTop(mbUser);
	}
	
	private void navigationHandler() {
		miCart.setOnAction(e -> {
			Cart cart = new Cart(stage, UserId);
		});
		miLogout.setOnAction(e -> {
			Login login = new Login(stage);
		});
	}

	@SuppressWarnings("unchecked")
	private void initTbView() {
		TableColumn<Cup, String> colCupName = new TableColumn<Cup, String>("Cup Name");
		TableColumn<Cup, Integer> colPrice = new TableColumn<Cup, Integer>("Cup Price");
		colCupName.setCellValueFactory(new PropertyValueFactory<>("cupName"));
		colPrice.setCellValueFactory(new PropertyValueFactory<>("cupPrice"));
		tbView.getColumns().addAll(colCupName, colPrice);
		tbView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
		colCupName.setMinWidth(bp.getWidth()/4);
		colPrice.setMinWidth(bp.getWidth()/4);

	}
	
	
	private void eventHandling() {
		 try {
		        String query = "SELECT * FROM mscup"; 
		        ResultSet rs = ConnectDB.getDB().executeQuery(query);

		        while (rs.next()) {
		        	String cupId = rs.getString("CupID");
		            String cupName = rs.getString("CupName"); 
		            int cupPrice = rs.getInt("CupPrice");
		            Cup cup = new Cup(cupId, cupName, cupPrice, 0, 0);
		            tbView.getItems().add(cup);
		        }	
		 } catch (SQLException e) {
		        e.printStackTrace();
		    }
		        
	  btInsert.setOnMouseClicked(e -> addToCartClicked());
	
	  tbView.setOnMouseClicked(e -> {
	        Cup selectedCup = tbView.getSelectionModel().getSelectedItem();
	        if (selectedCup != null) {
	        	 lbCupName.setText("Cup Name: " + selectedCup.getCupName());
	            int price = selectedCup.getCupPrice();
	            int quantity = spCup.getValue();
	            int totalPrice = quantity * price;
	            lbPrice.setText("Total price: " + totalPrice);
	        }
		 });
	  
	  spCup.valueProperty().addListener(new ChangeListener<Integer>() {
          @Override
          public void changed(ObservableValue<? extends Integer> observable, Integer oldValue, Integer newValue) {
              updateTotalPrice();
          }
      });
  }

	private void addToCartClicked() {
	    Cup selectedCup = tbView.getSelectionModel().getSelectedItem();
	    if (selectedCup == null) {
	        showAlert("Cart Error", "Please select a cup to be added");
	    }else {
	    	String cupId = selectedCup.getCupId();
	        int quantity = spCup.getValue();
	        if (isCupInCart(cupId)) {
	            updateCartQuantity(cupId, quantity);
	        } else {	           
	            ConnectDB.getDB().addtoCart(UserId, cupId, quantity);
	        }

	        Alert alert = new Alert(AlertType.INFORMATION);
	        alert.setContentText("Item Successfully added to cart! ");
	        alert.setHeaderText("Cart Info");
	        alert.showAndWait();
	        Cart cart = new Cart(stage, UserId);
	        return;
	    }
	}
	
	private boolean isCupInCart(String cupId) {
		try {
	        String query = "SELECT * FROM cart WHERE UserID = ? AND CupID = ?";
	        PreparedStatement preparedStatement = ConnectDB.con.prepareStatement(query);
	        preparedStatement.setString(1, UserId);
	        preparedStatement.setString(2, cupId);

	        ResultSet resultSet = preparedStatement.executeQuery();
	        return resultSet.next(); 
	    } catch (SQLException e) {
	        e.printStackTrace();
	    return false;
	    }
	}

	private void updateCartQuantity(String cupId, int quantity) {
		try {
	        String query = "UPDATE cart SET Quantity = Quantity + ? WHERE UserID = ? AND CupID = ?";
	        PreparedStatement preparedStatement = ConnectDB.con.prepareStatement(query);
	        preparedStatement.setInt(1, quantity);
	        preparedStatement.setString(2, UserId);
	        preparedStatement.setString(3, cupId);

	        preparedStatement.executeUpdate();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	 private void updateTotalPrice() {
	        Cup selectedCup = tbView.getSelectionModel().getSelectedItem();
	        if (selectedCup != null) {
	        	 lbCupName.setText("Cup Name: " + selectedCup.getCupName());
	            int price = selectedCup.getCupPrice();
	            int quantity = spCup.getValue();
	            int totalPrice = quantity * price;
	            lbPrice.setText("Total price: " + totalPrice);
	        }
	    }

	private void showAlert(String title, String content) {
		Alert alert = new Alert(AlertType.ERROR);
		alert.setContentText("Please select a cup to be added");
		alert.setHeaderText("Cart Error");
		alert.showAndWait();
		return;
	}
	
	
	
	public Home(Stage stage, String UserId){
		this.UserId = UserId;
		createHome();
		initTbView();
		eventHandling();
		navigationHandler();
		this.stage = stage;
		this.stage.setScene(scene);
		this.stage.show();
	}


}
