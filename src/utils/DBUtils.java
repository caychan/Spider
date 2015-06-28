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
import pipeline.SaveToFile_Old;

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
				if (rs.next()) { //存在url
					if (rs.getString("signature").equals(signature)) { //数据库中该url的signature和现在爬去的url内容相同
						System.out.println( "存在该url且内容没变");
						//do nothing
					} else{ //如果url内容已变，更新信息
					 	path = stf.saveToLocal(entity, taskId);
						System.out.println("存在该url但内容已变");
						rs.updateInt("task_id", taskId);
						rs.updateString("resource_pattern", pattern);    
						rs.updateString("time_stamp", timeStamp);  
						rs.updateString("resource_path", path);   
						rs.updateString("signature", signature);
						rs.updateRow();
					}
				} else{ //数据库中没有该url
					System.out.println("不存在该url");
					path = stf.saveToLocal(entity, taskId);
					String getSet = "select * from resource order by resource_id DESC limit 1";
					closeResultSet(rs);//不加这句，下面就会有警告“Resource leak: 'rs' is not closed at this location”
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
				//ResultSet.TYPE_SCROLL_INSENSITIVE:双向滚动，但不及时更新，就是如果数据库里的数据修改过，并不在ResultSet中反应出来。   
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
				//执行该sql语句
				rs = stmt.executeQuery(getSet);		
				//遍历得到的ResultSet
				while(rs.next()){	
					if(isEqual(rs.getString("start_url").trim(), url.trim())){
//						System.out.println("same domain");
						if (timeToCount) {	//如果考虑时间间隔
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
						} else {//不考虑时间间隔
							flag = true;
						}
						break; //对一个url只看最近爬的一次。
					}	//contains domain
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				closeConn(conn);
				closeResultSet(rs);
			}
			return flag;
		} else {	//如果没有建立数据库连接，按重复处理，即不执行该任务
			closeConn(conn);
			return true;
		}
	}
	
	//判断两个url是否相同
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
				PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);//传入参数：Statement.RETURN_GENERATED_KEYS
				pstmt.setString(1, url);
				pstmt.setString(2, username);
				pstmt.setString(3, password);
				pstmt.setString(4, description);
				pstmt.setString(5, timeStamp);
				pstmt.executeUpdate();
				ResultSet rs = pstmt.getGeneratedKeys(); //获取结果   
				if (rs.next()) {
					id = rs.getInt(1);//取得ID
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
