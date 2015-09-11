package utils;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.entity.BufferedHttpEntity;

import pipeline.SaveToFile;

public class DBUtils {
	Connection conn = null;
	
	public Connection getconnection(){
        try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/clawer","root","zichen"); 
			return conn;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	public void closeConn(Connection conn){
		if (conn != null) {
			try {
				conn.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	public void closeResultSet(ResultSet rs){
		if (rs != null) {
			try {
				rs.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void insertOrUpdateResource(HttpEntity entity, int taskId, String url){
		conn = getconnection();
		if (conn != null) {
			try {
				entity = new BufferedHttpEntity(entity);
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			SaveToFile stf = new SaveToFile();
			String signature = stf.getSignature(entity);
		 	String pattern = entity.getContentType().getValue();
			
			String timeStamp = NumberUtils.getTime();
			Statement stmt = null;
			ResultSet rs = null;
			String path;
			try {
		        String selectInResource = "select * from resource where resource_url = '" + url + "'";
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(selectInResource);
				if (rs.next()) { //����url
					if (rs.getString("signature").equals(signature)) { //��ݿ��и�url��signature��������ȥ��url������ͬ
						System.out.println( "���ڸ�url������û��");
						//do nothing
					} else{ //���url�����ѱ䣬������Ϣ
					 	path = stf.saveToLocal(entity, taskId);
						System.out.println("���ڸ�url�������ѱ�");
						rs.updateInt("task_id", taskId);
						rs.updateString("resource_pattern", pattern);    
						rs.updateString("time_stamp", timeStamp);  
						rs.updateString("resource_path", path);   
						rs.updateString("signature", signature);
						rs.updateRow();
					}
				} else{ //��ݿ���û�и�url
					System.out.println("�����ڸ�url");
					path = stf.saveToLocal(entity, taskId);
					String getSet = "select * from resource order by resource_id DESC limit 1";
					closeResultSet(rs);//������䣬����ͻ��о��桰Resource leak: 'rs' is not closed at this location��
//					System.out.println("------------------------------------------------------------------");
					rs = stmt.executeQuery(getSet);
					rs.moveToInsertRow();
					rs.updateInt("task_id", taskId);
					rs.updateString("resource_pattern", pattern);  
					rs.updateString("resource_url", url);   
					rs.updateString("time_stamp", timeStamp);  
					rs.updateString("resource_path", path);   
					rs.updateString("signature", signature);
					rs.insertRow();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				closeConn(conn);
				closeResultSet(rs);
			}
		} else {
			closeConn(conn);
		}
	}
	
	public boolean taskDuplicte(String url, String username, boolean timeToCount, int day){
		boolean flag = false;
		conn = getconnection();
		if (conn != null) {
			String getSet = null;
			if(username != null){
				getSet = "select * from taskinfo where login_username = '" + username + "' order by time_stamp desc";
			} else{
				getSet = "select * from taskinfo order by time_stamp desc";
			} 
				
//			String getSet = "select * from taskinfo where login_username = 'cxl_cxlcxl@sina.com'";
			Statement stmt = null;
			ResultSet rs = null;
			
			try {
				//ResultSet.TYPE_SCROLL_INSENSITIVE:˫�������������ʱ���£����������ݿ��������޸Ĺ����ResultSet�з�Ӧ������   
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				//ִ�и�sql���
				rs = stmt.executeQuery(getSet);		
				//����õ���ResultSet
				while(rs.next()){	
					if(isEqual(rs.getString("start_url").trim(), url.trim())){
//						System.out.println("same domain");
						if (timeToCount) {	//�����ʱ����
							String now_time = NumberUtils.getTime();
							String last_time = rs.getString("time_stamp");
							
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
							Date date_now = sdf.parse(now_time);
							Date date_last = sdf.parse(last_time);
							long diff = date_now.getTime() - date_last.getTime();
					        long diffDays = diff / (24 * 60 * 60 * 1000);
//							System.out.println(diffDays);
							if (diffDays < day) {
//								System.out.println("time problem");
								flag = true;
							} else {
								flag = false;
								break;
							}
						} else {//������ʱ����
							flag = true;
						}
						break; //��һ��urlֻ���������һ�Ρ�
					}	//contains domain
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				closeConn(conn);
				closeResultSet(rs);
			}
			return flag;
		} else {	//���û�н�����ݿ����ӣ����ظ����?����ִ�и�����
			closeConn(conn);
			return true;
		}
	}
	
	//�ж�����url�Ƿ���ͬ
	private boolean isEqual(String dbStr, String str){
		if (dbStr.equalsIgnoreCase(str)) {
			return true;
		} else {
			if (dbStr.endsWith("/")) {
				dbStr = dbStr.substring(0, dbStr.length()-1);
				if (dbStr.equalsIgnoreCase(str)) {
					return true;
				}
			} else if (str.endsWith("/")) {
				str = str.substring(0, str.length()-1);
				if (str.equalsIgnoreCase(dbStr)) {
					return true;
				}
			}
		}
		return false;
	}
	
	public int addTask(String url, String username, String password, String description) {
		int id = -1;
		conn = getconnection();
		if (conn != null) {
			System.out.println(12);
			String timeStamp = NumberUtils.getTime();
			try {
				String sql = "INSERT INTO taskinfo(start_url, login_username, login_password, description, time_stamp) VALUES(?, ?, ?, ?, ?);";
				PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//�������Statement.RETURN_GENERATED_KEYS
				pstmt.setString(1, url);
				pstmt.setString(2, username);
				pstmt.setString(3, password);
				pstmt.setString(4, description);
				pstmt.setString(5, timeStamp);
				pstmt.executeUpdate();
				ResultSet rs = pstmt.getGeneratedKeys(); //��ȡ���   
				if (rs.next()) {
					id = rs.getInt(1);//ȡ��ID
				} 
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				closeConn(conn);
			}
		} else {
			closeConn(conn);
		}
		return id;
	}
}
