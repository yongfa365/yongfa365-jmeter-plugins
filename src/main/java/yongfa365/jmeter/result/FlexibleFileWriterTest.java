package yongfa365.jmeter.result;

import net.minidev.json.JSONValue;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.jmeter.engine.util.NoThreadClone;
import org.apache.jmeter.reporters.AbstractListenerElement;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.samplers.Remoteable;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleListener;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestStateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import yongfa365.jmeter.ReportVO;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.util.*;

/**
 * @see ResultCollector
 */
public class FlexibleFileWriterTest
        extends AbstractListenerElement
        implements SampleListener, Serializable,
        TestStateListener, Remoteable, NoThreadClone {

    private static final Logger log = LoggerFactory.getLogger(FlexibleFileWriterTest.class);

    /**
     * 用于增量收集数据以及后续对结果集的数据结构转换
     */
    private Map<String, List<ReportVO.Record>> dataMap = new LinkedHashMap<>();


    public FlexibleFileWriterTest() {
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
    public void testEnded() {
        //统计
        statisticalData();
    }

    @Override
    public void testEnded(String host) {
        testEnded();
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
            record.URL = urlAsString;
            record.label = sampleLabel;
            //存起来
            if (!dataMap.containsKey(threadName)) {
                dataMap.put(threadName, new ArrayList<>());
            }
            dataMap.get(threadName).add(record);
        }
    }

    /**
     * 统计数据
     */
    private void statisticalData() {
        if (dataMap != null && dataMap.size() > 0) {
            //统计---统计完清空数据，这里加锁防止重复统计
            FileOutputStream fos = null;
            try {
                //获取结果数据
                ReportVO reportVO = getReportVO();
                //输出结果到控制台
                outputToConsole(reportVO);

                //输出结果到文件
                String reportVOsStr = JSONValue.toJSONString(reportVO);
                //遍历项目所有java文件
                File file = new File("D:\\Sources.git\\yongfa365-jmeter-plugins\\src\\main\\resources\\templates\\data.js");
                // 创建基于文件的输出流
                fos = new FileOutputStream(file);
                // 把数据写入到输出流
                fos.write(("let data = " + reportVOsStr).getBytes());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                dataMap.clear();
                if (fos != null) {
                    try {
                        // 关闭输出流
                        fos.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
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

        //数据结构转换
        for (Map.Entry<String, List<ReportVO.Record>> entry : dataMap.entrySet()) {
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
                    featureSet.add(record.label + record.URL);
                    summary.RequestCount++;
                    if (record.HasError) {
                        summary.ErrorCount++;
                        threadGroup.HasError = true;
                    }
                    if (ObjectUtils.isNotEmpty(record.URL)) {
                        summary.UrlCount++;
                    }
                    if (record.URL.contains("mock")) {
                        summary.MockCount++;
                    }

                }
            }
        }
        summary.FeatureCount = featureSet.size();
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
                            stringBuilder.append("        xxx ").append(record.failureMessage.replaceAll("\n", "")).append("\r\n");
                        } else {
                            stringBuilder.append("   [✔]").append(label).append("\r\n");
                        }
                    });
                }
            });
            System.out.println(stringBuilder.toString());
        }
    }

}