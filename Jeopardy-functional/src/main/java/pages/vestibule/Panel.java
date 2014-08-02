package pages.vestibule;

public enum Panel {
	SignIn ("sign-in-panel", null),
	Ready ("ready-panel", "sign-out-button-ready"),
	Start ("start-panel", "sign-out-button-start");
	
	private String panelId;
	private String signOutId;
	
	private Panel (String panelId, String signOutId) {
		this.panelId = panelId;
		this.signOutId = signOutId;
	}
	
	public String getPanelId () {return panelId;}
	
	public String getSignOutId () {return signOutId;}
}