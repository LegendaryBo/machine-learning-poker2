package edu.stanford.cs229.web;

import java.io.Serializable;

import edu.stanford.cs229.AbstractPlayer;
import edu.stanford.cs229.ApplicationException;
import edu.stanford.cs229.GameState;
import edu.stanford.cs229.PlayerAction;

/**
 * Whenever the GameEngine interacts with this class, we have to set the
 * "isTurnSignal" to true, so that the servlet knows it can proceed.
 * 
 * @author ago
 * 
 */
public class WebPlayer extends AbstractPlayer implements Serializable {
	int SLEEP_DELAY = 500; //in ms
	int MAX_ATTEMPTS = 1000;
	
	private String playAgainSignal = null;
	private PlayerAction currentAction = null;  //Signal: to let the Game thread know about the player's decision 
	private boolean isTurnSignal;  //Signal used to let the Servlet know that it should show the page to the user
	
	public WebPlayer(String name) {
		super(name);
	}
	
	/**
	 * PubSub model
	 */
	public PlayerAction getAction(GameState state) throws ApplicationException {
		this.isTurnSignal = true;

		try {
			int count = 0;
			while(currentAction == null && count < MAX_ATTEMPTS) {
				count++;
				//logger.finest("Did not find action for " + name);
				Thread.sleep(SLEEP_DELAY);
				logger.info("Waiting");
			}
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
		
		PlayerAction action = new PlayerAction(currentAction.getActionType(), currentAction.getBet()); 
		this.currentAction = null;
		this.isTurnSignal = false;
		return action;
	}

	public void setCurrentAction(PlayerAction currentAction) {
		this.currentAction = currentAction;
	}
	
	public PlayerAction getCurrectAction() {
		return currentAction;
	}

	public boolean isTurn() {
		return isTurnSignal;
	}

	public void setTurn(boolean isTurn) {
		this.isTurnSignal = isTurn;
	}
	
	/**
	 * Waits for the "decision" signal to continue
	 */
	public boolean isDonePlaying() {
		isTurnSignal = true;  //Signal to servlet that it can continue
		
		int count = 0;
		try {
			while (playAgainSignal == null && count < MAX_ATTEMPTS) {
				count++;
				Thread.sleep(SLEEP_DELAY);
				logger.info("Waiting");
			}
			playAgainSignal = null;
			isTurnSignal = false;
			return false;
		} catch (InterruptedException e) {
			e.printStackTrace();
			return false;
		}
		
	}

	
	public String getPlayAgainSignal() {
		return playAgainSignal;
	}

	public void setPlayAgainSignal(String decisionToContinue) {
		this.playAgainSignal = decisionToContinue;
	}
	

}
