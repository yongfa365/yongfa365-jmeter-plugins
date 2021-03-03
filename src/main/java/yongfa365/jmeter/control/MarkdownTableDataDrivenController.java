package yongfa365.jmeter.control;

import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.control.WhileController;
import org.apache.jmeter.engine.event.LoopIterationEvent;
import org.apache.jmeter.samplers.Sampler;
import org.apache.jmeter.threads.JMeterContextService;
import org.apache.jmeter.threads.JMeterVariables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.stream.Stream;

public class MarkdownTableDataDrivenController extends WhileController {
    private static final Logger log = LoggerFactory.getLogger(MarkdownTableDataDrivenController.class);


    // property name
    public static final String MARKDOWN_DATA = "Markdown.Data";

    private final static String ITEM_SPLITTER = "\\|";
    private String[] items = null;
    private String[] heads = null;


    public MarkdownTableDataDrivenController() {
        super();
    }


    /**
     * This skips controller entirely if the condition is false on first entry.
     * <p>
     * {@inheritDoc}
     */
    @Override
    public Sampler next() {
        JMeterVariables vars = JMeterContextService.getContext().getVariables();

        // 跳过head
        int lineIndex = getIterCount() + 1;

        if (lineIndex == items.length) {
            // 清理现场
            for (String head : heads) {
                vars.remove(head.trim());
            }
            // 结束循环
            return null;
        }

        String[] line = items[lineIndex].split(ITEM_SPLITTER);

        // 列变量赋值当前行的值
        for (int i = 0; i < line.length; i++) {
            vars.put(heads[i].trim(), line[i].trim());
        }

        return super.next();
    }

    @Override
    public void iterationStart(LoopIterationEvent iterEvent) {
        super.iterationStart(iterEvent);

        String data = getPropertyAsString(MARKDOWN_DATA);
        if (data.isEmpty()) {
            return;
        }

        //拆分数据
        items = Stream.of(data.split("\n"))
                .filter(p -> StringUtils.isNotBlank(p) && !p.trim().startsWith("#"))
                .toArray(String[]::new);

        // 获取列名
        heads = items[0].split(ITEM_SPLITTER);
    }


}