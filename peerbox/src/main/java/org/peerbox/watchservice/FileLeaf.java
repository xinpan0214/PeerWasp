package org.peerbox.watchservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.hive2hive.core.security.EncryptionUtil;
import org.hive2hive.core.security.HashUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileLeaf extends AbstractFileComponent implements FileComponent{
	private Action action;
	private Path path;
	private Path fileName;
	private String contentHash;
	private FolderComposite parent;
	private boolean isUploaded;
	
	private static final Logger logger = LoggerFactory.getLogger(FileLeaf.class);
	
	public FileLeaf(Path path){
		this.path = path;
		this.fileName = path.getFileName();
		this.action = new Action(path);
		this.contentHash = "";
		this.isUploaded = false;
		updateContentHash();
	}
	
	public FileLeaf(Path path, boolean maintainContentHashes){
		this.path = path;
		this.fileName = path.getFileName();
		this.action = new Action(path);
		this.contentHash = "";
		this.isUploaded = false;
		if(maintainContentHashes){
			updateContentHash();
		}

	}

	@Override
	public Action getAction() {
		return this.action;
	}

	@Override
	public void putComponent(String path, FileComponent component) {
		System.err.println("put on file not defined.");
	}

	@Override
	public FileComponent deleteComponent(String path) {
		System.err.println("delete on file not defined");
		return null;
	}

	@Override
	public FileComponent getComponent(String path) {
		System.err.println("get on file not defined");
		return null;
	}
	
	@Override
	public String getContentHash() {
		return contentHash;
	}

	@Override
	public void bubbleContentHashUpdate() {
		boolean hasChanged = updateContentHash();
		if(hasChanged){
			parent.bubbleContentHashUpdate();
		}
	}

	@Override
	public void setParent(FolderComposite parent) {
		this.parent = parent;
	}

	@Override
	public Path getPath() {
		return this.path;
	}
	
	/**
	 * Computes and updates this FileLeafs contentHash property.
	 * @return true if the contentHash hash changed, false otherwise
	 */
	@Override
	public boolean updateContentHash() {
		String newHash = "";
		if(path != null && path.toFile() != null){
			try {
				byte[] rawHash = HashUtil.hash(path.toFile());
				if(rawHash != null){
					newHash = Action.createStringFromByteArray(rawHash);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		logger.debug("File {} has contentHash: {}", path, newHash);
		if(!contentHash.equals(newHash)){
			contentHash = newHash;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public FolderComposite getParent() {
		return this.parent;
	}

	@Override
	public boolean getIsUploaded() {
		return this.isUploaded;
	}

	@Override
	public void setIsUploaded(boolean isUploaded) {
		this.isUploaded = isUploaded;
	}
	
	@Override
	public void setPath(Path parentPath){	
		if(parentPath != null){
			this.path = Paths.get(new File(parentPath.toString(), fileName.toString()).getPath());
			logger.debug("Set path to {}", path);
			action.setPath(this.path);
		}
		
	}

	@Override
	public boolean isFile() {
		return true;
	}
}
