package yongfa365.jmeter.control.gui;


import org.apache.commons.lang3.StringUtils;
import org.apache.jmeter.control.gui.AbstractControllerGui;
import org.apache.jmeter.testbeans.gui.TextAreaEditor;
import org.apache.jmeter.testelement.TestElement;
import yongfa365.jmeter.control.MarkdownTableDataDrivenController;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class MarkdownTableDataDrivenControllerGui extends AbstractControllerGui {
    private static final long serialVersionUID = 2412L;

    private TextAreaEditor txtSyntax;

    @Override
    public String getStaticLabel() {
        return "Markdown Table Data-driven Controller";
    }

    @Override
    public String getLabelResource() {
        return null;
    }

    @Override
    public TestElement createTestElement() {
        MarkdownTableDataDrivenController lc = new MarkdownTableDataDrivenController();
        modifyTestElement(lc);
        return lc;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        configureTestElement(element);
        if (element instanceof MarkdownTableDataDrivenController) {
            MarkdownTableDataDrivenController obj = (MarkdownTableDataDrivenController) element;
            obj.setProperty(MarkdownTableDataDrivenController.MARKDOWN_DATA, txtSyntax.getAsText());
        }
    }

    @Override
    public void configure(TestElement element) {
        txtSyntax.setAsText(element.getPropertyAsString(MarkdownTableDataDrivenController.MARKDOWN_DATA));

        super.configure(element);
    }

    public MarkdownTableDataDrivenControllerGui() {
        init();
    }

    private void init() {
        setLayout(new BorderLayout(0, 5));
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);
        add(createBodyPanel(), BorderLayout.CENTER);
    }

    private JPanel createBodyPanel() {
        // https://docs.oracle.com/javase/8/docs/api/java/awt/BorderLayout.html

        JPanel bodyPanel = new JPanel(new BorderLayout());
        JPanel labelPannel = new JPanel(new BorderLayout());

        txtSyntax = new TextAreaEditor();
        Component component = txtSyntax.getCustomEditor();

        JLabel label1 = new JLabel("Markdown Table Data，like 'While Controller',first line will be the variable name");
        label1.setLabelFor(component);

        JButton btnFormat = new JButton("Format");
        btnFormat.addActionListener(e -> {
            String result = format(txtSyntax.getAsText());
            txtSyntax.setAsText(result);
        });

        labelPannel.add(label1, BorderLayout.NORTH);
        labelPannel.add(btnFormat, BorderLayout.CENTER);

        bodyPanel.add(labelPannel, BorderLayout.NORTH);
        bodyPanel.add(component, BorderLayout.CENTER);

        return bodyPanel;
    }

    public String format(String input) {
        List<String> items = Stream.of(input.split("\n"))
                .filter(p -> !p.isEmpty())
                .collect(Collectors.toList());

        HashMap<Integer, Integer> dict = new HashMap<>();

        items.forEach(item ->
        {
            String[] items2 = item.split("\\|");
            for (int i = 0; i < items2.length; i++) {
                int oldlen = 0;
                if (dict.containsKey(i)) {
                    oldlen = dict.get(i);
                }


                int newlen = items2[i].trim().replaceAll("[^\\x00-\\xff]", "aa").length();
                dict.put(i, Math.max(oldlen, newlen));
            }
        });

        StringBuilder sb = new StringBuilder();
        items.forEach(item ->
        {
            //sb.Append("|");
            String[] items2 = item.split("\\|");
            for (int i = 0; i < items2.length; i++) {
                String nowItem = items2[i].trim();

                int expectLen = dict.get(i) + 4;
                int nowLen = nowItem.trim().replaceAll("[^\\x00-\\xff]", "aa").length();
                int left = (expectLen - nowLen) / 2;
                left = 0;
                int right = expectLen - nowLen - left;
                sb.append(StringUtils.leftPad(" ", left));
                sb.append(nowItem);
                if (i != items2.length - 1) {
                    sb.append(StringUtils.rightPad(" ", right)).append("|");
                }
            }

            sb.append("\r\n");
        });
        return StringUtils.stripEnd(sb.toString(), null);
    }


}

