package f_pipeline;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class FileWordPipeline implements Pipeline {

	public String path;

	public static String pathSeperator = "\\";

	static {
		String property = System.getProperties().getProperty("file.separator");
		if (property != null) {
			pathSeperator = property;
		}
	}

	public FileWordPipeline(String path) {
		setPath(path);
	}

	@Override
	public void process(File file) {
		
	}
	
	public void process(String path, File file, List<String> list) {
		
		PrintWriter printWriter = null;
		 try {
			printWriter = new PrintWriter(new OutputStreamWriter(new FileOutputStream(
			 		getFile(path, file),true),"utf-8"));
			System.out.println("-----------------");
			printWriter.println(file.getAbsolutePath());

			for (String string : list){
				printWriter.print(string);
			}
		} catch (UnsupportedEncodingException | FileNotFoundException e) {
			e.printStackTrace();
		} finally{
			printWriter.flush();
			printWriter.close();
		}
	}
	

	private File getFile(String path, File file) {
		if (! path.endsWith(pathSeperator)) {
			path += pathSeperator;
		}
		File dir = new File(path);
		if (! dir.exists()) {
			dir.mkdirs();
		}
		path += file.getName();
		return new File(path);
	}

	private void setPath(String path) {
		this.path = path;
	}

	private String getPath() {
		return path;
	}

}
