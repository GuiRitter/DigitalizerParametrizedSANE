package io.github.guiritter.digitalizer_parametrized_sane;

import static io.github.guiritter.graphical_user_interface.LabelledComponentFactory.buildFileChooser;
import static io.github.guiritter.graphical_user_interface.LabelledComponentFactory.buildLabelledComponent;
import static java.awt.GridBagConstraints.NORTH;
import static java.awt.GridBagConstraints.SOUTH;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.lang.System.out;
import static java.util.Locale.ROOT;
import static java.util.stream.Collectors.toList;
import static javax.swing.BoxLayout.LINE_AXIS;
import static javax.swing.JOptionPane.ERROR_MESSAGE;
import static javax.swing.JOptionPane.INFORMATION_MESSAGE;
import static javax.swing.JOptionPane.WARNING_MESSAGE;
import static javax.swing.JOptionPane.showMessageDialog;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import javax.imageio.ImageIO;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import io.github.guiritter.external_execution_output_treatment.ExternalExecutionOutputTreatment;
import io.github.guiritter.fit_linear.FitLinear;
import io.github.guiritter.graphical_user_interface.FileChooserResponse;
import io.github.guiritter.image_component.ImageComponentMultiple;

public class DigitalizerParametrizedSANE {

	private static final String CROP_LEFT = "Left";
	private static final String CROP_RIGHT = "Right";
	private static final String CROP_TOP = "Top";
	private static final String CROP_BOTTOM = "Bottom";

	private static final int BLACK[] = new int[]{0, 0, 0, 255};
	
	private static final Map<String, Integer> CROP_PX = new HashMap<>();

	private static final int HALF_PADDING = 5;
	private static final int FULL_PADDING = 2 * HALF_PADDING;

	public static final String SANE_X_MAX_STRING = "215.9";
	public static final String SANE_Y_MAX_STRING = "296.926";

	public static final BigDecimal SANE_X_MAX = new BigDecimal(SANE_X_MAX_STRING);
	public static final BigDecimal SANE_Y_MAX = new BigDecimal(SANE_Y_MAX_STRING);

	private static final ExternalExecutionOutputTreatment TERMINAL = new ExternalExecutionOutputTreatment() {
		@Override
		public void treatErrorLine(String line) {
			out.println(line);
		}

		@Override
		public void treatInputLine(String line) {
			out.println(line);
		}
	};

	private static final int TRANSPARENT[] = new int[]{0, 0, 0, 0};

	public static final int WIA_DISPLAY_X_MAX = 492;
	public static final int WIA_DISPLAY_X_MIN = 279;
	public static final int WIA_DISPLAY_Y_MAX = 307;
	public static final int WIA_DISPLAY_Y_MIN = 18;

	public static final int WIA_PAPER_X_MAX = 486;
	public static final int WIA_PAPER_X_MIN = 285;
	public static final int WIA_PAPER_Y_MAX = 301;
	public static final int WIA_PAPER_Y_MIN = 24;

	public static final FitLinear FIT_LINEAR_X = new FitLinear(WIA_PAPER_X_MIN, 0, WIA_PAPER_X_MAX, SANE_X_MAX.doubleValue());
	public static final FitLinear FIT_LINEAR_Y = new FitLinear(WIA_PAPER_Y_MIN, 0, WIA_PAPER_Y_MAX, SANE_Y_MAX.doubleValue());

	private static String cropGuide;

	private static int cropValue;

	private static File outputFile;

	private static File outputFolder;

	private static String outputName;

	private static int x, y;

	private static int width, height;

	static {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);

