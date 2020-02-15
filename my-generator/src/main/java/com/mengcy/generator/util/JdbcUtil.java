package com.mengcy.generator.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author mengcy
 * JDBC工具类
 */
public class JdbcUtil {

	private String driver;

	/** 定义数据库的链接 */
	private Connection conn;
	private Map<String, PreparedStatement> prepareCache = new HashMap<>(16);

	public static Class<?> loadClass(String className) {
		Class<?> clazz = null;
		if(className == null) {
			return null;
		} else {
			try {
				return Class.forName(className);
			} catch (ClassNotFoundException var5) {
				ClassLoader ctxClassLoader = Thread.currentThread().getContextClassLoader();
				if(ctxClassLoader != null) {
					try {
						clazz = ctxClassLoader.loadClass(className);
					} catch (ClassNotFoundException e) {
						// do nothing
					}
				}

				return clazz;
			}
		}
	}

	public JdbcUtil(String driver, String url,  String username, String password){
		this.driver = driver;
	}

	public JdbcUtil(String url, String username, String password) {
		Class driverClz;
		if(this.driver != null){
			driverClz = loadClass(this.driver);
		}else {
			driverClz = loadClass("com.mysql.cj.jdbc.Driver");
			if (driverClz == null) {
				driverClz = loadClass("com.mysql.jdbc.Driver");
			}
		}

		if(driverClz != null){
			try {
				conn = DriverManager.getConnection(url, username, password);
				conn.setAutoCommit(false);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private PreparedStatement getPrepareStatement(String sql) throws SQLException {
		PreparedStatement statement = prepareCache.get(sql);
		if(statement == null){
			statement = conn.prepareStatement(sql);
			prepareCache.put(sql, statement);
		}
		return statement;
	}

	private void commit() {
		try {
			if (conn != null) {
				conn.commit();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void rollback() {
		try {
			if (conn != null) {
				conn.rollback();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean updateByParams(String sql, Object... params){
		// 影响行数
		int result = -1;
		try {
			PreparedStatement pstmt = getPrepareStatement(sql, params);
			result = pstmt.executeUpdate();
			commit();
		} catch (SQLException e) {
			e.printStackTrace();
			rollback();
		}
		return result > 0 ? true : false;
	}


	public List<Map<String, Object>> selectByParams(String sql, Object... params){
		List<Map<String, Object>> list = new ArrayList<> ();
		try {
			PreparedStatement pstmt = getPrepareStatement(sql, params);

			ResultSet resultSet = pstmt.executeQuery();

			ResultSetMetaData metaData = resultSet.getMetaData();
			int columns = metaData.getColumnCount();
			while (resultSet.next()) {
				Map<String, Object> map = new HashMap();
				for (int i = 0; i < columns; i ++) {
					String colName = metaData.getColumnName(i + 1);
					Object colValue = resultSet.getObject(colName);
					map.put(colName, colValue);
				}
				list.add(map);
			}
			resultSet.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public int countByParams(String sql, Object... params) {
		try {
			PreparedStatement pstmt = getPrepareStatement(sql, params);
			ResultSet resultSet = pstmt.executeQuery();
			if(resultSet.next()){
				int count = resultSet.getInt(1);
				resultSet.close();
				return count;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}

	private PreparedStatement getPrepareStatement(String sql, Object... params) throws SQLException {
		PreparedStatement statement = getPrepareStatement(sql);
		if(params != null){
			int i = 1;
			for (Object param : params) {
				statement.setObject(i, param);
				i++;
			}
		}
		return statement;
	}

	public void release() {
		try {
			if (prepareCache != null){
				for(Map.Entry<String, PreparedStatement> entry : prepareCache.entrySet()){
					entry.getValue().close();
				}
			}
			if (null != conn) {conn.close();}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	public boolean executeSql(String sql) {
		try {
			boolean result = conn.createStatement().execute(sql);
			conn.commit();
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		return false;
	}
}
