package f_process;

import java.io.File;

import f_scheduler.QueueScheduler;
import f_searcher.Searcher;


public class ExtraFiles{
	
	QueueScheduler scheduler = Searcher.getScheduler();
	
	public void getExtraFiles(File file){
		
		File[] files = file.listFiles();
		synchronized (scheduler) {
			for (File f : files) {
				scheduler.push(f);
			}
		}
	}
}
