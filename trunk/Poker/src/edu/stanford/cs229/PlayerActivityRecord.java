package edu.stanford.cs229;

import java.util.Calendar;
import java.util.logging.Logger;

/***
 * Represents an "activity record" for a player.  This is used for:
 * <ul>
 * <li>Displaying information in the web application and on the command-line</li>
 * <li>To log to a history file</li>
 * </ul>
 * @author ago
 *
 */
public class PlayerActivityRecord {
	private static Logger logger = Logger.getLogger("edu.stanford.cs229.PlayerActivityRecord");
	private static Logger fileLogger = Logger.getLogger("file.PlayerActivityRecord");
	
	private final String playerId;
	private final String name;
	private final int gameNum;
	private final int phaseNum;
	private Hand hand;
	private long responseTime;
	private PlayerAction playerAction;
	private int resultState;
	
	/**
	 * Constructor used to record a player action (check or call/bet or raise/fold).
	 * @param player
	 * @param gameNum
	 * @param phaseNum
	 * @param playerAction
	 */
	public PlayerActivityRecord (AbstractPlayer player, int gameNum, int phaseNum, PlayerAction playerAction) {
		this.playerId = player.getId();
		this.name = player.getName();
		this.hand = player.getHand();
		this.gameNum = gameNum;
		this.phaseNum = phaseNum;
		this.playerAction = playerAction;
		this.responseTime = playerAction.getResponseTime();
		logger.fine(this.toString());
		logToFile();
	}
	
	/**
	 * Constructor used to a record a result (win/lose/tie).
	 * @param player
	 * @param gameNum
	 * @param phaseNum
	 * @param resultState
	 */
	public PlayerActivityRecord (AbstractPlayer player, int gameNum, int phaseNum, int resultState) {
		this.playerId = player.getId();
		this.name = player.getName();
		this.hand = player.getHand();
		this.gameNum = gameNum;
		this.phaseNum = phaseNum;
		this.resultState = resultState;
		logger.fine(this.toString());
		logToFile();
	}
	
	/**
	 * Logs the activity record to a file.  This is used for analysis.
	 * @return
	 */
	public String logToFile() {
		Calendar c = Calendar.getInstance();
		
		String s = c.getTime() + "," + playerId + "," + gameNum + "," + phaseNum + ",";
		s += hand.toString() + "," + Util.computeValue(hand) + ",";
		if(playerAction != null) {
			s += "," +playerAction.getActionType() + "," + playerAction.getBet() + "," + responseTime;
		} else {
			s += "," + resultState;
		}
		fileLogger.finest(s);
		return s;
	}
	
	/**
	 * Prints a pretty string version of the activity record. This is used in
	 * the HTML of the web application.
	 */
	public String toString() {
		if (playerAction != null) {
			String amountString = "";
			int amount = playerAction.getBet();
			if(amount > 0) {
				amountString += "$" + Integer.toString(amount);
			}
			if (playerAction.getActionType() == ActionType.FOLD)
				return name + " folds";
			if (playerAction.getActionType() == ActionType.BET_OR_RAISE) {
				if(playerAction.getBetType().equals(BetType.SMALL_BLIND)) 
					return name + " puts a small blind of " + amountString; 
				if(playerAction.getBetType().equals(BetType.BIG_BLIND))
					return name + " puts a big blind of " + amountString;
				if(playerAction.getBetType().equals(BetType.BET)) 
					return name + " bets " + amountString;
				if(playerAction.getBetType().equals(BetType.RAISE))
					return name + " raises " + amountString;
				throw new IllegalArgumentException("Invalid bet type specified");
			}
			if (playerAction.getActionType() == ActionType.CHECK_OR_CALL)
				if(amount > 0) {
					return name + " calls " + amountString;
				} else {
					return name + " checks";
				}
		} else {
			if (resultState == ResultState.WIN) {
				return "<b>" + name + " wins!</b>";
			}
			if (resultState == ResultState.TIE) {
				return "<b>" + name + " ties</b>";
			}
			if (resultState == ResultState.LOSE) {
				return "<b>" + name + " loses</b>";
			}
		}
		throw new RuntimeException("PlayeActivityRecord is malformed");		
	}

	public int getGameNum() {
		return gameNum;
	}

	public String getName() {
		return name;
	}

	public int getPhaseNum() {
		return phaseNum;
	}

	public PlayerAction getPlayerAction() {
		return playerAction;
	}

	public String getPlayerId() {
		return playerId;
	}

	public int getResultState() {
		return resultState;
	}

	public Hand getHand() {
		return hand;
	}

	public long getResponseTime() {
		return responseTime;
	}
}