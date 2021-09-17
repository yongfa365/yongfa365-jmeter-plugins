package yongfa365.jmeter.result;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.samplers.Remoteable;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.services.FileServer;
import org.apache.jmeter.testelement.TestStateListener;
import org.apache.jmeter.util.JMeterUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yongfa365.jmeter.utils.MyUtils;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;

public class ConsoleAndHtmlReport
        extends AbstractListenerElement
        implements SampleListener, Serializable,
        TestStateListener, Remoteable, NoThreadClone {

    private static final Logger log = LoggerFactory.getLogger(ConsoleAndHtmlReport.class);

    /**
     * 用于增量收集数据以及后续对结果集的数据结构转换
     */
    private final Map<String, List<ReportVO.Record>> DATA = new LinkedHashMap<>();


    public static final String IS_DEFAULT = "IS_DEFAULT";
    public static final String IS_CUSTOM = "IS_CUSTOM";
    public static final String ROOT_PATH = "ROOT_PATH";


    public boolean isDefault() {
        return getPropertyAsBoolean(IS_DEFAULT, false);
    }

    public String getRootPath() {
        if (isDefault()) {
            return FileServer.getFileServer().getBaseDir();
        } else {
            return getPropertyAsString(ROOT_PATH, "");
        }
    }


    public ConsoleAndHtmlReport() {
        super();
    }

    @Override
    public void sampleStarted(SampleEvent e) {
    }

    @Override
    public void sampleStopped(SampleEvent e) {

    }

    @Override
    public void testStarted() {
    }

    @Override
    public void testStarted(String host) {
        testStarted();
    }

    @Override
    public void testEnded(String host) {
        testEnded();
    }

    @Override
    public void testEnded() {
        if (ObjectUtils.isEmpty(DATA)) {
            return;
        }
        ReportVO reportVO = getReportVO();
        outputToConsole(reportVO);
        outputToHtml(reportVO);
    }

    @Override
    public void sampleOccurred(SampleEvent evt) {
        if (evt != null && evt.getResult() != null) {
            //收集数据
            SampleResult result = evt.getResult();
            String threadName = result.getThreadName();
            //结果数据
            String sampleLabel = result.getSampleLabel();
            String urlAsString = result.getUrlAsString();
            String responseCode = result.getResponseCode();
            String responseMessage = result.getResponseMessage();
            String firstAssertionFailureMessage = result.getFirstAssertionFailureMessage();
            int errorCount = result.getErrorCount();

            ReportVO.Record record = new ReportVO.Record();
            record.HasError = errorCount > 0;
            record.failureMessage = firstAssertionFailureMessage;
            record.responseCode = responseCode;
            record.responseMessage = responseMessage;
            record.URL = urlAsString.replaceAll("/(APP|H55|WEB).{24}", "");
            record.label = sampleLabel;
            //存起来
            if (!DATA.containsKey(threadName)) {
                DATA.put(threadName, new ArrayList<>());
            }
            DATA.get(threadName).add(record);
        }
    }


    /**
     * 获取结果数据
     *
     * @return
     */
    private ReportVO getReportVO() {
        ReportVO reportVO = new ReportVO();
        List<ReportVO.ThreadGroup> threadGroups = new ArrayList<>();
        ReportVO.Summary summary = new ReportVO.Summary();
        reportVO.summary = summary;
        reportVO.items = threadGroups;
        Set<String> featureSet = new HashSet<>();
        Set<String> urlSet = new HashSet<>();
        //数据结构转换
        for (Map.Entry<String, List<ReportVO.Record>> entry : DATA.entrySet()) {
            String s = entry.getKey();
            List<ReportVO.Record> records = entry.getValue();

            ReportVO.ThreadGroup threadGroup = new ReportVO.ThreadGroup();
            threadGroups.add(threadGroup);

            if (threadGroup.Name == null) {
                threadGroup.Name = s;
            }
            threadGroup.Steps = records;
            if (records != null) {
                for (ReportVO.Record record : records) {
                    featureSet.add(record.label);
                    summary.RequestCount++;
                    if (record.HasError) {
                        summary.ErrorCount++;
                        threadGroup.HasError = true;
                    }
                    if (ObjectUtils.isNotEmpty(record.URL)) {
                        urlSet.add(record.URL);
                    }
                    if (record.URL.contains("mock")) {
                        summary.MockCount++;
                    }

                }
            }
        }
        summary.FeatureCount = featureSet.size();
        summary.UrlCount = urlSet.size();
        return reportVO;
    }

    /**
     * 输出结果到控制台
     *
     * @param reportVO
     */
    private void outputToConsole(ReportVO reportVO) {
        if (reportVO.items != null) {
            StringBuilder stringBuilder = new StringBuilder();
            reportVO.items.forEach(threadGroup -> {
                String name = threadGroup.Name;
                if (threadGroup.HasError) {
                    stringBuilder.append("[✖]").append(name).append("\r\n");
                } else {
                    stringBuilder.append("[✔]").append(name).append("\r\n");
                }
                if (threadGroup.Steps != null) {
                    threadGroup.Steps.forEach(record -> {
                        String label = record.label;
                        if (record.HasError) {
                            stringBuilder.append("   [✖]").append(label).append("\r\n");
                            stringBuilder.append("      ").append(StringUtils.trimToEmpty(record.failureMessage).replaceAll("\n", "\n          ")).append("\r\n");
                        } else {
                            stringBuilder.append("   [✔]").append(label).append("\r\n");
                        }
                    });
                }
            });

            outputToConsole(stringBuilder);
        }
    }

    private void outputToConsole(StringBuilder sb) {
        try {
            PrintStream ps = new PrintStream(System.out, true, "UTF-8");
            ps.println(sb);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    private void outputToHtml(ReportVO reportVO) {
        try {
            String testPlanFile = FileServer.getFileServer().getScriptName();

            String rootPath = getRootPath();
            String baseName = FilenameUtils.getBaseName(testPlanFile);
            String reportPath = rootPath + "\\" + baseName;
            Files.createDirectories(Paths.get(reportPath));

            String index = JMeterUtils.getResourceFileAsText("index.html");
            MyUtils.writeToFile(Paths.get(reportPath, "index.html"), index);

            String result = "let data = " + MyUtils.toJson(reportVO);
            MyUtils.writeToFile(Paths.get(reportPath, "data.js"), result);

            System.out.printf("\n【html report】%s\n", reportPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}