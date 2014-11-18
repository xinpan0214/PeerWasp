package org.peerbox.watchservice.states;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Path;

import org.hive2hive.core.exceptions.IllegalFileLocation;
import org.hive2hive.core.exceptions.NoPeerConnectionException;
import org.hive2hive.core.exceptions.NoSessionException;
import org.hive2hive.processframework.exceptions.InvalidProcessStateException;
import org.hive2hive.processframework.interfaces.IProcessComponent;
import org.peerbox.FileManager;
import org.peerbox.watchservice.Action;
import org.peerbox.watchservice.PeerboxVersionSelector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecoverState extends AbstractActionState{
	
	private final static Logger logger = LoggerFactory.getLogger(RecoverState.class);
	
	private int fVersionToRecover = 0;
	public RecoverState(Action action, int versionToRecover) {
		super(action);
		fVersionToRecover = versionToRecover;
		// TODO Auto-generated constructor stub
	}

	@Override
	public AbstractActionState changeStateOnLocalCreate() {
		// TODO Auto-generated method stub
		return new LocalCreateState(action);
	}

	@Override
	public AbstractActionState changeStateOnLocalDelete() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractActionState changeStateOnLocalUpdate() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractActionState changeStateOnLocalMove(Path oldFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractActionState changeStateOnLocalRecover(int versionToRecover) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractActionState changeStateOnRemoteDelete() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractActionState changeStateOnRemoteUpdate() {
		// TODO Auto-generated method stub
		return new InitialState(action);
	}

	@Override
	public AbstractActionState changeStateOnRemoteMove(Path oldFilePath) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void execute(FileManager fileManager) throws NoSessionException,
			NoPeerConnectionException, IllegalFileLocation, InvalidProcessStateException {
			
		Path path = action.getFilePath();
		logger.debug("Execute RECOVER: {}", path);
		File currentFile = path.toFile();
		
		try {
			IProcessComponent process;
			process = fileManager.recover(currentFile, new PeerboxVersionSelector(fVersionToRecover));
			if(process != null){
				process.attachListener(new FileManagerProcessListener());
			} else {
				System.err.println("process is null");
			}
			
		} catch (FileNotFoundException | IllegalArgumentException e) {
			logger.error("{} in RECOVER: {}", e.getClass().getName(), path);
			e.printStackTrace();
		}
		
	}

	@Override
	public AbstractActionState changeStateOnRemoteCreate() {
		// TODO Auto-generated method stub
		return new ConflictState(action);
	}

	@Override
	public void handleLocalCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleLocalDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleLocalUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleLocalMove() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleLocalRecover() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRemoteCreate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRemoteDelete() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRemoteUpdate() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleRemoteMove() {
		// TODO Auto-generated method stub
		
	}

}
