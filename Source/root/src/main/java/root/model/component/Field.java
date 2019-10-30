package root.model.component;

import java.util.List;

import javafx.geometry.Point2D;
import root.model.PlayerData;
import root.model.component.option.FieldOption;
import root.model.event.ModelEventHandler;

public interface Field {

	boolean isVisible();

	Unit getUnit();

	Terrain getTerrain();

	PlayerData getPlayer();

	void setUnit(Unit new_unit);

	Point2D getStoragePosition();

	void setModelEventHandler(ModelEventHandler event_handler);

	// TODO add something about battle

	boolean isInBattle();

	void addFieldOptions(List<FieldOption> options);

	void addFieldOption(FieldOption option);

	List<FieldOption> adjustOptionsFor(List<FieldOption> options, Field second_field);

	List<FieldOption> getEnabledOptions();

}
