package no.janco;


import java.util.List;

public class Locales {

    public static final Locale LOCALE_NORWEGIAN = new Locale("nb", "NO", "Norwegian", "Ida");
    public static final Locale LOCALE_ENGLISH = new Locale("en", "EN", "English", "Ruth");
    public static final Locale LOCALE_SPANISH = new Locale("es", "ES", "Spanish", "Lucia");
   // public static final Locale LOCALE_FRENCH = new Locale("fr", "FR", "French", "Léa");
    public static final Locale LOCALE_GERMAN = new Locale("de", "DE", "German", "Vicki");
    public static final Locale LOCALE_JAPAN = new Locale("js", "JA", "Japanese", "Kazuha");
    public static final Locale LOCALE_ITALIAN = new Locale("it", "IT", "Italian", "Bianca");
    public static final Locale LOCALE_PORTUGUESE = new Locale("por", "PT", "Portuguese", "Camila");

    public static final List<Locale> ALL = List.of(LOCALE_NORWEGIAN, LOCALE_ENGLISH, LOCALE_SPANISH, LOCALE_GERMAN, LOCALE_JAPAN, LOCALE_ITALIAN, LOCALE_PORTUGUESE);

}
