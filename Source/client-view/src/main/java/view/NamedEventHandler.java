package view;

public abstract class NamedEventHandler implements ViewEventHandler {

	private String name;

	// method from ViewEventHandler interface
	public abstract void handle(ViewEventArg arg);

	public NamedEventHandler() {
		// TODO Auto-generated constructor stub
	}

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