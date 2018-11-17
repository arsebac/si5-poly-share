package tools.util;

import com.google.appengine.api.taskqueue.TaskOptions;

import java.util.Map;

public class QueueHelper {
    public static TaskOptions createQueueMessage(String url, Map<String, String> params) {
        TaskOptions taskOptions = TaskOptions.Builder.withUrl(url);
        params.forEach(taskOptions::param);
        return taskOptions;
    }

}
