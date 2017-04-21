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

    public String getTypeOfRecord() {
        return this.typeOfRecord.toString();
    }

    public String getBibliographicLevel() {
        return this.bibliographicLevel.toString();
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

    private enum TypeOfRecord {
        LANGUAGE_MATERIAL('a'),
        NOTATED_MUSIC('c'),
        MANUSCRIPT_NOTATED_MUSIC('d'),
        CARTOGRAPHIC_MATERIAL('e'),
        MANUSCRIPT_CARTOGRAPHIC_MATERIAL('f'),
        PROJECTED_MEDIUM('g'),
        NONMUSICAL_SOUND_RECORDING('i'),
        MUSICAL_SOUND_RECORDING('j'),
        TWO_DIMENSIONAL_NONPROJECTABLE_GRAPHIC('k'),
        COMPUTER_FILE('m'),
        KIT('o'),
        MIXED_MATERIALS('p'),
        THREE_DIMENSIONAL_ARTIFACT_OR_NATURALLY_OCCURRING_OBJECT('r'),
        MANUSCRIPT_LANGUAGE_MATERIAL('t');

        private char code;

        TypeOfRecord(final char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }
    }

    private enum BibliographicLevel {
        MONOGRAPHIC_COMPONENT_PART('a'),
        SERIAL_COMPONENT_PART('b'),
        COLLECTION('c'),
        SUBUNIT('d'),
        INTEGRATING_RESOURCE('i'),
        MONOGRAPH_ITEM('m'),
        SERIAL('s');

        private char code;

        BibliographicLevel(final char code) {
            this.code = code;
        }

        public char getCode() {
            return code;
        }
    }

}
