package root.model.component;

import java.util.List;

import root.model.action.move.MoveType;
import root.model.component.option.FieldOption;
import root.model.event.ModelEventHandler;

public interface Unit extends Cloneable {

	// attention may be unused
	String getUnitId();

	// unit type ... basic, advanced, archers...
	String getUnitName();

	Field getField();

	void setField(Field field);

	boolean canMove();

	// attention maybe list of move types
	MoveType getMoveType();

	void relocateTo(Field next_field);

	boolean haveAirAttack();

	boolean haveGroundAttack();

	Unit clone() throws CloneNotSupportedException;

	void setEventHandler(ModelEventHandler event_handler);

	List<FieldOption> adjustOptionsFor(Field targetField, Unit targetUnit);

	List<FieldOption> getAvailableOptions();

}
