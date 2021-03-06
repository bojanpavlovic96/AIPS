package communication;

import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Channel;

import controller.command.CtrlInitializeCommand;
import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import model.PlayerModelData;
import model.component.field.ModelField;
import root.command.Command;
import root.command.CommandQueue;
import root.communication.MessageTranslator;
import root.communication.GameServerProxy;
import root.model.PlayerData;
import root.model.component.Field;
import root.model.component.Terrain;
import root.model.event.ModelEventArg;

public class RabbitGameServerProxy implements GameServerProxy {

	private Channel channel;

	private MessageTranslator translator;

	private CommandQueue commandQueue;

	// used for calculating routing key
	private String username;
	private String room_name;

	// constructors

	public RabbitGameServerProxy(Channel channel,
			MessageTranslator translator,
			String username,
			String room_name) {

		this.channel = channel;
		this.translator = translator;

		this.room_name = room_name;
		this.room_name = room_name;

		this.commandQueue = new CommandQueue();

		this.initCommunicationChannel();
	}

	// methods

	// implement create queues and stuff
	private void initCommunicationChannel() {
		// configure channels and start listening for server messages

		// first message must be model initialization message

		// attention do not start receiving messages until queue is set !!!

		// fake first initialization message
		List<PlayerData> players = new ArrayList<PlayerData>();
		players.add(new PlayerModelData("user 1", Color.RED));
		players.add(new PlayerModelData("user 2", Color.GREEN));
		players.add(new PlayerModelData("user 3", Color.BLACK));

		List<Field> fieldModels = new ArrayList<Field>();

		int left = 3;
		int right = 17;

		int playerCounter = 0;

		for (int i = 1; i < 16; i++) {

			for (int j = left; j < right; j++) {
				if (i % 2 == 0 && j % 5 == 0) {
					fieldModels.add(new ModelField(
							new Point2D(j, i),
							players.get(playerCounter),
							true,
							null,
							new Terrain("mountains", 1)));
				} else {
					fieldModels.add(new ModelField(
							new Point2D(j, i),
							players.get(playerCounter),
							true,
							null,
							new Terrain("water", 1)));
				}

				playerCounter++;
				playerCounter %= 3;

			}

			if (left > -3)
				left--;
		}

		this.commandQueue.enqueue(new CtrlInitializeCommand(players, fieldModels));

	}

	@Override
	public Channel getCommunicationChannel() {
		return this.channel;
	}

	@Override
	public void setCommunicationChannel(Channel new_channel) {
		this.channel = new_channel;

	}

	@Override
	public MessageTranslator getMessageTranslator() {
		return this.translator;
	}

	@Override
	public void setMessageTranslator(MessageTranslator new_translator) {
		this.translator = new_translator;
	}

	@Override
	public void setConsumerQueue(CommandQueue consumer_queue) {
		this.commandQueue = consumer_queue;
	}

	@Override
	public CommandQueue getConsumerQueue() {
		return this.commandQueue;
	}

	@Override
	public void sendIntention(ModelEventArg action) {

		// debug
		System.out.println("Sending intention: "
				+ action.getEventName()
				+ "@ BasicServeProxy.sendIntention");

		byte[] message = this.translator.translate(action);

		Command received_command = this.translator.translate(message);

		this.commandQueue.enqueue(received_command);

		// TODO somehow send it through channel

	}


}
