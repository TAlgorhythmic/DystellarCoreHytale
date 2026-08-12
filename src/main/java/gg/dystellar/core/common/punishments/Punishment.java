package gg.dystellar.core.common.punishments;

import java.time.LocalDateTime;
import java.util.Optional;

import javax.annotation.Nullable;

public final class Punishment {

    private LocalDateTime creationDate;
    private LocalDateTime expirationDate;
	private String title;
	private String type;
    private String reason;
    private long id;
	private boolean allowChat;
	private boolean allowRanked;
	private boolean allowUnranked;
	private boolean allowJoinMinigames;

    public Punishment(long id, String title, String type, @Nullable LocalDateTime creationDate, LocalDateTime expirationDate, String reason, boolean allowChat, boolean allowRanked, boolean allowUnranked, boolean allowJoinMinigames) {
        this.id = id;
		this.title = title;
		this.type = type;
        this.creationDate = creationDate;
        this.expirationDate = expirationDate;
        this.reason = reason;

		this.allowChat = allowChat;
		this.allowRanked = allowRanked;
		this.allowUnranked = allowUnranked;
		this.allowJoinMinigames = allowJoinMinigames;
    }

	public long getId() { return this.id; }

    public boolean allowChat() { return this.allowChat; }
    public boolean allowRanked() { return this.allowRanked; }
    public boolean allowUnranked() { return this.allowUnranked; }
    public boolean allowJoinMinigames() { return this.allowJoinMinigames; }
    public String getTitle() { return this.title; }
	public String getType() { return type; }
    public String getReason() { return reason; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public Optional<LocalDateTime> getExpirationDate() { return Optional.ofNullable(expirationDate); }
    public void setExpirationDate(@Nullable LocalDateTime date) { this.expirationDate = date; }

	/**
	 * Whether this punishment has run out. A punishment without an expiration date is permanent and never expires.
	 */
	public boolean isExpired() {
		return this.expirationDate != null && this.expirationDate.isBefore(LocalDateTime.now());
	}
}
