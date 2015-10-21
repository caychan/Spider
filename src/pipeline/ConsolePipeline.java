package pipeline;

import java.util.Map;

import clawer.Page;
import clawer.ResultItems;
import clawer.Task;

/**
 * Write results in console.<br>
 * Usually used in test.
 *
 */
public class ConsolePipeline implements Pipeline {

    @Override
    public void process(ResultItems resultItems, Task task) {
        System.out.println("get page: " + resultItems.getRequest().getUrl());
        for (Map.Entry<String, Object> entry : resultItems.getAll().entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }

	@Override
	public void process(Task task, Page page) {
		// TODO Auto-generated method stub
		
	}
}