		clearCropValue();
	}

	private static final GridBagConstraints buildGBC(int x, int y, int topPadding, int leftPadding, int bottomPadding, int rightPadding, int ...anchor) {
		GridBagConstraints gbc = new GridBagConstraints();
		if ((anchor != null) && anchor.length > 0) {
			gbc.anchor = Arrays.stream(anchor).findAny().getAsInt();
		} else {
			gbc.anchor = NORTH;
		}
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(topPadding, leftPadding, bottomPadding, rightPadding);
		return gbc;
	}

	private static final GridBagConstraints buildGBCRight(int y, int topPadding, int bottomPadding, int ...anchor) {
		return buildGBC(2, y, topPadding, HALF_PADDING, bottomPadding, FULL_PADDING, anchor);
	}

	private static final GridBagConstraints buildGBC() {
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 1;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(FULL_PADDING, FULL_PADDING, HALF_PADDING, HALF_PADDING);
		gbc.gridheight = 7;
		return gbc;
	}

	private static final void clearCropValue() {
		CROP_PX.put(CROP_LEFT, WIA_PAPER_X_MIN);
		CROP_PX.put(CROP_RIGHT, WIA_PAPER_X_MAX);
		CROP_PX.put(CROP_TOP, WIA_PAPER_Y_MIN);
		CROP_PX.put(CROP_BOTTOM, WIA_PAPER_Y_MAX);
	}

	private static final void clearCropValue(WritableRaster foreGroundRaster, ImageComponentMultiple imageComponent, JButton button) {
		clearCropValue();
		updateForeGround(foreGroundRaster, imageComponent);
		setCropButtonText(button);
	}

	public static String doubleToString(String decimalPlaces, double value) {
		return String.format(ROOT, "%." + decimalPlaces + "f", value);
	}

	private static final String getCropBottom() {
		return yToString(FIT_LINEAR_Y.f(CROP_PX.get(CROP_BOTTOM)));
	}

	private static final String getCropLeft() {
		return xToString(FIT_LINEAR_X.f(CROP_PX.get(CROP_LEFT)));
	}

	private static final String getCropRight() {
		return xToString(FIT_LINEAR_X.f(CROP_PX.get(CROP_RIGHT)));
	}

	private static final String getCropTop() {
		return yToString(FIT_LINEAR_Y.f(CROP_PX.get(CROP_TOP)));
	}

	private static String getFormattedFileName(JTextField outputFileNameFormatField, JSpinner outputFileNameIndexSpinner) {
		return String.format(replaceTimeStamp(outputFileNameFormatField.getText()), outputFileNameIndexSpinner.getValue());
	}

	private static final String getMode(JRadioButton modeColorRadioButton, JRadioButton modeGrayRadioButton) {
		if (modeColorRadioButton.isSelected()) {
			return "Color";
		}
		if (modeGrayRadioButton.isSelected()) {
			return "Gray";
		}
		throw new UnsupportedOperationException("A mode must be selected.");
	}

	private static String replaceTimeStamp(String format) {
		return format.replaceAll("%z", OffsetDateTime.now().toString()).replaceAll(":", "：");
	}

	private static void runSANE(File outputFile, String mode, String resolution) throws IOException, InterruptedException {
		var leftRight = Arrays.stream(new BigDecimal[]{
			new BigDecimal(getCropLeft()),
			new BigDecimal(getCropRight())
		}).sorted().collect(toList());
		var topBottom = Arrays.stream(new BigDecimal[]{
			new BigDecimal(getCropTop()),
			new BigDecimal(getCropBottom())
		}).sorted().collect(toList());
		var left = leftRight.get(0);
		var right = leftRight.get(1);
		var top = topBottom.get(0);
		var bottom = topBottom.get(1);
		var command = new Command(outputFile.getAbsolutePath(),
			mode,
			resolution,
			left.toString(),
			right.subtract(left).toString(),
			top.toString(),
			bottom.subtract(top).toString());
		out.println(command.toString());
		TERMINAL.execute(
			command.PROGRAM,
			command.DEVICE,
			command.FORMAT,
			command.OUTPUT_KEY + command.OUTPUT_VALUE,
			command.MODE_KEY, command.MODE_VALUE,
			command.RESOLUTION_KEY, command.RESOLUTION_VALUE + command.RESOLUTION_UNIT,
			command.LEFT_KEY, command.LEFT_VALUE + command.LEFT_UNIT,
			command.WIDTH_KEY, command.WIDTH_VALUE + command.WIDTH_UNIT,
			command.TOP_KEY, command.TOP_VALUE + command.TOP_UNIT,
			command.HEIGHT_KEY, command.HEIGHT_VALUE + command.HEIGHT_UNIT
		);
	}

	private static void scanImage(AtomicReference<File> file, JFrame frame, JTextField outputFileNameFormatField, JSpinner outputFileNameIndexSpinner, String mode, String resolution) {
		try {
			outputFolder = file.get();
			if (outputFolder == null) {
				showMessageDialog(frame, "Choose a folder to write the scanned images to.", "Reminder", WARNING_MESSAGE);
				return;
			}
			try {
				outputName = getFormattedFileName(outputFileNameFormatField, outputFileNameIndexSpinner);
			} catch (IllegalFormatException ex) {
				showMessageDialog(frame, "The format used for the output image name is invalid.", "Warning", WARNING_MESSAGE);
				return;
			}
			outputFile = file.get().toPath().resolve(outputName).toFile();
			try {
				if (outputFile.createNewFile()) {
					outputFile.delete();
				} else {
					showMessageDialog(frame, "File " + outputFile.getAbsolutePath() + " already exists.", "Reminder", WARNING_MESSAGE);
					return;
				}
			} catch (Exception ex) {
				showMessageDialog(frame, "Can't write to " + outputFile.getAbsolutePath(), "Warning", WARNING_MESSAGE);
				return;
			}
			runSANE(outputFile, mode, resolution);
		} catch (Exception ex) {
			ex.printStackTrace();
			showMessageDialog(frame, ex.getMessage(), "Error", ERROR_MESSAGE);
		}
	}

	private static void setCropButtonText(JButton button) {
		button.setText(String.format(
			"left: %s; right: %s; top: %s, bottom: %s",
			getCropLeft(), getCropRight(), getCropTop(), getCropBottom()
		));
	}

	private static void testFormat(JFrame frame, JTextField outputFileNameFormatField, JSpinner outputFileNameIndexSpinner) {
		showMessageDialog(frame, getFormattedFileName(outputFileNameFormatField, outputFileNameIndexSpinner), "Info", INFORMATION_MESSAGE);
	}

	private static void treatSelectedFile(FileChooserResponse response, AtomicReference<File> file) {
		if (
			(response.state == JFileChooser.APPROVE_OPTION)
			&& (response.selectedFile != null)
		) {
			file.set(response.selectedFile);
		} else {
			file.set(null);
		}
	}

	private static void updateForeGround(WritableRaster foreGroundRaster, ImageComponentMultiple imageComponent) {
		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {
				foreGroundRaster.setPixel(x, y, TRANSPARENT);
			}
		}
		x = CROP_PX.get(CROP_LEFT);
		for (y = WIA_DISPLAY_Y_MIN; y <= WIA_DISPLAY_Y_MAX; y++) {
			foreGroundRaster.setPixel(x, y, BLACK);
		}
		x = CROP_PX.get(CROP_RIGHT);
		for (y = WIA_DISPLAY_Y_MIN; y <= WIA_DISPLAY_Y_MAX; y++) {
			foreGroundRaster.setPixel(x, y, BLACK);
		}
		y = CROP_PX.get(CROP_TOP);
		for (x = WIA_DISPLAY_X_MIN; x <= WIA_DISPLAY_X_MAX; x++) {
			foreGroundRaster.setPixel(x, y, BLACK);
		}
		y = CROP_PX.get(CROP_BOTTOM);
		for (x = WIA_DISPLAY_X_MIN; x <= WIA_DISPLAY_X_MAX; x++) {
			foreGroundRaster.setPixel(x, y, BLACK);
		}
		imageComponent.update();
	}

	public static String xToString(double value) {
		return doubleToString("1", value);
		// return doubleToString("0", value);
	}

	public static String yToString(double value) {
		return doubleToString("3", value);
		// return doubleToString("0", value);
	}

	public static void main(String args[]) throws IOException {
		var frame = new JFrame("Digitalizer Parametrized");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new GridBagLayout());

		var cropButton = new JButton();

		frame.getContentPane().add(buildLabelledComponent(
				"Crop settings (click to clear)",
				cropButton,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBC(1, 7, HALF_PADDING, FULL_PADDING, FULL_PADDING, HALF_PADDING));

		var backGroundImage = ImageIO.read(ClassLoader.getSystemResourceAsStream("backGround.png"));
		width = backGroundImage.getWidth();
		height = backGroundImage.getHeight();

		var foreGroundImage = new BufferedImage(width, height, TYPE_INT_ARGB);
		var foreGroundRaster = foreGroundImage.getRaster();

		var imageComponent = new ImageComponentMultiple();
		imageComponent.images.add(backGroundImage);
		imageComponent.images.add(foreGroundImage);
		imageComponent.update();

		updateForeGround(foreGroundRaster, imageComponent);

		cropButton.addActionListener((ActionEvent e) -> clearCropValue(foreGroundRaster, imageComponent, cropButton));

		frame.getContentPane().add(imageComponent, buildGBC());

		int y = 0;

		var file = new AtomicReference<File>();
		frame.getContentPane().add(buildFileChooser(
				"Select Output Path",
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0,
				JFileChooser.DIRECTORIES_ONLY,
				(FileChooserResponse response) -> treatSelectedFile(response, file)
		), buildGBCRight(y++, FULL_PADDING, HALF_PADDING));
		
		var outputFileNameFormatField = new JTextField("%z.png");
		outputFileNameFormatField.setToolTipText("Use %z for an ISO 8601 timestamp at file creation time.");

		frame.getContentPane().add(buildLabelledComponent(
				"Name Format",
				outputFileNameFormatField,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBCRight(y++, HALF_PADDING, HALF_PADDING));

		var outputFileNameIndexSpinner = new JSpinner(new SpinnerNumberModel(0, 0, Integer.MAX_VALUE, 1));

		frame.getContentPane().add(buildLabelledComponent(
				"Name Index",
				outputFileNameIndexSpinner,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBCRight(y++, HALF_PADDING, HALF_PADDING));

		var testButton = new JButton("Test Format");
		testButton.addActionListener((ActionEvent event) -> testFormat(frame, outputFileNameFormatField, outputFileNameIndexSpinner));

		frame.getContentPane().add(testButton, buildGBCRight(y++, HALF_PADDING, HALF_PADDING));

		var modeGroup = new ButtonGroup();

		var modePanel = new JPanel();
		modePanel.setLayout(new BoxLayout(modePanel, LINE_AXIS));

		var modeColorRadioButton = new JRadioButton("Color");
		modePanel.add(modeColorRadioButton);
		modeGroup.add(modeColorRadioButton);
		modeColorRadioButton.setSelected(true);
		
		var modeGrayRadioButton = new JRadioButton("Gray scale");
		modePanel.add(modeGrayRadioButton);
		modeGroup.add(modeGrayRadioButton);

		frame.getContentPane().add(buildLabelledComponent(
				"Mode",
				modePanel,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBCRight(y++, HALF_PADDING, HALF_PADDING));

		var resolutionComboBox = new JComboBox<String>(new String[]{"75", "100", "150", "200", "300", "600", "1200"});
		resolutionComboBox.setSelectedIndex(3);

		frame.getContentPane().add(buildLabelledComponent(
				"Resolution (dpi)",
				resolutionComboBox,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBCRight(y++, HALF_PADDING, HALF_PADDING));

		var cropComboBox = new JComboBox<String>(new String[]{
			CROP_LEFT,
			CROP_RIGHT,
			CROP_TOP,
			CROP_BOTTOM
		});

		frame.getContentPane().add(buildLabelledComponent(
				"Crop guides",
				cropComboBox,
				SwingConstants.CENTER,
				SwingConstants.LEFT,
				0
		), buildGBCRight(y++, FULL_PADDING, HALF_PADDING));

		imageComponent.addMouseListener(new MouseAdapter() {
			
			@Override
			public void mouseClicked(MouseEvent e) {
				cropGuide = (String) cropComboBox.getSelectedItem();
				switch(cropGuide) {
					case CROP_LEFT:
					case CROP_RIGHT:
						cropValue = min(max(e.getX(), 285), 486);
						break;
					case CROP_TOP:
					case CROP_BOTTOM:
						cropValue = min(max(e.getY(), 24), 301);
				}
				CROP_PX.put(cropGuide, cropValue);
				setCropButtonText(cropButton);
				updateForeGround(foreGroundRaster, imageComponent);
				super.mouseClicked(e);
			}
		});

		var scanButton = new JButton("Scan");
		scanButton.addActionListener((ActionEvent event) -> scanImage(file, frame, outputFileNameFormatField, outputFileNameIndexSpinner, getMode(modeColorRadioButton, modeGrayRadioButton), (String)resolutionComboBox.getSelectedItem()));

		frame.getContentPane().add(scanButton, buildGBCRight(y++, HALF_PADDING, FULL_PADDING, SOUTH));

		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);

		// sometimes it's needed, sometimes it's not...
		(new Thread(() -> {
			try {
				Thread.sleep(1000);
				frame.setVisible(true);
				frame.pack();
				frame.setLocationRelativeTo(null);
			}
			catch (Exception e){
				System.err.println(e);
			}
		})).start();
	}
}
