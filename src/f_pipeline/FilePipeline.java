package f_pipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class FilePipeline implements Pipeline {

	public String path;

	public static String pathSeperator = "\\";

	static {
		String property = System.getProperties().getProperty("file.separator");
		if (property != null) {
			pathSeperator = property;
		}
	}

	public FilePipeline(String path) {
		setPath(path);
	}

	@Override
	public void process(File file) {
		
	}
	
	public void process(File file, List<Integer> list) {
		
		PrintWriter printWriter = null;
		 try {
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
			 		getPath(),true),"utf-8"));
			System.out.println("-----------------");
			printWriter.println(file.getAbsolutePath());
			printWriter.print("每一个的起始位置：");
			for (Integer integer : list) {
				printWriter.print(integer + " ");
			}
			printWriter.println();
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} finally{
			printWriter.flush();
			printWriter.close();
		}
	}
	

	private void setPath(String path) {
		this.path = path;
	}

	private String getPath() {
		return path;
	}

}
