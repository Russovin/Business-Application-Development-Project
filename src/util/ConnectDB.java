package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import model.Cup;


public class ConnectDB {
	private final String USERNAME = "root";
	private final String PASSWORD = "";
	private final String DATABASE = "cangkir";
	private final String HOST = "localhost:3306";
	private final String CONNECTION = String.format("jdbc:mysql://%s/%s", HOST, DATABASE);

	public static Connection con;
	public Statement state;
	private static ConnectDB db;

	public static ConnectDB getDB() {
		if (db == null) {
			db = new ConnectDB();
		}
		return db;
	}

	private ConnectDB() {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			con = DriverManager.getConnection(CONNECTION, USERNAME, PASSWORD);
			state = con.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public ResultSet executeQuery(String query) {
		ResultSet rs = null;
		try {
			rs = state.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return rs;
	}

	public void executeUpdate(String query) {
		try {
			state.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ResultSet getUserData() {
		String query = "SELECT * FROM msuser";
		ResultSet rs = null;
		rs = executeQuery(query);
		return rs;
	}
	
	public void addUser(String name, String email, String password, String gender, String role, String UserId) {
	    String query = "INSERT INTO msuser (UserID, Username, UserEmail, UserPassword, UserGender, UserRole) VALUES (?, ?, ?, ?, ?, ?)";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setString(1, UserId);
	        ps.setString(2, name);
	        ps.setString(3, email);
	        ps.setString(4, password);
	        ps.setString(5, gender);
	        ps.setString(6, role);

	        int affectedRows = ps.executeUpdate();

	        if (affectedRows == 0) {
	            throw new SQLException("Creating user failed, no rows affected.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}
	
	public void addtoCart(String UserId, String CupId, Integer quantity) {
	    String query = "INSERT INTO cart (UserID, CupID, Quantity) VALUES (?, ?, ?)";
	    try (PreparedStatement ps = con.prepareStatement(query)) {
	        ps.setString(1, UserId);
	        ps.setString(2, CupId);
	        ps.setInt(3, quantity);

	        int affectedRows = ps.executeUpdate();

	        if (affectedRows == 0) {
	            throw new SQLException("Creating user failed, no rows affected.");
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	}

	public String generateUserId() {
	    String query = "SELECT MAX(CAST(SUBSTRING(UserID, 3) AS SIGNED)) + 1 FROM msuser";

	    try (ResultSet rs = state.executeQuery(query)) {
	        if (rs.next()) {
	            int nextUserId = rs.getInt(1);
	            return String.format("US%03d", nextUserId);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return null;
	}
	
	public String generateCupId() {
	    String query = "SELECT MAX(CAST(SUBSTRING(CupID, 3) AS SIGNED)) + 1 FROM mscup";

	    try (ResultSet rs = state.executeQuery(query)) {
	        if (rs.next()) {
	            int nextcupId = rs.getInt(1);
	            return String.format("CU%03d", nextcupId);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return null;
	}

	public String generateTransactionId() {
	    String query = "SELECT MAX(CAST(SUBSTRING(TransactionID, 3) AS SIGNED)) + 1 FROM transactionheader";

	    try (ResultSet rs = state.executeQuery(query)) {
	        if (rs.next()) {
	            int nexttransactionId = rs.getInt(1);
	            return String.format("TR%03d", nexttransactionId);
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	    return null;
	}
	
	 public boolean isUsernameUnique(String username) {
	        try {
	            String query = "SELECT * FROM msuser WHERE Username = ?";
	            PreparedStatement preparedStatement = con.prepareStatement(query);
	            preparedStatement.setString(1, username);

	            ResultSet resultSet = preparedStatement.executeQuery();
	            return !resultSet.next(); 
	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false; 
	        }
	    }

	    public boolean isEmailUnique(String email) {
	        try {
	            String query = "SELECT * FROM msuser WHERE UserEmail = ?";
	            PreparedStatement preparedStatement = con.prepareStatement(query);
	            preparedStatement.setString(1, email);

	            ResultSet resultSet = preparedStatement.executeQuery();
	            return !resultSet.next(); 

	        } catch (SQLException e) {
	            e.printStackTrace();
	            return false; 
	        }
	    }
	
	    public void addCup(String cupName, int cupPrice, String cupId) {
	        String query = "INSERT INTO mscup (CupID, CupName, CupPrice) VALUES (?, ?, ?)";
	        try (PreparedStatement ps = con.prepareStatement(query)) {
	        	ps.setString(1, cupId);
	        	ps.setString(2, cupName);
	            ps.setInt(3, cupPrice);

	            int affectedRows = ps.executeUpdate();

	            if (affectedRows == 0) {
	                throw new SQLException("Adding cup failed, no rows affected.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public void updateCupPrice(String cupName, int newPrice) {
	        String query = "UPDATE mscup SET CupPrice = ? WHERE CupName = ?";
	        try (PreparedStatement ps = con.prepareStatement(query)) {
	            ps.setInt(1, newPrice);
	            ps.setString(2, cupName);

	            int affectedRows = ps.executeUpdate();

	            if (affectedRows == 0) {
	                throw new SQLException("Updating cup price failed, no rows affected.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public void removeCup(String cupName) {
	        String query = "DELETE FROM mscup WHERE CupName = ?";
	        try (PreparedStatement ps = con.prepareStatement(query)) {
	            ps.setString(1, cupName);

	            int affectedRows = ps.executeUpdate();

	            if (affectedRows == 0) {
	                throw new SQLException("Removing cup failed, no rows affected.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }

	    public List<Cup> getAllCups() {
	        List<Cup> cups = new ArrayList<>();
	        String query = "SELECT * FROM mscup";

	        try (ResultSet rs = state.executeQuery(query)) {
	            while (rs.next()) {
	            	String cupId = rs.getString("CupID");
	                String cupName = rs.getString("CupName");
	                int cupPrice = rs.getInt("CupPrice");
	                Cup cup = new Cup(cupId, cupName, cupPrice, 0, 0);
	                cups.add(cup);
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }

	        return cups;
	    }
	    
	    
	    public void addTransactionHeader(String transactionId, String userId, String courierId, String transactionDate, int insurance) {
	        String query = "INSERT INTO transactionheader (TransactionID, UserID, CourierID, TransactionDate, UseDeliveryInsurance) VALUES (?, ?, ?, ?, ?)";
	        try (PreparedStatement ps = con.prepareStatement(query)) {
	        	ps.setString(1, transactionId);
	        	ps.setString(2, userId);
	            ps.setString(3, courierId);
	            java.sql.Date currentDate = new java.sql.Date(System.currentTimeMillis());
	            ps.setDate(4, currentDate);
	            ps.setInt(5, insurance);

	            int affectedRows = ps.executeUpdate();

	            if (affectedRows == 0) {
	                throw new SQLException("Adding cup failed, no rows affected.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
	    

	    public void addTransactionDetail(String transactionId, String cupId, int quantity) {
	        String query = "INSERT INTO transactiondetail (TransactionID, CupID, Quantity) VALUES (?, ?, ?)";
	        try (PreparedStatement ps = con.prepareStatement(query)) {
	        	ps.setString(1, transactionId);
	        	ps.setString(2, cupId);
	            ps.setInt(3, quantity);
	            
	            int affectedRows = ps.executeUpdate();

	            if (affectedRows == 0) {
	                throw new SQLException("Adding cup failed, no rows affected.");
	            }
	        } catch (SQLException e) {
	            e.printStackTrace();
	        }
	    }
		
	
}

