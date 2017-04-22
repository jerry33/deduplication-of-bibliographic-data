package models;

/**
 * Created by jerry on 4/21/17.
 */
public class TypeOfMaterial {

    private TypeOfRecord typeOfRecord;
    private BibliographicLevel bibliographicLevel;

    public TypeOfMaterial(final char typeOfRecord, final char bibliographicLevel) {
        this.typeOfRecord = resolveTypeOfRecord(typeOfRecord);
        this.bibliographicLevel = resolveBibliographicLevel(bibliographicLevel);
    }

    public TypeOfMaterial(final String typeOfMaterial) {
        if (typeOfMaterial.length() >= 2) { // should always be 2
            this.typeOfRecord = resolveTypeOfRecord(typeOfMaterial.charAt(0));
            this.bibliographicLevel = resolveBibliographicLevel(typeOfMaterial.charAt(1));
        }
    }

    private TypeOfRecord resolveTypeOfRecord(final char c) {
        for (TypeOfRecord typeOfRecord : TypeOfRecord.values()) {
            if (typeOfRecord.getCode() == c) {
                return typeOfRecord;
            }
        }
        throw new IllegalArgumentException("wrong MARC letter for type of record!");
    }

    public TypeOfRecord getTypeOfRecord() {
        return this.typeOfRecord;
    }

    public BibliographicLevel getBibliographicLevel() {
        return this.bibliographicLevel;
    }

    @Override
    public String toString() {
        return new StringBuilder().append(typeOfRecord.getCode()).append(bibliographicLevel.getCode()).toString();
    }

    private BibliographicLevel resolveBibliographicLevel(final char c) {
        for (BibliographicLevel bibliographicLevel : BibliographicLevel.values()) {
            if (bibliographicLevel.getCode() == c) {
                return bibliographicLevel;
            }
        }
        throw new IllegalArgumentException("wrong MARC letter for bibliographic level!");
    }

    public enum TypeOfRecord {
        LANGUAGE_MATERIAL('a', "Jazykový materiál"),
        NOTATED_MUSIC('c', "Notovaná hudba"),
        MANUSCRIPT_NOTATED_MUSIC('d', "Rukopisne notovaná hudba"),
        CARTOGRAPHIC_MATERIAL('e', "Kartografický materiál"),
        MANUSCRIPT_CARTOGRAPHIC_MATERIAL('f', "Rukopisný kartografický materiál"),
        PROJECTED_MEDIUM('g', "Projektované médium"),
        NONMUSICAL_SOUND_RECORDING('i', "Nehudobná zvuková nahrávka"),
        MUSICAL_SOUND_RECORDING('j', "Hudobná zvuková nahrávka"),
        TWO_DIMENSIONAL_NONPROJECTABLE_GRAPHIC('k', "Dvojdimenzionálna neprojektovateľná grafika"),
        COMPUTER_FILE('m', "Počítačový súbor"),
        KIT('o', "Set"),
        MIXED_MATERIALS('p', "Zmiešané materiály"),
        THREE_DIMENSIONAL_ARTIFACT_OR_NATURALLY_OCCURRING_OBJECT('r', "Trojdimenzionálny artefakt prirodzene vyskytujúceho sa objektu"),
        MANUSCRIPT_LANGUAGE_MATERIAL('t', "Rukopisný jazykový materiál");

        private char code;
        private String formattedTitle;

        TypeOfRecord(final char code, final String formattedTitle) {
            this.code = code;
            this.formattedTitle = formattedTitle;
        }

        public char getCode() {
            return code;
        }

        public String getFormattedTitle() {
            return formattedTitle;
        }
    }

    public enum BibliographicLevel {
        MONOGRAPHIC_COMPONENT_PART('a', "Časť monografu"),
        SERIAL_COMPONENT_PART('b', "Časť seriálu"),
        COLLECTION('c', "Kolekcia"),
        SUBUNIT('d', "Subjednotka"),
        INTEGRATING_RESOURCE('i', "Integrovaný zdroj"),
        MONOGRAPH_ITEM('m', "Monograf"),
        SERIAL('s', "Seriál");

        private char code;
        private String formattedTitle;

        BibliographicLevel(final char code, final String formattedTitle) {
            this.code = code;
            this.formattedTitle = formattedTitle;
        }

        public char getCode() {
            return code;
        }

        public String getFormattedTitle() {
            return formattedTitle;
        }
    }

}
