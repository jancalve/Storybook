package no.janco;

public class Locale {

    private java.util.Locale locale;

    private String chatIdentifier;

    private String voiceId;

    public Locale(String language, String country, String chatIdentifier, String voiceId) {
        this.locale = new java.util.Locale(language, country);
        this.chatIdentifier = chatIdentifier;
        this.voiceId = voiceId;
    }

    public String getCountry() {
        return this.locale.getCountry();
    }

    public String getLanguage() {
        return this.locale.getLanguage();
    }

    public String getChatIdentifier() {
        return this.chatIdentifier;
    }

    public String getVoiceId() {
        return this.voiceId;
    }


}
