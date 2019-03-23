package root.command;

import java.util.LinkedList;
import java.util.Queue;

public class CommandQueue {

	private boolean handling = false;

	private Queue<Command> queue;

	private CommandProcessor on_enqueue = null;

	public CommandQueue() {
		this.queue = new LinkedList<Command>();
	}

	public void enqueue(Command new_command) {
		this.queue.add(new_command);

		if (!this.handling && this.on_enqueue != null) {
			this.handling = true;

			this.on_enqueue.execute(this);

			this.handling = false;
		}

	}

	public Command dequeue() {
		if (!this.queue.isEmpty()) {
			return this.queue.remove();
		}

		return null;

	}

	public boolean isEmpty() {
		return this.queue.isEmpty();
	}

	public void setCommandProcessor(CommandProcessor command_processor) {
		this.on_enqueue = command_processor;
	}

}