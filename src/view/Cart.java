package view;


import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.Cup;
import util.ConnectDB;

public class Cart{
	private Scene scene;
	private Stage stage;
	private BorderPane bp;
	private GridPane gp;
	private Label lbOwner, lbDelete, lbCourier, lbCourierPrice, lbTotal;

	private TableView<Cup> tbView;

	private Button btDelete;
	private Button btCheckout;
	
	private ComboBox<String> cbCourier;
	
	private CheckBox cbOption;
	
	private VBox vb;
	
	private Menu userMenu;
	private MenuBar mbUser;
	private MenuItem miHome, miCart, miLogout;
	
	
	private void createCart() {
		bp = new BorderPane();
		gp = new GridPane();
		
		lbOwner = new Label(Login.username +  "'s Cart");
		lbDelete = new Label("Delete Item");
		lbCourier = new Label("Courier");
		lbCourierPrice = new Label("Courier Price:");
		lbTotal = new Label("Total Price:");
		
		tbView = new TableView<Cup>();
				
		btDelete = new Button("Delete Item");
		btCheckout = new Button("Checkout");
		
		cbCourier = new ComboBox<>();
		
		cbOption = new CheckBox("Use Delivery Insurance");
		
		vb = new VBox(10);
		
		scene = new Scene(bp, 800, 700);  
		userMenu = new Menu("Menu");
		mbUser = new MenuBar();
		miHome = new MenuItem("Home");
		miCart = new MenuItem("Cart");
		miLogout = new MenuItem("Log Out");
		
		mbUser.getMenus().add(userMenu);
		userMenu.getItems().addAll(miHome, miCart, miLogout);
		
		bp.setTop(mbUser);
		bp.setBottom(gp);
		
		gp.add(lbOwner, 0, 0);
		gp.add(tbView, 0, 1);
		gp.add(vb, 1, 1);
		vb.getChildren().addAll(lbDelete, btDelete, lbCourier, cbCourier, lbCourierPrice, cbOption, lbTotal, btCheckout);
		gp.setVgap(10);
		gp.setHgap(10);
		lbOwner.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		lbDelete.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		lbCourier.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		lbCourierPrice.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		lbTotal.setFont(Font.font("Roboto", FontWeight.BOLD, 20));
		gp.setPadding(new Insets(10));
		
	}
	
	@SuppressWarnings({ "unchecked" })
	private void initTbView() {
		TableColumn<Cup, String> colCupName = new TableColumn<Cup, String>("Cup Name");
		TableColumn<Cup, Integer> colPrice = new TableColumn<Cup, Integer>("Cup Price");
		TableColumn<Cup, Integer> colQty = new TableColumn<Cup, Integer>("Quantity");
		TableColumn<Cup, Integer> colTotal = new TableColumn<Cup, Integer>("Total");
		
		colCupName.setCellValueFactory(new PropertyValueFactory<>("cupName"));
		colPrice.setCellValueFactory(new PropertyValueFactory<>("cupPrice"));
		colQty.setCellValueFactory(new PropertyValueFactory<>("quantity"));
		colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
		tbView.getColumns().addAll(colCupName, colPrice, colQty, colTotal);
		
		colCupName.setMinWidth(bp.getWidth()/8);
		colPrice.setMinWidth(bp.getWidth()/8);
		colQty.setMinWidth(bp.getWidth()/8);
		colTotal.setMinWidth(bp.getWidth()/8);

	}
	
	public ComboBox<String> getCbCourier() {
        return cbCourier;
    }

    public CheckBox getCbOption() {
        return cbOption;
    }
	
