package com.maria_sube.cedric.htwg;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

public class HighTechWorldGenerator {

	final static String DATA_DIRECTORY = "data";
	
  public static void main(String[] args) {
  	if (args.length > 0) {
  	  String planetResourceFile = args[0];
  	  if (planetResourceFile != null) {
  	    ResourceBundle planetResources = ResourceBundle.getBundle(DATA_DIRECTORY + "." + planetResourceFile);
  	    
  	    String planetName = planetResources.getString("name");
  	    int imageWidth = new Integer(planetResources.getString("width"));
  	    int imageHeight = new Integer(planetResources.getString("height"));
  	    
  	    BufferedImage bi = new BufferedImage(imageWidth, imageHeight, BufferedImage.TYPE_INT_RGB);
  	    
  	    File planetImageFile = new File("./resources/img/" + planetName + ".jpg");
  	    if (!planetImageFile.exists()) {
  	    	try {
						planetImageFile.createNewFile();
					} catch (IOException e) {
						e.printStackTrace();
					}
  	    } // if
  	    
  	    FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(planetImageFile);

					Graphics2D g2d = (Graphics2D) bi.getGraphics();

					g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
					g2d.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
					g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
					g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
					
					// Set up backround
	  	    renderBackground(planetResources, imageWidth, imageHeight, g2d);
	  	    
	  	    // Foreground color
	  			int foregroundRed = new Integer(planetResources.getString("foreground.color.red"));
	  			int foregroundGreen = new Integer(planetResources.getString("foreground.color.green"));
	  			int foregroundBlue = new Integer(planetResources.getString("foreground.color.blue"));
	  			Color foregroundColor = new Color(foregroundRed, foregroundGreen, foregroundBlue);
	  	    
	  	    // Set up equators
	  			renderEquators(planetResources, imageWidth, imageHeight, g2d, foregroundColor);
	  			
	  			// Set up megacities
	  			renderMegacities(planetResources, imageWidth, imageHeight, g2d, foregroundColor);

	  			// Save planet map
		  		try {
						ImageIO.write(bi, "jpg", fos);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} // try
				catch (FileNotFoundException fnfe) {
					fnfe.printStackTrace();
				} // FileNotFoundException
				finally {
					if (fos !=  null) {
		  		  try {
							fos.close();
						} // try
		  		  catch (IOException ioe) {
							ioe.printStackTrace();
						} // IOException
					} // if
				} // finally
  	    
				System.out.println("Your planet map was generated here : " + planetImageFile.getAbsolutePath());
  	  } // if
  	} // if
  }

	private static void renderMegacities(ResourceBundle planetResources, int imageWidth, int imageHeight, Graphics2D g2d, Color foregroundColor) {
		int numberOfMegacities = new Integer(planetResources.getString("number.of.megacities"));
		if (numberOfMegacities > 0) {
			int megacityCounter = 0;
			while (megacityCounter < numberOfMegacities) {
				float sizeFactor = imageHeight / 256;
				int megacityDiameter = generateMegaCityDiameter(imageHeight, sizeFactor);
				
				int megacityX = generateMegaCityPosition(imageWidth, megacityDiameter);
				int megacityY = generateMegaCityPosition(imageHeight, megacityDiameter);
				
				// Draw megacity
				// Dark
				renderForegroundCircle(g2d, foregroundColor, sizeFactor, megacityDiameter, megacityX, megacityY);

				// Set up suburbs
				renderMegacitySuburb(planetResources, g2d, foregroundColor, sizeFactor, megacityDiameter, megacityX, megacityY);
				
				megacityCounter++;
			} // while
		} // if
	}

	private static void renderMegacitySuburb(ResourceBundle planetResources, Graphics2D g2d, Color foregroundColor, float sizeFactor, int megacityDiameter, int megacityX, int megacityY) {
		int maxNumberOfMegacitiesSuburbs = new Integer(planetResources.getString("max.number.of.megacities.suburbs"));
		int numberOfMegacitiesSuburbs = (int) (Math.random() * maxNumberOfMegacitiesSuburbs);
		if (numberOfMegacitiesSuburbs > 0) {
			int suburbCounter = 0;
			while (suburbCounter < numberOfMegacitiesSuburbs) {
				int megacityRadius = megacityDiameter / 2;
				int suburbX = generateSuburbPosition(megacityX - megacityRadius, megacityX + megacityRadius);
				int suburbY = generateSuburbPosition(megacityY - megacityRadius, megacityY + megacityRadius);
				int suburbDiameter = generateMegaCityDiameter(megacityDiameter, sizeFactor);
				
				renderForegroundCircle(g2d, foregroundColor, sizeFactor, suburbDiameter, suburbX, suburbY);
				
				suburbCounter++;
			} // while
		} // if
	}

	private static void renderForegroundCircle(Graphics2D g2d, Color foregroundColor, float sizeFactor, int diameter, int positionX, int positionY) {
		BasicStroke darkStroke = new BasicStroke(sizeFactor * 3);
		g2d.setStroke(darkStroke);
		g2d.setPaint(foregroundColor.darker().darker());
		g2d.draw(new Ellipse2D.Double(positionX, positionY,diameter, diameter));
		// Medium
		BasicStroke mediumStroke = new BasicStroke(sizeFactor * 2);
		g2d.setStroke(mediumStroke);
		g2d.setPaint(foregroundColor);
		g2d.draw(new Ellipse2D.Double(positionX, positionY,diameter, diameter));
		// Light
		BasicStroke lightStroke = new BasicStroke(sizeFactor * 1);
		g2d.setStroke(lightStroke);
		g2d.setPaint(foregroundColor.brighter().brighter());
		g2d.draw(new Ellipse2D.Double(positionX, positionY,diameter, diameter));
	}

