package first;

import java.sql.*;
import java.sql.Date;
import java.util.ArrayList;

public class SQL {
	private static Connection c;

	public static void init(String url, String user, String pass) {
		SQL.c = getConnection(url, user, pass);
		SQL.setAutoCommit(c, true);
	}

	private static Connection getConnection(String url, String user, String pass) {
		try {
			return DriverManager.getConnection(url, user, pass);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	private static void setAutoCommit(Connection c, boolean ToF) {
		try {
			c.setAutoCommit(ToF);
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Set Auto Commit didn't work!");
		}

	}

	public static void createPictureTable(String tableName) {
		try {
			Statement stmt = c.createStatement();
			String sql = "drop table if exists " + tableName + ";";
			stmt.executeUpdate(sql);
			sql = "create table " + tableName
					+ "(PID int primary key auto_increment, parentPath varchar(255), name varchar(255),datum date, ext varchar(10), fileSize bigint);";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void insertCustomTableTable(String tableName) {
		try {
			Statement stmt = c.createStatement();
			String sql = "insert into CustomTables (name) values (\"" + tableName  + "\");";
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static Date getSQLDate(long time) {
		Date d = new Date(time);
		return d;
	}

	public static void insertIntoDB(Pictures ps, String tableName) {
		for (int i = 0; i < ps.getLength(); i++) {
			Picture p = ps.getPicture(i);
			try {
				String sql = "insert into " + tableName + "(parentPath, name,datum,ext,fileSize) values(?,?,?,?,?);";
				PreparedStatement stmt = c.prepareStatement(sql);
				stmt.setString(1, p.getParentPath());
				stmt.setString(2, p.getName());
				stmt.setDate(3, SQL.getSQLDate(p.getLastModTime()));
				stmt.setString(4, p.getExtension());
				stmt.setLong(5, p.getFileSize());
				stmt.executeUpdate();
				stmt.close();

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public static void updateParentPath(int PID, String newParentPath, String tableName) {
		try {
			String sql = "update " + tableName + " set parentpath = ? where PID = ?;";
			PreparedStatement stmt = c.prepareStatement(sql);
			stmt.setString(1, newParentPath);
			stmt.setInt(2, PID);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

	public static void updateName(int PID, String newName, String ext, String tableName) {
		try {
			newName = newName + "." + ext;
			String sql = "update " + tableName + " set name = ? where PID = ?;";
			PreparedStatement stmt = c.prepareStatement(sql);
			stmt.setString(1, newName + "." + ext);
			stmt.setInt(2, PID);
			stmt.executeUpdate();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void deletePicture(int PID, String tableName) {
		try {
			String sql = "delete from " + tableName + " where PID = ?;";
			PreparedStatement stmt = c.prepareStatement(sql);
			stmt.setInt(1, PID);
			stmt.executeUpdate(sql);
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	

	public static Pictures selectAll(String tableName) {
		Pictures ps = new Pictures();
		try {
			String sql = "select pid,parentPath, name,datum,ext,fileSize from " + tableName + " ;";
			Statement stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				int pid = rs.getInt("pid");
				String parentPath = rs.getString("parentPath");
				String name = rs.getString("name");
				Date datum = rs.getDate("datum");
				String ext = rs.getString("ext");
				long fileSize = rs.getLong("fileSize");
								
				Picture p = new Picture(pid, parentPath, name, ext, fileSize, datum.getTime());
				ps.addPic(p);
			}
			rs.close();
			stmt.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return ps;
	}
	
	//Valle
	
	public static boolean existingTables(String tableName) {
		try {
			Statement stmt = c.createStatement();
			String sql = "select name from CustomTables;";
			ResultSet rs = stmt.executeQuery(sql);
			while (rs.next()) {
				String name = rs.getString("name");
				if (name.equals(tableName)) {
					return true;
				}
			}
			return false;
		} catch (SQLException e) {
			System.out.println("SQL-Fehler._2");
			e.printStackTrace();
		}
		return false;
	}
	
	static ArrayList<String> getExistingTables() {
		ArrayList<String> tables = new  ArrayList<>();
		try {
			Statement stmt = c.createStatement();
			String sql = "select name from CustomTables;";
			ResultSet rs = stmt.executeQuery(sql);
			//int x = 0;
			while (rs.next()) {
				String s = rs.getString("name");
				tables.add(s);
			}
			rs.close();
			stmt.close();
			return tables;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static void setTableName(String tableName, String s) {
		tableName = s;
	}

}