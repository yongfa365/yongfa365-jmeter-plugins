package yongfa365.jmeter.result.gui;

import net.miginfocom.swing.MigLayout;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;
import yongfa365.jmeter.result.ConsoleAndHtmlReport;


import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

public class ConsoleAndHtmlReportGui extends AbstractListenerGui implements ClipboardOwner, ChangeListener {
    private static final long serialVersionUID = 123123123123454L;

    protected JRadioButton isDefault = null;

    protected JRadioButton isCustom = null;
    protected JTextField rootPath = null;


    @Override
    public String getStaticLabel() {
        return "Console And Html Report";
    }


    public ConsoleAndHtmlReportGui() {
        init();
    }

    void init() {
        setLayout(new BorderLayout());
        setBorder(makeBorder());

        JPanel panel = buildPanel();
        add(panel, BorderLayout.CENTER);

        isCustom.addChangeListener(this);
    }

    protected JPanel buildPanel() {
        JPanel panel = new JPanel(new MigLayout("fillx, wrap 2, insets 0", "[][fill,grow]"));
        JPanel panel2 = new JPanel(new MigLayout("fillx, wrap 8, insets 0", "[][fill,grow]"));
        JPanel panelCount = new JPanel(new MigLayout("fillx, wrap 8, insets 0", "[][fill,grow]"));


        ButtonGroup g1 = new ButtonGroup();
        isDefault = new JRadioButton("Default. use *.jmx Path");
        g1.add(isDefault);


        //size
        isCustom = new JRadioButton("Custom RootPath");
        g1.add(isCustom);
        rootPath = new JTextField(36);
        panelCount.add(rootPath);


        panel2.add(isDefault);
        panel2.add(isCustom);
        panel2.add(panelCount);


        panel.add(panel2, "span");

        return panel;
    }

    @Override
    public void clearGui() {
        super.clearGui();

        isDefault.setSelected(true);
        isCustom.setSelected(false);
    }

    @Override
    public TestElement createTestElement() {
        ConsoleAndHtmlReport jpAssertion = new ConsoleAndHtmlReport();
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
        if (element instanceof ConsoleAndHtmlReport) {
            element.setProperty(ConsoleAndHtmlReport.IS_DEFAULT, isDefault.isSelected());
            element.setProperty(ConsoleAndHtmlReport.IS_CUSTOM, isCustom.isSelected());
            element.setProperty(ConsoleAndHtmlReport.ROOT_PATH, rootPath.getText());
        }
    }

    @Override
    public void configure(TestElement element) {
        super.configure(element);
        if (element instanceof ConsoleAndHtmlReport) {

            isDefault.setSelected(element.getPropertyAsBoolean(ConsoleAndHtmlReport.IS_DEFAULT));

            isCustom.setSelected(element.getPropertyAsBoolean(ConsoleAndHtmlReport.IS_CUSTOM));
            rootPath.setText(element.getPropertyAsString(ConsoleAndHtmlReport.ROOT_PATH));
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {

        rootPath.setEnabled(isCustom.isSelected());

    }

    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {

    }
}
