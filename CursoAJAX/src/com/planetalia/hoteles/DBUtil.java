package com.planetalia.hoteles;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
	static {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			//Class.forName("com.mysql.jdbc.Driver");
		} catch(ClassNotFoundException e) {
			throw new RuntimeException("No se pudo cargar el driver de la base de datos");
		}
	}
	
	public static Connection getConnection() throws SQLException{
		//return DriverManager.getConnection("jdbc:mysql://localhost/hoteles","hoteles","hoteles");
		return DriverManager.getConnection("jdbc:oracle:thin:@192.168.1.2:1521:test","hoteles","hoteles");
	}

}
