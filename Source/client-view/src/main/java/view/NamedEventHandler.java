package view;

import javafx.event.Event;
import root.view.event.ViewEventHandler;

public abstract class NamedEventHandler implements ViewEventHandler {

	private String name;

	// method from ViewEventHandler interface
	public abstract void execute(Event arg);

	public NamedEventHandler(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
