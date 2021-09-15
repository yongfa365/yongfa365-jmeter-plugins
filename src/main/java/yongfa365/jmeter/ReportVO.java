package yongfa365.jmeter;

import java.util.ArrayList;
import java.util.List;

public class ReportVO {

    public List<ThreadGroup> items = new ArrayList<>();
    public Summary summary = new Summary();

    public static class Summary {
        public int RequestCount;
        public int FeatureCount;
        public int ErrorCount;
        public int MockCount;
        public int UrlCount;
    }

    public static class ThreadGroup {
        public String Name;
        public List<Record> Steps;
        public boolean HasError;

    }

    public static class Record {
        public String timeStamp;
        public String elapsed;
        public String label;
        public String responseCode;
        public String responseMessage;
        public String threadName;
        public String dataType;
        public String success;
        public String failureMessage;
        public String bytes;
        public String sentBytes;
        public String grpThreads;
        public String allThreads;
        public String URL;
        public String Latency;
        public String IdleTime;
        public String Connect;
        public boolean HasError;
    }
}
