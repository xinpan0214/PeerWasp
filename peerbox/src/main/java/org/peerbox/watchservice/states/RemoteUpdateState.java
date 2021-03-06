package org.peerbox.watchservice.states;

import java.nio.file.Path;

import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.exceptions.ProcessExecutionException;
import org.peerbox.app.manager.file.FileInfo;
import org.peerbox.app.manager.file.IFileManager;
import org.peerbox.watchservice.IAction;
import org.peerbox.watchservice.conflicthandling.ConflictHandler;
import org.peerbox.watchservice.filetree.IFileTree;
import org.peerbox.watchservice.filetree.composite.FileComponent;
import org.peerbox.watchservice.states.listeners.RemoteFileUpdateListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Files in the RemoteUpdateState have been remotely updated. The H2H network
 * already delivered a notification to announce the file's update. If a file
 * is in the RemoteUpdateState, it is existing on the disk, but it might be
 * already downloading the newest version.
 * @author Claudio
 *
 */
public class RemoteUpdateState extends AbstractActionState {

	private boolean localUpdateHappened = false;
	private final static Logger logger = LoggerFactory.getLogger(RemoteUpdateState.class);

	public RemoteUpdateState(IAction action) {
		super(action, StateType.REMOTE_UPDATE);
	}

	@Override
	public ExecutionHandle execute(IFileManager fileManager) throws NoSessionException, NoPeerConnectionException, InvalidProcessStateException, ProcessExecutionException {
		final Path path = action.getFile().getPath();
		logger.debug("Execute REMOTE UPDATE, download the file: {}", path);

		handle = fileManager.download(path);
		if (handle != null && handle.getProcess() != null) {
			FileInfo file = new FileInfo(action.getFile());
			handle.getProcess().attachListener(new RemoteFileUpdateListener(file, action.getFileEventManager().getMessageBus()));
			handle.executeAsync();
		} else {
			logger.warn("process or handle is null");
		}

		return new ExecutionHandle(action, handle);
	}

	@Override
	public AbstractActionState changeStateOnLocalCreate() {
		logStateTransition(getStateType(), EventType.LOCAL_CREATE, StateType.REMOTE_UPDATE);
		return this; //new LocalUpdateState(action);
	}

	@Override
	public AbstractActionState changeStateOnLocalDelete() {
		logStateTransition(getStateType(), EventType.LOCAL_DELETE, StateType.REMOTE_UPDATE);
		return this;
	}

	@Override
	public AbstractActionState changeStateOnLocalMove(Path newPath) {
		logStateTransition(getStateType(), EventType.LOCAL_MOVE, StateType.LOCAL_MOVE);
		return new LocalMoveState(action, newPath);
	}
	
	@Override
	public AbstractActionState changeStateOnRemoteCreate() {
		logStateTransition(getStateType(), EventType.REMOTE_CREATE, StateType.REMOTE_UPDATE);
		return this;
	}

	@Override
	public AbstractActionState handleLocalCreate() {
		ConflictHandler.resolveConflict(action.getFile().getPath());
		action.updateTimeAndQueue();
		return changeStateOnLocalCreate();
	}

	@Override
	public AbstractActionState handleLocalUpdate() {
		action.getFile().updateContentHash();
		ConflictHandler.resolveConflict(action.getFile().getPath());
		action.updateTimeAndQueue();
		return changeStateOnLocalUpdate();
	}

	public AbstractActionState changeStateOnLocalUpdate(){
		logStateTransition(getStateType(), EventType.LOCAL_UPDATE, StateType.REMOTE_UPDATE);
		return this;
	}

	@Override
	public AbstractActionState handleRemoteMove(Path path) {
		logger.info("The file which was remotely moved after it has been "
				+ "remotely updated. RemoteUpdate at destination"
				+ "of move operation initiated to download the file: {}", path);
		action.updateTimeAndQueue();
		IFileTree fileTree = action.getFileEventManager().getFileTree();
		fileTree.deleteFile(action.getFile().getPath());
		action.getFileEventManager().getFileComponentQueue().remove(action.getFile());
		FileComponent moveDest = fileTree.getOrCreateFileComponent(path, action.getFileEventManager());
		fileTree.putFile(path, moveDest);
		moveDest.getAction().handleRemoteUpdateEvent();

		return changeStateOnRemoteMove(path);
	}

	@Override
	public AbstractActionState changeStateOnRemoteMove(Path oldFilePath) {
		logStateTransition(getStateType(), EventType.REMOTE_MOVE, StateType.INITIAL);
		return new InitialState(action);
	}

	public void setLocalUpdateHappened(boolean b){
		localUpdateHappened = b;
	}

	public boolean getLocalUpdateHappened(){
		return localUpdateHappened;
	}
}