	private void fetchCartData(String UserId) {
	    try {
	        String query = "SELECT cart.CupID, mscup.CupName, mscup.CupPrice, cart.Quantity " +
	                       "FROM cart, mscup " +
	                       "WHERE cart.CupID = mscup.CupID AND cart.UserID = '" + UserId + "'";
	        ResultSet rs = ConnectDB.getDB().executeQuery(query);

	        while (rs.next()) {
	            String cupName = rs.getString("CupName");
	            int cupPrice = rs.getInt("CupPrice");
	            int quantity = rs.getInt("Quantity");
	            int total = cupPrice * quantity;

	            Cup cup = new Cup(null, cupName, cupPrice, quantity, total);
	            tbView.getItems().add(cup);
	        }
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
	
	private void navigationHandler() {
		String userId = getUserIdFromUsername(Login.username); 
		miHome.setOnAction(e -> {
			Home home = new Home(stage, userId);
		});
		
		miLogout.setOnAction(e -> {
			Login login = new Login(stage);
		});
	}
	
    private void eventHandling() {
    	btCheckout.setOnMouseClicked(e -> { 
    		 String selectedCourier = cbCourier.getValue();
    	 if(selectedCourier == null) {
         	Alert alert = new Alert(AlertType.ERROR);
         	alert.setContentText("Please select your courier");
				alert.setHeaderText("Checkout Error");
				alert.showAndWait();
				return;
    	 }
    	 });
        
    	btDelete.setOnMouseClicked(e -> {
    		Cup selectedCart = tbView.getSelectionModel().getSelectedItem();
    		 if (selectedCart == null) {
    			 Alert alert = new Alert(AlertType.ERROR);
    			    alert.setTitle("Error");
    			    alert.setHeaderText("Deletion Error");
    			    alert.setContentText("Please select the item you want to delete");
    			    alert.showAndWait();
    		        return;
    		    }
    		
    		String username = Login.username; 
			String query = "DELETE FROM cart WHERE UserID = (SELECT UserID FROM msuser WHERE Username = ?) AND CupID IN (SELECT CupID FROM mscup WHERE CupName = ?) AND Quantity = ?";
			PreparedStatement ps;
			try {
				ps = ConnectDB.getDB().con.prepareStatement(query);
				ps.setString(1, username);
	            ps.setString(2, selectedCart.getCupName());
	            ps.setInt(3, selectedCart.getQuantity());
	            ps.execute();
	            
	            tbView.getItems().remove(selectedCart);
	            Alert alert = new Alert(AlertType.INFORMATION);
			    alert.setTitle("Message");
			    alert.setHeaderText("Deletion Information");
			    alert.setContentText("Cart Delete Succesfully");
			    alert.showAndWait();
		        return;
			} catch (Exception e1) {
				e1.printStackTrace();
			}				
    });
    	
    	cbCourier.valueProperty().addListener((observable, oldValue, newValue) -> {
    		 updateTotalPrice();
    	     updateCourierPriceLabel();
    	    });

    	    cbOption.setOnMouseClicked(e -> {
    	    	updateTotalPrice();  	 	        
    	  }); 
    	    
    	    btCheckout.setOnMouseClicked(e -> {
    	       PopUp popup = new PopUp(stage, this);
    	       popup.setTableView(tbView);
    	    });
    }
    
    private void updateCourierPriceLabel() {
        try {
            String selectedCourier = cbCourier.getValue();
            if (selectedCourier != null) {
                String query = "SELECT CourierPrice FROM mscourier WHERE CourierName = ?";
                PreparedStatement ps = ConnectDB.getDB().con.prepareStatement(query);
                ps.setString(1, selectedCourier);

                ResultSet rs = ps.executeQuery();
                
                if (rs.next()) {
                    int courierPrice = rs.getInt("CourierPrice");
                    lbCourierPrice.setText("Courier Price: " + courierPrice);
                }
            }
          
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    private void updateTotalPrice() {
        try {
            String selectedCourier = cbCourier.getValue();
            if (selectedCourier != null) {
                String query = "SELECT CourierPrice FROM mscourier WHERE CourierName = ?";
                PreparedStatement ps = ConnectDB.getDB().con.prepareStatement(query);
                ps.setString(1, selectedCourier);

                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    int courierPrice = rs.getInt("CourierPrice");
                    int insuranceFee = cbOption.isSelected() ? 2000 : 0;

                    int totalCourierPrice = courierPrice + insuranceFee;
                    lbCourierPrice.setText("Courier Price: " + courierPrice);
                    
                    int totalCupPrice = calculateTotalCupPrice();
                    
                    int totalPrice = totalCupPrice + totalCourierPrice;
                    lbTotal.setText("Total Price: " + totalPrice);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private int calculateTotalCupPrice() {
        int totalCupPrice = 0;
        for (Cup cup : tbView.getItems()) {
            totalCupPrice += cup.getTotal();
        }
        return totalCupPrice;
    }
    	    
    
    private void populateCourierComboBox() {
        try {
            String query = "SELECT CourierName FROM mscourier";
            ResultSet rs = ConnectDB.getDB().executeQuery(query);
            cbCourier.getItems().clear();
            while (rs.next()) {
                String courierName = rs.getString("CourierName");
                cbCourier.getItems().add(courierName);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
    public String getCupIdForTransaction(Cup cup) {
        try {
            String query = "SELECT CupID FROM mscup WHERE CupName = ?";
            PreparedStatement ps = ConnectDB.getDB().con.prepareStatement(query);
            ps.setString(1, cup.getCupName());

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String cupId = rs.getString("CupID");
                if (cupId != null && !cupId.isEmpty()) {
                    return cupId;
                } else {
                    System.err.println("CupID is empty for CupName: " + cup.getCupName());
                    return null; 
                }
            } else {
                System.err.println("CupID not found for CupName: " + cup.getCupName());
                return null;  
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;  
        }
    }

    public int calculateTotalQuantity() {
        int totalQuantity = 0;
        for (Cup cup : tbView.getItems()) {
            totalQuantity += cup.getQuantity();
        }
        return totalQuantity;
    }

    
   
 
	public Cart(Stage stage, String UserId){
		createCart();
		initTbView();
		eventHandling();
		navigationHandler();
		fetchCartData(UserId);
		populateCourierComboBox();
		this.stage = stage;
		this.stage.setScene(scene);
		this.stage.show();
		
	}

}
