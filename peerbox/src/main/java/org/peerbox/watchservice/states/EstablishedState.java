package org.peerbox.watchservice.states;

import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;

import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.peerbox.FileManager;
import org.peerbox.exceptions.NotImplException;
import org.peerbox.watchservice.Action;
import org.peerbox.watchservice.FileComponent;
import org.peerbox.watchservice.FileEventManager;
import org.peerbox.watchservice.IFileEventManager;
import org.peerbox.watchservice.PathUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.status.OnConsoleStatusListener;

import com.google.common.collect.SetMultimap;
import com.google.common.io.Files;

public class EstablishedState extends AbstractActionState{

	private static final Logger logger = LoggerFactory.getLogger(EstablishedState.class);
	
	public EstablishedState(Action action) {
		super(action, StateType.ESTABLISHED);
		// TODO Auto-generated constructor stub
	}

	@Override
	public AbstractActionState changeStateOnLocalCreate() {
		logStateTransission(getStateType(), EventType.LOCAL_CREATE, StateType.LOCAL_CREATE);
		logger.debug("File {} - LocalCreateEvent captured, update content hash for file", action.getFilePath());
		action.getFile().updateContentHash();
		return this;
	}

	@Override
	public AbstractActionState changeStateOnLocalDelete() {
		logStateTransission(getStateType(), EventType.LOCAL_CREATE, StateType.LOCAL_DELETE);
		return new LocalDeleteState(action);
	}

	@Override
	public AbstractActionState changeStateOnLocalUpdate() {
		// TODO Auto-generated method stub
		logStateTransission(getStateType(), EventType.LOCAL_CREATE, StateType.LOCAL_UPDATE);
		return new LocalUpdateState(action);
	}

	@Override
	public AbstractActionState changeStateOnLocalMove(Path oldFilePath) {
		// TODO Auto-generated method stub
		logStateTransission(getStateType(), EventType.LOCAL_CREATE, StateType.LOCAL_MOVE);
		return new LocalMoveState(action, oldFilePath);
	}

	@Override
	public AbstractActionState changeStateOnRemoteDelete() {
		// TODO Auto-generated method stub
		logStateTransission(getStateType(), EventType.LOCAL_CREATE, StateType.INITIAL);
		return new InitialState(action);
	}

	@Override
	public AbstractActionState changeStateOnRemoteCreate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractActionState changeStateOnRemoteUpdate() {
		// TODO Auto-generated method stub
		return new RemoteUpdateState(action);
	}

	@Override
	public AbstractActionState changeStateOnRemoteMove(Path oldFilePath) {
		logStateTransission(getStateType(), EventType.LOCAL_CREATE, StateType.REMOTE_MOVE);
		return new RemoteMoveState(action, oldFilePath);
	}

	@Override
	public ExecutionHandle execute(FileManager fileManager) throws NoSessionException,
			NoPeerConnectionException, InvalidProcessStateException {
		// TODO Auto-generated method stub
		logger.error("Execute in the ESTABLISHED state is only called due to wrong behaviour! {}", action.getFilePath());
		notifyActionExecuteSucceeded();
		return null;
	}

	@Override
	public AbstractActionState handleLocalCreate() {
		// TODO Auto-generated method stub
		action.getFile().updateContentHash();
		return changeStateOnLocalCreate();
	}

//	@Override
//	public AbstractActionState handleLocalDelete() {
//		if(action.getFile().isFile()){
//			SetMultimap<String, FileComponent> deletedFiles = action.getFileEventManager().getDeletedFileComponents();
//			deletedFiles.put(action.getFile().getContentHash(), action.getFile());
//		}
//		IFileEventManager eventManager = action.getFileEventManager();
//		FileComponent file = eventManager.getFileTree().deleteComponent(action.getFilePath().toString());
//		eventManager.getFileComponentQueue().add(file);
//		return changeStateOnLocalDelete();
//	}

	@Override
	public AbstractActionState handleLocalUpdate() {
		// TODO Auto-generated method stub
//		String newHash = PathUtils.computeFileContentHash(action.getFile().getPath());
//		logger.debug("File {} - Old hash: {} New Hash {}", action.getFilePath(), action.getFile().getContentHash(), newHash);
//		if(action.getFile().getContentHash().equals(newHash)){
//			logger.info("The content hash has not changed despite the onLocalFileModified event. No actions taken & returned.");
//			return this;
//		}
//		action.getFile().updateContentHash(newHash);

		updateTimeAndQueue();
		return changeStateOnLocalUpdate();
	}

	@Override
	public AbstractActionState handleLocalMove(Path newPath) {
		// TODO Auto-generated method stub
//		throw new NotImplException("EstablishedState.handleLocalMove");
//		try {
//			Files.move(action.getFilePath().toFile(), newPath.toFile());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		action.getFileEventManager().getFileTree().deleteComponent(action.getFilePath().toString());
//		action.getFileEventManager().getFileTree().putComponent(newPath.toString(), action.getFile());

		return changeStateOnLocalMove(newPath);
	}

	@Override
	public AbstractActionState handleRemoteCreate() {
		// TODO Auto-generated method stub
		throw new NotImplException("EstablishedState.handleRemoteCreate");
	}

	@Override
	public AbstractActionState handleRemoteDelete() {
		logger.debug("EstablishedState.handleRemoteDelete");
		IFileEventManager eventManager = action.getEventManager();
		eventManager.getFileTree().deleteComponent(action.getFilePath().toString());
		eventManager.getFileComponentQueue().remove(action.getFile());
//		action.getFilePath().toFile().delete();
		try {
			java.nio.file.Files.delete(action.getFilePath());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return changeStateOnRemoteDelete();
	}

	@Override
	public AbstractActionState handleRemoteUpdate() {
		// TODO Auto-generated method stub
		updateTimeAndQueue();
		return changeStateOnRemoteUpdate();
	}

	@Override
	public AbstractActionState handleRemoteMove(Path dstPath) {
		Path oldPath = action.getFilePath();
		logger.debug("Modify the tree accordingly. Src: {} Dst: {}", action.getFilePath(), dstPath);
		FileComponent deleted = action.getEventManager().getFileTree().deleteComponent(action.getFilePath().toString());
		action.getEventManager().getFileTree().putComponent(dstPath.toString(), action.getFile());
		
		Path path = dstPath;
		logger.debug("Execute REMOTE MOVE: {}", path);
		try {
			com.google.common.io.Files.move(oldPath.toFile(), path.toFile());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
//		notifyActionExecuteSucceeded();
//		if(deleted.equals(action.getFile())){
//			logger.debug("EQUALS {}", action.getCurrentState().getClass());
//		} else {
//			logger.debug("NOT EQUALS", deleted.getAction().getCurrentState().getClass());
//		}
//		IFileEventManager manager = action.getFileEventManager();
//		manager.cr
//		action.getFileEventManager().getFileTree().putComponent(dstPath.toString(), action.getFile());
//		logger.debug("--oldPath {} ", oldPath);
		
		//updateTimeAndQueue();
		return changeStateOnRemoteMove(oldPath);
	}

	@Override
	public AbstractActionState changeStateOnLocalRecover(int version) {
		// TODO Auto-generated method stub
		return new RecoverState(action, version);
	}

	@Override
	public AbstractActionState handleLocalRecover(int version) {
		// TODO Auto-generated method stub
		updateTimeAndQueue();
		return changeStateOnLocalRecover(version);
	}

}
