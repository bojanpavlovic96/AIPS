package controller.option;

import root.controller.Controller;
import root.model.component.Field;
import root.model.component.option.FieldOption;

public class StopMovingFieldOption extends FieldOption {

	public static final String Name = "stop-moving-field-option";

	public StopMovingFieldOption(Controller gameController) {
		super(StopMovingFieldOption.Name, gameController);
	}

	@Override
	public void run() {
		
	}

	@Override
	public FieldOption getCopy() {
		return new StopMovingFieldOption(this.controller);
	}

	@Override
	public boolean isAdequateFor(Field field) {
		return (field.getUnit() != null &&
				field.getUnit().getMoveType() != null &&
				field.getUnit().getMoveType().isMoving());
	}

}