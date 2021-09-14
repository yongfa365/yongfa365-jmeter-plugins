package yongfa365.jmeter.assertions.gui;

import net.miginfocom.swing.MigLayout;
import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.gui.GUIMenuSortOrder;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import yongfa365.jmeter.assertions.JSONPathAssertionPlus;
import yongfa365.jmeter.util.NumberDocument;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

@GUIMenuSortOrder(2)
public class JSONPathAssertionPlusGui extends AbstractAssertionGui implements ChangeListener {

    private static final long serialVersionUID = 12312312312345L;

    protected JTextField jsonPath = null;
    protected JSyntaxTextArea jsonValue = null;
    protected JCheckBox isRegex;
    protected JTextField dataSize = null;

    protected JRadioButton isAsc = null;
    protected JRadioButton isDesc = null;
    protected JRadioButton isAllMatch = null;
    protected JRadioButton isAnyMatch = null;
    protected JRadioButton isUseMatch = null;

    protected JRadioButton isUseSize = null;
    protected JTextField beginSize = null;
    protected JTextField endSize = null;


    @Override
    public String getStaticLabel() {
        return "JsonPath Assertion";
    }

    public JSONPathAssertionPlusGui() {
        init();
    }

    void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());
        add(makeTitlePanel(), BorderLayout.NORTH);

        JPanel panel = buildPanel();
        add(panel, BorderLayout.CENTER);

        isUseSize.addChangeListener(this);
        isUseMatch.addChangeListener(this);
    }

    protected JPanel buildPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, wrap 2, insets 0", "[][fill,grow]"));
        JPanel panel2 = new JPanel(new MigLayout("fillx, wrap 8, insets 0", "[][fill,grow]"));
        JPanel sizePanel = new JPanel(new MigLayout("fillx, wrap 8, insets 0", "[][fill,grow]"));

        jsonPath = new JTextField();
        panel.add(JMeterUtils.labelFor(jsonPath, "JsonPath:", "JsonPath"));
        panel.add(jsonPath, "span, growx");

        dataSize = new JTextField();
        dataSize.setDocument(new NumberDocument());
        panel.add(JMeterUtils.labelFor(dataSize, "Size:", "Size"));
        panel.add(dataSize, "span, growx");

        ButtonGroup g1 = new ButtonGroup();
        isAsc = new JRadioButton("Is Asc");
        g1.add(isAsc);

        isDesc = new JRadioButton("Is Desc      ");
        g1.add(isDesc);

        //size
        isUseSize = new JRadioButton("Size");
        g1.add(isUseSize);
        beginSize = new JTextField(6);
        beginSize.setDocument(new NumberDocument());
        sizePanel.add(beginSize);

        endSize = new JTextField(6);
        endSize.setDocument(new NumberDocument());
        sizePanel.add(JMeterUtils.labelFor(endSize, "--", "endSize"));
        sizePanel.add(endSize);

        panel2.add(isAsc);
        panel2.add(isDesc);
        panel2.add(isUseSize);
        panel2.add(sizePanel);
        isUseMatch = new JRadioButton("Match --> ");
        g1.add(isUseMatch);

        panel2.add(isUseMatch);


        ButtonGroup g2 = new ButtonGroup();

        isRegex = new JCheckBox("Regex");

        isAllMatch = new JRadioButton("All Match");
        g2.add(isAllMatch);

        isAnyMatch = new JRadioButton("Any Match");
        g2.add(isAnyMatch);


        panel2.add(isRegex);
        panel2.add(isAllMatch);
        panel2.add(isAnyMatch);
        panel.add(panel2, "span");

        jsonValue = JSyntaxTextArea.getInstance(5, 60);
        panel.add(JMeterUtils.labelFor(jsonValue, "Expected:   ", "expected"));
        panel.add(JTextScrollPane.getInstance(jsonValue));

        return panel;
    }

    @Override
    public void clearGui() {
        super.clearGui();
        jsonPath.setText("");
        jsonValue.setText("");
        isRegex.setSelected(false);
        dataSize.setText("");


        isUseMatch.setSelected(true);
        isAsc.setSelected(false);
        isDesc.setSelected(false);
        isUseSize.setSelected(false);
        isAllMatch.setSelected(true);
        isAnyMatch.setSelected(false);

        beginSize.setText("");
        endSize.setText("");

    }

    @Override
    public TestElement createTestElement() {
        JSONPathAssertionPlus jpAssertion = new JSONPathAssertionPlus();
        modifyTestElement(jpAssertion);
        return jpAssertion;
    }

    @Override
    public String getLabelResource() {
        return null;
    }

    @Override
    public void modifyTestElement(TestElement element) {
        super.configureTestElement(element);
        if (element instanceof JSONPathAssertionPlus) {
            JSONPathAssertionPlus jpAssertion = (JSONPathAssertionPlus) element;
            jpAssertion.setJsonPath(jsonPath.getText());
            jpAssertion.setExpectedValue(jsonValue.getText());
            jpAssertion.setIsRegex(isRegex.isSelected());

            element.setProperty(JSONPathAssertionPlus.DATA_SIZE,dataSize.getText());
            element.setProperty(JSONPathAssertionPlus.IS_USE_MATCH, isUseMatch.isSelected());
            element.setProperty(JSONPathAssertionPlus.IS_ASC, isAsc.isSelected());
            element.setProperty(JSONPathAssertionPlus.IS_DESC, isDesc.isSelected());
            element.setProperty(JSONPathAssertionPlus.IS_USE_SIZE, isUseSize.isSelected());
            element.setProperty(JSONPathAssertionPlus.IS_ALL_MATCH, isAllMatch.isSelected());
            element.setProperty(JSONPathAssertionPlus.IS_ANY_MATCH, isAnyMatch.isSelected());

            element.setProperty(JSONPathAssertionPlus.BEGIN_SIZE, beginSize.getText());
            element.setProperty(JSONPathAssertionPlus.END_SIZE, endSize.getText());
        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof JSONPathAssertionPlus) {
            JSONPathAssertionPlus jpAssertion = (JSONPathAssertionPlus) element;
            jsonPath.setText(jpAssertion.getJsonPath());
            jsonValue.setText(jpAssertion.getExpectedValue());
            isRegex.setSelected(jpAssertion.isUseRegex());

            dataSize.setText(element.getPropertyAsString(JSONPathAssertionPlus.DATA_SIZE));
            isUseMatch.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_USE_MATCH));
            isAsc.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_ASC));
            isDesc.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_DESC));
            isUseSize.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_USE_SIZE));
            isAllMatch.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_ALL_MATCH));
            isAnyMatch.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_ANY_MATCH));

            beginSize.setText(element.getPropertyAsString(JSONPathAssertionPlus.BEGIN_SIZE));
            endSize.setText(element.getPropertyAsString(JSONPathAssertionPlus.END_SIZE));
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        isRegex.setEnabled(isUseMatch.isSelected());
        isAllMatch.setEnabled(isUseMatch.isSelected());
        isAnyMatch.setEnabled(isUseMatch.isSelected());
        jsonValue.setEnabled(isUseMatch.isSelected());
        beginSize.setEnabled(isUseSize.isSelected());
        endSize.setEnabled(isUseSize.isSelected());
    }
}
