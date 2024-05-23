package view;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import util.ConnectDB;

public class Login{
	private Scene scene;
	private Stage stage;
	private GridPane gp;
	
	private Label login;
	private Label lbUsername;
	private Label lbPassword;
	private Hyperlink hlRegister;
	
	private TextField tfUsername;
	private PasswordField pfPassword;
	
	private Button btLogin;
	public static String username;
	public static String userId;
	
	 public void setUsername(String username) {
	        this.username = username;
	    }
	
	private void createLogin() {
		gp = new GridPane();
		
		login = new Label("Login");
		lbUsername = new Label("Username");
		lbPassword = new Label("Password");
		 
		hlRegister = new Hyperlink("Don't have an account yet? Register Here!");
		
		tfUsername = new TextField();
		pfPassword = new PasswordField();
		
		btLogin = new Button("Login");
		
		scene = new Scene(gp, 800, 700);

		gp.add(login, 0, 0);
		gp.add(lbUsername, 0, 1);
		gp.add(tfUsername, 0, 2);
		gp.add(lbPassword, 0, 3);
		gp.add(pfPassword, 0, 4);
		gp.add(btLogin, 0, 6);
		gp.add(hlRegister, 0, 7);
		gp.setAlignment(Pos.CENTER);
		GridPane.setHalignment(login, HPos.CENTER);
		GridPane.setHalignment(btLogin, HPos.CENTER);
		gp.setVgap(10);
		login.setFont(Font.font("Roboto", FontWeight.BOLD, 30));
		tfUsername.setMaxWidth(350);
		pfPassword.setMaxWidth(350);
		btLogin.setMinWidth(100);
		tfUsername.setPromptText("Input your username here");
		pfPassword.setPromptText("Input your password here");
	}
	
	private void eventHandler() {
		btLogin.setOnMouseClicked(new EventHandler<Event>() {

			@Override
			public void handle(Event event) {
				String name = tfUsername.getText();
				String password = pfPassword.getText();
				
				if(name.isEmpty()) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Please fill out your username");
					alert.setHeaderText("Login Error");
					alert.showAndWait();
					return;
				}else if(password.isEmpty()) {
					Alert alert = new Alert(AlertType.ERROR);
					alert.setContentText("Please fill out your password");
					alert.setHeaderText("Login Error");
					alert.showAndWait();
					return;
				}else if(validateLogin(name, password)) {
	            
				}
			else if(!validateLogin(name, password)) {
				Alert alert = new Alert(AlertType.ERROR);
				alert.setContentText("Username and Password don't match");
				alert.setHeaderText("Login Error");
				alert.showAndWait();
				return;
				}
			}
		});
		hlRegister.setOnMouseClicked(e -> {
			Register regis = new Register(stage);
		});
		
	}
	
	private boolean validateLogin(String username, String password) {
	    ConnectDB database = ConnectDB.getDB();
	    String query = "SELECT * FROM `msuser` WHERE `Username` = ? AND `UserPassword` = ?";
	    try (PreparedStatement preparedStatement = database.con.prepareStatement(query)) {
	        preparedStatement.setString(1, username);
	        preparedStatement.setString(2, password);
	        ResultSet resultSet = preparedStatement.executeQuery();
	        if(resultSet.next()) {
	        	String userId = resultSet.getString("UserId");
	        	String userRole = resultSet.getString("UserRole");
	        	if ("admin".equalsIgnoreCase(userRole)) {
	                CupManagement cupManagement = new CupManagement(stage);
	                setUsername(username);
	                return true;
	        	} else {
	        	Home home = new Home(stage, userId);
	        	setUsername(username);
	        	return true;
	        	}
	        }
	        return false;
	    } catch (SQLException ex) {
	        ex.printStackTrace();
	        return false;
	    }
	}

	
	public Login(Stage stage) {
		createLogin();
		eventHandler();
		this.stage = stage;
		this.stage.setScene(scene);
		this.stage.show();
	}

}
