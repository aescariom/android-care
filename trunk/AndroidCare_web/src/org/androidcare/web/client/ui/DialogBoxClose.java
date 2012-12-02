package org.androidcare.web.client.ui;

import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.HasDirection;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Panel;

/**
 * This class overrides the default DialogBox to allow us to close by clicking on the 'x' that is on the top-right corner
 * @author Alejandro Escario MŽndez
 *
 */
//@Comentario yo movería esta clase a org.androidcare.web.client
public class DialogBoxClose extends DialogBox {

	private Anchor closeAnchor;

	/**
	 * 
	 * @param title
	 * @param panel
	 */
	public DialogBoxClose(String title, Panel panel) {
        super(true);
        closeAnchor = new Anchor("x");

        FlexTable captionLayoutTable = new FlexTable();
        captionLayoutTable.setWidth("600px");
        captionLayoutTable.setText(0, 0, title);
        captionLayoutTable.setWidget(0, 1, closeAnchor);
        captionLayoutTable.getCellFormatter().setHorizontalAlignment(0, 1,
                HasHorizontalAlignment.HorizontalAlignmentConstant.endOf(HasDirection.Direction.LTR));

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
        
        this.center();
        this.setPopupPosition(this.getPopupLeft() - 150, 50);
    }
	
	/**
	 * close handler behaviour
	 * @param handler
	 */
    public void addCloseHandler(ClickHandler handler) {
        closeAnchor.addClickHandler(handler);
    }	    
  }