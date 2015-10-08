package f_process;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sun.net.www.http.KeepAliveCache;
import f_pipeline.ConsolePipeline;
import f_scheduler.FileCacheScheduler;
import f_searcher.Searcher;

public class Search implements Processor {

	private static final String FILE_PATH = "F:\\Clawer\\LNU_BBS_test\\";
	private String keyWord = "辽宁大学";
	static Searcher searcher = new Searcher(new Search());
	
	RecordToFile fwPipeline = new RecordToFile("F:\\Clawer\\LNU_TBBS\\record");
	
//	保存处理的文件信息到list中，供前端使用
	List<List<String>> lstFiles = new ArrayList<List<String>>();
	
	
	int i = 0;
	
	
//	public static void main(String[] args) {
		public static void M() {
		searcher.start();
		File file = new File(FILE_PATH);
		if (file.exists()) {
			searcher.startFile(file)
//			.setScheduler(new FileCacheScheduler(FILECACHEPATH))
			//在console中显示当前处理的文件
			.addPipeline(new ConsolePipeline())
			//开启多线程同步处理
			//.thread(5)
			.setSleepTime(500)
			.run();
		}
	}
		


	@Override
	public void process(File file) {
		if (file.isDirectory()) {
			searcher.getExtraFiles(file);
		} else if (file.isFile() && file.canRead()) {
			SearchHtmlKeyWord shkw = new SearchHtmlKeyWord();
			List<String> sentence = new ArrayList<String>();
			sentence = shkw.searchKeyWordReturnSentence(file, keyWord);
			if (sentence.size() > 1) {
				System.out.println("------" + file.getAbsolutePath());
//				addInfoToList(sentence);
/*				for (String str : sentence) {
					System.out.println(str);
				}*/
				
//				fwPipeline.process(file, sentence);
				
			}
		}
	}
	
	
	public void stopSearch(){
		searcher.stop();
	}

	private void addInfoToList(List<String> sentence){
		synchronized (lstFiles) {
			lstFiles.add(sentence);
		}
	}
	
	private void clearList(){
		synchronized (lstFiles) {
			lstFiles.clear();
		}
	}
	
	
}
