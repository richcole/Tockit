/*
 * Copyright DSTC Pty.Ltd. (http://www.dstc.com), Technische Universitaet Darmstadt
 * (http://www.tu-darmstadt.de) and the University of Queensland (http://www.uq.edu.au).
 * Please read licence.txt in the toplevel source directory for licensing information.
 *
 * $Id$
 */
package org.tockit.canvas.imagewriter;

/**
 * Stores all settings needed for creating graphic exports from a Canvas.
 *
 * This stores the graphic format, size and auto-update information for a graphic
 * export. The file name is not stored.
 */
public class DiagramExportSettings {
    /**
     * Stores the type of graphic format to be used.
     */
    private GraphicFormat format;

    /**
     * Stores the target width.
     */
    private int width;

    /**
     * Stores the target width.
     */
    private int height;

    /**
     * Stores if the settings should be updated automatically.
     */
    private boolean autoMode;

	/**
	 * Stores if the history comments should be saved to a text file.
	 */
	private boolean saveCommentsToFile;

	/**
	 * Stores if the history comments should be copied to a clipboard.
	 */
	private boolean saveCommentToClipboard;
		
    /**
     * Initialisation constructor.
     *
     * If autoMode is set, the other values will be overwritten each time a
     * diagram gets exported.
     */
    public DiagramExportSettings(GraphicFormat format, int width, int height, boolean autoMode) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.autoMode = autoMode;
		this.saveCommentsToFile= false;
		this.saveCommentToClipboard= false;
    }

    /**
     * Sets the graphic format to be used when exporting.
     */
    public void setGraphicFormat(GraphicFormat format) {
        this.format = format;
    }

    /**
     * Sets the size of the image to be created.
     */
    public void setImageSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * Sets the auto mode.
     *
     * If autoMode is set, the other values will be overwritten each time a
     * diagram gets exported.
     */
    public void setAutoMode(boolean mode) {
        this.autoMode = mode;
    }
    
    /**
     * Get the current image format
     */
    public GraphicFormat getGraphicFormat() {
        return this.format;
    }

    /**
     * Get the current image width
     */
    public int getImageWidth() {
        return this.width;
    }

    /**
     * Get the current image height
     */
    public int getImageHeight() {
        return this.height;
    }

    /**
     * Returns true if settings should be updated automatically.
     */
    public boolean usesAutoMode() {
        return this.autoMode;
    }
	
    /**
     * Debugging output.
     */
    public String toString() {
        String retVal = "DiagramExportSettings[ Size: (" + this.width + ", " + this.height + "), Format: " + this.format;
        if (this.autoMode) {
            retVal += " (AUTO)";
        }
        retVal += "]\n";
        return retVal;
    }
	/**
	 * Returns true if the history comments are to be saved to a file.
	 * @return boolean
	 */
	public boolean getSaveCommentsToFile() {
		return saveCommentsToFile;
	}

	/**
	 * Returns true if the history comments are to be saved to the clipboard.
	 * @return boolean
	 */
	public boolean getSaveCommentToClipboard() {
		return saveCommentToClipboard;
	}

	/**
	 * Sets whether to save the history comments to a file.
	 * @param saveCommentsToFile The saveCommentsToFile to set
	 */
	public void setSaveCommentsToFile(boolean saveCommentsToFile) {
		this.saveCommentsToFile = saveCommentsToFile;
	}

	/**
	 * Sets whether to save the history comments to the system clipboard.
	 * @param saveCommentToClipboard The saveCommentToClipboard to set
	 */
	public void setSaveCommentToClipboard(boolean saveCommentToClipboard) {
		this.saveCommentToClipboard = saveCommentToClipboard;
	}

}

