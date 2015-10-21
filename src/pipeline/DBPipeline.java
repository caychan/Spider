package pipeline;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;

import utils.DBUtils;
import utils.NumberUtils;
import clawer.Page;
import clawer.ResultItems;
import clawer.Task;


public class DBPipeline implements Pipeline{
	
	private static final int LENGTH = 128;
	static Connection conn = null;
	Statement stmt=null;  
	ResultSet rs=null;  
	DBUtils dbUtils = new DBUtils();

	@Override
	public void process(Task task, Page page) {
		
		int taskId = task.getTaskId();
		String url = page.getUrl().toString();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
		String timeStamp = sdf.format(new Date());
		String content = page.getHtml().toString();
		
		char[] signature = new char[LENGTH];
		signature = NumberUtils.xor(LENGTH, content);
		String sign = new String(signature);

		conn = dbUtils.getconnection();
		if (conn != null) {
			try {
				String selectInDoc = "select * from html_doc where html_url = '" + url + "'";
				stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
				rs = stmt.executeQuery(selectInDoc);
				if (rs.next()) { //存在url
					System.out.println("数据库中存在url");
					if (rs.getString("signature").equals(sign)) { //数据库中该url的signature和现在爬去的url内容相同
						System.out.println( "----------> url内容没有改变");
						//do nothing
					} else{ //如果url内容已变，更新信息
						if (rs.getString(5).equals(content)) {
							System.out.println("url 内容确实变了");
						}
						System.out.println("url内容已变");
						rs.updateInt("task_id", taskId);    //"task_id"
						rs.updateString("time_stamp", timeStamp);  //"time_stamp"
						rs.updateString("content", content);  //"content"
						rs.updateString("signature", sign);
						rs.updateRow();
//						rs.insertRow();
					}
				} else{ //数据库中没有该url
					System.out.println("数据库中不存在url");
					String getSet = "select * from html_doc order by doc_id DESC limit 1";
					rs = stmt.executeQuery(getSet);
					rs.moveToInsertRow();
					rs.updateInt("task_id", taskId);    //"task_id"
					rs.updateString("html_url", url);   //"html_url"
					rs.updateString("time_stamp", timeStamp);  //"time_stamp"
					rs.updateString("content", content);  //"content"
					rs.updateString("signature", new String(signature));
					rs.insertRow();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} finally{
				dbUtils.closeConn(conn);
				dbUtils.closeResultSet(rs);
			}
		} else {
			dbUtils.closeConn(conn);
		}

/*          stmt = conn.createStatement();
            String tet = " insert into html_doc(content) values (" + content + ")"; 
            int cot = stmt.executeUpdate(tet);
            System.out.println(cot + "shou ying xiang");*/
            /*            String insertSql = "INSERT INTO html_doc(task_id, html_url, timestamp, signature) VALUES ("
            		+ taskId + "," + url + "," + timeStamp + "," + signature + ")"; */
/*      		while (rs.next())   
      		{   
      			System.out.println(rs.getInt("doc_id"));
      		}*/
//            rs.moveToInsertRow(); // moves cursor to the insert row
//            System.out.println("the insert row id is "  + rs.getInt(1));
            
/*            rs.last();
            System.out.println("the last row id is "  + rs.getInt(1));
            rs.moveToCurrentRow();
            System.out.println("the current row id is "  + rs.getInt(1));
            rs.close();*/
            
//            rs.updateInt(2, 5); // updates the first column of the insert row to be AINSWORTH
//            rs.updateString(3, url); // updates the second column to be 35
//            rs.updateBoolean(3, true); // updates the third column to true
//            rs.insertRow();
//            rs.moveToCurrentRow();
      		

/*          String updateSql = "UPDATE html_doc set content = " + content + "where timestamp = " + timeStamp;
            int count = stmt.executeUpdate(insertSql);
            int cont = stmt.executeUpdate(updateSql);
        
            System.out.println(count + "条插入，" + cont + "条更改");*/

	}

	@Override
	public void process(ResultItems resultItems, Task task) {}


}
