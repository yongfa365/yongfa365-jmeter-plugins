package yongfa365.jmeter.assertions.gui;

import net.miginfocom.swing.MigLayout;
import org.apache.jmeter.assertions.gui.AbstractAssertionGui;
import org.apache.jmeter.gui.GUIMenuSortOrder;
import org.apache.jmeter.gui.util.JSyntaxTextArea;
import org.apache.jmeter.gui.util.JTextScrollPane;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.util.JMeterUtils;
import yongfa365.jmeter.assertions.JSONPathAssertionPlus;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;

@GUIMenuSortOrder(2)
public class JSONPathAssertionPlusGui extends AbstractAssertionGui implements ChangeListener {

    private static final long serialVersionUID = 12312312312345L;

    protected JTextField jsonPath = null;
    protected JSyntaxTextArea jsonValue = null;

    protected JRadioButton isAsc = null;
    protected JRadioButton isDesc = null;

    protected JRadioButton isUseMatch = null;
    protected JCheckBox isRegex;
    protected JRadioButton isAllMatch = null;
    protected JRadioButton isAnyMatch = null;


    protected JRadioButton isUseCount = null;
    protected JTextField minCount = null;
    protected JTextField maxCount = null;


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

        isUseCount.addChangeListener(this);
        isUseMatch.addChangeListener(this);
    }

    protected JPanel buildPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, wrap 2, insets 0", "[][fill,grow]"));
        JPanel panel2 = new JPanel(new MigLayout("fillx, wrap 8, insets 0", "[][fill,grow]"));
        JPanel panelCount = new JPanel(new MigLayout("fillx, wrap 8, insets 0", "[][fill,grow]"));

        jsonPath = new JTextField();
        panel.add(JMeterUtils.labelFor(jsonPath, "JsonPath:", "JsonPath"));
        panel.add(jsonPath, "span, growx");


        ButtonGroup g1 = new ButtonGroup();
        isAsc = new JRadioButton("Is Asc");
        g1.add(isAsc);

        isDesc = new JRadioButton("Is Desc      ");
        g1.add(isDesc);

        //size
        isUseCount = new JRadioButton("Count");
        isUseCount.setToolTipText("JsonPath Result Count,eg:[1,3,4] count is 3");
        g1.add(isUseCount);
        minCount = new JTextField(2);
        panelCount.add(minCount);

        maxCount = new JTextField(2);
        panelCount.add(JMeterUtils.labelFor(maxCount, "<= count <=", "maxCount"));
        panelCount.add(maxCount);

        panel2.add(isAsc);
        panel2.add(isDesc);
        panel2.add(isUseCount);
        panel2.add(panelCount);
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


        isAsc.setSelected(false);
        isDesc.setSelected(false);

        isUseMatch.setSelected(true);
        isAllMatch.setSelected(true);
        isAnyMatch.setSelected(false);

        isUseCount.setSelected(false);
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


            element.setProperty(JSONPathAssertionPlus.IS_DEFAULT, isAsc.isSelected());
            element.setProperty(JSONPathAssertionPlus.IS_DESC, isDesc.isSelected());

            element.setProperty(JSONPathAssertionPlus.IS_USE_MATCH, isUseMatch.isSelected());
            element.setProperty(JSONPathAssertionPlus.IS_ALL_MATCH, isAllMatch.isSelected());
            element.setProperty(JSONPathAssertionPlus.IS_ANY_MATCH, isAnyMatch.isSelected());

            element.setProperty(JSONPathAssertionPlus.IS_CUSTOM, isUseCount.isSelected());
            element.setProperty(JSONPathAssertionPlus.ROOT_PATH, minCount.getText());
            element.setProperty(JSONPathAssertionPlus.MAX_COUNT, maxCount.getText());
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


            isAsc.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_DEFAULT));
            isDesc.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_DESC));


            isUseMatch.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_USE_MATCH));
            isAllMatch.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_ALL_MATCH));
            isAnyMatch.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_ANY_MATCH));

            isUseCount.setSelected(element.getPropertyAsBoolean(JSONPathAssertionPlus.IS_CUSTOM));
            minCount.setText(element.getPropertyAsString(JSONPathAssertionPlus.ROOT_PATH));
            maxCount.setText(element.getPropertyAsString(JSONPathAssertionPlus.MAX_COUNT));
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        isRegex.setEnabled(isUseMatch.isSelected());
        isAllMatch.setEnabled(isUseMatch.isSelected());
        isAnyMatch.setEnabled(isUseMatch.isSelected());
        jsonValue.setEnabled(isUseMatch.isSelected());
        minCount.setEnabled(isUseCount.isSelected());
        maxCount.setEnabled(isUseCount.isSelected());
    }
}
