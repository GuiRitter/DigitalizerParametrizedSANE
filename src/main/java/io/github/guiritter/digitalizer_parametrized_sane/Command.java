package io.github.guiritter.digitalizer_parametrized_sane;

public final class Command {

	public final String PROGRAM = "scanimage";

	public final String DEVICE = "--device-name=hpaio:/usb/Photosmart_D110_series?serial=BR0C1FK12Y05N9";

	public final String FORMAT = "--format=png";

	public final String OUTPUT_KEY = "--output-file=";
	public final String OUTPUT_VALUE;

	public final String MODE_KEY = "--mode";
	public final String MODE_VALUE;

	public final String RESOLUTION_KEY = "--resolution";
	public final String RESOLUTION_VALUE;
	public final String RESOLUTION_UNIT = "dpi";

	public final String LEFT_KEY = "-l";
	public final String LEFT_VALUE;
	public final String LEFT_UNIT = "mm";

	public final String WIDTH_KEY = "-x";
	public final String WIDTH_VALUE;
	public final String WIDTH_UNIT = "mm";

	public final String TOP_KEY = "-t";
	public final String TOP_VALUE;
	public final String TOP_UNIT = "mm";

	public final String HEIGHT_KEY = "-y";
	public final String HEIGHT_VALUE;
	public final String HEIGHT_UNIT = "mm";

	public Command(String output, String mode, String resolution,
			String left, String width, String top, String height) {
		this.OUTPUT_VALUE = output;
		this.MODE_VALUE = mode;
		this.RESOLUTION_VALUE = resolution;
		this.LEFT_VALUE = left;
		this.WIDTH_VALUE = width;
		this.TOP_VALUE = top;
		this.HEIGHT_VALUE = height;
	}

	@Override
	public String toString() {
		return this.PROGRAM + " " +
				this.DEVICE + " " +
				this.FORMAT + " " +
				this.OUTPUT_KEY + this.OUTPUT_VALUE + " " +
				this.MODE_KEY + " " + this.MODE_VALUE + " " +
				this.RESOLUTION_KEY + " " + this.RESOLUTION_VALUE + this.RESOLUTION_UNIT + " " +
				this.LEFT_KEY + " " + this.LEFT_VALUE + this.LEFT_UNIT + " " +
				this.WIDTH_KEY + " " + this.WIDTH_VALUE + this.WIDTH_UNIT + " " +
				this.TOP_KEY + " " + this.TOP_VALUE + this.TOP_UNIT + " " +
				this.HEIGHT_KEY + " " + this.HEIGHT_VALUE + this.HEIGHT_UNIT;
	}
}
