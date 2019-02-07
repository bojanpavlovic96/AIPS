package view.component;

import javafx.geometry.Point2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import model.component.Terrain;
import view.ResourceManager;

public class ViewTerrain {

	private String terrain_name;
	private int intensity;

	public ViewTerrain(Terrain model) {

		this.terrain_name = model.getTerrainName();
		this.intensity = model.getIntensity();

	}

	public void drawTerrain(GraphicsContext gc, Point2D hex_center, double hex_side, double border_width) {

		Image image = ResourceManager.getInstance().getTerrain(this.terrain_name, intensity);

		if (image == null) {
			System.out.println("Terrain image is null... " + this.terrain_name
								+ "\t @ ViewTerrain.drawTerrain");
		}

		double hex_width = (double) (Math.sqrt(3) * hex_side);
		double hex_height = 2 * hex_side;

		gc.save();

		gc.drawImage(	image,
						hex_center.getX() - hex_width / 2 + border_width,
						hex_center.getY() - hex_height / 2 + border_width,
						hex_width - 2 * border_width,
						hex_height - 2 * border_width);

		gc.restore();

	}

}