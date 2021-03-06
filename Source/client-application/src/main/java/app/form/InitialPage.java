package app.form;

import app.event.RoomFormActionHandler;
import app.event.UserFormActionHandler;

public interface InitialPage extends MessageDisplay {

	void setOnLoginHandler(UserFormActionHandler handler);

	void setOnRegisterHandler(UserFormActionHandler handler);

	void setOnLogoutHandler(UserFormActionHandler handler);

	void setOnCreateRoomHandler(RoomFormActionHandler handler);

	void setOnJoinRoomHandler(RoomFormActionHandler handler);

	void setOnStartGameHandler();

	public void showUserForm();

	public void hideUserForm();

	public void showRoomForm();

	public void hideRoomForm();

	public void setUsername(String username);

	public void setUserPassword(String password);

	public String getUsername();

	public String getPassword();

	void show(); // expose show from javafx stage

	void hide(); // expose hide from javafx stage

}
