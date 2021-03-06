package controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

import controller.option.AddToPathFieldOption;
import controller.option.MoveFieldOption;
import controller.option.SelectPathFieldOption;

import root.ActiveComponent;
import root.command.BasicCommandProcessor;
import root.command.Command;
import root.command.CommandDrivenComponent;
import root.command.CommandProcessor;
import root.command.CommandProducer;
import root.command.CommandQueue;
import root.communication.GameServerProxy;
import root.controller.Controller;
import root.model.Model;
import root.model.component.Field;
import root.model.component.option.FieldOption;
import root.model.event.ModelEventArg;
import root.model.event.ModelEventHandler;
import root.view.View;
import root.view.event.ViewEventArg;
import root.view.event.ViewEventHandler;
import root.view.menu.Menu;
import view.command.PopulateMenuCommand;
import view.command.SelectFieldCommand;
import view.command.ShowFieldInfoCommand;
import view.command.ZoomInCommand;
import view.command.ZoomOutCommand;

public class GameBrain implements Controller {

	private GameServerProxy serverProxy;

	private CommandProcessor serverCommandProcessor;
	private CommandQueue serverCommandQueue;

	private View view;
	private CommandQueue viewCommandQueue;

	private Model model;

	private Field selectedField;
	private Field focusedField;
	private List<Command> undoStack;

	private List<FieldOption> fieldOptions;

	// constructors
	public GameBrain(GameServerProxy server_proxy, View view, Model model) {
		super();

		this.view = view;
		this.model = model;
		this.serverProxy = server_proxy;

		this.undoStack = new ArrayList<Command>();

		// attention let's say that every controller implementations has its own
		// ModelEventHandler (maybe this isn't the best approach)

		// this.model.setEventHandler(new DefaultModelEventHandler(this));
		this.model.setEventHandler((ModelEventHandler) this);

		this.initFieldOptions();

		// connect serverProxy and controller
		this.serverCommandQueue = ((CommandProducer) this.serverProxy).getConsumerQueue();
		this.serverCommandProcessor = new BasicCommandProcessor(
				Executors.newSingleThreadExecutor(),
				(CommandDrivenComponent) this);
		this.serverCommandQueue.setCommandProcessor(this.serverCommandProcessor);

		this.viewCommandQueue = this.view.getCommandQueue();

		// view events, click, key press ...
		this.initViewEventHandlers();

		this.view.show();

	}

	// methods

	private void initViewEventHandlers() {

		this.view.addEventHandler("left-mouse-click-event", new ViewEventHandler() {

			public void execute(ViewEventArg arg) {

				Field focused_field = model.getField(arg.getFieldPosition());

				if (focused_field != null) {

					// undo all previous commands
					if (!undoStack.isEmpty()) {
						for (int i = (undoStack.size() - 1); i >= 0; i--) {
							viewCommandQueue.enqueue(undoStack.get(i).getAntiCommand());
						}
					}
					undoStack.clear();

					// execute new command
					Command select_command = new SelectFieldCommand(focused_field);
					viewCommandQueue.enqueue(select_command);

					undoStack.add(select_command);

					selectedField = focused_field;
					focusedField = null;// note focusedField != focused_field

				}

			}

		});

		this.view.addEventHandler("right-mouse-click-event", new ViewEventHandler() {

			public void execute(ViewEventArg arg) {

				focusedField = model.getField(arg.getFieldPosition());

				// valid click
				if (focusedField != null) {

					Command showMenuCommand;
					if (selectedField != null) {
						showMenuCommand = new ShowFieldInfoCommand(selectedField, focusedField);
					} else {
						showMenuCommand = new ShowFieldInfoCommand(focusedField, focusedField);
					}

					viewCommandQueue.enqueue(showMenuCommand);

					undoStack.add(showMenuCommand);

				}

			}

		});

		// TODO maybe for the purpose of redrawing path and similar options
		// add additional list of command which are "stateless" and which execution wont
		// do any damage to the current state if they are executed more than once

		this.view.addEventHandler("key-event-char-1", new ViewEventHandler() {

			public void execute(ViewEventArg arg) {

				ZoomInCommand command = new ZoomInCommand(model.getFields());
				viewCommandQueue.enqueue(command);

				// this wont be valid in situation when attacak and build commands get
				// implemented
				// // reset old state
				// for (Command prev_command : toUndo) {
				// viewCommandQueue.enqueue(prev_command);
				// }

			}
		});

		this.view.addEventHandler("key-event-char-2", new ViewEventHandler() {

			public void execute(ViewEventArg arg) {

				ZoomOutCommand command = new ZoomOutCommand(model.getFields());
				viewCommandQueue.enqueue(command);

				// this wont be valid in sitation when attack and build command get implemented
				// // reset old state
				// for (Command prev_command : toUndo) {
				// viewCommandQueue.enqueue(prev_command);
				// }

			}
		});

	}

	private void initFieldOptions() {

		this.fieldOptions = new ArrayList<FieldOption>();

		this.fieldOptions.add(new SelectPathFieldOption(this));
		this.fieldOptions.add(new MoveFieldOption(this));
		this.fieldOptions.add(new AddToPathFieldOption(this));

	}

	// getters and setters

	@Override
	public View getView() {
		return this.view;
	}

	@Override
	public void setView(View view) {
		this.view = view;
	}

	@Override
	public Model getModel() {
		return this.model;
	}

	@Override
	public void setModel(Model model) {
		this.model = model;
	}

	@Override
	public void shutdown() {

		if (this.serverCommandProcessor != null) {
			((ActiveComponent) this.serverCommandProcessor).shutdown();
		}

		if (this.view != null) {
			((ActiveComponent) this.view).shutdown();
		}

		if (this.model != null) {
			this.model.shutdown();
		}

	}

	@Override
	public void handleModelEvent(ModelEventArg event_argument) {
		this.serverProxy.sendIntention(event_argument);
	}

	@Override
	public GameServerProxy getServerProxy() {
		return this.serverProxy;
	}

	@Override
	public void setServerProxy(root.communication.GameServerProxy new_proxy) {
		this.serverProxy = new_proxy;
	}

	@Override
	public CommandQueue getCommandQueue() {
		return this.serverCommandQueue;
	}

	@Override
	public void setCommandQueue(CommandQueue new_queue) {
		this.serverCommandQueue = new_queue;
	}

	@Override
	public CommandProcessor getCommandProcessor() {
		return this.serverCommandProcessor;
	}

	@Override
	public void setConsumerQueue(CommandQueue consumer_queue) {
		this.viewCommandQueue = consumer_queue;
	}

	@Override
	public CommandQueue getConsumerQueue() {
		return this.viewCommandQueue;
	}

	@Override
	public List<FieldOption> getPossibleFieldOptions() {
		return this.fieldOptions;
	}

	@Override
	public void enqueueForUndone(Command new_command) {
		this.undoStack.add(new_command);
	}

	@Override
	public Field getSelectedField() {
		return this.selectedField;
	}

	@Override
	public void selectField(Field fieldToSelect) {

		this.selectedField = fieldToSelect;
		viewCommandQueue.enqueue(new SelectFieldCommand(this.selectedField));

		Menu fieldMenu = view.getOptionMenu();
		if (fieldMenu.isDisplayed()) {
			selectedField.adjustOptionsFor(focusedField);
			viewCommandQueue.enqueue(new PopulateMenuCommand(selectedField.getEnabledOptions()));
		}

	}

}
