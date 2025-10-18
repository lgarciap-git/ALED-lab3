package es.upm.dit.aled.lab3.binary;

/**
 * Represents a suffix in the FASTA data by storing its starting index. This
 * class is designed to be sorted by an external Comparator that uses the actual
 * sequence data.
 *
 * @author mmiguel, rgarciacarmona
 */
public class Suffix {
	public final int suffixIndex; 		// Posición en content en la que está el sufijo

	/**
	 * Creates a new Suffix.
	 * 
	 * @param index The starting position of the suffix in the data array.
	 */
	public Suffix(int index) {
		suffixIndex = index;
	}
}