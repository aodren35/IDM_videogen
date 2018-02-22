package utils;

import java.util.Arrays;
import java.util.Random;


/*
 * Classe permettant de générer de l'aléatoire dans le média séléctionné
 */

public class Randomiser {
	private boolean displayed;
	private int choices;
	private double prob;
	private boolean[] displayedTab;
	
	public boolean[] getDisplayedTab() {
		return displayedTab;
	}

	public void setDisplayedTab(boolean[] displayedTab) {
		this.displayedTab = displayedTab;
	}

	public double getProb() {
		return prob;
	}

	public void setProb(double prob) {
		this.prob = prob;
	}

	public Randomiser() {
		this.choices = 0;
		this.prob = -1;
	}
	
	public int getChoices() {
		return choices;
	}

	public void setChoices(int choices) {
		this.choices = choices;
		this.displayedTab = new boolean[choices];
		Arrays.fill(displayedTab, false);
	}

	public boolean isDisplayed() {
		return displayed;
	}

	public void setDisplayed(boolean displayed) {
		this.displayed = displayed;
	}

	public int randomize() {
		if(prob == -1) {
			if (choices == 0) {
				// 50%
				prob = 0.5;
				if (this.setDisplayedByProb()) {
					return 1;
				} else  {
					return 0;
				}
			} else if (choices == -1) {
				// no choices 0%
				prob = 0;
				return 0;
			}else if (choices == 1) {
				// 100%
				prob = 1;
				return 0;
			} else {
				// proportionnel au nombre de choix
				prob = 1/choices;
				return this.getDisplayedInAlts();
			}
		} else {
			// prob already set
			if (this.setDisplayedByProb()) {
				return 1;
			} else  {
				return 0;
			}
		}
	}
	
	private boolean setDisplayedByProb() {
		 return Math.random() > 1.0 - prob;
	}
	
	private int getDisplayedInAlts() {
		Random r = new Random();
		int Low = 0;
		int High = choices - 1;
		return r.nextInt(High-Low) + Low;
	}
}
