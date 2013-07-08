package org.androidcare.web.client.widgets;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Panel;

public class DialogBoxClose extends DialogBox {

	private Anchor closeAnchor;
    FlexTable captionLayoutTable = new FlexTable();
	private Panel panel;

	public DialogBoxClose(String title, Panel panel) {
        super(true);
        closeAnchor = new Anchor("x");
        this.panel = panel;

        captionLayoutTable.setText(0, 1, title);
        captionLayoutTable.setWidget(0, 0, closeAnchor);

        HTML caption = (HTML) getCaption();
        caption.getElement().appendChild(captionLayoutTable.getElement());

        caption.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                EventTarget target = event.getNativeEvent().getEventTarget();
                Element targetElement = (Element) target.cast();

                if (targetElement == closeAnchor.getElement()) {
                    closeAnchor.fireEvent(event);
                }
            }
        });

        addCloseHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        
        this.add(panel);
    }
	
	@Override
	public void show(){
		super.show();
		autoCenter();
	}
	
	public void autoCenter(){
        int left = (Window.getClientWidth() - panel.getOffsetWidth())/2 - 5;
        if(left < 0) left = 0;
        int top = (Window.getClientHeight() - panel.getOffsetHeight())/2 - 30;
        if(top < 0) top = 0;
        captionLayoutTable.setWidth(panel.getOffsetWidth() + "px");
        this.setPopupPosition(left, top);
	}
	
	public void addCloseHandler(ClickHandler handler) {
        closeAnchor.addClickHandler(handler);
    }	    
}