package org.peerbox.watchservice.states;

import java.nio.file.Path;

import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.peerbox.FileManager;
import org.peerbox.exceptions.NotImplException;
import org.peerbox.watchservice.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RemoteMoveState extends AbstractActionState {

	private final static Logger logger = LoggerFactory.getLogger(RemoteMoveState.class);

	private Path oldPath;
	public RemoteMoveState(Action action, Path oldPath) {
		super(action, StateType.REMOTE_MOVE);
		this.oldPath = oldPath;
	}

//	public AbstractActionState getDefaultState(){
//		logger.debug("Stay in default state 'RemoteMoveState': {}", action.getFile().getPath());
//		return this;
//	}
	
	@Override
	public AbstractActionState changeStateOnLocalCreate() {
		logger.debug("Local Create Event:  ({})", action.getFilePath());
		return new EstablishedState(action);
	}

	@Override
	public AbstractActionState changeStateOnLocalUpdate() {
		logger.debug("Local Update Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public AbstractActionState changeStateOnLocalDelete() {
		logger.debug("Local Delete Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public AbstractActionState changeStateOnLocalMove(Path oldPath) {
		logger.debug("Local Move Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public AbstractActionState changeStateOnRemoteUpdate() {
		logger.debug("Remote Update Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public AbstractActionState changeStateOnRemoteDelete() {
		logger.debug("Remote Delete Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public AbstractActionState changeStateOnRemoteMove(Path oldPath) {
		logger.debug("Remote Move Event:  ({})", action.getFilePath());
		return this;
	}

	@Override
	public ExecutionHandle execute(FileManager fileManager) throws NoSessionException,
			NoPeerConnectionException {
		Path path = action.getFilePath();
		logger.debug("Execute REMOTE MOVE: {}", path);
//		try {
//			com.google.common.io.Files.move(oldPath.toFile(), path.toFile());
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		notifyActionExecuteSucceeded();
		return null;
	}

	@Override
	public AbstractActionState changeStateOnRemoteCreate() {
		// TODO Auto-generated method stub
		return new ConflictState(action);
	}
//	
//	@Override
//	public AbstractActionState getDefaultState(){
//		logger.debug("Returned own state");
//		return this;
//	}

	@Override
	public AbstractActionState handleLocalCreate() {

		// TODO Auto-generated method stub
//		throw new NotImplementedException("RemoteMoveState.handleLocalCreate");
//		updateTimeAndQueue();
//		notifyActionExecuteSucceeded();
		return changeStateOnLocalCreate();
	}

	@Override
	public AbstractActionState handleLocalDelete() {
		// TODO Auto-generated method stub
		throw new NotImplException("RemoteMoveState.handleLocalDelete");
	}

	@Override
	public AbstractActionState handleLocalUpdate() {
		// TODO Auto-generated method stub
		throw new NotImplException("RemoteMoveState.handleLocalUpdate");
	}

	@Override
	public AbstractActionState handleLocalMove(Path oldPath) {
		// TODO Auto-generated method stub
		throw new NotImplException("RemoteMoveState.handleLocalMove");
	}

	@Override
	public AbstractActionState handleRemoteCreate() {
		// TODO Auto-generated method stub
		throw new NotImplException("RemoteMoveState.handleRemoteCreate");
	}

	@Override
	public AbstractActionState handleRemoteDelete() {
		// TODO Auto-generated method stub
		throw new NotImplException("RemoteMoveState.handleRemoteDelete");
	}

	@Override
	public AbstractActionState handleRemoteUpdate() {
		// TODO Auto-generated method stub
		throw new NotImplException("RemoteMoveState.handleRemoteUpdate");
	}

	@Override
	public AbstractActionState handleRemoteMove(Path path) {
		// TODO Auto-generated method stub
		throw new NotImplException("RemoteMoveState.handleRemoteMove");
	}

	@Override
	public AbstractActionState changeStateOnLocalRecover(int version) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractActionState handleLocalRecover(int version) {
		// TODO Auto-generated method stub
		throw new NotImplException("RemoteMoveState.handleLocalRecover");
	}

}
