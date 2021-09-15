package yongfa365.jmeter.result;

import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.visualizers.gui.AbstractListenerGui;

import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.Transferable;

public class FlexibleFileWriterTestGui extends AbstractListenerGui implements ClipboardOwner {


    @Override
    public String getStaticLabel() {
        return "FlexibleFileWriterTestGui";
    }

    @Override
    public String getLabelResource() {
        return getClass().getCanonicalName();
    }

    @Override
    public TestElement createTestElement() {
        TestElement te = new FlexibleFileWriterTest();
        modifyTestElement(te);
        return te;
    }

    @Override
    public void modifyTestElement(TestElement te) {
        super.configureTestElement(te);
    }

    @Override
    public void clearGui() {
        super.clearGui();
    }


    @Override
    public void configure(TestElement element) {
        super.configure(element);
    }


    @Override
    public void lostOwnership(Clipboard clipboard, Transferable contents) {
        // do nothing
    }

}