	private static int generateMegaCityPosition(int maxDimension, int megacityDiameter) {
		int megacityPosition = (int) (Math.random() * maxDimension);
		
		if (megacityPosition > maxDimension - megacityDiameter) {
			megacityPosition = generateMegaCityPosition(maxDimension, megacityDiameter);
		} // if
		
		return megacityPosition;
	}

	private static int generateSuburbPosition(int minPosition, int maxPosition) {
		int position = (int) (Math.random() * maxPosition);
		
		if (position > maxPosition) {
			position = generateSuburbPosition(minPosition, maxPosition);
		}
		else if (position < minPosition) {
			position = generateSuburbPosition(minPosition, maxPosition);			
		} // if
		
		return position;
	}

	private static int generateMegaCityDiameter(int imageHeight, float sizeFactor) {
		int megacityDiameter = (int) (((Math.random() * imageHeight) / (sizeFactor * 1.5)));
		
		if (megacityDiameter < (imageHeight / 8)) {
			megacityDiameter = generateMegaCityDiameter(imageHeight, sizeFactor);
		} // if 
		
		return megacityDiameter;
	}

	private static void renderEquators(ResourceBundle planetResources, int imageWidth, int imageHeight, Graphics2D g2d, Color foregroundColor) {
		int numberOfEquators = new Integer(planetResources.getString("number.of.equators"));
		if (numberOfEquators > 0) {
			int equatorCounter = 0;
			while (equatorCounter < numberOfEquators) {
				int equatorLevel = (int) (((Math.random() * imageHeight) / numberOfEquators) + (equatorCounter * imageHeight) / numberOfEquators);
				
				float sizeFactor = imageHeight / 256;
				
				// Draw equator
				// Dark
				BasicStroke darkStroke = new BasicStroke(sizeFactor * 3);
				g2d.setStroke(darkStroke);
				g2d.setPaint(foregroundColor.darker().darker());
				g2d.draw(new Line2D.Double(0, equatorLevel, imageWidth, equatorLevel));
				// Medium
				BasicStroke mediumStroke = new BasicStroke(sizeFactor * 2);
				g2d.setStroke(mediumStroke);
				g2d.setPaint(foregroundColor);
				g2d.draw(new Line2D.Double(0, equatorLevel, imageWidth, equatorLevel));
				// Light
				BasicStroke lightStroke = new BasicStroke(sizeFactor * 1);
				g2d.setStroke(lightStroke);
				g2d.setPaint(foregroundColor.brighter().brighter());
				g2d.draw(new Line2D.Double(0, equatorLevel, imageWidth, equatorLevel));

				equatorCounter++;
			} // while
		} // if
	}

	private static void renderBackground(ResourceBundle planetResources, int imageWidth, int imageHeight, Graphics2D g2d) {
		int backgroundRed = new Integer(planetResources.getString("background.color.red"));
		int backgroundGreen = new Integer(planetResources.getString("background.color.green"));
		int backgroundBlue = new Integer(planetResources.getString("background.color.blue"));

		//Fill background with base color
		Color backgroundFullColor = new Color(backgroundRed, backgroundGreen, backgroundBlue);
		g2d.setPaint(backgroundFullColor);
		g2d.fill (new Rectangle2D.Double(0, 0, imageWidth, imageHeight));
		
		// Generate metal plate textures
		renderMetalPlates(planetResources, imageWidth, imageHeight, g2d, backgroundFullColor);
		
		String backgroundAlphaString = null;
		try {
			backgroundAlphaString = planetResources.getString("background.color.alpha");
		} // try
		catch (MissingResourceException mre) {
		  // No alpha defined
	  } // MissingResourceException
		
		Color backgroundColor = null;
		if (backgroundAlphaString != null) {		
			int backgroundAlpha = new Integer(backgroundAlphaString);
			backgroundColor = new Color(backgroundRed, backgroundGreen, backgroundBlue, backgroundAlpha);
		}
		else {
			backgroundColor = new Color(backgroundRed, backgroundGreen, backgroundBlue);		
		} // if
		
		GradientPaint lightToDarkBackground = new GradientPaint(0, 0 , backgroundColor.brighter().brighter().brighter(), 0, imageHeight, backgroundColor.darker().darker().darker().darker().darker().darker().darker());
		g2d.setPaint(lightToDarkBackground);
		g2d.fill (new Rectangle2D.Double(0, 0, imageWidth, imageHeight));
	}

	private static void renderMetalPlates(ResourceBundle planetResources, int imageWidth, int imageHeight, Graphics2D g2d, Color backgroundFullColor) {
		int maxNumberOfPlates = new Integer(planetResources.getString("background.texture.density"));
		int plateCounter = 0;
		while (plateCounter < maxNumberOfPlates) {
			int plateWidth = (int) (Math.random() * imageWidth / 10);
			int plateHeight = (int) (Math.random() * imageHeight / 15);
			int plateX = (int) (Math.random() * imageWidth);
			int plateY = (int) (Math.random() * imageHeight);
			
			g2d.setPaint(backgroundFullColor.darker().darker());
			g2d.fill (new Rectangle2D.Double(plateX + 2, plateY + 2, plateWidth, plateHeight));
			g2d.setPaint(backgroundFullColor.brighter().brighter());
			g2d.fill (new Rectangle2D.Double(plateX, plateY, plateWidth, plateHeight));
			plateCounter++;
		} // while
	}
}