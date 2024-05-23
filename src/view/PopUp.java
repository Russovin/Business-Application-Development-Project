package view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import jfxtras.labs.scene.control.window.Window;
import model.Cup;
import util.ConnectDB;

public class PopUp{
	private Stage stage;
	private Scene scene;
	private BorderPane bp;
	private FlowPane fp;
	private VBox vb;
	private Window window;
	private Label confirmationLbl;
	private Button yesBtn, noBtn;
	private TableView<Cup> tbView;
	private ComboBox<String> cbCourier;
	private CheckBox cbOption;
	private Cart cart;
	

	public void initialize() {
		bp = new BorderPane();
		fp = new FlowPane();
		vb = new VBox(25);
		scene = new Scene(bp, 800,700);
		
		window = new Window("Checkout confirmation");
		
		confirmationLbl = new Label("Are you sure want to purchase?");
		
		yesBtn = new Button("Yes");
		noBtn = new Button("No");

		bp.setCenter(window);
	
		fp.getChildren().addAll(yesBtn, noBtn);
		vb.getChildren().addAll(confirmationLbl, fp);
		window.getContentPane().getChildren().add(vb);
	
		vb.setAlignment(Pos.CENTER);
		fp.setAlignment(Pos.CENTER);
		fp.setHgap(30);
		
		yesBtn.setPadding(new Insets(10, 30, 10, 30));
		noBtn.setPadding(new Insets(10, 30, 10, 30));
	
		confirmationLbl.setFont(Font.font(null, FontWeight.BOLD, 20));
	}
	
	  public void setTableView(TableView<Cup> tbView) {
	        this.tbView = tbView;
	    }
	
	 public void eventHandling() {
	        yesBtn.setOnAction(event -> {
	        	handleYesButtonClick();
	        });

	        noBtn.setOnAction(event -> {
	            handleNoButtonClick();
	        });
	    }
	 
	 
	 

		 private void handleYesButtonClick() {
			 String userId = getUserIdFromUsername(Login.username);
			    String courierId = getCourierIdFromName(cbCourier.getValue());
			    
			    String transactionId = ConnectDB.getDB().generateTransactionId();
			   
			    for (Cup cup : tbView.getItems()) {
			        String cupId = cart.getCupIdForTransaction(cup);
			        if (cupId != null) {
			            int quantity = cup.getQuantity();
			            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
			            String transactionDate = currentDate.toString();
			            int insurance = cbOption.isSelected() ? 1 : 0;

			            ConnectDB.getDB().addTransactionHeader(transactionId, userId, courierId, transactionDate, insurance);

			            ConnectDB.getDB().addTransactionDetail(transactionId, cupId, quantity);
			        } else {
			            System.err.println("CupID is null for Cup: " + cup.getCupName());
			        }
			    }
			    clearCart();
			    
			    Cart cart = new Cart(stage, getUserIdFromUsername(Login.username));
			    showAlert("Checkout Success", "Transaction completed successfully!");
			   
				}


	    private void handleNoButtonClick() {    
	    	  Cart cart = new Cart(stage, getUserIdFromUsername(Login.username));
			
	 }
	    
	    

	    private void clearCart() {
	    	// Delete all items from the cart for the current user
	    	String deleteCartQuery = "DELETE FROM cart WHERE UserID = (SELECT UserID FROM msuser WHERE Username = ?)";
	    	try {
	    		PreparedStatement deleteCartPs = ConnectDB.getDB().con.prepareStatement(deleteCartQuery);
	    		deleteCartPs.setString(1, Login.username);
	    		deleteCartPs.executeUpdate();
	    	} catch (SQLException e) {
	    		e.printStackTrace();
	    	}
	    }
	    
	    private String getUserIdFromUsername(String username) {
	        try {
	            String query = "SELECT UserID FROM msuser WHERE Username = ?";
	            PreparedStatement ps = ConnectDB.getDB().con.prepareStatement(query);
	            ps.setString(1, username);

	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                return rs.getString("UserID");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    private String getCourierIdFromName(String courierName) {
	        try {
	            String query = "SELECT CourierID FROM mscourier WHERE CourierName = ?";
	            PreparedStatement ps = ConnectDB.getDB().con.prepareStatement(query);
	            ps.setString(1, courierName);

	            ResultSet rs = ps.executeQuery();
	            if (rs.next()) {
	                return rs.getString("CourierID");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	    
	    private void showAlert(String title, String content) {
	    	Alert alert = new Alert(AlertType.INFORMATION);
	    	alert.setTitle(title);
	    	alert.setHeaderText(null);
	    	alert.setContentText(content);
	    	alert.showAndWait();
	    }

	
	public PopUp(Stage stage, Cart cart) {
		initialize();
		eventHandling();
		this.stage = stage;
		this.stage.setScene(scene);
		this.stage.show();
		this.cbCourier = cart.getCbCourier();
		this.cbOption = cart.getCbOption();
		this.cart = cart;
	
	}
	

}
